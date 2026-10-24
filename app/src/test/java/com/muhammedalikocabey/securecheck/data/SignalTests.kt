package com.muhammedalikocabey.securecheck.data

import com.muhammedalikocabey.securecheck.data.check.app.InstallerCheck
import com.muhammedalikocabey.securecheck.data.check.app.SignatureCheck
import com.muhammedalikocabey.securecheck.data.check.device.EmulatorCheck
import com.muhammedalikocabey.securecheck.data.check.device.PackageLookup
import com.muhammedalikocabey.securecheck.data.check.device.RootCheck
import com.muhammedalikocabey.securecheck.data.check.device.SecurityPatchCheck
import com.muhammedalikocabey.securecheck.data.check.platform.ComponentRecord
import com.muhammedalikocabey.securecheck.data.check.platform.ExportedComponentsCheck
import com.muhammedalikocabey.securecheck.data.check.runtime.DebuggerCheck
import com.muhammedalikocabey.securecheck.data.check.runtime.FridaCheck
import com.muhammedalikocabey.securecheck.data.check.runtime.InjectedCodeCheck
import com.muhammedalikocabey.securecheck.data.check.runtime.MagiskCheck
import com.muhammedalikocabey.securecheck.data.check.runtime.ProcessMaps
import com.muhammedalikocabey.securecheck.data.check.runtime.XposedCheck
import com.muhammedalikocabey.securecheck.data.system.confidence
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckStatus
import com.muhammedalikocabey.securecheck.domain.model.Confidence
import com.muhammedalikocabey.securecheck.domain.model.Severity
import com.muhammedalikocabey.securecheck.domain.scan.TimeSource
import com.muhammedalikocabey.securecheck.support.FakeFiles
import com.muhammedalikocabey.securecheck.support.FakeProps
import com.muhammedalikocabey.securecheck.support.buildFields
import com.muhammedalikocabey.securecheck.support.buildInfo
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SignalTests {
    @Test
    fun `signal weights map to confidence tiers`() {
        assertNull(emptyList<com.muhammedalikocabey.securecheck.data.system.Signal>().confidence())
        assertEquals(Confidence.LOW, listOf(sig(1)).confidence())
        assertEquals(Confidence.MEDIUM, listOf(sig(1), sig(1)).confidence())
        assertEquals(Confidence.HIGH, listOf(sig(3)).confidence())
    }

    private fun sig(weight: Int) = com.muhammedalikocabey.securecheck.data.system.Signal("s", weight)
}

class RootCheckTest {
    private val installed = mutableSetOf<String>()
    private val packages = PackageLookup { it in installed }

    private fun check(files: FakeFiles = FakeFiles(), props: FakeProps = FakeProps(), tags: String = "release-keys") =
        RootCheck(files, props, packages, buildFields(tags = tags))

    @Test
    fun `clean device passes`() = runBlocking {
        assertEquals(CheckStatus.PASSED, check().run().status)
    }

    @Test
    fun `su binary is a high confidence finding`() = runBlocking {
        val result = check(FakeFiles(existing = setOf("/system/xbin/su"))).run()
        assertEquals(CheckStatus.FINDING, result.status)
        assertEquals(Confidence.HIGH, result.confidence)
    }

    @Test
    fun `test-keys alone is only a low confidence signal`() = runBlocking {
        val result = check(tags = "test-keys").run()
        assertEquals(Confidence.LOW, result.confidence)
    }

    @Test
    fun `root manager package counts`() = runBlocking {
        installed += "com.topjohnwu.magisk"
        assertEquals(Confidence.MEDIUM, check().run().confidence)
    }

    @Test
    fun `writable system mount is detected from proc mounts`() {
        val mounts = listOf(
            "/dev/block/dm-3 /system ext4 rw,seclabel,relatime 0 0",
            "/dev/block/dm-4 /vendor ext4 ro,seclabel,relatime 0 0",
            "tmpfs /data tmpfs rw,nosuid 0 0",
        )
        assertEquals(listOf("/system"), RootCheck.writableSystemMounts(mounts))
    }

    @Test
    fun `ro debuggable property adds a signal`() = runBlocking {
        val result = check(props = FakeProps(mapOf("ro.debuggable" to "1"))).run()
        assertEquals(CheckStatus.FINDING, result.status)
    }
}

class EmulatorCheckTest {
    @Test
    fun `real device has no signals`() {
        assertTrue(EmulatorCheck.signals(buildFields(), false, emptyList()).isEmpty())
    }

    @Test
    fun `ranchu emulator is detected`() {
        val fields = buildFields(
            fingerprint = "google/sdk_gphone64_arm64/emu64a:15/AP1A:userdebug/dev-keys",
            model = "sdk_gphone64_arm64",
            hardware = "ranchu",
            product = "sdk_gphone64_arm64",
        )
        assertEquals(Confidence.HIGH, EmulatorCheck.signals(fields, true, listOf("/dev/qemu_pipe")).confidence())
    }

    @Test
    fun `emulator in a debug build is expected, not a finding`() = runBlocking {
        val fields = buildFields(hardware = "ranchu", model = "Android SDK built for x86")
        val check = EmulatorCheck(fields, buildInfo(isDebug = true), FakeProps(), FakeFiles())
        assertEquals(CheckStatus.EXPECTED, check.run().status)
    }

    @Test
    fun `emulator in a release build is a finding`() = runBlocking {
        val fields = buildFields(hardware = "ranchu", model = "Android SDK built for x86")
        val check = EmulatorCheck(fields, buildInfo(isDebug = false), FakeProps(), FakeFiles())
        assertEquals(CheckStatus.FINDING, check.run().status)
    }
}

class SecurityPatchCheckTest {
    private val today = SecurityPatchCheck.epochDayOf("2026-06-01")!! * 86_400_000L

    private fun result(patch: String) = runBlocking {
        SecurityPatchCheck(buildFields(securityPatch = patch), TimeSource { today }).run()
    }

    @Test
    fun `epoch day matches known dates`() {
        assertEquals(0L, SecurityPatchCheck.epochDayOf("1970-01-01"))
        assertEquals(19_723L, SecurityPatchCheck.epochDayOf("2024-01-01"))
        assertEquals(11_016L, SecurityPatchCheck.epochDayOf("2000-02-29"))
    }

    @Test
    fun `invalid input is rejected`() {
        assertNull(SecurityPatchCheck.epochDayOf(""))
        assertNull(SecurityPatchCheck.epochDayOf("2026-13-01"))
        assertNull(SecurityPatchCheck.epochDayOf("not-a-date"))
    }

    @Test
    fun `recent patch passes`() = assertEquals(CheckStatus.PASSED, result("2026-03-01").status)

    @Test
    fun `patch between 180 and 365 days is a medium finding`() {
        val r = result("2025-10-01")
        assertEquals(CheckStatus.FINDING, r.status)
        assertEquals(Severity.MEDIUM, r.severity)
    }

    @Test
    fun `patch older than a year is a high finding`() {
        assertEquals(Severity.HIGH, result("2025-01-01").severity)
    }

    @Test
    fun `unreadable patch level is unavailable`() = assertEquals(CheckStatus.UNAVAILABLE, result("").status)
}

class SignatureVerdictTest {
    private val id = CheckId.SIGNATURE

    @Test
    fun `pinned certificate match passes`() {
        val r = SignatureCheck.verdict(id, listOf("abcd"), false, "AB:CD", false)
        assertEquals(CheckStatus.PASSED, r.status)
    }

    @Test
    fun `pinned certificate mismatch is critical`() {
        val r = SignatureCheck.verdict(id, listOf("abcd"), false, "ffff", false)
        assertEquals(Severity.CRITICAL, r.severity)
        assertEquals(CheckStatus.FINDING, r.status)
    }

    @Test
    fun `debug certificate is expected in debug and a finding in release`() {
        assertEquals(CheckStatus.EXPECTED, SignatureCheck.verdict(id, listOf("a"), true, "", true).status)
        assertEquals(CheckStatus.FINDING, SignatureCheck.verdict(id, listOf("a"), true, "", false).status)
    }

    @Test
    fun `without a pin the fingerprint is informational`() {
        assertEquals(CheckStatus.INFO, SignatureCheck.verdict(id, listOf("a"), false, "", false).status)
    }

    @Test
    fun `sha256 of empty input is the well known digest`() {
        assertEquals(
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            SignatureCheck.sha256Hex(ByteArray(0)),
        )
    }
}

class InstallerCheckTest {
    @Test
    fun `play store is trusted`() = assertEquals(
        CheckStatus.PASSED,
        InstallerCheck.evaluate(CheckId.INSTALLER, "com.android.vending", false).status,
    )

    @Test
    fun `sideload in release is a medium confidence finding`() {
        val r = InstallerCheck.evaluate(CheckId.INSTALLER, null, false)
        assertEquals(CheckStatus.FINDING, r.status)
        assertEquals(Confidence.MEDIUM, r.confidence)
    }

    @Test
    fun `any installer in a debug build is expected`() =
        assertEquals(CheckStatus.EXPECTED, InstallerCheck.evaluate(CheckId.INSTALLER, null, true).status)
}

class RuntimeSignalTest {
    @Test
    fun `maps parser extracts the path column`() {
        val line = "7a1c000000-7a1c021000 r-xp 00000000 fd:05 1234  /data/local/tmp/frida-agent-64.so"
        assertEquals("/data/local/tmp/frida-agent-64.so", ProcessMaps.pathOf(line))
        assertNull(ProcessMaps.pathOf("7a1c000000-7a1c021000 rw-p 00000000 00:00 0"))
    }

    @Test
    fun `frida agent in maps is a strong signal`() {
        val maps = listOf("7a-7b r-xp 0 fd:05 1  /data/local/tmp/frida-agent-64.so")
        val signals = FridaCheck.signals(maps, emptyList(), emptyList(), false)
        assertEquals(Confidence.HIGH, signals.confidence())
    }

    @Test
    fun `glib thread names alone stay low confidence`() {
        val signals = FridaCheck.signals(emptyList(), listOf("gmain", "main"), emptyList(), false)
        assertEquals(Confidence.LOW, signals.confidence())
    }

    @Test
    fun `frida check passes on a clean process`() = runBlocking {
        val check = FridaCheck(
            FakeFiles(
                lines = mapOf(
                    "/proc/self/maps" to listOf("7a-7b r-xp 0 fd:05 1 /system/lib64/libc.so"),
                ),
            ),
            com.muhammedalikocabey.securecheck.support.FakePorts(),
        )
        assertEquals(CheckStatus.PASSED, check.run().status)
    }

    @Test
    fun `xposed bridge on the class path is detected`() {
        val signals = XposedCheck.signals(
            emptyList(),
            "/system/framework/XposedBridge.jar",
            emptyList(),
            emptyList(),
            emptyList(),
        )
        assertEquals(Confidence.HIGH, signals.confidence())
    }

    @Test
    fun `this app's own xposed check class on the stack is not a signal`() {
        val own = listOf(XposedCheck::class.java.name, "kotlinx.coroutines.DispatchedTask")
        assertTrue(XposedCheck.signals(emptyList(), "", emptyList(), own, emptyList()).isEmpty())
    }

    @Test
    fun `hook framework frames on the stack are a high confidence signal`() {
        val frames = listOf("LSPHooker_.onCreate", "de.robv.android.xposed.XposedBridge")
        val signals = XposedCheck.signals(emptyList(), "", emptyList(), frames, emptyList())
        assertEquals(Confidence.HIGH, signals.confidence())
    }

    @Test
    fun `xposed manager package alone is medium confidence`() {
        val signals = XposedCheck.signals(emptyList(), "", emptyList(), emptyList(), listOf("org.lsposed.manager"))
        assertEquals(Confidence.MEDIUM, signals.confidence())
    }

    @Test
    fun `magisk traces in the mount table are detected`() {
        val signals = MagiskCheck.signals(
            emptyList(),
            listOf("1 2 0:3 / /sbin/.magisk rw - tmpfs tmpfs rw"),
            emptyList(),
        )
        assertEquals(Confidence.HIGH, signals.confidence())
    }

    @Test
    fun `injected code flags tmp and foreign app paths but not own paths`() {
        val paths = setOf(
            "/data/local/tmp/inject.so",
            "/data/app/~~x/com.example.app-1/lib/arm64/libok.so",
            "/data/data/com.example.app/code_cache/libown.so",
            "/data/data/com.other.app/files/libhook.so",
            "/system/lib64/libc.so",
        )
        val flagged = InjectedCodeCheck.signals(paths, "com.example.app").map { it.description }
        assertEquals(2, flagged.size)
        assertTrue(flagged.any { "inject.so" in it })
        assertTrue(flagged.any { "libhook.so" in it })
    }

    @Test
    fun `data files mapped from another app are not injected code`() {
        val paths = setOf(
            "/data/user/0/com.google.android.gms/files/fonts/opentype/Noto_COLR_Emoji_Compat.ttf",
            "/data/data/com.example.app/no_backup/androidx.work.workdb-shm",
            "/storage/emulated/0/Download/sample.ttf",
        )
        assertTrue(InjectedCodeCheck.signals(paths, "com.example.app").isEmpty())
    }

    @Test
    fun `dex or jar from a shared location is still flagged`() {
        val paths = setOf("/sdcard/payload.dex", "/data/local/tmp/agent.jar")
        assertEquals(2, InjectedCodeCheck.signals(paths, "com.example.app").size)
    }

    @Test
    fun `deleted shared object is a weaker signal`() {
        val signals = InjectedCodeCheck.signals(setOf("/data/dalvik-cache/x.so (deleted)"), "com.example.app")
        assertEquals(Confidence.MEDIUM, signals.confidence())
    }

    @Test
    fun `tracer pid is parsed from proc status`() {
        assertEquals(0, DebuggerCheck.tracerPid(listOf("Name:\tapp", "TracerPid:\t0")))
        assertEquals(4242, DebuggerCheck.tracerPid(listOf("TracerPid:\t4242")))
        assertNull(DebuggerCheck.tracerPid(emptyList()))
    }

    @Test
    fun `debugger attached in a release build is a finding`() = runBlocking {
        val files = FakeFiles(lines = mapOf("/proc/self/status" to listOf("TracerPid:\t99")))
        val release = DebuggerCheck({ false }, files, buildInfo(isDebug = false)).run()
        val debug = DebuggerCheck({ false }, files, buildInfo(isDebug = true)).run()
        assertEquals(CheckStatus.FINDING, release.status)
        assertEquals(CheckStatus.EXPECTED, debug.status)
    }
}

class ExportedComponentsTest {
    @Test
    fun `only unprotected exported components are reported, launcher excluded`() {
        val records = listOf(
            ComponentRecord("activity", "pkg.MainActivity", exported = true, permission = null),
            ComponentRecord("service", "pkg.Job", exported = true, permission = "android.permission.BIND_JOB_SERVICE"),
            ComponentRecord("receiver", "pkg.Open", exported = true, permission = null),
            ComponentRecord("provider", "pkg.Private", exported = false, permission = null),
        )
        val exposed = ExportedComponentsCheck.exposed(records, "pkg.MainActivity")
        assertEquals(listOf("pkg.Open"), exposed.map { it.name })
    }
}

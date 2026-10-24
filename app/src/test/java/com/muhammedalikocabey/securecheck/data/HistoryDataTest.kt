package com.muhammedalikocabey.securecheck.data

import com.muhammedalikocabey.securecheck.data.export.HtmlReport
import com.muhammedalikocabey.securecheck.data.history.AesGcmReportCipher
import com.muhammedalikocabey.securecheck.data.history.JsonFileReportStore
import com.muhammedalikocabey.securecheck.data.history.ReportJson
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.support.reportOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class HistoryDataTest {
    @get:Rule
    val folder = TemporaryFolder()

    private fun store(limit: Int = 30) =
        JsonFileReportStore(File(folder.root, "reports"), limit, io = Dispatchers.Unconfined)

    @Test
    fun `report survives an encode and decode round trip`() {
        val report = reportOf("r1", 1_700_000_000_000, CheckId.ROOT, CheckId.USER_CA)
        val decoded = ReportJson.decode(ReportJson.encode(report))

        assertEquals(report, decoded)
    }

    @Test
    fun `pretty and compact output decode to the same report`() {
        val report = reportOf("r1", 5, CheckId.VPN)

        assertEquals(ReportJson.decode(ReportJson.encode(report)), ReportJson.decode(ReportJson.encode(report, true)))
    }

    @Test
    fun `a check that no longer exists drops out instead of invalidating the report`() {
        val text = ReportJson.encode(reportOf("r1", 1)).replace("\"${CheckId.ROOT.key}\"", "\"removed_check\"")
        val decoded = ReportJson.decode(text)!!

        assertNull(decoded.result(CheckId.ROOT))
        assertEquals(CheckId.entries.size - 1, decoded.results.size)
    }

    @Test
    fun `a newer schema and corrupt input are rejected`() {
        val text = ReportJson.encode(reportOf("r1", 1))
        val newer = text.replace("\"schemaVersion\":${ReportJson.SCHEMA_VERSION}", "\"schemaVersion\":99")

        assertNull(ReportJson.decode(newer))
        assertNull(ReportJson.decode("{not json"))
        assertNull(ReportJson.decode(""))
    }

    @Test
    fun `unknown keys from a later build are ignored`() {
        val text = ReportJson.encode(reportOf("r1", 1)).replaceFirst("{", "{\"added\":true,")

        assertNotNull(ReportJson.decode(text))
    }

    @Test
    fun `store returns newest first and keeps only the limit`() = runTest {
        val store = store(limit = 3)
        (1..5).forEach { store.save(reportOf("r$it", it * 1_000L)) }

        assertEquals(listOf("r5", "r4", "r3"), store.all().map { it.id })
    }

    @Test
    fun `a corrupt file is skipped and the rest still loads`() = runTest {
        val store = store()
        store.save(reportOf("good", 1_000))
        File(folder.root, "reports/999999999999999-broken.json").writeText("garbage")

        assertEquals(listOf("good"), store.all().map { it.id })
    }

    private fun newKey(): SecretKey = KeyGenerator.getInstance("AES").apply { init(256) }.generateKey()

    private fun encryptedStore(key: SecretKey) = JsonFileReportStore(
        File(folder.root, "reports"),
        cipher = AesGcmReportCipher { key },
        io = Dispatchers.Unconfined,
    )

    private fun reportFile() = File(folder.root, "reports").listFiles { f -> f.name.endsWith(".json") }!!.single()

    @Test
    fun `encrypted reports round trip and hide their content on disk`() = runTest {
        val store = encryptedStore(newKey())
        store.save(reportOf("secret-id", 1_000, CheckId.ROOT))

        assertEquals(listOf("secret-id"), store.all().map { it.id })
        val onDisk = String(reportFile().readBytes(), Charsets.ISO_8859_1)
        assertFalse(onDisk.contains("secret-id"))
        assertFalse(onDisk.contains("ROOT"))
    }

    @Test
    fun `reports saved before encryption existed are still read`() = runTest {
        val key = newKey()
        store().save(reportOf("legacy", 1_000))
        val store = encryptedStore(key)
        store.save(reportOf("fresh", 2_000))

        assertEquals(listOf("fresh", "legacy"), store.all().map { it.id })
    }

    @Test
    fun `a tampered file and a file sealed with another key are skipped`() = runTest {
        val store = encryptedStore(newKey())
        store.save(reportOf("good", 2_000))
        store.save(reportOf("tampered", 1_000))
        val tampered = File(folder.root, "reports").listFiles()!!.first { it.name.contains("tampered") }
        tampered.writeBytes(tampered.readBytes().also { it[it.size - 1] = (it[it.size - 1].toInt() xor 1).toByte() })

        assertEquals(listOf("good"), store.all().map { it.id })
        assertTrue(encryptedStore(newKey()).all().isEmpty())
    }

    @Test
    fun `clear removes every report and leaves no temporary file`() = runTest {
        val store = store()
        store.save(reportOf("a", 1))
        store.save(reportOf("b", 2))
        store.clear()

        assertTrue(store.all().isEmpty())
        assertTrue(File(folder.root, "reports").listFiles().orEmpty().isEmpty())
    }

    @Test
    fun `an id with path characters cannot escape the folder`() = runTest {
        val store = store()
        store.save(reportOf("../../evil", 1))

        assertEquals(1, store.all().size)
        assertEquals(1, File(folder.root, "reports").listFiles()!!.size)
        assertFalse(File(folder.root.parentFile, "evil.json").exists())
    }

    @Test
    fun `html report escapes evidence and stays script free`() {
        val base = reportOf("r1", 0)
        val hostile = base.copy(
            results = base.results.map {
                if (it.id == CheckId.ROOT) CheckResult.finding(it.id, listOf("<script>alert('x')</script>&")) else it
            },
        )
        val html = HtmlReport.render(hostile)

        assertFalse(html.contains("<script"))
        assertTrue(html.contains("&lt;script&gt;alert(&#39;x&#39;)&lt;/script&gt;&amp;"))
        assertTrue(html.contains("<title>SecureCheck report</title>"))
    }

    @Test
    fun `escape handles every special character`() {
        assertEquals("&amp;&lt;&gt;&quot;&#39;ok", HtmlReport.escape("&<>\"'ok"))
    }
}

package com.muhammedalikocabey.securecheck.di

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.muhammedalikocabey.securecheck.BuildConfig
import com.muhammedalikocabey.securecheck.data.AndroidScanEnvironment
import com.muhammedalikocabey.securecheck.data.check.app.AndroidInstallerSource
import com.muhammedalikocabey.securecheck.data.check.app.AndroidSigningCertificates
import com.muhammedalikocabey.securecheck.data.check.app.InstallerSource
import com.muhammedalikocabey.securecheck.data.check.app.SigningCertificates
import com.muhammedalikocabey.securecheck.data.check.device.AndroidPackageLookup
import com.muhammedalikocabey.securecheck.data.check.device.PackageLookup
import com.muhammedalikocabey.securecheck.data.history.JsonFileReportStore
import com.muhammedalikocabey.securecheck.data.history.KeystoreReportKey
import com.muhammedalikocabey.securecheck.data.notify.AndroidChangeNotifier
import com.muhammedalikocabey.securecheck.data.settings.PreferencesSettingsStore
import com.muhammedalikocabey.securecheck.data.system.AndroidDebuggerProbe
import com.muhammedalikocabey.securecheck.data.system.BuildFields
import com.muhammedalikocabey.securecheck.data.system.BuildInfo
import com.muhammedalikocabey.securecheck.data.system.ClassLookup
import com.muhammedalikocabey.securecheck.data.system.DebuggerProbe
import com.muhammedalikocabey.securecheck.data.system.GetpropSystemProps
import com.muhammedalikocabey.securecheck.data.system.PortProbe
import com.muhammedalikocabey.securecheck.data.system.ProcFiles
import com.muhammedalikocabey.securecheck.data.system.RealProcFiles
import com.muhammedalikocabey.securecheck.data.system.RuntimeClassLookup
import com.muhammedalikocabey.securecheck.data.system.SocketPortProbe
import com.muhammedalikocabey.securecheck.data.system.SystemProps
import com.muhammedalikocabey.securecheck.data.work.WorkManagerScanScheduler
import com.muhammedalikocabey.securecheck.domain.history.ChangeNotifier
import com.muhammedalikocabey.securecheck.domain.history.ReportStore
import com.muhammedalikocabey.securecheck.domain.scan.ScanEnvironment
import com.muhammedalikocabey.securecheck.domain.scan.TimeSource
import com.muhammedalikocabey.securecheck.domain.settings.ScanScheduler
import com.muhammedalikocabey.securecheck.domain.settings.SettingsStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.io.File
import javax.inject.Singleton

@Module
abstract class AppModule {
    @Binds abstract fun scanEnvironment(impl: AndroidScanEnvironment): ScanEnvironment

    @Binds abstract fun procFiles(impl: RealProcFiles): ProcFiles

    @Binds abstract fun systemProps(impl: GetpropSystemProps): SystemProps

    @Binds abstract fun portProbe(impl: SocketPortProbe): PortProbe

    @Binds abstract fun classLookup(impl: RuntimeClassLookup): ClassLookup

    @Binds abstract fun debuggerProbe(impl: AndroidDebuggerProbe): DebuggerProbe

    @Binds abstract fun packageLookup(impl: AndroidPackageLookup): PackageLookup

    @Binds abstract fun signingCertificates(impl: AndroidSigningCertificates): SigningCertificates

    @Binds abstract fun installerSource(impl: AndroidInstallerSource): InstallerSource

    @Binds abstract fun settingsStore(impl: PreferencesSettingsStore): SettingsStore

    @Binds abstract fun scanScheduler(impl: WorkManagerScanScheduler): ScanScheduler

    @Binds abstract fun changeNotifier(impl: AndroidChangeNotifier): ChangeNotifier

    companion object {
        @Provides
        fun packageManager(context: Context): PackageManager = context.packageManager

        @Provides
        fun timeSource(): TimeSource = TimeSource { System.currentTimeMillis() }

        /** Checks do blocking file, process and socket work, so they belong on the IO pool. */
        @Provides
        @Singleton
        fun checkDispatcher(): CoroutineDispatcher = Dispatchers.IO

        @Provides
        @Singleton
        fun reportStore(context: Context): ReportStore =
            JsonFileReportStore(File(context.filesDir, "reports"), cipher = KeystoreReportKey.cipher())

        @Provides
        @Singleton
        fun buildInfo(context: Context): BuildInfo = BuildInfo(
            packageName = context.packageName,
            isDebug = BuildConfig.DEBUG,
            versionName = BuildConfig.VERSION_NAME,
            versionCode = BuildConfig.VERSION_CODE.toLong(),
            builtTargetSdk = BuildConfig.BUILT_TARGET_SDK,
            expectedCertSha256 = BuildConfig.EXPECTED_CERT_SHA256,
            integrityProjectNumber = BuildConfig.INTEGRITY_PROJECT_NUMBER,
        )

        @Provides
        @Singleton
        fun buildFields(): BuildFields = BuildFields(
            fingerprint = Build.FINGERPRINT,
            model = Build.MODEL,
            manufacturer = Build.MANUFACTURER,
            brand = Build.BRAND,
            device = Build.DEVICE,
            product = Build.PRODUCT,
            hardware = Build.HARDWARE,
            board = Build.BOARD,
            tags = Build.TAGS.orEmpty(),
            securityPatch = Build.VERSION.SECURITY_PATCH.orEmpty(),
        )
    }
}

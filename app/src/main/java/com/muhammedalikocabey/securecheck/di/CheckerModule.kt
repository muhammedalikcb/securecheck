package com.muhammedalikocabey.securecheck.di

import android.app.Application
import android.content.Context
import androidx.security.crypto.MasterKey
import com.muhammedalikocabey.securecheck.core.crypto.ISecureCipher
import com.muhammedalikocabey.securecheck.core.crypto.SecureCipher
import com.muhammedalikocabey.securecheck.core.icc.ComponentScanner
import com.muhammedalikocabey.securecheck.core.icc.IntentHijackDetector
import com.muhammedalikocabey.securecheck.core.icc.PendingIntentLeak
import com.muhammedalikocabey.securecheck.core.location.MockLocationDetector
import com.muhammedalikocabey.securecheck.core.network.PinningTrustManager
import com.muhammedalikocabey.securecheck.core.network.SslPinningChecker
import com.muhammedalikocabey.securecheck.core.network.VpnDetector
import com.muhammedalikocabey.securecheck.core.obfuscation.ObfuscationDetector
import com.muhammedalikocabey.securecheck.core.tamper.DebugBuildDetector
import com.muhammedalikocabey.securecheck.core.webview.WebViewSecurityAnalyzer
import com.muhammedalikocabey.securecheck.data.datasource.SecurityChecker
import com.muhammedalikocabey.securecheck.data.detector.debugger.DebuggerDetectorImpl
import com.muhammedalikocabey.securecheck.data.detector.debugger.IDebuggerDetector
import com.muhammedalikocabey.securecheck.data.detector.installer.IInstallerValidator
import com.muhammedalikocabey.securecheck.data.detector.installer.InstallerValidatorImpl
import com.muhammedalikocabey.securecheck.data.detector.signature.ISignatureValidator
import com.muhammedalikocabey.securecheck.data.local.secure.SecureStorageManager
import com.muhammedalikocabey.securecheck.domain.usecase.CheckDebuggerStatusUseCase
import com.muhammedalikocabey.securecheck.domain.usecase.CheckInstallerUseCase
import com.muhammedalikocabey.securecheck.domain.usecase.CheckPlayIntegrityUseCase
import com.muhammedalikocabey.securecheck.domain.usecase.CheckRootStatusUseCase
import com.muhammedalikocabey.securecheck.domain.usecase.CheckSafetyNetUseCase
import com.muhammedalikocabey.securecheck.domain.usecase.CheckSignatureUseCase
import com.muhammedalikocabey.securecheck.domain.usecase.CheckTamperStatusUseCase
import com.muhammedalikocabey.securecheck.core.reflection.ReflectionUsageDetector
import com.muhammedalikocabey.securecheck.core.reflection.DexLoaderDetector
import com.muhammedalikocabey.securecheck.core.reverseanalysis.EmulatorDetector
import com.muhammedalikocabey.securecheck.core.reverseanalysis.FridaDetector
import com.muhammedalikocabey.securecheck.core.reverseanalysis.XposedDetector
import com.muhammedalikocabey.securecheck.data.detector.integrity.IPlayIntegrityValidator
import com.muhammedalikocabey.securecheck.data.detector.integrity.PlayIntegrityValidatorImpl
import com.muhammedalikocabey.securecheck.data.detector.root.IRootDetector
import com.muhammedalikocabey.securecheck.data.detector.root.RootDetectorImpl
import com.muhammedalikocabey.securecheck.data.detector.safetynet.ISafetyNetValidator
import com.muhammedalikocabey.securecheck.data.detector.safetynet.SafetyNetValidatorImpl
import com.muhammedalikocabey.securecheck.data.detector.signature.SignatureValidatorImpl
import com.muhammedalikocabey.securecheck.data.detector.tamper.ITamperDetector
import com.muhammedalikocabey.securecheck.data.detector.tamper.TamperDetectorImpl
import com.muhammedalikocabey.securecheck.domain.usecase.CheckComponentExportedUseCase
import com.muhammedalikocabey.securecheck.domain.usecase.CheckDebugBuildUseCase
import com.muhammedalikocabey.securecheck.domain.usecase.CheckDexLoaderUseCase
import com.muhammedalikocabey.securecheck.domain.usecase.CheckEmulatorUseCase
import com.muhammedalikocabey.securecheck.domain.usecase.CheckFridaUseCase
import com.muhammedalikocabey.securecheck.domain.usecase.CheckIntentHijackUseCase
import com.muhammedalikocabey.securecheck.domain.usecase.CheckMockLocationUseCase
import com.muhammedalikocabey.securecheck.domain.usecase.CheckObfuscationStatusUseCase
import com.muhammedalikocabey.securecheck.domain.usecase.CheckPendingIntentLeakUseCase
import com.muhammedalikocabey.securecheck.domain.usecase.CheckReflectionUseCase
import com.muhammedalikocabey.securecheck.domain.usecase.CheckSslPinningUseCase
import com.muhammedalikocabey.securecheck.domain.usecase.CheckStorageEncryptedUseCase
import com.muhammedalikocabey.securecheck.domain.usecase.CheckVpnStatusUseCase
import com.muhammedalikocabey.securecheck.domain.usecase.CheckWebViewSecurityUseCase
import com.muhammedalikocabey.securecheck.domain.usecase.CheckXposedDetectorUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

/**
 * Tüm güvenlik UseCase’lerini CompositeCheckSecurityUseCase içinde birleştiren modül
 *
 * Aggregates all security use cases into a single CompositeCheckSecurityUseCase
 *
 * @since 11.05.2025
 */
@Module
class CheckerModule {

    // Context & MasterKey
    @Provides
    @Singleton
    @Named("app_context")
    fun provideAppContext(app: Application): Context = app

    @Suppress("DEPRECATION")
    @Provides
    @Singleton
    fun provideMasterKey(@Named("app_context") context: Context): MasterKey =
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()


    // Secure Storage
    @Suppress("DEPRECATION")
    @Provides
    @Singleton
    fun provideSecureStorageManager(
        @Named("app_context") context: Context,
        masterKey: MasterKey
    ) = SecureStorageManager(context, masterKey)


    // Core Detectors
    @Provides
    @Singleton
    fun provideComponentScanner() = ComponentScanner()

    @Provides
    @Singleton
    fun providePendingIntentLeak() = PendingIntentLeak()

    @Provides
    @Singleton
    fun provideReflectionUsageDetector() = ReflectionUsageDetector()

    @Provides
    @Singleton
    fun provideDexLoaderDetector() = DexLoaderDetector()

    @Provides
    @Singleton
    fun provideFridaDetector() = FridaDetector()

    @Provides
    @Singleton
    fun provideXposedDetector() = XposedDetector()

    @Provides
    @Singleton
    fun provideEmulatorDetector() = EmulatorDetector()

    @Provides
    @Singleton
    fun provideMockLocationDetector() = MockLocationDetector()

    @Provides
    @Singleton
    fun provideVpnDetector() = VpnDetector()

    @Provides
    @Singleton
    fun provideWebViewSecurityAnalyzer() = WebViewSecurityAnalyzer()

    @Provides
    @Singleton
    fun provideDebugBuildDetector() = DebugBuildDetector()

    @Provides @Singleton
    fun provideObfuscationDetector(@Named("app_context") context: Context) = ObfuscationDetector(context)

    @Provides
    @Singleton
    fun provideIntentHijackDetector() = IntentHijackDetector()

    @Provides
    @Singleton
    fun provideSecureCipher(): ISecureCipher = SecureCipher()


    // Domain UseCases
    @Provides
    fun provideCheckRootStatusUseCase(checker: SecurityChecker) = CheckRootStatusUseCase(checker)

    @Provides
    fun provideCheckDebuggerStatusUseCase(checker: SecurityChecker) = CheckDebuggerStatusUseCase(checker)

    @Provides
    fun provideCheckInstallerUseCase(checker: SecurityChecker) = CheckInstallerUseCase(checker)

    @Provides
    fun provideCheckSignatureUseCase(checker: SecurityChecker) = CheckSignatureUseCase(checker)

    @Provides
    fun provideCheckTamperStatusUseCase(checker: SecurityChecker) = CheckTamperStatusUseCase(checker)

    @Provides
    fun provideCheckSafetyNetUseCase(checker: SecurityChecker) = CheckSafetyNetUseCase(checker)

    @Provides
    fun provideCheckPlayIntegrityUseCase(checker: SecurityChecker) = CheckPlayIntegrityUseCase(checker)

    @Provides
    fun provideCheckStorageEncryptedUseCase(manager: SecureStorageManager) = CheckStorageEncryptedUseCase(manager)

    @Provides
    fun provideCheckComponentExportedUseCase(scanner: ComponentScanner) = CheckComponentExportedUseCase(scanner)

    @Provides
    fun provideCheckPendingIntentLeakUseCase(detector: PendingIntentLeak) = CheckPendingIntentLeakUseCase(detector)

    @Provides
    fun provideCheckObfuscationStatusUseCase(detector: ObfuscationDetector) = CheckObfuscationStatusUseCase(detector)

    @Provides
    fun provideCheckIntentHijackUseCase(detector: IntentHijackDetector) = CheckIntentHijackUseCase(detector)

    @Provides
    fun provideCheckDebugBuildUseCase(detector: DebugBuildDetector) = CheckDebugBuildUseCase(detector)

    @Provides
    fun provideCheckWebViewSecurityUseCase(analyzer: WebViewSecurityAnalyzer) = CheckWebViewSecurityUseCase(analyzer)

    @Provides
    fun provideCheckDexLoaderUseCase(detector: DexLoaderDetector) = CheckDexLoaderUseCase(detector)

    @Provides
    fun provideCheckReflectionUseCase(detector: ReflectionUsageDetector) = CheckReflectionUseCase(detector)

    @Provides
    fun provideCheckFridaUseCase(detector: FridaDetector) = CheckFridaUseCase(detector)

    @Provides
    fun provideCheckXposedDetectorUseCase(detector: XposedDetector) = CheckXposedDetectorUseCase(detector)

    @Provides
    fun provideCheckEmulatorUseCase(detector: EmulatorDetector) = CheckEmulatorUseCase(detector)

    @Provides
    fun provideCheckVpnStatusUseCase(detector: VpnDetector) = CheckVpnStatusUseCase(detector)

    @Provides
    fun provideCheckMockLocationUseCase(detector: MockLocationDetector) = CheckMockLocationUseCase(detector)


    // Interface Bindings
    @Provides
    fun provideDebuggerDetector(): IDebuggerDetector = DebuggerDetectorImpl()

    @Provides
    fun provideInstallerValidator(): IInstallerValidator = InstallerValidatorImpl()

    @Provides
    @Singleton
    fun provideSignatureValidator(cipher: ISecureCipher): ISignatureValidator = SignatureValidatorImpl(cipher)

    @Provides
    @Singleton
    fun provideRootDetector(): IRootDetector = RootDetectorImpl()

    @Provides
    @Singleton
    fun provideTamperDetector(validator: ISignatureValidator): ITamperDetector = TamperDetectorImpl(validator)

    @Provides
    @Singleton
    fun providePlayIntegrityValidator(): IPlayIntegrityValidator = PlayIntegrityValidatorImpl()

    @Provides
    @Singleton
    fun provideSafetyNetValidator(): ISafetyNetValidator = SafetyNetValidatorImpl()

    @Provides
    @Singleton
    fun provideSecurityChecker(
        rootDetector: IRootDetector,
        signatureValidator: ISignatureValidator,
        debuggerDetector: IDebuggerDetector,
        installerValidator: IInstallerValidator,
        tamperDetector: ITamperDetector,
        playIntegrityValidator: IPlayIntegrityValidator,
        safetyNetValidator: ISafetyNetValidator
    ): SecurityChecker = SecurityChecker(
        rootDetector, signatureValidator, debuggerDetector,
        installerValidator, tamperDetector, playIntegrityValidator, safetyNetValidator
    )

    @Provides
    @Singleton
    fun providePinningTrustManager(): PinningTrustManager = PinningTrustManager()

    @Provides
    @Singleton
    fun provideSslPinningChecker(
        pinningTrustManager: PinningTrustManager,
        @Named("app_context") context: Context
    ): SslPinningChecker = SslPinningChecker(pinningTrustManager, context)

    @Provides
    fun provideCheckSslPinningUseCase(
        checker: SslPinningChecker
    ): CheckSslPinningUseCase = CheckSslPinningUseCase(checker)
}

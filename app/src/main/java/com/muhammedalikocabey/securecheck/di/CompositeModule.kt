package com.muhammedalikocabey.securecheck.di

import com.muhammedalikocabey.securecheck.domain.usecase.*
import dagger.Module
import dagger.Provides

/**
 * Tüm güvenlik kontrollerini içeren UseCase'leri bir araya getirerek
 * CompositeCheckSecurityUseCase sınıfına toplu şekilde sağlayan Dagger module
 *
 * Dagger module that groups all security check use cases into a single
 * CompositeCheckSecurityUseCase via a centralized SecurityCheckInputs data class
 *
 * Bu yapı, tüm kontrollerin tek seferde çalıştırılmasını sağlayan birleştirici mantığı destekler
 *
 * Supports batch execution of multiple security checks by providing grouped dependencies
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
@Module
class CompositeModule {

    @Provides
    fun provideSecurityCheckInputs(
        checkDebugBuild: CheckDebugBuildUseCase,
        checkDebuggerStatus: CheckDebuggerStatusUseCase,
        checkInstaller: CheckInstallerUseCase,
        checkIntentHijack: CheckIntentHijackUseCase,
        checkObfuscationStatus: CheckObfuscationStatusUseCase,
        checkPendingIntentLeak: CheckPendingIntentLeakUseCase,
        checkPlayIntegrity: CheckPlayIntegrityUseCase,
        checkRootStatus: CheckRootStatusUseCase,
        checkSafetyNet: CheckSafetyNetUseCase,
        checkSignature: CheckSignatureUseCase,
        checkSslPinning: CheckSslPinningUseCase,
        checkStorageEncrypted: CheckStorageEncryptedUseCase,
        checkTamperStatus: CheckTamperStatusUseCase,
        checkComponentExported: CheckComponentExportedUseCase,
        checkWebViewSecurityUseCase: CheckWebViewSecurityUseCase,
        checkDexLoaderUseCase: CheckDexLoaderUseCase,
        checkReflectionUseCase: CheckReflectionUseCase,
        checkFridaUseCase: CheckFridaUseCase,
        checkXposedDetectorUseCase: CheckXposedDetectorUseCase,
        checkEmulatorUseCase: CheckEmulatorUseCase,
        checkVpnStatusUseCase: CheckVpnStatusUseCase,
        checkMockLocationUseCase: CheckMockLocationUseCase
    ): SecurityCheckInputs {
        return SecurityCheckInputs(
            checkDebugBuild,
            checkDebuggerStatus,
            checkInstaller,
            checkIntentHijack,
            checkObfuscationStatus,
            checkPendingIntentLeak,
            checkPlayIntegrity,
            checkRootStatus,
            checkSafetyNet,
            checkSignature,
            checkSslPinning,
            checkStorageEncrypted,
            checkTamperStatus,
            checkComponentExported,
            checkWebViewSecurityUseCase,
            checkDexLoaderUseCase,
            checkReflectionUseCase,
            checkFridaUseCase,
            checkXposedDetectorUseCase,
            checkEmulatorUseCase,
            checkVpnStatusUseCase,
            checkMockLocationUseCase
        )
    }

    @Provides
    fun provideCompositeCheckSecurityUseCase(inputs: SecurityCheckInputs): CompositeCheckSecurityUseCase =
        CompositeCheckSecurityUseCase(inputs)
}
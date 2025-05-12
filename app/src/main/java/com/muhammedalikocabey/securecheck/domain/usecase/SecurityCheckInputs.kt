package com.muhammedalikocabey.securecheck.domain.usecase

/**
 * Tüm güvenlik UseCase bağımlılıklarının gruplandığı veri sınıfı
 *
 * Data class grouping all security use case dependencies for injection
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
data class SecurityCheckInputs(
    val checkDebugBuild: CheckDebugBuildUseCase,
    val checkDebuggerStatus: CheckDebuggerStatusUseCase,
    val checkInstaller: CheckInstallerUseCase,
    val checkIntentHijack: CheckIntentHijackUseCase,
    val checkObfuscationStatus: CheckObfuscationStatusUseCase,
    val checkPendingIntentLeak: CheckPendingIntentLeakUseCase,
    val checkPlayIntegrity: CheckPlayIntegrityUseCase,
    val checkRootStatus: CheckRootStatusUseCase,
    val checkSafetyNet: CheckSafetyNetUseCase,
    val checkSignature: CheckSignatureUseCase,
    val checkSslPinning: CheckSslPinningUseCase,
    val checkStorageEncrypted: CheckStorageEncryptedUseCase,
    val checkTamperStatus: CheckTamperStatusUseCase,
    val checkComponentExported: CheckComponentExportedUseCase,
    val checkWebViewSecurity: CheckWebViewSecurityUseCase,
    val checkDexLoader: CheckDexLoaderUseCase,
    val checkReflection: CheckReflectionUseCase,
    val checkFrida: CheckFridaUseCase,
    val checkXposedDetector: CheckXposedDetectorUseCase,
    val checkEmulatorUseCase: CheckEmulatorUseCase,
    val checkVpnStatusUseCase: CheckVpnStatusUseCase,
    val checkMockLocationUseCase: CheckMockLocationUseCase,
)
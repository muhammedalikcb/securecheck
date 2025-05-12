package com.muhammedalikocabey.securecheck.data.datasource

import android.content.Context
import javax.inject.Inject
import com.muhammedalikocabey.securecheck.data.detector.debugger.IDebuggerDetector
import com.muhammedalikocabey.securecheck.data.detector.installer.IInstallerValidator
import com.muhammedalikocabey.securecheck.data.detector.integrity.IPlayIntegrityValidator
import com.muhammedalikocabey.securecheck.data.detector.root.IRootDetector
import com.muhammedalikocabey.securecheck.data.detector.safetynet.ISafetyNetValidator
import com.muhammedalikocabey.securecheck.data.detector.signature.ISignatureValidator
import com.muhammedalikocabey.securecheck.data.detector.tamper.ITamperDetector

/**
 * Uygulamanın güvenlik durumunu çeşitli dedektör sınıflarını kullanarak kontrol eden merkez sınıf.
 * Root, debugger, installer, imza, tamper, SafetyNet ve Play Integrity gibi birçok kontrolü içerir.
 *
 * Centralized class responsible for checking the app’s security posture.
 * Performs validations such as root, debugger, installer, signature, tampering, SafetyNet and Play Integrity.
 *
 * Bu sınıf tüm UseCase'ler tarafından kullanılır ve soyut bir güvenlik API'si olarak görev yapar.
 *
 * Used by all security UseCases as an abstraction layer for performing core validations.
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class SecurityChecker @Inject constructor(
    private val rootDetector: IRootDetector,
    private val signatureValidator: ISignatureValidator,
    private val debuggerDetector: IDebuggerDetector,
    private val installerValidator: IInstallerValidator,
    private val tamperDetector: ITamperDetector,
    private val playIntegrityValidator: IPlayIntegrityValidator,
    private val safetyNetValidator: ISafetyNetValidator
) {
    /** Root erişimi var mı kontrol eder / Checks for root access. */
    fun isRooted(): Boolean = rootDetector.isRooted()

    /** APK imzası doğru mu kontrol eder / Validates APK signature. */
    fun isSignatureValid(context: Context): Boolean = signatureValidator.isSignatureValid(context)

    /** Debugger bağlı mı kontrol eder / Checks if debugger is attached. */
    fun isDebuggerAttached(): Boolean = debuggerDetector.isDebuggerAttached()

    /** Uygulama Play Store'dan mı yüklü kontrol eder / Checks installer source. */
    fun isFromPlayStore(context: Context): Boolean = installerValidator.isFromPlayStore(context)

    /** APK'da değişiklik yapılmış mı kontrol eder / Checks if app is tampered. */
    fun isAppTampered(context: Context): Boolean = tamperDetector.isAppTampered(context)

    /** Play Integrity API sonucunu döner / Executes Play Integrity. */
    suspend fun checkPlayIntegrity(context: Context): Boolean = playIntegrityValidator.checkPlayIntegrity(context)

    /** SafetyNet API sonucunu döner / Executes SafetyNet validation. */
    suspend fun performSafetyNet(context: Context): Pair<Boolean, Boolean> =
        safetyNetValidator.performSafetyNet(context)
}
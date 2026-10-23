package com.muhammedalikocabey.securecheck.di

import com.muhammedalikocabey.securecheck.data.check.app.CodeShrinkingCheck
import com.muhammedalikocabey.securecheck.data.check.app.DebuggableCheck
import com.muhammedalikocabey.securecheck.data.check.app.InstallerCheck
import com.muhammedalikocabey.securecheck.data.check.app.SignatureCheck
import com.muhammedalikocabey.securecheck.data.check.app.TargetSdkCheck
import com.muhammedalikocabey.securecheck.data.check.data.BackupCheck
import com.muhammedalikocabey.securecheck.data.check.data.HardwareKeystoreCheck
import com.muhammedalikocabey.securecheck.data.check.device.BiometricsCheck
import com.muhammedalikocabey.securecheck.data.check.device.DeveloperOptionsCheck
import com.muhammedalikocabey.securecheck.data.check.device.EmulatorCheck
import com.muhammedalikocabey.securecheck.data.check.device.KeyAttestationCheck
import com.muhammedalikocabey.securecheck.data.check.device.PlayIntegrityCheck
import com.muhammedalikocabey.securecheck.data.check.device.RootCheck
import com.muhammedalikocabey.securecheck.data.check.device.ScreenLockCheck
import com.muhammedalikocabey.securecheck.data.check.device.SecurityPatchCheck
import com.muhammedalikocabey.securecheck.data.check.device.UsbDebuggingCheck
import com.muhammedalikocabey.securecheck.data.check.network.CleartextCheck
import com.muhammedalikocabey.securecheck.data.check.network.ProxyCheck
import com.muhammedalikocabey.securecheck.data.check.network.UserCaCheck
import com.muhammedalikocabey.securecheck.data.check.network.VpnCheck
import com.muhammedalikocabey.securecheck.data.check.platform.AccessibilityServicesCheck
import com.muhammedalikocabey.securecheck.data.check.platform.ExportedComponentsCheck
import com.muhammedalikocabey.securecheck.data.check.runtime.DebuggerCheck
import com.muhammedalikocabey.securecheck.data.check.runtime.FridaCheck
import com.muhammedalikocabey.securecheck.data.check.runtime.InjectedCodeCheck
import com.muhammedalikocabey.securecheck.data.check.runtime.MagiskCheck
import com.muhammedalikocabey.securecheck.data.check.runtime.XposedCheck
import com.muhammedalikocabey.securecheck.domain.scan.SecurityCheck
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

/** Registers every check. Adding a check means adding one line here and one entry in CheckId. */
@Module
abstract class ChecksModule {
    @Binds @IntoSet
    abstract fun rootCheck(impl: RootCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun keyAttestationCheck(impl: KeyAttestationCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun screenLockCheck(impl: ScreenLockCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun securityPatchCheck(impl: SecurityPatchCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun developerOptionsCheck(impl: DeveloperOptionsCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun usbDebuggingCheck(impl: UsbDebuggingCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun emulatorCheck(impl: EmulatorCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun biometricsCheck(impl: BiometricsCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun playIntegrityCheck(impl: PlayIntegrityCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun signatureCheck(impl: SignatureCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun installerCheck(impl: InstallerCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun debuggableCheck(impl: DebuggableCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun codeShrinkingCheck(impl: CodeShrinkingCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun targetSdkCheck(impl: TargetSdkCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun debuggerCheck(impl: DebuggerCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun fridaCheck(impl: FridaCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun xposedCheck(impl: XposedCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun magiskCheck(impl: MagiskCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun injectedCodeCheck(impl: InjectedCodeCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun vpnCheck(impl: VpnCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun proxyCheck(impl: ProxyCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun userCaCheck(impl: UserCaCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun cleartextCheck(impl: CleartextCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun hardwareKeystoreCheck(impl: HardwareKeystoreCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun backupCheck(impl: BackupCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun exportedComponentsCheck(impl: ExportedComponentsCheck): SecurityCheck

    @Binds @IntoSet
    abstract fun accessibilityServicesCheck(impl: AccessibilityServicesCheck): SecurityCheck
}

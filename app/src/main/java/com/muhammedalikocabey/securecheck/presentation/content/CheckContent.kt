package com.muhammedalikocabey.securecheck.presentation.content

import androidx.annotation.StringRes
import com.muhammedalikocabey.securecheck.R
import com.muhammedalikocabey.securecheck.domain.model.CheckId

/** User-facing copy for one check: a name, a line for each outcome, why it matters and how to fix it. */
data class CheckText(
    @StringRes val title: Int,
    @StringRes val ok: Int,
    @StringRes val bad: Int,
    @StringRes val why: Int,
    @StringRes val fix: Int,
    @StringRes val info: Int,
)

fun CheckId.text(): CheckText = when (this) {
    CheckId.ROOT -> CheckText(
        R.string.check_root_title,
        R.string.check_root_ok,
        R.string.check_root_bad,
        R.string.check_root_why,
        R.string.check_root_fix,
        R.string.check_root_ok,
    )
    CheckId.KEY_ATTESTATION -> CheckText(
        R.string.check_key_attestation_title,
        R.string.check_key_attestation_ok,
        R.string.check_key_attestation_bad,
        R.string.check_key_attestation_why,
        R.string.check_key_attestation_fix,
        R.string.check_key_attestation_ok,
    )
    CheckId.SCREEN_LOCK -> CheckText(
        R.string.check_screen_lock_title,
        R.string.check_screen_lock_ok,
        R.string.check_screen_lock_bad,
        R.string.check_screen_lock_why,
        R.string.check_screen_lock_fix,
        R.string.check_screen_lock_ok,
    )
    CheckId.SECURITY_PATCH -> CheckText(
        R.string.check_security_patch_title,
        R.string.check_security_patch_ok,
        R.string.check_security_patch_bad,
        R.string.check_security_patch_why,
        R.string.check_security_patch_fix,
        R.string.check_security_patch_ok,
    )
    CheckId.DEVELOPER_OPTIONS -> CheckText(
        R.string.check_developer_options_title,
        R.string.check_developer_options_ok,
        R.string.check_developer_options_bad,
        R.string.check_developer_options_why,
        R.string.check_developer_options_fix,
        R.string.check_developer_options_ok,
    )
    CheckId.USB_DEBUGGING -> CheckText(
        R.string.check_usb_debugging_title,
        R.string.check_usb_debugging_ok,
        R.string.check_usb_debugging_bad,
        R.string.check_usb_debugging_why,
        R.string.check_usb_debugging_fix,
        R.string.check_usb_debugging_ok,
    )
    CheckId.EMULATOR -> CheckText(
        R.string.check_emulator_title,
        R.string.check_emulator_ok,
        R.string.check_emulator_bad,
        R.string.check_emulator_why,
        R.string.check_emulator_fix,
        R.string.check_emulator_ok,
    )
    CheckId.BIOMETRICS -> CheckText(
        R.string.check_biometrics_title,
        R.string.check_biometrics_ok,
        R.string.check_biometrics_bad,
        R.string.check_biometrics_why,
        R.string.check_biometrics_fix,
        R.string.check_biometrics_info,
    )
    CheckId.PLAY_INTEGRITY -> CheckText(
        R.string.check_play_integrity_title,
        R.string.check_play_integrity_ok,
        R.string.check_play_integrity_bad,
        R.string.check_play_integrity_why,
        R.string.check_play_integrity_fix,
        R.string.check_play_integrity_info,
    )
    CheckId.SIGNATURE -> CheckText(
        R.string.check_signature_title,
        R.string.check_signature_ok,
        R.string.check_signature_bad,
        R.string.check_signature_why,
        R.string.check_signature_fix,
        R.string.check_signature_info,
    )
    CheckId.INSTALLER -> CheckText(
        R.string.check_installer_title,
        R.string.check_installer_ok,
        R.string.check_installer_bad,
        R.string.check_installer_why,
        R.string.check_installer_fix,
        R.string.check_installer_ok,
    )
    CheckId.DEBUGGABLE -> CheckText(
        R.string.check_debuggable_title,
        R.string.check_debuggable_ok,
        R.string.check_debuggable_bad,
        R.string.check_debuggable_why,
        R.string.check_debuggable_fix,
        R.string.check_debuggable_ok,
    )
    CheckId.CODE_SHRINKING -> CheckText(
        R.string.check_code_shrinking_title,
        R.string.check_code_shrinking_ok,
        R.string.check_code_shrinking_bad,
        R.string.check_code_shrinking_why,
        R.string.check_code_shrinking_fix,
        R.string.check_code_shrinking_ok,
    )
    CheckId.TARGET_SDK -> CheckText(
        R.string.check_target_sdk_title,
        R.string.check_target_sdk_ok,
        R.string.check_target_sdk_bad,
        R.string.check_target_sdk_why,
        R.string.check_target_sdk_fix,
        R.string.check_target_sdk_ok,
    )
    CheckId.DEBUGGER -> CheckText(
        R.string.check_debugger_title,
        R.string.check_debugger_ok,
        R.string.check_debugger_bad,
        R.string.check_debugger_why,
        R.string.check_debugger_fix,
        R.string.check_debugger_ok,
    )
    CheckId.FRIDA -> CheckText(
        R.string.check_frida_title,
        R.string.check_frida_ok,
        R.string.check_frida_bad,
        R.string.check_frida_why,
        R.string.check_frida_fix,
        R.string.check_frida_ok,
    )
    CheckId.XPOSED -> CheckText(
        R.string.check_xposed_title,
        R.string.check_xposed_ok,
        R.string.check_xposed_bad,
        R.string.check_xposed_why,
        R.string.check_xposed_fix,
        R.string.check_xposed_ok,
    )
    CheckId.MAGISK -> CheckText(
        R.string.check_magisk_title,
        R.string.check_magisk_ok,
        R.string.check_magisk_bad,
        R.string.check_magisk_why,
        R.string.check_magisk_fix,
        R.string.check_magisk_ok,
    )
    CheckId.INJECTED_CODE -> CheckText(
        R.string.check_injected_code_title,
        R.string.check_injected_code_ok,
        R.string.check_injected_code_bad,
        R.string.check_injected_code_why,
        R.string.check_injected_code_fix,
        R.string.check_injected_code_ok,
    )
    CheckId.VPN -> CheckText(
        R.string.check_vpn_title,
        R.string.check_vpn_ok,
        R.string.check_vpn_bad,
        R.string.check_vpn_why,
        R.string.check_vpn_fix,
        R.string.check_vpn_info,
    )
    CheckId.PROXY -> CheckText(
        R.string.check_proxy_title,
        R.string.check_proxy_ok,
        R.string.check_proxy_bad,
        R.string.check_proxy_why,
        R.string.check_proxy_fix,
        R.string.check_proxy_ok,
    )
    CheckId.USER_CA -> CheckText(
        R.string.check_user_ca_title,
        R.string.check_user_ca_ok,
        R.string.check_user_ca_bad,
        R.string.check_user_ca_why,
        R.string.check_user_ca_fix,
        R.string.check_user_ca_ok,
    )
    CheckId.CLEARTEXT -> CheckText(
        R.string.check_cleartext_title,
        R.string.check_cleartext_ok,
        R.string.check_cleartext_bad,
        R.string.check_cleartext_why,
        R.string.check_cleartext_fix,
        R.string.check_cleartext_ok,
    )
    CheckId.HW_KEYSTORE -> CheckText(
        R.string.check_hw_keystore_title,
        R.string.check_hw_keystore_ok,
        R.string.check_hw_keystore_bad,
        R.string.check_hw_keystore_why,
        R.string.check_hw_keystore_fix,
        R.string.check_hw_keystore_ok,
    )
    CheckId.BACKUP -> CheckText(
        R.string.check_backup_title,
        R.string.check_backup_ok,
        R.string.check_backup_bad,
        R.string.check_backup_why,
        R.string.check_backup_fix,
        R.string.check_backup_ok,
    )
    CheckId.EXPORTED_COMPONENTS -> CheckText(
        R.string.check_exported_components_title,
        R.string.check_exported_components_ok,
        R.string.check_exported_components_bad,
        R.string.check_exported_components_why,
        R.string.check_exported_components_fix,
        R.string.check_exported_components_ok,
    )
    CheckId.ACCESSIBILITY_SERVICES -> CheckText(
        R.string.check_accessibility_services_title,
        R.string.check_accessibility_services_ok,
        R.string.check_accessibility_services_bad,
        R.string.check_accessibility_services_why,
        R.string.check_accessibility_services_fix,
        R.string.check_accessibility_services_ok,
    )
}

package com.muhammedalikocabey.securecheck.domain.model

/**
 * Every check the app can run. [key] is stable and written to exported reports, so never rename one.
 * [masvs] is the OWASP MASVS v2 control the check informs. Where the MASVS/MASWE weakness catalogue has no entry for a
 * device setting (developer options, USB debugging, install source, patch level) it is the nearest control.
 */
enum class CheckId(val key: String, val category: Category, val severity: Severity, val masvs: String?) {
    ROOT("root", Category.DEVICE, Severity.HIGH, "MASVS-RESILIENCE-1"),
    KEY_ATTESTATION("key_attestation", Category.DEVICE, Severity.HIGH, "MASVS-RESILIENCE-1"),
    SCREEN_LOCK("screen_lock", Category.DEVICE, Severity.HIGH, "MASVS-CRYPTO-2"),
    SECURITY_PATCH("security_patch", Category.DEVICE, Severity.MEDIUM, "MASVS-CODE-1"),
    DEVELOPER_OPTIONS("developer_options", Category.DEVICE, Severity.LOW, "MASVS-RESILIENCE-1"),
    USB_DEBUGGING("usb_debugging", Category.DEVICE, Severity.MEDIUM, "MASVS-RESILIENCE-1"),
    EMULATOR("emulator", Category.DEVICE, Severity.MEDIUM, "MASVS-RESILIENCE-1"),
    BIOMETRICS("biometrics", Category.DEVICE, Severity.INFO, "MASVS-AUTH-2"),
    PLAY_INTEGRITY("play_integrity", Category.DEVICE, Severity.INFO, "MASVS-RESILIENCE-1"),

    SIGNATURE("signature", Category.APP, Severity.HIGH, "MASVS-RESILIENCE-2"),
    INSTALLER("installer", Category.APP, Severity.MEDIUM, "MASVS-RESILIENCE-2"),
    DEBUGGABLE("debuggable", Category.APP, Severity.HIGH, "MASVS-RESILIENCE-4"),
    CODE_SHRINKING("code_shrinking", Category.APP, Severity.LOW, "MASVS-RESILIENCE-3"),
    TARGET_SDK("target_sdk", Category.APP, Severity.LOW, "MASVS-CODE-1"),

    DEBUGGER("debugger", Category.RUNTIME, Severity.HIGH, "MASVS-RESILIENCE-4"),
    FRIDA("frida", Category.RUNTIME, Severity.HIGH, "MASVS-RESILIENCE-4"),
    XPOSED("xposed", Category.RUNTIME, Severity.HIGH, "MASVS-RESILIENCE-4"),
    MAGISK("magisk", Category.RUNTIME, Severity.HIGH, "MASVS-RESILIENCE-1"),
    INJECTED_CODE("injected_code", Category.RUNTIME, Severity.HIGH, "MASVS-RESILIENCE-2"),

    VPN("vpn", Category.NETWORK, Severity.INFO, "MASVS-NETWORK-1"),
    PROXY("proxy", Category.NETWORK, Severity.MEDIUM, "MASVS-NETWORK-1"),
    USER_CA("user_ca", Category.NETWORK, Severity.MEDIUM, "MASVS-NETWORK-1"),
    CLEARTEXT("cleartext", Category.NETWORK, Severity.MEDIUM, "MASVS-NETWORK-1"),

    HW_KEYSTORE("hw_keystore", Category.DATA, Severity.MEDIUM, "MASVS-CRYPTO-2"),
    BACKUP("backup", Category.DATA, Severity.MEDIUM, "MASVS-STORAGE-2"),

    EXPORTED_COMPONENTS("exported_components", Category.PLATFORM, Severity.MEDIUM, "MASVS-PLATFORM-1"),
    ACCESSIBILITY_SERVICES("accessibility_services", Category.PLATFORM, Severity.MEDIUM, "MASVS-PLATFORM-3"),
    ;

    companion object {
        fun fromKey(key: String): CheckId? = entries.firstOrNull { it.key == key }
    }
}

package com.muhammedalikocabey.securecheck.domain.model

enum class CheckStatus {
    PASSED,

    /** Something risky was found. Counts against the score. */
    FINDING,

    /** Context the user should know about; never counts against the score. */
    INFO,

    /** The check could not give an answer. This is never shown or scored as a pass. */
    UNAVAILABLE,

    /** A finding that is normal for this build type (for example a debuggable debug build). Not scored. */
    EXPECTED,
}

enum class UnavailableReason {
    NOT_CONFIGURED,
    UNSUPPORTED,
    PERMISSION,
    TIMEOUT,
    ERROR,
}

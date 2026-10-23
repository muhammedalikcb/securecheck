package com.muhammedalikocabey.securecheck.domain.scan

import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult

/**
 * One independently runnable check. Implementations are contributed to a Dagger set, so adding a check
 * means adding a class and one binding; the engine, score and UI pick it up from [id].
 */
interface SecurityCheck {
    val id: CheckId

    /** May block (file or system calls); the engine runs it on an IO dispatcher with a timeout. */
    suspend fun run(): CheckResult
}

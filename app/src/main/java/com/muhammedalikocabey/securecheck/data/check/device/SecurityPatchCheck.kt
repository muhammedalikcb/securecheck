package com.muhammedalikocabey.securecheck.data.check.device

import com.muhammedalikocabey.securecheck.data.system.BuildFields
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.Severity
import com.muhammedalikocabey.securecheck.domain.model.UnavailableReason
import com.muhammedalikocabey.securecheck.domain.scan.SecurityCheck
import com.muhammedalikocabey.securecheck.domain.scan.TimeSource
import javax.inject.Inject

class SecurityPatchCheck @Inject constructor(private val build: BuildFields, private val time: TimeSource) :
    SecurityCheck {
    override val id = CheckId.SECURITY_PATCH

    override suspend fun run(): CheckResult {
        val patchDay = epochDayOf(build.securityPatch)
            ?: return CheckResult.unavailable(
                id,
                UnavailableReason.ERROR,
                listOf("patch level: ${build.securityPatch}"),
            )
        val age = time.nowMs() / MS_PER_DAY - patchDay
        val evidence = listOf("patch level: ${build.securityPatch}", "age: $age days")
        return when {
            age <= RECENT_DAYS -> CheckResult.passed(id, evidence)
            age <= STALE_DAYS -> CheckResult.finding(id, evidence, severity = Severity.MEDIUM)
            else -> CheckResult.finding(id, evidence, severity = Severity.HIGH)
        }
    }

    companion object {
        const val RECENT_DAYS = 180
        const val STALE_DAYS = 365
        private const val MS_PER_DAY = 86_400_000L

        /** Days since 1970-01-01 for an ISO `yyyy-MM-dd` string, or null. Avoids java.time, which needs API 26. */
        fun epochDayOf(iso: String): Long? {
            val parts = iso.split('-').map { it.toIntOrNull() ?: return null }
            if (parts.size != 3) return null
            val (year, month, day) = parts
            if (month !in 1..12 || day !in 1..31) return null
            return daysFromCivil(year, month, day)
        }

        /** Howard Hinnant's days-from-civil algorithm. */
        private fun daysFromCivil(year: Int, month: Int, day: Int): Long {
            val y = if (month <= 2) year - 1 else year
            val era = (if (y >= 0) y else y - 399) / 400
            val yearOfEra = y - era * 400
            val shifted = if (month > 2) month - 3 else month + 9
            val dayOfYear = (153 * shifted + 2) / 5 + day - 1
            val dayOfEra = yearOfEra * 365 + yearOfEra / 4 - yearOfEra / 100 + dayOfYear
            return era.toLong() * 146_097 + dayOfEra - 719_468
        }
    }
}

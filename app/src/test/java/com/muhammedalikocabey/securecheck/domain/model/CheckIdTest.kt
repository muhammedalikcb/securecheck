package com.muhammedalikocabey.securecheck.domain.model

import org.junit.Assert.assertTrue
import org.junit.Test

class CheckIdTest {
    // The 24 controls of OWASP MASVS v2 (https://mas.owasp.org/MASVS/controls/). Anything else would link to a 404.
    private val officialControls =
        setOf(
            "MASVS-STORAGE-1", "MASVS-STORAGE-2",
            "MASVS-CRYPTO-1", "MASVS-CRYPTO-2",
            "MASVS-AUTH-1", "MASVS-AUTH-2", "MASVS-AUTH-3",
            "MASVS-NETWORK-1", "MASVS-NETWORK-2",
            "MASVS-PLATFORM-1", "MASVS-PLATFORM-2", "MASVS-PLATFORM-3",
            "MASVS-CODE-1", "MASVS-CODE-2", "MASVS-CODE-3", "MASVS-CODE-4",
            "MASVS-RESILIENCE-1", "MASVS-RESILIENCE-2", "MASVS-RESILIENCE-3", "MASVS-RESILIENCE-4",
            "MASVS-PRIVACY-1", "MASVS-PRIVACY-2", "MASVS-PRIVACY-3", "MASVS-PRIVACY-4",
        )

    @Test
    fun `every check maps to a real MASVS v2 control`() {
        val unknown = CheckId.entries.filter { it.masvs != null && it.masvs !in officialControls }
        assertTrue("Unknown MASVS controls: $unknown", unknown.isEmpty())
    }

    @Test
    fun `check keys are unique`() {
        assertTrue(CheckId.entries.map { it.key }.toSet().size == CheckId.entries.size)
    }
}

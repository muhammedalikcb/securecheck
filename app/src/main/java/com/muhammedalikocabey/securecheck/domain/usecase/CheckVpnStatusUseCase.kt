package com.muhammedalikocabey.securecheck.domain.usecase

import android.content.Context
import com.muhammedalikocabey.securecheck.core.network.VpnDetector
import javax.inject.Inject

/**
 * Cihazın aktif bir VPN bağlantısı üzerinden çalışıp çalışmadığını kontrol eder
 *
 * Detects if the device is currently using a VPN connection
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class CheckVpnStatusUseCase @Inject constructor(
    private val vpnDetector: VpnDetector
) {
    operator fun invoke(context: Context): Boolean = vpnDetector.isVpnActive(context)
}

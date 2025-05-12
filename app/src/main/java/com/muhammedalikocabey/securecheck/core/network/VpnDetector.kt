package com.muhammedalikocabey.securecheck.core.network

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import javax.inject.Inject

/**
 * Cihazın VPN bağlantısı üzerinden internete çıkıp çıkmadığını tespit eder.
 *
 * Detects whether the device is currently using a VPN connection.
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class VpnDetector @Inject constructor() {

    /**
     * Aktif ağ bağlantısında VPN olup olmadığını kontrol eder.
     *
     * Checks if the current network connection is routed through a VPN.
     *
     * @param context Uygulama bağlamı
     * @return Eğer VPN aktifse true, değilse false
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isVpnActive(context: Context): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val caps = connectivity?.getNetworkCapabilities(connectivity.activeNetwork)
        return caps?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true
    }
}

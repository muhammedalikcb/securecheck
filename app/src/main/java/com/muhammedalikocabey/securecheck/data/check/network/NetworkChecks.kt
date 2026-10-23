package com.muhammedalikocabey.securecheck.data.check.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.security.NetworkSecurityPolicy
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.Confidence
import com.muhammedalikocabey.securecheck.domain.model.UnavailableReason
import com.muhammedalikocabey.securecheck.domain.scan.SecurityCheck
import java.security.KeyStore
import javax.inject.Inject

class VpnCheck @Inject constructor(private val context: Context) : SecurityCheck {
    override val id = CheckId.VPN

    override suspend fun run(): CheckResult {
        val manager = context.getSystemService(ConnectivityManager::class.java)
            ?: return CheckResult.unavailable(id, UnavailableReason.UNSUPPORTED)
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
            ?: return CheckResult.unavailable(id, UnavailableReason.ERROR, listOf("no active network"))
        return if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
            CheckResult.info(id, listOf("active network uses a VPN transport"))
        } else {
            CheckResult.passed(id)
        }
    }
}

/** Reports a system HTTP proxy. Only the port goes into the evidence; the host can identify a person or a company. */
class ProxyCheck @Inject constructor(private val context: Context) : SecurityCheck {
    override val id = CheckId.PROXY

    override suspend fun run(): CheckResult {
        val manager = context.getSystemService(ConnectivityManager::class.java)
        val proxy = manager?.defaultProxy
        val propertyPort = System.getProperty("http.proxyPort")?.takeIf { System.getProperty("http.proxyHost") != null }
        val port = proxy?.port?.takeIf { proxy.host.isNotEmpty() } ?: propertyPort?.toIntOrNull()
        return if (port != null) {
            CheckResult.finding(id, listOf("system proxy configured, port $port"), Confidence.MEDIUM)
        } else {
            CheckResult.passed(id)
        }
    }
}

class UserCaCheck @Inject constructor() : SecurityCheck {
    override val id = CheckId.USER_CA

    override suspend fun run(): CheckResult {
        val aliases = try {
            KeyStore.getInstance("AndroidCAStore").apply { load(null) }.aliases().toList()
        } catch (_: java.security.GeneralSecurityException) {
            return CheckResult.unavailable(id, UnavailableReason.ERROR)
        }
        val user = aliases.count { it.startsWith(USER_PREFIX) }
        // Managed devices install company CAs on purpose, so this is a signal and not a verdict.
        return if (user > 0) {
            CheckResult.finding(id, listOf("user-installed CA certificates: $user"), Confidence.MEDIUM)
        } else {
            CheckResult.passed(id)
        }
    }

    private companion object {
        const val USER_PREFIX = "user:"
    }
}

class CleartextCheck @Inject constructor() : SecurityCheck {
    override val id = CheckId.CLEARTEXT

    override suspend fun run(): CheckResult = if (NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted) {
        CheckResult.finding(id, listOf("cleartext traffic permitted by network security policy"))
    } else {
        CheckResult.passed(id)
    }
}

package com.muhammedalikocabey.securecheck.presentation.screen.security

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.muhammedalikocabey.securecheck.R
import com.muhammedalikocabey.securecheck.domain.extension.calculatePassedCheckCount
import com.muhammedalikocabey.securecheck.domain.extension.riskLevel
import com.muhammedalikocabey.securecheck.domain.extension.totalCheckCount
import com.muhammedalikocabey.securecheck.domain.model.SecurityState
import com.muhammedalikocabey.securecheck.core.mock.FakeStateFactory
import com.muhammedalikocabey.securecheck.presentation.theme.LocalRiskColors
import com.muhammedalikocabey.securecheck.presentation.theme.SecureCheckTheme

/**
 * Tekli güvenlik kontrol öğesini temsil eden data class
 *
 * Model representing a single security check item in the list
 */
data class SecurityCheckItemModel(
    val label: String,
    val isSafe: Boolean,
    val suggestion: String
)

/**
 * Güvenlik durumunu gösteren ana ekran component
 * Risk seviyesi, geçilen kontrol sayısı ve her kontrolün durumu listelenir
 *
 * Main UI screen that visualizes the current SecurityState
 * Displays risk level, passed checks, and itemized security check details
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
@Composable
fun SecurityStatusScreen(
    modifier: Modifier = Modifier,
    securityState: SecurityState,
    onExportClick: () -> Unit
) {
    val passedCount = securityState.calculatePassedCheckCount()
    val totalChecks = securityState.totalCheckCount()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cihaz Güvenlik Durumu", style = MaterialTheme.typography.headlineSmall)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (securityState.overallSafe)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.errorContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (securityState.overallSafe) "Cihazınız güvende ✅"
                    else "Cihazınızda riskler mevcut ⚠️",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (securityState.overallSafe)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onErrorContainer
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Aşağıdaki güvenlik kontrollerinin detaylarını inceleyin",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Geçilen güvenlik kontrolleri: $passedCount / $totalChecks",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 12.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Risk Düzeyi: ${securityState.riskLevel}",
            style = MaterialTheme.typography.bodyMedium,
            color = when (securityState.riskLevel) {
                "Güvenli" -> LocalRiskColors.current.low
                "Orta Risk" -> LocalRiskColors.current.medium
                "Yüksek Risk" -> LocalRiskColors.current.high
                else -> Color.Gray
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        RenderAllSecurityChecks(securityState)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onExportClick, modifier = Modifier.align(Alignment.End)) {
            Text(stringResource(id = R.string.export_report))
        }
    }
}

/**
 * Tek bir güvenlik kontrol itemini görsel olarak çizer
 *
 * Displays a single item in the security check list with status icon and suggestion
 */
@Composable
fun SecurityCheckItem(item: SecurityCheckItemModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (item.isSafe) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = if (item.isSafe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(text = item.label, style = MaterialTheme.typography.bodyLarge)
            if (!item.isSafe) {
                Text(text = item.suggestion, style = MaterialTheme.typography.bodySmall, color = Color.Red)
            }
        }
    }
}

/**
 * Tüm SecurityState verilerini alarak tek tek UI öğesi olarak çizer
 *
 * Renders all checks from SecurityState as a scrollable list
 */
@Composable
fun RenderAllSecurityChecks(state: SecurityState) {
    val items = listOf(
        SecurityCheckItemModel(
            label = stringResource(R.string.danger_root),
            isSafe = !state.isRooted,
            suggestion = stringResource(R.string.suggestion_root)
        ),
        SecurityCheckItemModel(
            label = stringResource(R.string.danger_debugger),
            isSafe = !state.isDebuggerAttached,
            suggestion = stringResource(R.string.suggestion_debugger)
        ),
        SecurityCheckItemModel(
            label = stringResource(R.string.danger_installer),
            isSafe = state.isFromPlayStore,
            suggestion = stringResource(R.string.suggestion_installer)
        ),
        SecurityCheckItemModel(
            label = stringResource(R.string.danger_signature),
            isSafe = state.isSignatureValid,
            suggestion = stringResource(R.string.suggestion_signature)
        ),
        SecurityCheckItemModel(
            label = stringResource(R.string.danger_safetynet),
            isSafe = state.ctsProfileMatch && state.basicIntegrity,
            suggestion = stringResource(R.string.suggestion_safetynet)
        ),
        SecurityCheckItemModel(
            label = stringResource(R.string.danger_integrity),
            isSafe = state.isGenuine,
            suggestion = stringResource(R.string.suggestion_integrity)
        ),
        SecurityCheckItemModel(
            label = stringResource(R.string.danger_tamper),
            isSafe = !state.isTampered,
            suggestion = stringResource(R.string.suggestion_tamper)
        ),
        SecurityCheckItemModel(
            label = stringResource(R.string.danger_obfuscation),
            isSafe = state.isObfuscated,
            suggestion = stringResource(R.string.suggestion_obfuscation)
        ),
        SecurityCheckItemModel(
            label = stringResource(R.string.danger_storage),
            isSafe = state.isStorageEncrypted,
            suggestion = stringResource(R.string.suggestion_storage)
        ),
        SecurityCheckItemModel(
            label = stringResource(R.string.danger_exported),
            isSafe = !state.hasExportedComponents,
            suggestion = stringResource(R.string.suggestion_exported)
        ),
        SecurityCheckItemModel(
            label = stringResource(R.string.danger_pending_intent),
            isSafe = state.isSafePendingIntent,
            suggestion = stringResource(R.string.suggestion_pending_intent)
        ),
        SecurityCheckItemModel(
            label = stringResource(R.string.danger_ssl_pinning),
            isSafe = state.isSslPinningPassed,
            suggestion = stringResource(R.string.suggestion_ssl_pinning)
        ),
        SecurityCheckItemModel(
            label = stringResource(R.string.danger_intent_hijack),
            isSafe = !state.isIntentHijackable,
            suggestion = stringResource(R.string.suggestion_intent_hijack)
        ),
        SecurityCheckItemModel(
            label = stringResource(R.string.danger_debuggable),
            isSafe = !state.isDebuggable,
            suggestion = stringResource(R.string.suggestion_debuggable)
        ),
        SecurityCheckItemModel(
            label = stringResource(R.string.danger_dex),
            isSafe = !state.isDexLoaded,
            suggestion = stringResource(R.string.suggestion_dex)
        ),
        SecurityCheckItemModel(
            label = stringResource(R.string.danger_reflection),
            isSafe = !state.isReflectionUsed,
            suggestion = stringResource(R.string.suggestion_reflection)
        ),
        SecurityCheckItemModel(
            label = stringResource(R.string.danger_frida),
            isSafe = !state.isFridaDetected,
            suggestion = stringResource(R.string.suggestion_frida)
        ),
        SecurityCheckItemModel(
            label = stringResource(R.string.danger_xposed_check),
            isSafe = !state.isXposed,
            suggestion = stringResource(R.string.suggestion_xposed_check)
        )
    )

    val webViewItems = state.webViewSecurity?.let { web ->
        listOf(
            SecurityCheckItemModel(
                label = stringResource(R.string.danger_webview_js),
                isSafe = !web.isJsEnabled,
                suggestion = stringResource(R.string.suggestion_webview_js)
            ),
            SecurityCheckItemModel(
                label = stringResource(R.string.danger_webview_file),
                isSafe = !web.isFileAccessAllowed,
                suggestion = stringResource(R.string.suggestion_webview_file)
            ),
            SecurityCheckItemModel(
                label = stringResource(R.string.danger_webview_interface),
                isSafe = !web.hasInsecureJsInterface,
                suggestion = stringResource(R.string.suggestion_webview_interface)
            )
        )
    } ?: emptyList()

    (items + webViewItems).forEach { SecurityCheckItem(it) }
}



@Preview(showBackground = true, name = "Güvenli kontrol örneği")
@Composable
fun PreviewSecurityCheckItemSafe() {
    SecureCheckTheme {
        SecurityCheckItem(
            SecurityCheckItemModel(
                label = stringResource(R.string.danger_root),
                isSafe = true,
                suggestion = stringResource(R.string.suggestion_root)
            )
        )
    }
}

@Preview
@Composable
fun PreviewSecurityCheckItemRisk() {
    SecurityCheckItem(
        SecurityCheckItemModel(
            label = "Root erişimi",
            isSafe = false,
            suggestion = "Root kaldırılmalı."
        )
    )
}

@Preview(showBackground = true, name = "Güvenli Durum Ekranı")
@Composable
fun PreviewSecurityStatusScreenSafe() {
    val fakeState = FakeStateFactory.createSafeState()

    SecureCheckTheme {
        SecurityStatusScreen(
            securityState = fakeState,
            onExportClick = {}
        )
    }
}
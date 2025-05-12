package com.muhammedalikocabey.securecheck.presentation.screen.security

import android.Manifest
import android.content.Context
import android.webkit.WebView
import androidx.annotation.RequiresPermission
import androidx.lifecycle.viewModelScope
import com.muhammedalikocabey.securecheck.data.log.SecurityLogExporter
import com.muhammedalikocabey.securecheck.domain.log.ILogExporter
import com.muhammedalikocabey.securecheck.domain.model.SecurityState
import com.muhammedalikocabey.securecheck.domain.usecase.CompositeCheckSecurityUseCase
import com.muhammedalikocabey.securecheck.presentation.common.base.BaseViewModel
import com.muhammedalikocabey.securecheck.presentation.common.state.UiState
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Tüm güvenlik kontrollerini çalıştırıp `SecurityState` üretiminden ve iletiminden sorumlu ViewModel
 * `UiState` kullanarak ekranın Loading / Success / Error durumlarını yönetir
 *
 * ViewModel responsible for executing all security checks and producing a SecurityState
 * Exposes state as UiState<T> for easy UI state transitions in Compose
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class SecurityViewModel @Inject constructor(
    private val useCase: CompositeCheckSecurityUseCase,
    private val exporter: ILogExporter
) : BaseViewModel<SecurityState>() {

    /**
     * Tüm güvenlik kontrollerini çalıştırır ve sonucu UI'ya aktarır
     *
     * Launches all security checks and emits the result to UI layer
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun check(context: Context, webView: WebView? = null) {
        viewModelScope.launch {
            try {
                val result = useCase(context, webView = webView)
                stateMutable.value = UiState.Success(result)
            } catch (e: Exception) {
                stateMutable.value = UiState.Error(e.message ?: "Beklenmeyen bir hata oluştu")
            }
        }
    }

    /**
     * Mevcut durumu dosyaya export eder (JSON)
     *
     * Exports current SecurityState to file using ILogExporter
     */
    fun export(context: Context) {
        val current = state.value
        if (current is UiState.Success) {
            exporter.exportToFile(context, current.data)
        }
    }
}

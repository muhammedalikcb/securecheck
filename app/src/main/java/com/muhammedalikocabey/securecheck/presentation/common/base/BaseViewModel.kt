package com.muhammedalikocabey.securecheck.presentation.common.base

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.muhammedalikocabey.securecheck.presentation.common.state.UiState

/**
 * Ekran ViewModel'leri için UI durum yönetimini sağlayan temel soyut sınıf
 * StateFlow veya LiveData yerine sade bir `UiState<T>` ve Compose uyumlu state yönetimi sunar
 *
 * Abstract base class for screen-level ViewModel state management
 * Uses a simple mutableStateOf structure to emit UiState<T> for Compose
 *
 * ViewModel sınıfları bu sınıftan türetilerek `state` üzerinden UI güncellemeleri yapabilir
 *
 * ViewModel classes can extend this to emit Loading, Success or Error states
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
abstract class BaseViewModel<T> : ViewModel() {
    /**
     * İç state değişkeni (yalnızca ViewModel tarafından değiştirilebilir)
     */
    protected val stateMutable = mutableStateOf<UiState<T>>(UiState.Loading)

    /**
     * Dışarıya yalnızca gözlemlenebilir state verilmesi sağlanır
     */
    val state: State<UiState<T>> = stateMutable
}
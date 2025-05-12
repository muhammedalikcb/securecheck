package com.muhammedalikocabey.securecheck.presentation.common.state

/**
 * UI katmanında kullanılan veri yüklenme durumlarını temsil eden sealed class yapısı
 * ViewModel ve ekranlar arasında `Loading`, `Success`, `Error` gibi durum geçişlerini standartlaştırır
 *
 * Sealed class representing UI loading states for data flow between ViewModel and screen
 * Supports `Loading`, `Success<T>`, and `Error` states in a type-safe way
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
sealed class UiState<out T> {
    /**
     * Veri yükleniyor veya network bekleniyor durumu
     */
    data object Loading : UiState<Nothing>()

    /**
     * Veri başarıyla alındı
     *
     * @param data Başarılı veri içeriği
     */
    data class Success<T>(val data: T) : UiState<T>()

    /**
     * Bir hata meydana geldi (açıklama mesajı ile)
     *
     * @param message Gösterilecek hata mesajı
     */
    data class Error(val message: String) : UiState<Nothing>()
}
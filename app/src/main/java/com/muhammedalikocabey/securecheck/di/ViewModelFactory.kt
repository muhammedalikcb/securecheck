package com.muhammedalikocabey.securecheck.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.muhammedalikocabey.securecheck.domain.usecase.CompositeCheckSecurityUseCase
import com.muhammedalikocabey.securecheck.presentation.screen.security.SecurityViewModel
import javax.inject.Inject
import javax.inject.Provider

/**
 * Dagger multibinding ile ViewModel nesnelerini sağlayan fabrika sınıfı
 *
 * ViewModel factory that supports Dagger multibinding for constructor injection
 *
 * @since 11.05.2025
 */
class ViewModelFactory @Inject constructor(
    private val creators: Map<Class<out ViewModel>,
    @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creators[modelClass] ?: creators.entries.firstOrNull {
            modelClass.isAssignableFrom(it.key)
        }?.value ?: throw IllegalArgumentException("Unknown ViewModel class: $modelClass")

        @Suppress("UNCHECKED_CAST")
        return creator.get() as T
    }
}
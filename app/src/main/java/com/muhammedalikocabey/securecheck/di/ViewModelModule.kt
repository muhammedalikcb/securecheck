package com.muhammedalikocabey.securecheck.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.muhammedalikocabey.securecheck.presentation.screen.security.SecurityViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * ViewModel sınıflarını Dagger multibinding ile sağlayan modül
 *
 * Provides ViewModel classes via Dagger multibinding
 *
 * @since 11.05.2025
 */
@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SecurityViewModel::class)
    abstract fun bindSecurityViewModel(viewModel: SecurityViewModel): ViewModel
}
package com.muhammedalikocabey.securecheck.di

import android.app.Application
import com.muhammedalikocabey.securecheck.presentation.screen.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * Dagger app component
 * AppModule, CheckerModule, CompositeModule ve ViewModelModule modüllerini içerir
 *
 * Dagger app component
 * Includes AppModule, CheckerModule, CompositeModule, and ViewModelModule
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
@Singleton
@Component(modules = [AppModule::class, CheckerModule::class, CompositeModule::class, ViewModelModule::class])
interface AppComponent {

    /** MainActivity sınıfına bağımlılıkları inject eder */
    fun inject(activity: MainActivity)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}
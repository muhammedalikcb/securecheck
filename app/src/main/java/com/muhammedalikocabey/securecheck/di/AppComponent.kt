package com.muhammedalikocabey.securecheck.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.muhammedalikocabey.securecheck.data.export.ReportExporter
import com.muhammedalikocabey.securecheck.domain.history.BackgroundScan
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ChecksModule::class, ViewModelModule::class])
interface AppComponent {
    fun viewModelFactory(): ViewModelProvider.Factory

    fun backgroundScan(): BackgroundScan

    fun reportExporter(): ReportExporter

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}

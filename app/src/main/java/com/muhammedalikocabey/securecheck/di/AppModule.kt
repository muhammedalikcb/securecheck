package com.muhammedalikocabey.securecheck.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.muhammedalikocabey.securecheck.data.log.SecurityLogExporter
import com.muhammedalikocabey.securecheck.domain.log.ILogExporter
import com.muhammedalikocabey.securecheck.domain.model.AppIntegritySnapshot
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Uygulama düzeyinde bağımlılıkları sağlayan Dagger module
 *
 * Provides application-level dependencies such as context, preferences, logging and snapshot exporters
 *
 * @since 11.05.2025
 */
@Module
class AppModule {

    @Provides
    fun provideContext(app: Application): Context = app

    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences("user_settings", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideAppIntegritySnapshot(): AppIntegritySnapshot = AppIntegritySnapshot()

    @Provides
    @Singleton
    fun provideILogExporter(snapshot: AppIntegritySnapshot): ILogExporter =
        SecurityLogExporter(snapshot)
}
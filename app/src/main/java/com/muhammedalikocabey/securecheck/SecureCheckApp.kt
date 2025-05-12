package com.muhammedalikocabey.securecheck

import android.app.Application
import com.muhammedalikocabey.securecheck.di.AppComponent
import com.muhammedalikocabey.securecheck.di.DaggerAppComponent

/**
 * SecureCheck uygulamasının Application sınıfı
 * Dagger AppComponent yapısını başlatır ve uygulama genelinde erişilebilir hale getirir
 *
 * Application class for SecureCheck
 * Initializes and holds the Dagger AppComponent instance globally
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class SecureCheckApp : Application() {

    /**
     * Tüm modülleri içeren ve injection işlemlerini yöneten Dagger bileşeni
     *
     * Dagger application-wide component managing all dependency graphs
     */
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }
}

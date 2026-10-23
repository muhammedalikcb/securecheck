package com.muhammedalikocabey.securecheck

import android.app.Application
import com.muhammedalikocabey.securecheck.di.AppComponent
import com.muhammedalikocabey.securecheck.di.DaggerAppComponent

class SecureCheckApp : Application() {
    val component: AppComponent by lazy { DaggerAppComponent.factory().create(this) }
}

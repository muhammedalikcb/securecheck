package com.muhammedalikocabey.securecheck.presentation.screen

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import com.muhammedalikocabey.securecheck.SecureCheckApp
import com.muhammedalikocabey.securecheck.di.ViewModelFactory
import com.muhammedalikocabey.securecheck.presentation.common.state.UiState
import com.muhammedalikocabey.securecheck.presentation.screen.security.SecurityMotionLayout
import com.muhammedalikocabey.securecheck.presentation.screen.security.SecurityViewModel
import com.muhammedalikocabey.securecheck.presentation.theme.SecureCheckTheme
import javax.inject.Inject

/**
 * Uygulamanın giriş aktivitesi
 * DI üzerinden SecurityViewModel'i alır ve uygulama temasında güvenlik ekranını gösterir
 *
 * Entry activity of the SecureCheck application
 * Injects SecurityViewModel via Hilt/Dagger and sets the themed content with security analysis screen
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: SecurityViewModel

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Uygulamanın AppComponent bileşeninden bağımlılıkları alır
         * Hilt yerine manuel Dagger setup kullanıldığı için gereklidir
         */
        (application as? SecureCheckApp)
            ?.appComponent?.inject(this)
            ?: throw IllegalStateException("Application is not SecureCheckApp")

        viewModel = ViewModelProvider(this, viewModelFactory)[SecurityViewModel::class.java]

        setContent {
            val context = LocalContext.current
            val state by viewModel.state

            SecureCheckTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxSize()) {

                        when (val current = state) {
                            is UiState.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }

                            is UiState.Success -> {
                                SecurityMotionLayout(current.data, onExportClick = {viewModel.export(context = context)})
                            }

                            is UiState.Error -> {
                                Text(
                                    text = current.message,
                                    color = Color.Red,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }

            /**
             * Uygulama başladığında güvenlik kontrollerini başlat
             */
            LaunchedEffect(Unit) {
                viewModel.check(context)
            }
        }
    }
}
package com.muhammedalikocabey.securecheck.presentation.scan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muhammedalikocabey.securecheck.SecureCheckApp
import com.muhammedalikocabey.securecheck.presentation.SecureCheckApp as SecureCheckScreen

class MainActivity : ComponentActivity() {
    private val viewModel: ScanViewModel by viewModels { (application as SecureCheckApp).component.viewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            SecureCheckScreen(state, onScan = viewModel::scan, onRerun = viewModel::rerun)
        }
    }
}

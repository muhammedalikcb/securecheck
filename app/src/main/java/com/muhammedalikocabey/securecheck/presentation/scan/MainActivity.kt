package com.muhammedalikocabey.securecheck.presentation.scan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.muhammedalikocabey.securecheck.SecureCheckApp

class MainActivity : ComponentActivity() {
    private val viewModel: ScanViewModel by viewModels { (application as SecureCheckApp).component.viewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state by viewModel.state.collectAsState()
            Text(state.toString())
        }
    }
}

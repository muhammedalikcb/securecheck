package com.muhammedalikocabey.securecheck.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammedalikocabey.securecheck.domain.history.ReportStore
import com.muhammedalikocabey.securecheck.domain.model.ScanReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class HistoryViewModel @Inject constructor(private val store: ReportStore) : ViewModel() {
    private val _reports = MutableStateFlow<List<ScanReport>>(emptyList())

    /** Newest first. Call [refresh] when the page opens, since scans and clears happen elsewhere. */
    val reports: StateFlow<List<ScanReport>> = _reports.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _reports.value = try {
                store.all()
            } catch (_: IOException) {
                emptyList()
            }
        }
    }
}

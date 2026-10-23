package com.muhammedalikocabey.securecheck.data.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.muhammedalikocabey.securecheck.SecureCheckApp
import com.muhammedalikocabey.securecheck.domain.settings.ScanScheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScanWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        (applicationContext as SecureCheckApp).component.backgroundScan().run()
        return Result.success()
    }
}

class WorkManagerScanScheduler @Inject constructor(private val context: Context) : ScanScheduler {
    override fun setDaily(enabled: Boolean) {
        val manager = WorkManager.getInstance(context)
        if (!enabled) {
            manager.cancelUniqueWork(WORK_NAME)
            return
        }
        val request = PeriodicWorkRequestBuilder<ScanWorker>(1, TimeUnit.DAYS)
            .setConstraints(Constraints.Builder().setRequiresBatteryNotLow(true).build())
            .build()
        manager.enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.UPDATE, request)
    }

    private companion object {
        const val WORK_NAME = "daily-scan"
    }
}

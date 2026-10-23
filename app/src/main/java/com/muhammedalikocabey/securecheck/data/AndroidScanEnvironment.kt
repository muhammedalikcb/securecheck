package com.muhammedalikocabey.securecheck.data

import android.os.Build
import com.muhammedalikocabey.securecheck.data.system.BuildFields
import com.muhammedalikocabey.securecheck.data.system.BuildInfo
import com.muhammedalikocabey.securecheck.domain.model.AppSummary
import com.muhammedalikocabey.securecheck.domain.model.DeviceSummary
import com.muhammedalikocabey.securecheck.domain.scan.ScanEnvironment
import javax.inject.Inject

class AndroidScanEnvironment @Inject constructor(private val info: BuildInfo, private val fields: BuildFields) :
    ScanEnvironment {
    override val isDebugBuild: Boolean get() = info.isDebug

    override fun device() = DeviceSummary(
        manufacturer = fields.manufacturer,
        model = fields.model,
        androidVersion = Build.VERSION.RELEASE,
        sdkInt = Build.VERSION.SDK_INT,
        securityPatch = fields.securityPatch,
    )

    override fun app() = AppSummary(info.versionName, info.versionCode, info.isDebug)
}

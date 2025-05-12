package com.muhammedalikocabey.securecheck.core.icc

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import javax.inject.Inject

/**
 * AndroidManifest.xml dosyasındaki exported=true olan bileşenleri (Activity, Service, Receiver, Provider) tespit eder.
 * Bu bileşenler, kötü amaçlı uygulamalar tarafından çağrılabilir olduğu için güvenlik riski oluşturabilir.
 *
 * Detects components (Activity, Service, Receiver, Provider) in AndroidManifest.xml with `exported=true`.
 * These may be invoked by malicious apps and can pose security risks.
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */

class ComponentScanner @Inject constructor() {

    /**
     * Exported olan bileşenlerin adlarını döner. Güvenlik testi için kullanılır.
     *
     * Returns a list of names of exported components. Useful for security analysis.
     *
     * @param context Uygulama bağlamı (context)
     * @return Exported olan bileşen isimlerinin listesi
     */
    fun listExportedComponents(context: Context): List<String> {
        val exported = mutableListOf<String>()
        val pm = context.packageManager
        val pkg = context.packageName

        try {
            val info = pm.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES or
                    PackageManager.GET_SERVICES or
                    PackageManager.GET_RECEIVERS or
                    PackageManager.GET_PROVIDERS)

            info.activities?.filter { it.exported }?.forEach {
                exported.add("Activity: ${it.name}")
            }
            info.services?.filter { it.exported }?.forEach {
                exported.add("Service: ${it.name}")
            }
            info.receivers?.filter { it.exported }?.forEach {
                exported.add("Receiver: ${it.name}")
            }
            info.providers?.filter { it.exported }?.forEach {
                exported.add("ContentProvider: ${it.name}")
            }

        } catch (e: Exception) {
            Log.e("ComponentScanner", "Hata: ${e.message}")
        }

        return exported
    }
}
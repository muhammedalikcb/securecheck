package com.muhammedalikocabey.securecheck.data.log

import android.content.Context
import com.muhammedalikocabey.securecheck.domain.log.ILogExporter
import com.muhammedalikocabey.securecheck.domain.model.AppIntegritySnapshot
import com.muhammedalikocabey.securecheck.domain.model.SecurityState
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SecurityState verisini JSON formatında dışa aktaran log sınıfı
 * Uygulama bütünlüğü durumunu bir dosyaya serialize eder
 *
 * Exports SecurityState data as a JSON file
 * Useful for external reporting or debugging app integrity
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
@Singleton
class SecurityLogExporter @Inject constructor(
    private val appIntegritySnapshot: AppIntegritySnapshot
) : ILogExporter {

    /**
     * Belirtilen formatta güvenlik durumunu dışa aktarır
     *
     * Exports the given security state as a file in the specified format
     *
     * @param context Uygulama bağlamı
     * @param state Export edilecek SecurityState nesnesi
     * @param format Format (default = json)
     * @return Başarılıysa dosya nesnesi, değilse null
     */
    override fun exportToFile(context: Context, state: SecurityState, format: String): File? {
        val content = if (format == "json")
            appIntegritySnapshot.toJson(state)
        else
            state.toString()

        return try {
            val file = File(context.filesDir, "security_log.$format")
            file.writeText(content)
            file
        } catch (e: Exception) {
            null
        }
    }
}

package com.muhammedalikocabey.securecheck.domain.log

import android.content.Context
import com.muhammedalikocabey.securecheck.domain.model.SecurityState
import java.io.File

/**
 * Uygulamanın güvenlik durumunu dışa aktarmak için kullanılan interface
 * JSON veya metin gibi formatlarda dosya üretimi sağlar
 *
 * Interface for exporting app security state as a file
 * Supports formats such as JSON or plain text
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
interface ILogExporter {

    /**
     * Belirtilen SecurityState nesnesini verilen formatta dosyaya dönüştürür
     *
     * Exports given SecurityState as a file in the specified format
     *
     * @param context Uygulama bağlamı
     * @param state Güvenlik durumu verisi
     * @param format json, text gibi formatlar (default = json)
     * @return Eğer başarılıysa oluşturulan dosya, aksi halde null
     */
    fun exportToFile(context: Context, state: SecurityState, format: String = "json"): File?
}

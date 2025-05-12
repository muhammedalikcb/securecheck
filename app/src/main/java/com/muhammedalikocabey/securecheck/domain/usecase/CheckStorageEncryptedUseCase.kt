package com.muhammedalikocabey.securecheck.domain.usecase

import com.muhammedalikocabey.securecheck.data.local.secure.SecureStorageManager
import javax.inject.Inject

/**
 * SecureStorageManager üzerinden şifreli dosyaya yazma/okuma testi yaparak storage'ın şifrelenip şifrelenmediğini kontrol eder
 *
 * Verifies if secure storage is properly encrypted by writing and reading a test value
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class CheckStorageEncryptedUseCase @Inject constructor(
    private val secureStorageManager: SecureStorageManager
) {
    operator fun invoke(): Boolean {
        val sampleData = "secure_test"
        return secureStorageManager.writeEncryptedFile(sampleData) &&
                secureStorageManager.readEncryptedFile() == sampleData
    }
}
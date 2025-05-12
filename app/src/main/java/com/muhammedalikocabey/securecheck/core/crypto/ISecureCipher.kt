package com.muhammedalikocabey.securecheck.core.crypto

/**
 * Güvenli şifreleme ve çözme işlemleri için interface
 * Farklı şifreleme sınıflarının bağımlılık olarak verilmesini sağlar; test edilebilirliği artırır
 *
 * Interface for secure encryption and decryption operations.
 * It allows injecting different cipher implementations, improving testability.
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
interface ISecureCipher {
    fun encrypt(plainText: String): String
    fun decrypt(cipherText: String): String
}
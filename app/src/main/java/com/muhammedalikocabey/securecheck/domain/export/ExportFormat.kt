package com.muhammedalikocabey.securecheck.domain.export

enum class ExportFormat(val extension: String, val mimeType: String) {
    JSON("json", "application/json"),
    HTML("html", "text/html"),
}

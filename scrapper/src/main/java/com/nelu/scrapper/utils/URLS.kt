package com.nelu.scrapper.utils

fun String.toProfileUrl() = "https://www.tiktok.com/@$this"

fun extractUsernameFromUrl(url: String): String {
    val regex = Regex("""@([^/\s]+)""")
    val matchResult = regex.find(url)
    return matchResult?.groupValues?.get(1) ?: ""
}

fun extractVideoIdFromUrl(url: String): String {
    val regex = Regex("/video/(\\d+)")
    val matchResult = regex.find(url)
    return matchResult?.groupValues?.getOrNull(1) ?: System.currentTimeMillis().toString()
}
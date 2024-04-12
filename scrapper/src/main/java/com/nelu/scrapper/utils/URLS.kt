package com.nelu.scrapper.utils

import java.util.regex.Pattern

fun String.toProfileUrl() = "https://www.tiktok.com/@$this"

fun extractUsernameFromTiktokUrl(url: String): String {
    val regex = Regex("""@([^/\s]+)""")
    val matchResult = regex.find(url)
    return matchResult?.groupValues?.get(1) ?: ""
}

fun extractVideoIdFromTiktokUrl(url: String): String {
    //https://vt.tiktok.com/ZSFsHXB3F/
    val regex = Regex("/video/(\\d+)")
    val matchResult = regex.find(url)
    return matchResult?.groupValues?.getOrNull(1) ?: System.currentTimeMillis().toString()
}


fun extractIdFromFacebookUrl(url: String): String {
    val pattern = Pattern.compile("/v/([^/?]+)")
    val matcher = pattern.matcher(url)
    return if (matcher.find()) matcher.group(1)!! else System.currentTimeMillis().toString()
}
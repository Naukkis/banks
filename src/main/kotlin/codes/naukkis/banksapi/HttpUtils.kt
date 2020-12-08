package codes.naukkis.banksapi

import java.net.http.HttpRequest
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun createFormData(data: Map<String, String>): HttpRequest.BodyPublisher? {
    val builder = StringBuilder()
    for ((key, value) in data) {
        if (builder.isNotEmpty()) {
            builder.append("&")
        }
        builder.append(key)
        builder.append("=")
        builder.append(value)
    }
    return HttpRequest.BodyPublishers.ofString(builder.toString())
}

fun getHttpDate(): String {
    return DateTimeFormatter.RFC_1123_DATE_TIME
            .withZone(ZoneOffset.UTC)
            .format(Instant.now())
}
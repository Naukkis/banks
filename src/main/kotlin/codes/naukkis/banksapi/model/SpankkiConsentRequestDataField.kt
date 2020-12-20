package codes.naukkis.banksapi.model

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy::class)
data class SpankkiConsentRequestDataField(
    val expirationDateTime: String,
    val permissions: Array<String>,
    val transactionsFromDateTime: String,
    val transactionsToDateTime: String
)
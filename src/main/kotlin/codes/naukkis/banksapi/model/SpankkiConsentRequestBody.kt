package codes.naukkis.banksapi.model

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy::class)
data class SpankkiConsentRequestBody(val data: SpankkiConsentRequestDataField, val risk: Risk)

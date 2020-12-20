package codes.naukkis.banksapi.model

data class PaymentRequest(
    val amount: String,
    val recipientName: String,
    val recipientAccount: String,
    val message: String,
    val debtorAccountNumber: String
)

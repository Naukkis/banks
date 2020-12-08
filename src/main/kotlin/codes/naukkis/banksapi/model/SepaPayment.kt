package codes.naukkis.banksapi.model

data class SepaPayment(val creditor: Creditor, val debtor: Debtor, val amount: String, val currency: String)

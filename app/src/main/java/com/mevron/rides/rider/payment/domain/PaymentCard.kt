package com.mevron.rides.rider.payment.domain

import androidx.annotation.DrawableRes
import com.mevron.rides.rider.R

data class PaymentCard(
    val bin: String,
    val brand: String,
    val expiryMonth: String,
    val expiryYear: String,
    val lastDigits: String,
    val type: String,
    val uuid: String
) {
    companion object {
        val EMPTY = PaymentCard(
            bin = "",
            brand = "",
            expiryMonth = "",
            expiryYear = "",
            lastDigits = "",
            type = "cash",
            uuid = "cash"
        )

        val WALLET = PaymentCard(
            bin = "",
            brand = "",
            expiryMonth = "",
            expiryYear = "",
            lastDigits = "",
            type = "wallet",
            uuid = "wallet"
        )
    }
}

fun PaymentCard.isCash(): PAYTYPE{
    if (type.lowercase() == "cash".lowercase()){
        return PAYTYPE.CASH
    }
    if (type.lowercase() == "wallet".lowercase()){
        return PAYTYPE.WALLET
    }
    return PAYTYPE.CARD
}

@DrawableRes
fun PaymentCard.getCardImage(): Int =
    when (brand.lowercase()) {
        "mastercard" -> R.drawable.master_card_logo_svg
        "visa" -> R.drawable.ic_visa_card
        "verve" -> R.drawable.verve
        else -> R.drawable.ic_card
    }

data class PaymentCardDomainModel(
    val cards: List<PaymentCard> = listOf()
)
enum class PAYTYPE{
    CARD,
    CASH,
    WALLET,
}
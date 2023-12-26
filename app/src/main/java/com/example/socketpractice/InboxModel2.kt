package com.example.socketpractice

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

data class InboxModel2(
    @Keep
    @SerializedName("_id"         ) var _id          : Id?  = Id(),
    @Keep
    @SerializedName("unreadCount" ) var unreadCount : Int? = null
)
data class Id (
    @Keep
    @SerializedName("_id"          ) var _id           : String? = null,
    @Keep
    @SerializedName("customerName" ) var customerName : String? = null,
    @Keep
    @SerializedName("createdAt"    ) var createdAt    : String? = null,
    @Keep
    @SerializedName("updatedAt"    ) var updatedAt    : String? = null,
    @Keep
    @SerializedName("brandName"    ) var brandName    : String? = null,
    @Keep
    @SerializedName("customerId"   ) var customerId   : String? = null,
    @Keep
    @SerializedName("sellerId"     ) var sellerId     : String? = null,
    @Keep
    @SerializedName("storeId"      ) var storeId      : String? = null
)
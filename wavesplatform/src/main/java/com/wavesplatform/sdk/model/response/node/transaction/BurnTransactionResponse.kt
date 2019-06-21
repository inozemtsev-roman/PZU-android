package com.wavesplatform.sdk.model.response.node.transaction

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.wavesplatform.sdk.model.request.node.BaseTransaction
import kotlinx.android.parcel.Parcelize

@Parcelize
class BurnTransactionResponse(@SerializedName("assetId")
                              val assetId: String = "",
                              @SerializedName("quantity")
                              var quantity: Long = 0)
    : BaseTransactionResponse(type = BaseTransaction.BURN), Parcelable
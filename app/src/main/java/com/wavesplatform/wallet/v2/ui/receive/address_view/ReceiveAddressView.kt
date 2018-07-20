package com.wavesplatform.wallet.v2.ui.receive.address_view

import android.graphics.Bitmap
import com.wavesplatform.wallet.v2.ui.base.view.BaseMvpView

interface ReceiveAddressView : BaseMvpView {
    fun showQRCode(it: Bitmap?)

}

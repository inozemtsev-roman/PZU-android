/*
 * Created by Eduard Zaydel on 1/4/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.wallet.v2.ui.home.profile.address_book

import com.wavesplatform.wallet.v2.data.model.userdb.AddressBookUser
import com.wavesplatform.wallet.v2.ui.base.view.BaseMvpView

interface AddressBookView : BaseMvpView {
    fun afterSuccessGetAddress(list: MutableList<AddressBookUser>)
    fun afterFailedGetAddress()
}

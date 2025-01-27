/*
 * Created by Eduard Zaydel on 1/4/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.wallet.v2.ui.home.wallet.assets.details.content

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.wavesplatform.wallet.v2.data.model.local.HistoryItem
import com.wavesplatform.wallet.v2.data.model.remote.response.AssetBalance
import com.wavesplatform.wallet.v2.ui.base.view.BaseMvpView

@StateStrategyType(SkipStrategy::class)
interface AssetDetailsContentView : BaseMvpView {
    fun showLastTransactions(data: MutableList<HistoryItem>)
    fun onAssetAddressBalanceLoadSuccess(assetBalance: AssetBalance)
}

/*
 * Created by Eduard Zaydel on 1/4/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.wallet.v2.ui.home.dex.trade

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.wavesplatform.wallet.v2.data.model.local.WatchMarket
import com.wavesplatform.wallet.v2.data.model.remote.response.AssetInfo
import com.wavesplatform.wallet.v2.ui.base.presenter.BasePresenter
import com.wavesplatform.wallet.v2.util.RxUtil
import javax.inject.Inject

@InjectViewState
class TradePresenter @Inject constructor() : BasePresenter<TradeView>() {
    var watchMarket: WatchMarket? = null
    var amountAssetInfo: AssetInfo? = null
    var priceAssetInfo: AssetInfo? = null

    fun loadAssetsInfoOfPair() {
        addSubscription(apiDataManager.assetsInfoByIds(arrayListOf(watchMarket?.market?.amountAsset, watchMarket?.market?.priceAsset))
                .compose(RxUtil.applyObservableDefaultSchedulers())
                .subscribe { assetsInfo ->
                    val map = assetsInfo.associateBy { it.id }
                    amountAssetInfo = map[watchMarket?.market?.amountAsset]
                    priceAssetInfo = map[watchMarket?.market?.priceAsset]
                    Log.d("test", "test")
                })
    }
}

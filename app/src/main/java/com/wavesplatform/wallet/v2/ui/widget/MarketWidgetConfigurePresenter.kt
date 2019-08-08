package com.wavesplatform.wallet.v2.ui.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import com.arellomobile.mvp.InjectViewState
import com.wavesplatform.sdk.model.response.data.AssetInfoResponse
import com.wavesplatform.sdk.model.response.data.SearchPairResponse
import com.wavesplatform.sdk.utils.RxUtil
import com.wavesplatform.sdk.utils.WavesConstants
import com.wavesplatform.sdk.utils.isWaves
import com.wavesplatform.sdk.utils.isWavesId
import com.wavesplatform.wallet.v2.data.Constants
import com.wavesplatform.wallet.v2.ui.base.presenter.BasePresenter
import com.wavesplatform.wallet.v2.ui.widget.adapters.TokenAdapter
import com.wavesplatform.wallet.v2.ui.widget.model.MarketWidgetActiveAsset
import com.wavesplatform.wallet.v2.ui.widget.model.MarketWidgetActiveAssetPrefStore
import com.wavesplatform.wallet.v2.ui.widget.model.MarketWidgetStyle
import com.wavesplatform.wallet.v2.ui.widget.model.MarketWidgetUpdateInterval
import com.wavesplatform.wallet.v2.util.EnvironmentManager
import com.wavesplatform.wallet.v2.util.isFiat
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import javax.inject.Inject


@InjectViewState
class MarketWidgetConfigurePresenter @Inject constructor() : BasePresenter<MarketWidgetConfigureView>() {

    var assets = arrayListOf<String>()
    var themeName = MarketWidgetStyle.CLASSIC
    var intervalUpdate = MarketWidgetUpdateInterval.MIN_10
    var widgetAssetPairs = arrayListOf<MarketWidgetActiveAsset>()
    var canAddPair = false

    fun saveAppWidget(context: Context, widgetId: Int) {
        MarketWidgetActiveAssetPrefStore.saveAll(context, widgetId, widgetAssetPairs)
        // It is the responsibility of the configuration activity to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(context)
        MarketWidget.updateWidget(context, appWidgetManager, widgetId)
    }

    fun loadAssets(context: Context, widgetId: Int) {

        fun getFilledPairs(pairs: SearchPairResponse, assets: MutableList<String>)
                : MutableList<SearchPairResponse.Pair> {
            val filledResult = mutableListOf<SearchPairResponse.Pair>()
            for (index in 0 until pairs.data.size) {
                if (pairs.data[index].data != null) {
                    val pair = assets[index].split("/")
                    pairs.data[index].amountAsset = pair[0]
                    pairs.data[index].priceAsset = pair[1]
                    filledResult.add(pairs.data[index])
                }
            }

            return if (filledResult.size > 10) {
                filledResult.subList(0, 10)
            } else {
                filledResult
            }
        }

        fun createPairs(assets: List<String>): MutableList<String> {
            val initPairsList = mutableListOf<String>()

            for (priceAssetId in assets) {
                if (priceAssetId.isWaves()) {
                    initPairsList.add("${WavesConstants.WAVES_ASSET_ID_FILLED}/${Constants.Fiat.USD_ID}")
                    initPairsList.add("${Constants.Fiat.USD_ID}/${WavesConstants.WAVES_ASSET_ID_FILLED}")
                    continue
                } else {
                    initPairsList.add("${WavesConstants.WAVES_ASSET_ID_FILLED}/$priceAssetId")
                    initPairsList.add("$priceAssetId/${WavesConstants.WAVES_ASSET_ID_FILLED}")
                }
            }
            return initPairsList
        }

        setInitAppWidgetConfig(context, widgetId)

        val pairsList = createPairs(assets)
        addSubscription(
                Observable.zip(dataServiceManager.assets(ids = assets),
                        dataServiceManager.loadPairs(pairs = pairsList)
                                .flatMap { pairs ->
                                    Observable.just(getFilledPairs(pairs, pairsList))
                                },
                        BiFunction { assetInfoList: List<AssetInfoResponse>, searchPair: List<SearchPairResponse.Pair> ->
                            return@BiFunction Pair(assetInfoList, searchPair)
                        })
                        .compose(RxUtil.applyObservableDefaultSchedulers())
                        .subscribe({ (assetInfoList, searchPair) ->
                            val tokenPairList = arrayListOf<TokenAdapter.TokenPair>()
                            searchPair.forEach { assetPair ->

                                val id = when {
                                    assetPair.priceAsset?.isWaves() == true ->
                                        assetPair.amountAsset
                                    isFiat(assetPair.priceAsset ?: "") ->
                                        assetPair.amountAsset
                                    else ->
                                        assetPair.priceAsset
                                }

                                tokenPairList.add(TokenAdapter.TokenPair(
                                        assetInfoList.first { it.id == id },
                                        assetPair))
                            }
                            viewState.onUpdatePairs(tokenPairList)
                        }, {
                            it.printStackTrace()
                            viewState.onFailGetMarkets()
                        })
        )
    }

    fun loadPair(assetInfo: AssetInfoResponse) {
        addSubscription(dataServiceManager.loadPairs(searchByAsset = assetInfo.id)
                .compose(RxUtil.applyObservableDefaultSchedulers())
                .subscribe(
                        { result ->
                            viewState.onUpdatePair(assetInfo, result)
                        },
                        {
                            it.printStackTrace()
                            viewState.onFailGetMarkets()
                        }))
    }

    private fun setInitAppWidgetConfig(context: Context, widgetId: Int) {
        val assetsList =
                MarketWidgetActiveAssetPrefStore.queryAll(context, widgetId)

        if (assetsList.isEmpty()) {
            Constants.defaultCrypto().toList().forEach {
                if (it.isWavesId()) {
                    assets.add(WavesConstants.WAVES_ASSET_ID_FILLED)
                } else {
                    assets.add(it)
                }
            }
        } else {
            assetsList.forEach {
                assets.add(MarketWidgetActiveAsset.getMainAssetId(it))
            }
        }

        intervalUpdate = MarketWidgetUpdateInterval.getInterval(context, widgetId)
        themeName = MarketWidgetStyle.getTheme(context, widgetId)
    }
}
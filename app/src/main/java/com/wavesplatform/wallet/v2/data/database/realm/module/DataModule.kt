/*
 * Created by Eduard Zaydel on 5/4/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.wallet.v2.data.database.realm.module

import com.wavesplatform.wallet.v2.data.model.remote.response.*
import com.wavesplatform.wallet.v2.data.model.userdb.AddressBookUser
import com.wavesplatform.wallet.v2.data.model.userdb.AssetBalanceStore
import io.realm.annotations.RealmModule

@RealmModule(classes = [AssetBalance::class, IssueTransaction::class, Transaction::class, Data::class,
    Transfer::class, AssetPair::class, Order::class, Lease::class, Alias::class, SpamAsset::class,
    AssetInfo::class, Payment::class])
class DataModule
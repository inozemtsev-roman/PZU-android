/*
 * Created by Eduard Zaydel on 1/4/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.wallet.v1.payload;

import com.wavesplatform.wallet.v1.data.enums.OrderStatus;

public class MyOrder {

    public String id;
    public String type;
    public Long amount;
    public Long price;
    public Long timestamp;
    public Long filled;
    private String status;
    public AssetPair assetPair;

    public static class AssetPair {
        public String amountAsset;
        public String priceAsset;
    }

    public OrderStatus getStatus() {
        switch (status){
            case "Accepted":
                return OrderStatus.Accepted;
            case "PartiallyFilled":
                return OrderStatus.PartiallyFilled;
            case "Cancelled":
                return OrderStatus.Cancelled;
            case "Filled":
                return OrderStatus.Filled;
            default:
                return OrderStatus.Filled;
        }
    }
}


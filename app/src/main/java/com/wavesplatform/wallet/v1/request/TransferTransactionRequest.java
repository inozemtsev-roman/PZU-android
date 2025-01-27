/*
 * Created by Eduard Zaydel on 1/4/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.wallet.v1.request;

import android.util.Log;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.wavesplatform.wallet.v1.crypto.Base58;
import com.wavesplatform.wallet.v1.crypto.CryptoProvider;
import com.wavesplatform.wallet.v1.crypto.Hash;
import com.wavesplatform.wallet.v1.payload.TransferTransaction;
import com.wavesplatform.wallet.v1.util.SignUtil;
import com.wavesplatform.wallet.v2.util.AddressUtil;

public class TransferTransactionRequest {
    public static int SignatureLength = 64;
    public static int KeyLength = 32;
    public static int MaxAttachmentSize = 140;
    public static int MinFee = 100000;

    public String assetId;
    public String senderPublicKey;
    public String recipient;
    public long amount;
    public long timestamp;
    public String feeAssetId;
    public long fee;
    public String attachment;
    public String signature;

    public transient final int txType = 4;

    public byte[] toSignBytes() {
        try {
            byte[] timestampBytes  = Longs.toByteArray(timestamp);
            byte[] assetIdBytes = SignUtil.arrayOption(assetId);
            byte[] amountBytes     = Longs.toByteArray(amount);
            byte[] feeAssetIdBytes = SignUtil.arrayOption(feeAssetId);
            byte[] feeBytes        = Longs.toByteArray(fee);

            return Bytes.concat(new byte[] {txType},
                    Base58.decode(senderPublicKey),
                    assetIdBytes,
                    feeAssetIdBytes,
                    timestampBytes,
                    amountBytes,
                    feeBytes,
                    Base58.decode(recipient),
                    SignUtil.arrayWithSize(attachment));
        } catch (Exception e) {
            Log.e("Wallet", "Couldn't create transaction sign", e);
            return new byte[0];
        }
    }

    public void sign(byte[] privateKey)  {
        if (signature == null) {
            signature = Base58.encode(CryptoProvider.sign(privateKey, toSignBytes()));
        }
    }

    public int getAttachmentSize() {
        if (attachment == null) {
            return 0;
        }
        try {
            return Base58.decode(attachment).length;
        } catch (Base58.InvalidBase58 invalidBase58) {
            invalidBase58.printStackTrace();
            return 0;
        }
    }

    public TransferTransaction createDisplayTransaction() {
        TransferTransaction tt = new TransferTransaction(4, Base58.encode(Hash.fastHash(toSignBytes())),
                AddressUtil.addressFromPublicKey(senderPublicKey), timestamp, amount, fee,
                assetId, recipient, attachment);
        tt.isPending = true;
        return tt;
    }
}

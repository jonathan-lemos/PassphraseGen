package com.jonathanrlemos.passphrasegen;

import java.util.List;

public interface AssetListReaderCallback {
    void callbackSuccess(List<AssetList> list);
    void callbackFailure(AssetListReaderError error);
}

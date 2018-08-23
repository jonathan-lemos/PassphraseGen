package com.jonathanrlemos.passphrasegen;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;

public class AssetList implements Serializable {
    private static final long serialVersionUID = -5650540629923861561L;
    private String assetName;
    // Must be serializable, so it can't be a List<String>
    private ArrayList<String> list;
    private int listRandomCounter = 0;

    public AssetList(Context c, String assetName) throws IOException {
        this.assetName = assetName;
        BufferedReader br = new BufferedReader(new InputStreamReader(c.getAssets().open(assetName)));
        list = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null){
            list.add(line);
        }
        br.close();
    }

    public String getAssetName(){
        return assetName;
    }
}

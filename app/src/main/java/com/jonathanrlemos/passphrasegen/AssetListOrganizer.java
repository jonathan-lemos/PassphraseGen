package com.jonathanrlemos.passphrasegen;

import android.content.Context;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AssetListOrganizer implements Serializable {
	private static final long serialVersionUID = -6701817719700112782L;

	private ArrayList<AssetList> list;

	public AssetListOrganizer(){
		list = new ArrayList<>();
	}

	public AssetListOrganizer(Context c, String... assetNames) throws IOException{
		this();
		for (String name : assetNames) {
			list.add(new AssetList(c, name));
		}
	}

	public AssetListOrganizer(Context c, List<String> assetNames) throws IOException{
		this();
		for (String name : assetNames) {
			list.add(new AssetList(c, name));
		}
	}

	public AssetListOrganizer addList(Context c, String assetName) throws IOException{
		list.add(new AssetList(c, assetName));
		return this;
	}

	public AssetListOrganizer removeList(String assetName){
		for (AssetList al : list){
			if (al.getAssetName().equals(assetName)){
				list.remove(al);
				break;
			}
		}
		return this;
	}

	public AssetList getList(String assetName){
		for (AssetList al : list){
			if (al.getAssetName().equals(assetName)){
				return al;
			}
		}
		return null;
	}
}

package com.jonathanrlemos.passphrasegen;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AssetListReader extends AsyncTask<String, Void, List<AssetList>> {
    private WeakReference<Context> contextRef;
    private AssetListReaderCallback callback;
    private AssetListReaderError error = AssetListReaderError.NONE;

    public static String ErrorToString(AssetListReaderError error){
        switch (error){
            case NONE:
                return "The operation completed successfully";
            case IO_ERROR:
                return "Failure reading from asset file";
            case CONTEXT_EXPIRED:
                return "The calling context has expired";
            default:
                throw new IllegalArgumentException("The error code was invalid");
        }
    }

    public AssetListReader(Context context, AssetListReaderCallback callback){
        this.contextRef = new WeakReference<>(context);
        this.callback = callback;
    }

    @Override
    protected List<AssetList> doInBackground(String... assetNames){
        List<AssetList> ret = new ArrayList<>();
        Context c;

        if ((c = contextRef.get()) == null){
            error = AssetListReaderError.CONTEXT_EXPIRED;
            return null;
        }

        try {
            for (String name : assetNames){
                ret.add(new AssetList(c, name));
            }
        }
        catch (IOException e){
            error = AssetListReaderError.IO_ERROR;
            return null;
        }

        return ret;
    }

    @Override
    protected void onPostExecute(List<AssetList> list){
        if (list == null){
            callback.callbackFailure(error);
        }
        else {
            callback.callbackSuccess(list);
        }
    }
}

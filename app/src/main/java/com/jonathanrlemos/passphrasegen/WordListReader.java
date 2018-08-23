package com.jonathanrlemos.passphrasegen;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.List;

public class WordListReader extends AsyncTask<Void, Integer, WordList> {
    private final WeakReference<Context> contextRef;
    private WordListReader.Callback callback;
    private WordListReader.Error error = WordListReader.Error.NONE;

    private static final int PUBLISH_PROGRESS_LINES = 100;
    private int curProgress = 0;
    private int maxProgress = 0;

    public interface Callback {
        void callbackSuccess(WordList list);
        void callbackFailure(WordListReader.Error error);
    }

    public enum Error {
        NONE,
        CONTEXT_EXPIRED,
        IO_ERROR,
    }

    private class ContextExpiredException extends Exception{
        private static final long serialVersionUID = -2758012616697702290L;

        public ContextExpiredException(){
            super("The calling context has expired");
        }

        public ContextExpiredException(String message){
            super(message);
        }
    }

    public static String ErrorToString(WordListReader.Error error){
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

    public WordListReader(Context context, WordListReader.Callback callback){
        this.contextRef = new WeakReference<>(context);
        this.callback = callback;
    }

    private BufferedReader openAssetFile(String assetName) throws IOException, ContextExpiredException{
        Context c;
        if ((c = contextRef.get()) == null){
            throw new ContextExpiredException();
        }

        return new BufferedReader(new InputStreamReader(c.getAssets().open(assetName)));
    }

    private List<List<String>> readAssetFile(String assetName){
        try {
            BufferedReader br = openAssetFile(assetName);
            int ctr = 0;
            String line;
            while ((line = br.readLine()) != null){

            }
        }
        catch (IOException e){
            error = Error.IO_ERROR;
            return null;
        }
        catch (ContextExpiredException e){
            error = Error.CONTEXT_EXPIRED;
            return null;
        }
    }

    @Override
    protected WordList doInBackground(Void... v){
        WordList ret;

        if ((c = contextRef.get()) == null){
            error = WordListReader.Error.CONTEXT_EXPIRED;
            return null;
        }

        ret = new WordList();

        try {
            BufferedReader br = new BufferedReader()
        }
        catch (IOException e){
            error = WordListReader.Error.IO_ERROR;
            return null;
        }

        return ret;
    }

    @Override
    protected void onPostExecute(WordList list){
        if (list == null){
            callback.callbackFailure(error);
        }
        else {
            callback.callbackSuccess(list);
        }
    }
}

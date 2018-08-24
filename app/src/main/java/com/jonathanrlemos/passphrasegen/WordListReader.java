package com.jonathanrlemos.passphrasegen;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class WordListReader extends AsyncTask<Void, Integer, WordList> {
    private WeakReference<Context> contextRef;
    private WeakReference<ProgressBar> progressBarRef;
    private WordListReader.Callback callback;
    private WordListReader.Error error = WordListReader.Error.NONE;

    private static final int PUBLISH_PROGRESS_LINES = 1000;

    public interface Callback {
        void callbackSuccess(WordList list);
        void callbackFailure(WordListReader.Error error);
    }

    public enum Error {
        NONE,
        CONTEXT_EXPIRED,
        IO_ERROR,
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

    private class ContextExpiredException extends Exception{
        private static final long serialVersionUID = -2758012616697702290L;

        public ContextExpiredException(){
            super("The calling context has expired");
        }

        public ContextExpiredException(String message){
            super(message);
        }
    }

    private ProgressBar getProgressBar(){
        if (progressBarRef == null){
            return null;
        }
        return progressBarRef.get();
    }

    private Context getContext(){
        if (contextRef == null){
            return null;
        }
        return contextRef.get();
    }

    public WordListReader(Context context, WordListReader.Callback callback){
        this.contextRef = new WeakReference<>(context);
        this.callback = callback;
    }

    public WordListReader(Context context, WordListReader.Callback callback, ProgressBar progressBar){
        this(context, callback);
        progressBar.setIndeterminate(false);
        progressBarRef = new WeakReference<>(progressBar);
    }

    public WordListReader(Activity activity, WordListReader.Callback callback, int progressBarResId){
        this(activity, callback, (ProgressBar)activity.findViewById(progressBarResId));
    }

    private int getMaxProgress(String... assetNames) throws IOException, ContextExpiredException{
        int accumulator = 0;
        Context c;
        if ((c = getContext()) == null){
            throw new ContextExpiredException();
        }
        for (String name : assetNames){
            accumulator += c.getAssets().openFd(name).getLength();
        }
        return accumulator;
    }

    private BufferedReader openAssetFile(String assetName) throws IOException, ContextExpiredException{
        Context c;
        if ((c = getContext()) == null){
            throw new ContextExpiredException();
        }

        return new BufferedReader(new InputStreamReader(c.getAssets().open(assetName)));
    }

    private List<ArrayList<String>> readAssetFiles(String... assetNames) throws IOException, ContextExpiredException{
        List<ArrayList<String>> ret = new ArrayList<>();
        int curProgress = 0;
        int maxProgress = getMaxProgress(assetNames);

        for (String name : assetNames){
            BufferedReader br = openAssetFile(name);
            ArrayList<String> list = new ArrayList<>();
            int ctr = 0;
            String line;

            while ((line = br.readLine()) != null) {
                list.add(line);
                ctr++;
                curProgress += line.length() + 1;
                if (ctr >= PUBLISH_PROGRESS_LINES){
                    publishProgress(curProgress, maxProgress);
                    ctr = 0;
                }
            }

            ret.add(list);
        }

        return ret;
    }

    @Override
    protected WordList doInBackground(Void... v){
        WordList ret = new WordList();
        List<ArrayList<String>> list;

        try {
            list = readAssetFiles(WordList.ADJECTIVE_LIST_FILENAME, WordList.NOUN_LIST_FILENAME, WordList.ADVERB_LIST_FILENAME, WordList.VERB_LIST_FILENAME);
        }
        catch (IOException e){
            error = Error.IO_ERROR;
            return null;
        }
        catch (ContextExpiredException e){
            error = Error.CONTEXT_EXPIRED;
            return null;
        }

        ret.setAdjectiveList(list.get(0));
        ret.setNounList(list.get(1));
        ret.setAdverbList(list.get(2));
        ret.setVerbList(list.get(3));

        return ret;
    }

    @Override
    protected void onProgressUpdate(Integer... progress){
        ProgressBar pb;
        if ((pb = getProgressBar()) == null){
            return;
        }
        pb.setProgress(progress[0]);
        pb.setMax(progress[1]);
    }

    @Override
    protected void onPostExecute(WordList list){
        // the calling context has expired, so the callback probably won't work
        if (getContext() == null){
            return;
        }

        if (list == null){
            callback.callbackFailure(error);
        }
        else {
            callback.callbackSuccess(list);
        }
    }
}

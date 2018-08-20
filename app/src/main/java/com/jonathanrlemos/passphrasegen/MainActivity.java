package com.jonathanrlemos.passphrasegen;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<String> list_adj = null;
    private List<String> list_noun = null;
    private String current_entry = "";

    public void setListAdj(List<String> list){
        list_adj = list;
    }

    public void setListNoun(List<String> list){
        list_noun = list;
    }

    private static class AssetListReader extends AsyncTask<Void, Void, List<List<String>>> {
        private Snackbar sbInitial;
        private Snackbar sbComplete;
        private Snackbar sbFailure;
        private WeakReference<MainActivity> activity;

        protected AssetListReader(MainActivity activity, String initialMessage, String completeMessage, String failureMessage){
            CoordinatorLayout cl = activity.findViewById(R.id.CoordinatorLayoutMain);
            ConstraintLayout ml = activity.findViewById(R.id.ConstraintLayoutMain);
            this.sbInitial = SnackbarPushFactory.make(cl, initialMessage, Snackbar.LENGTH_LONG, ml);
            this.sbComplete = SnackbarPushFactory.make(cl, completeMessage, Snackbar.LENGTH_LONG, ml);
            this.sbFailure = SnackbarPushFactory.make(cl, completeMessage, Snackbar.LENGTH_LONG, ml);
            this.activity = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            sbInitial.show();
        }

        private List<String> readFromAsset(String assetName) throws IOException{
            MainActivity ma;
            if ((ma = activity.get()) == null){
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(ma.getAssets().open(assetName)));
            List<String> ret = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null){
                ret.add(line);
            }
            br.close();

            return ret;
        }

        @Override
        protected List<List<String>> doInBackground(Void... v){
            List<List<String>> ret = new ArrayList<>();
            ret.add(new ArrayList<String>());
            ret.add(new ArrayList<String>());

            if (activity.get() == null){
                return null;
            }
            try {
                ret.set(0, readFromAsset("adj.txt"));
                ret.set(1, readFromAsset("noun.txt"));
            }
            catch (IOException e){
                return null;
            }
            return ret;
        }

        @Override
        protected void onPostExecute(List<List<String>> list){
            MainActivity ma;
            if ((ma = activity.get()) == null){
                return;
            }

            if (sbInitial.isShown()){
                sbInitial.dismiss();
            }

            if (list == null){
                sbFailure.show();
                return;
            }

            if (BuildConfig.DEBUG && list.size() != 2){
                sbFailure.show();
                throw new RuntimeException("Returned list size is not 2");
            }

            ma.setListAdj(list.get(0));
            ma.setListNoun(list.get(1));
            sbComplete.show();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));

        if (savedInstanceState != null){
            list_adj = (List<String>)savedInstanceState.getSerializable("LIST_ADJ");
            list_noun = (List<String>)savedInstanceState.getSerializable("LIST_NOUN");
            current_entry = (String)savedInstanceState.getSerializable("CURRENT_ENTRY");
        }
        else {
            new AssetListReader(this, "Populating word lists...", "Finished populating word lists.", "Warning: failed to populate word lists.").execute();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putSerializable("LIST_ADJ", (Serializable)list_adj);
        savedInstanceState.putSerializable("LIST_NOUN", (Serializable)list_noun);

        super.onSaveInstanceState(savedInstanceState);
    }

    private static int randInt(int min, int max){
        return (int)(Math.random() * (max - min + 1)) + min;
    }

    private static <T> T listGetRandom(List<T> list){
        return list.get(randInt(0, list.size() - 1));
    }

    public float getScreenWidthDp(){
        Configuration c = getResources().getConfiguration();
        return c.screenWidthDp;
    }

    public void resizeTextView(TextView tv, float width){
        float currentSizeDp = tv.getPaint().measureText(tv.getText().toString());
        float currentTextSize = tv.getTextSize();
        tv.setTextSize(currentTextSize * (getScreenWidthDp() * width / currentSizeDp));
    }

    public void onClickButtonGenerate(View v){
        TextView tv = findViewById(R.id.TextViewPassphrase);
        if (list_adj == null || list_noun == null){
            SnackbarPushFactory.make(findViewById(R.id.CoordinatorLayoutMain), "Word lists are not initialized.", Snackbar.LENGTH_LONG, findViewById(R.id.ConstraintLayoutMain)).show();
            return;
        }
        String text = listGetRandom(list_adj) + listGetRandom(list_noun);
        tv.setText(text);
        resizeTextView(tv, 0.9f);
    }

    public void onClickButtonCopyToClipboard(View v){
        TextView tv = findViewById(R.id.TextViewPassphrase);
        ClipboardManager cm = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", tv.getText());
        String sb_text;
        if (cm != null && clip != null) {
            cm.setPrimaryClip(clip);
            sb_text = "Copied " + tv.getText() + " to clipboard";
        }
        else{
            sb_text = "Failed to copy to clipboard";
        }
        Snackbar sb = Snackbar.make(findViewById(R.id.CoordinatorLayoutMain), sb_text, Snackbar.LENGTH_SHORT);
        sb.show();
    }
}
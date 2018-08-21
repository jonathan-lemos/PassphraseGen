package com.jonathanrlemos.passphrasegen;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AssetList list_adj = null;
    private AssetList list_noun = null;
    private String current_entry = "";

    private void showSnackbar(String message, int length){
        SnackbarPushFactory.make(findViewById(R.id.CoordinatorLayoutMain), message, length, findViewById(R.id.ConstraintLayoutMain)).show();
    }

    public void setList(AssetList list){
        switch (list.getAssetName()){
            case "adj.txt":
                list_adj = list;
                break;
            case "noun.txt":
                list_noun = list;
                break;
            default:
                throw new IllegalArgumentException("list.getAssetName() does not correspond to any known value");
        }
    }

    public void readAssets(){
        AssetListReaderCallback callback = new AssetListReaderCallback() {
            @Override
            public void callbackSuccess(List<AssetList> list) {
                for (AssetList al : list){
                    setList(al);
                }
                showSnackbar("Finished populating word lists", Snackbar.LENGTH_SHORT);
            }

            @Override
            public void callbackFailure(AssetListReaderError error) {
                showSnackbar("Failed to populate word lists (" + AssetListReader.ErrorToString(error) + ")", Snackbar.LENGTH_INDEFINITE);
            }
        };

        new AssetListReader(this, callback).execute("adj.txt", "noun.txt");
    }

    public void restoreState(Bundle savedInstanceState){
        list_adj = (AssetList)savedInstanceState.getSerializable("LIST_ADJ");
        list_noun = (AssetList)savedInstanceState.getSerializable("LIST_NOUN");
        current_entry = (String)savedInstanceState.getSerializable("CURRENT_ENTRY");
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putSerializable("LIST_ADJ", list_adj);
        savedInstanceState.putSerializable("LIST_NOUN", list_noun);
        savedInstanceState.putSerializable("CURRENT_ENTRY", current_entry);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));

        if (savedInstanceState != null){
            restoreState(savedInstanceState);
        }
        else {
            readAssets();
        }
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
            showSnackbar("Word lists are not populated.", Snackbar.LENGTH_LONG);
            return;
        }
        String text = list_adj.getRandom() + list_noun.getRandom();
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
        showSnackbar(sb_text, Snackbar.LENGTH_SHORT);
    }
}
package com.jonathanrlemos.passphrasegen;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private AssetListOrganizer assetLists;
    private String currentEntry = "";
    private Snackbar currentSnackbar;

    private void showSnackbar(String message, int length) {
        currentSnackbar = SnackbarPushFactory.make(findViewById(R.id.CoordinatorLayoutMain), message, length, findViewById(R.id.ConstraintLayoutMain));
        currentSnackbar.show();
    }

    private void dismissCurrentSnackbar(){
        if (currentSnackbar != null && currentSnackbar.isShown()){
            currentSnackbar.dismiss();
        }
    }

    public void readAssets(){
        WordListReader.Callback callback = new WordListReader.Callback() {
            @Override
            public void callbackSuccess(WordListOrganizer list) {
                assetLists = list;
                showSnackbar("Finished populating word lists", Snackbar.LENGTH_SHORT);
            }

            @Override
            public void callbackFailure(WordListReader.Error error) {
                showSnackbar("Failed to populate word lists (" + WordListReader.ErrorToString(error) + ")", Snackbar.LENGTH_INDEFINITE);
            }
        };

        new WordListReader(this, callback).execute();
    }

    public void restoreState(Bundle savedInstanceState){
        assetLists = (AssetListOrganizer)savedInstanceState.getSerializable("assetLists");
        currentEntry = (String)savedInstanceState.getSerializable("currentEntry");
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putSerializable("assetLists", assetLists);
        savedInstanceState.putSerializable("currentEntry", currentEntry);

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
        if (assetLists == null){
            showSnackbar("Word lists are not populated.", Snackbar.LENGTH_LONG);
            return;
        }
        String text = assetLists.getList("adj.txt").getRandom() + assetLists.getList("noun.txt").getRandom();
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

    private void startSettingsActivity(){
        dismissCurrentSnackbar();
        startActivity(new Intent(this, SettingsActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m){
        getMenuInflater().inflate(R.menu.menu_main, m);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi){
        switch (mi.getItemId()){
            case R.id.actionSettings:
                startSettingsActivity();
                return true;
        }
        return false;
    }

}
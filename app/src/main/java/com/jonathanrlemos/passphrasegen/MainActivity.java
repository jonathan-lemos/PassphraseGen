package com.jonathanrlemos.passphrasegen;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private WordList wordList;
    private String currentEntry = "";
    private Snackbar currentSnackbar;
    private boolean currentlyReadingAssets = false;
    private Prefs prefs;

    private void showSnackbar(String message, int length) {
        currentSnackbar = SnackbarPushFactory.make(findViewById(R.id.CoordinatorLayoutMain), message, length, findViewById(R.id.ConstraintLayoutMain));
        currentSnackbar.show();
    }

    private void dismissCurrentSnackbar(){
        if (currentSnackbar != null && currentSnackbar.isShown()){
            currentSnackbar.dismiss();
        }
    }

    private void showButtonBar(){
        LinearLayout buttonBar = findViewById(R.id.LinearLayoutButtonBar);
        ProgressBar progressBar = findViewById(R.id.ProgressBar);
        TextView progressText = findViewById(R.id.TextViewProgress);

        buttonBar.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        progressText.setVisibility(View.INVISIBLE);
    }

    private void showProgressBar(){
        LinearLayout buttonBar = findViewById(R.id.LinearLayoutButtonBar);
        ProgressBar progressBar = findViewById(R.id.ProgressBar);
        TextView progressText = findViewById(R.id.TextViewProgress);

        buttonBar.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
    }

    public void readAssets(){
        if (currentlyReadingAssets){
            return;
        }

        WordListReader.Callback callback = new WordListReader.Callback() {
            @Override
            public void callbackSuccess(WordList list) {
                wordList = list;
                showButtonBar();
                currentlyReadingAssets = false;
                if (currentEntry.isEmpty()){
                    onClickButtonGenerate(null);
                }
            }

            @Override
            public void callbackFailure(WordListReader.Error error) {
                TextView tv = findViewById(R.id.TextViewProgress);
                tv.setText(R.string.TextViewProgressTextFailure);
                currentlyReadingAssets = false;
            }
        };

        showProgressBar();
        currentlyReadingAssets = true;
        new WordListReader(this, callback, R.id.ProgressBar).execute();
    }

    public void restoreState(Bundle savedInstanceState){
        wordList = (WordList)savedInstanceState.getSerializable("wordList");
        currentEntry = (String)savedInstanceState.getSerializable("currentEntry");
        ((TextView)findViewById(R.id.TextViewPassphrase)).setText(currentEntry);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putSerializable("wordList", wordList);
        savedInstanceState.putSerializable("currentEntry", currentEntry);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbarMain));

        prefs = new Prefs(this);

        if (savedInstanceState != null){
            restoreState(savedInstanceState);
        }

        if (wordList == null){
            readAssets();
        }
        else{
            showButtonBar();
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

    public void onClickButtonGenerate(@Nullable View v){
        TextView tv = findViewById(R.id.TextViewPassphrase);
        if (wordList == null){
            showSnackbar("Word lists are not populated.", Snackbar.LENGTH_LONG);
            return;
        }
        currentEntry = wordList.getRandomAdjective() + wordList.getRandomNoun();
        tv.setText(currentEntry);
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
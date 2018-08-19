package com.jonathanrlemos.passphrasegen;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void onClickLabel(View v){
        TextView tv = findViewById(R.id.TextViewPassphrase);
        tv.setText("Goodbye, World");
        resizeTextView(tv, 0.9f);
    }
}
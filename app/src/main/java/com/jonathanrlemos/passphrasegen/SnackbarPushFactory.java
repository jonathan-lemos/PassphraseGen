package com.jonathanrlemos.passphrasegen;

import android.support.design.widget.Snackbar;
import android.view.View;

public class SnackbarPushFactory {
    private SnackbarPushFactory(){}

    public static Snackbar make(View cl, String msg, int length, final View dependentView){
        Snackbar sb = Snackbar.make(cl, msg, length);
        sb.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener(){
            @Override
            public void onViewAttachedToWindow(View v){

            }

            @Override
            public void onViewDetachedFromWindow(View v){
                dependentView.setTranslationY(0);
            }
        });
        return sb;
    }
}

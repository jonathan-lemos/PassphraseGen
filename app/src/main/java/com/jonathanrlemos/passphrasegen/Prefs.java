package com.jonathanrlemos.passphrasegen;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Prefs {
    private SharedPreferences spref;

    private boolean keysImported = false;
    private final String KEY_PWGEN_CAPITALIZE_FIRST;
    private final String KEY_PWSTR_ENABLE;
    private final String KEY_PWSTR_ENTROPY;
    private final String KEY_PWSTR_CRACKTIME;
    private final String KEY_PWSTR_CRACKSTRENGTH;

    private boolean pwgenCapitalizeFirst;
    private boolean pwstrEnable;
    private boolean pwstrEntropy;
    private boolean pwstrCracktime;
    private int     pwstrCrackstrength;
    private long[]  pwstrCrackstrengthValues;

    public boolean capitalizeFirst(){
        return pwgenCapitalizeFirst;
    }

    public boolean pwstrEnabled(){
        return pwstrEnable;
    }

    public boolean pwstrEntropyEnabled(){
        return pwstrEntropy;
    }

    public boolean pwstrCracktimeEnabled(){
        return pwstrCracktime;
    }

    public long getPwstrCrackStrength(){
        return pwstrCrackstrengthValues[pwstrCrackstrength];
    }

    public class PrefNotFoundException extends RuntimeException{
        private static final long serialVersionUID = 1752896063162363813L;

        public PrefNotFoundException(){
            super("The requested key was not found in the preferences file.");
        }

        public PrefNotFoundException(String key){
            super("The key " + key + " was not found in the preferences file.");
        }
    }

    private <T> T getPref(String key, Class<T> tClass){
        if (!spref.contains(key)){
            throw new PrefNotFoundException(key);
        }

        try {
            switch (tClass.getSimpleName()) {
                case "boolean":
                case "Boolean":
                    return tClass.cast(spref.getBoolean(key, false));
                case "int":
                case "Integer":
                    return tClass.cast(spref.getInt(key, 0));
                case "long":
                case "Long":
                    return tClass.cast(spref.getLong(key, 0));
                case "float":
                case "Float":
                    return tClass.cast(spref.getFloat(key, 0));
                case "String":
                    return tClass.cast(spref.getString(key, null));
                default:
                    throw new IllegalArgumentException("Invalid tClass value: " + tClass.getSimpleName());
            }
        }
        catch (ClassCastException e){
            throw new RuntimeException("Failed to cast key " + key + " to type " + tClass.getSimpleName());
        }
    }

    private void populateValues(){
        if (!keysImported){
            throw new IllegalStateException("importKeys() must be called before populateValues()");
        }

        pwgenCapitalizeFirst = getPref(KEY_PWGEN_CAPITALIZE_FIRST, boolean.class);
        pwstrEnable = getPref(KEY_PWSTR_ENABLE, boolean.class);
        pwstrEntropy = getPref(KEY_PWSTR_ENTROPY, boolean.class);
        pwstrCracktime = getPref(KEY_PWSTR_CRACKTIME, boolean.class);
        pwstrCrackstrength = getPref(KEY_PWSTR_CRACKSTRENGTH, int.class);
    }

    private void registerPreferenceListener(){
        spref.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (sharedPreferences != spref){
                    return;
                }
                //cannot use switch because KEY_* values are not constant expressions
                if (key.equals(KEY_PWGEN_CAPITALIZE_FIRST)){
                    pwgenCapitalizeFirst = getPref(KEY_PWGEN_CAPITALIZE_FIRST, boolean.class);
                }
                else if (key.equals(KEY_PWSTR_ENABLE)){
                    pwstrEnable = getPref(KEY_PWSTR_ENABLE, boolean.class);
                }
                else if (key.equals(KEY_PWSTR_ENTROPY)){
                    pwstrEntropy = getPref(KEY_PWSTR_ENTROPY, boolean.class);
                }
                else if (key.equals(KEY_PWSTR_CRACKTIME)){
                    pwstrCracktime = getPref(KEY_PWSTR_CRACKTIME, boolean.class);
                }
                else if (key.equals(KEY_PWSTR_CRACKSTRENGTH)){
                    pwstrCrackstrength = getPref(KEY_PWSTR_CRACKSTRENGTH, int.class);
                }
            }
        });
    }

    public Prefs(Context c){
        this.spref = PreferenceManager.getDefaultSharedPreferences(c);
        KEY_PWGEN_CAPITALIZE_FIRST = c.getResources().getString(R.string.pref_pwgen_capitalize_first_key);
        KEY_PWSTR_ENABLE = c.getResources().getString(R.string.pref_pwstr_enable_key);
        KEY_PWSTR_ENTROPY = c.getResources().getString(R.string.pref_pwstr_entropy_key);
        KEY_PWSTR_CRACKTIME = c.getResources().getString(R.string.pref_pwstr_cracktime_key);
        KEY_PWSTR_CRACKSTRENGTH = c.getResources().getString(R.string.pref_pwstr_crackstrength_key);

        String[] tmp = c.getResources().getStringArray(R.array.pref_pwstr_crackstrength_options);
        pwstrCrackstrengthValues = new long[tmp.length];
        for (int i = 0; i < tmp.length; ++i){
            pwstrCrackstrengthValues[i] = Long.parseLong(tmp[i]);
        }

        populateValues();
        registerPreferenceListener();
    }
}
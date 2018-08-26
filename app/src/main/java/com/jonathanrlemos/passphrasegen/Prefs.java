package com.jonathanrlemos.passphrasegen;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.preference.PreferenceFragmentCompat;

import java.util.Set;

public class Prefs {
    private Context c;
    private SharedPreferences spref;

    public class PrefNotFoundException extends RuntimeException{
        private static final long serialVersionUID = 1752896063162363813L;

        public PrefNotFoundException(){
            super("The requested key was not found in the preferences file.");
        }

        public PrefNotFoundException(String key){
            super("The key " + key + " was not found in the preferences file.");
        }
    }

    public Prefs(Context c){
        this.c = c;
        this.spref = PreferenceManager.getDefaultSharedPreferences(c);
    }

    public String getKey(int resId){
        return c.getResources().getString(resId);
    }

    public void setBoolean(String key, boolean n){
        spref.edit().putBoolean(key, n).apply();
    }
    public void setBoolean(int resId, boolean n){
        setBoolean(getKey(resId), n);
    }

    public void setInt(String key, int n){
        spref.edit().putInt(key, n).apply();
    }
    public void setInt(int resId, int n){
        setInt(getKey(resId), n);
    }

    public void setLong(String key, long n){
        spref.edit().putLong(key, n).apply();
    }
    public void setLong(int resId, long n){
        setLong(getKey(resId), n);
    }

    public void setFloat(String key, float n){
        spref.edit().putFloat(key, n).apply();
    }
    public void setFloat(int resId, float n){
        setFloat(getKey(resId), n);
    }

    public void setString(String key, String n){
        spref.edit().putString(key, n).apply();
    }
    public void setString(int resId, String n){
        setString(getKey(resId), n);
    }

    public void setStringSet(String key, Set<String> n){
        spref.edit().putStringSet(key, n).apply();
    }
    public void setStringSet(int resId, Set<String> n){
        setStringSet(getKey(resId), n);
    }

    public Boolean getBoolean(String key){
        if (!spref.contains(key)){
            throw new PrefNotFoundException(key);
        }
        return spref.getBoolean(key, true);
    }
    public Boolean getBoolean(int resId){
        return getBoolean(getKey(resId));
    }

    public int getInt(String key){
        if (!spref.contains(key)){
            throw new PrefNotFoundException(key);
        }
        return spref.getInt(key, 0);
    }
    public int getInt(int resId){
        return getInt(getKey(resId));
    }

    public long getLong(String key){
        if (!spref.contains(key)){
            throw new PrefNotFoundException(key);
        }
        return spref.getLong(key, 0);
    }
    public long getLong(int resId){
        return getLong(getKey(resId));
    }

    public float getFloat(String key){
        if (!spref.contains(key)){
            throw new PrefNotFoundException(key);
        }
        return spref.getFloat(key, 0.0f);
    }
    public float getFloat(int resId){
        return getFloat(getKey(resId));
    }

    public String getString(String key){
        if (!spref.contains(key)){
            throw new PrefNotFoundException(key);
        }
        return spref.getString(key, null);
    }
    public String getString(int resId){
        return getString(getKey(resId));
    }

    public Set<String> getStringSet(String key){
        if (!spref.contains(key)){
            throw new PrefNotFoundException(key);
        }
        return spref.getStringSet(key, null);
    }
    public Set<String> getStringSet(int resId){
        return getStringSet(getKey(resId));
    }
}

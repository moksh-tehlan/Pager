package com.moksh.pager.utilities;

import android.content.Context;
import android.content.SharedPreferences;

// SharedPreferences are the way in which one can store and retriever small amounts of primitive data as key/value
// pairs to a file on the device storage sucn as String, int, float, Boolean etc.

public class PreferenceManager {

//    Creation of object of SharedPreferences as sharedPreferences
    private final SharedPreferences sharedPreferences;

//    Constructor of class PreferenceManager being made to initialise the object of SharedPreferences
    public PreferenceManager(Context context) {

//        initialising the object with a name which is stored in Constants.KEY_PREFERENCE_NAME (pagerPreference)
//        setting the MODE_PRIVATE so the files can be secure
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME,Context.MODE_PRIVATE);
    }

//    putBoolean function which takes a String key and boolean value and store it in the created shared preference
    public void putBoolean(String key, boolean value){

//        SharedPreferences.Editor is an Interface used to write data in the SP file
//        .edit() method is used to create new Editor for preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

//        storing the key value using the editor in SP file using putBoolean() method in editor
        editor.putBoolean(key,value);

//        .apply() method is used commit the changes made in SP file
        editor.apply();
    }

    public boolean getBoolean(String key){

//        getBoolean function is used to return the boolean value associate to the String key key
        return sharedPreferences.getBoolean(key,false);
    }

    public void putString(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public String getString(String key){
        return sharedPreferences.getString(key,null);
    }

    public void clear(){

//        clear() method is used to clear the SP file/memory
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}

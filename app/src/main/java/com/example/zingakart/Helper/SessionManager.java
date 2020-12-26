package com.example.zingakart.Helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import androidx.annotation.NonNull;

import com.example.zingakart.Activities.MainActivity;
import com.example.zingakart.Activities.SplashScreenActivity;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class SessionManager implements GoogleApiClient.OnConnectionFailedListener {
    private static final String PREF_NAME = "zingakart";
    private static final String IS_SKIP = "skip";
    private static final String IS_LOGIN = "is_login";
    private static final String IS_FACEBOOK_LOGIN = "fb_login";
    private static final String IS_GOOGLE_LOGIN = "google_login";
    private static final String KEY_STATE = "state";
    private static final String KEY_CITY = "city";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_ZIPCODE = "zipcode";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_LANKMARK = "landmark";
    private static final String KEY_CURRENT_ADDRESS = "currentAddress";
    SharedPreferences pref;
    Editor editor;
    Context context;
    int PRIVATE_MODE = 0;
    DatabaseHandler databaseHandler;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void logoutUser() {
        databaseHandler = new DatabaseHandler(context);
        if (isFacebookLoggedIn()) {
            LoginManager.getInstance().logOut();
            editor.clear();
            editor.commit();
            setLoggedInFacebook(false);
            databaseHandler.deleteUsers();
        } else if (isGoogleLoggedIn()) {
            editor.clear();
            editor.commit();
            setLoggedInGoogle(false);
            databaseHandler.deleteUsers();
        } else {
            editor.clear();
            editor.commit();
            setLoggedIn(false);
            databaseHandler.deleteUsers();
        }
        Intent i = new Intent(context, SplashScreenActivity.class);
        i.putExtra("type", "logout");
        context.startActivity(i);
    }

    public void logInUser() {
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, SplashScreenActivity.class);
        i.putExtra("type", "login");
        context.startActivity(i);
    }

    public void setLoggedInFacebook(boolean loggedInFacebook) {
        editor.putBoolean(IS_FACEBOOK_LOGIN, loggedInFacebook);
        editor.commit();
    }

    public void setLoggedInGoogle(boolean loggedInGoogle) {
        editor.putBoolean(IS_GOOGLE_LOGIN, loggedInGoogle);
        editor.commit();
    }

    public String getCurrentAddress() {
        return pref.getString(KEY_CURRENT_ADDRESS, null);
    }

    public void setCurrentAddress(String currentAddress) {
        editor.putString(KEY_CURRENT_ADDRESS, currentAddress);
        editor.commit();
    }

    public String getLongitude() {
        return pref.getString(KEY_LONGITUDE, null);
    }

    public void setLongitude(String longitude) {
        editor.putString(KEY_LONGITUDE, longitude);
        editor.commit();
    }

    public String getLatitude() {
        return pref.getString(KEY_LATITUDE, null);
    }

    public void setLatitude(String latitude) {
        editor.putString(KEY_LATITUDE, latitude);
        editor.commit();
    }

    public String getState() {
        return pref.getString(KEY_STATE, null);
    }

    public void setState(String state) {
        editor.putString(KEY_STATE, state);
        editor.commit();
    }

    public String getZipcode() {
        return pref.getString(KEY_ZIPCODE, null);
    }

    public void setZipcode(String zipcode) {
        editor.putString(KEY_ZIPCODE, zipcode);
        editor.commit();
    }

    public String getLandmark() {
        return pref.getString(KEY_LANKMARK, null);
    }

    public void setLandmark(String landmark) {
        editor.putString(KEY_LANKMARK, landmark);
        editor.commit();
    }

    public String getCountry() {
        return pref.getString(KEY_COUNTRY, null);
    }

    public void setCountry(String country) {
        editor.putString(KEY_COUNTRY, country);
        editor.commit();
    }

    public String getCity() {
        return pref.getString(KEY_CITY, null);
    }

    public void setCity(String city) {
        editor.putString(KEY_CITY, city);
        editor.commit();
    }

    public void skip() {
        editor.putBoolean(IS_SKIP, true);
        editor.commit();
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public boolean isSkipLogin() {
        return pref.getBoolean(IS_SKIP, false);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void setLoggedIn(boolean loggedIn) {
        editor.putBoolean(IS_LOGIN, loggedIn);
        editor.commit();
    }

    public boolean isFacebookLoggedIn() {
        return pref.getBoolean(IS_FACEBOOK_LOGIN, false);
    }

    public boolean isGoogleLoggedIn() {
        return pref.getBoolean(IS_GOOGLE_LOGIN, false);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

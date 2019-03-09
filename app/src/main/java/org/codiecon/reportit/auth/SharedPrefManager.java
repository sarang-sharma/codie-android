package org.codiecon.reportit.auth;
import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by admin on 14/02/17.
 */

public class SharedPrefManager {
    private static SharedPrefManager mInstance;

    private static Context mCtx;
    private static final String SHARED_PREF_NAME="mysharedpref12";
    private static final String KEY_USER_EMAIL="useremail";
    private static final String KEY_USER_TOKEN="usertoken";
    private static final String KEY_USER_ACCOUNTTYPE="useraccounttype";
    private static final String KEY_USER_ID="userID";
    private static final String KEY_Client_ID="clientID";
    private static final String KEY_Developer_ID="developerID";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_LOCATION = "userLocation";
    private static final String KEY_USER_MEDIA = "userMedia";



    //  private static final String KEY_PASSWORD="pass";

    private SharedPrefManager(Context context) {
        mCtx = context;

    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }


    public boolean isFirstTimeLaunch()
    {
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        if(sharedPreferences.getString(IS_FIRST_TIME_LAUNCH, null)!=null)
        {
            return true;
        }
        return false;


    }

    public boolean userWalkThrough(String userWalkToken)
    {
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putString(IS_FIRST_TIME_LAUNCH, userWalkToken);
        // editor.putString(KEY_PASSWORD,pass);

        editor.apply();
        return true;


    }

   public boolean userLogIn(String email)
   {
       SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
       SharedPreferences.Editor editor=sharedPreferences.edit();

       editor.putString(KEY_USER_EMAIL, email);
       // editor.putString(KEY_PASSWORD,pass);

       editor.apply();
       return true;


   }

    public boolean userToken(String token)
    {
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putString(KEY_USER_TOKEN, token);
        // editor.putString(KEY_PASSWORD,pass);

        editor.apply();
        return true;


    }

    public boolean userAccountType(String userAccountType)
    {
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putString(KEY_USER_ACCOUNTTYPE, userAccountType);
        // editor.putString(KEY_PASSWORD,pass);

        editor.apply();
        return true;


    }

    public boolean userID(String userID)
    {
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putString(KEY_USER_ID, userID);
        // editor.putString(KEY_PASSWORD,pass);

        editor.apply();
        return true;
        }


    public boolean userName(String userName)
    {
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putString(KEY_USER_NAME, userName);
        // editor.putString(KEY_PASSWORD,pass);

        editor.apply();
        return true;


    }



    public boolean userLocation(String userLocation)
    {
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putString(KEY_USER_LOCATION, userLocation);
        // editor.putString(KEY_PASSWORD,pass);

        editor.apply();
        return true;


    }



    public boolean userMedia(String userMedia)
    {
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putString(KEY_USER_MEDIA, userMedia);
        // editor.putString(KEY_PASSWORD,pass);

        editor.apply();
        return true;


    }


    public boolean developerID(String developerID)
    {
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putString(KEY_Developer_ID, developerID);
        // editor.putString(KEY_PASSWORD,pass);

        editor.apply();
        return true;


    }

    public boolean clientID(String clientID)
    {
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putString(KEY_Client_ID, clientID);
        // editor.putString(KEY_PASSWORD,pass);

        editor.apply();
        return true;


    }



    public boolean isLoggedIn()
    {
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        if(sharedPreferences.getString(KEY_USER_EMAIL, null)!=null)
        {
             return true;
        }
        return false;
    }


    public boolean logout()
    {
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.remove(KEY_USER_EMAIL);
        editor.apply();
        return true;
    }

    public String getUserEmail() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_EMAIL, null);

    }

    public String getUserName() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_NAME, null);

    }


    public String getUserToken() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_TOKEN, null);

    }

    public String getUserLocation() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_LOCATION, null);

    }

    public String getUserMedia() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_MEDIA, null);

    }

    public String getUserID() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_ID, null);

    }


    public String getClientID() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_Client_ID, null);

    }

    public String getDeveloperID() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_Developer_ID, null);

    }

}
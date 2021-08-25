package id.t12ue.kalimutu.pref;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;

import id.t12ue.kalimutu.model.User;
import id.t12ue.kalimutu.ui.activity.LoginActivity;

public class UserPref {
    private static final String SHARED_PREF_NAME = "userPref";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_ROLE = "role";


    private static UserPref mInstance;
    private static Context mCtx;

    private UserPref(Context context) {
        mCtx = context;
    }

    public static synchronized UserPref getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new UserPref(context);
        }
        return mInstance;
    }

    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ID, user.getId());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_ROLE, user.getRole());
        editor.apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ID, null) != null;
    }

    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString(KEY_ID, null),
                sharedPreferences.getString(KEY_NAME, null ),
                sharedPreferences.getString(KEY_ROLE, null )
        );
    }

    public void userLogout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(mCtx, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mCtx.startActivity(intent);
    }
}

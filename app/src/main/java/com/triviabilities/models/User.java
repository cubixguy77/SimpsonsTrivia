package com.triviabilities.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.triviabilities.MyApplication;
import com.triviabilities.R;

public class User {
    private static User user = null;
    private static SharedPreferences sharedPrefs;

    /* User Properties */
    private String userName;
    public String getUserName() { return this.userName; }
    String getUserNameURI() { return Uri.encode(this.userName, "utf-8"); }
    public void setUserName(String userName) {
        this.userName = userName;
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("UserName", userName);
        editor.apply();
    }
    public Boolean isRegisteredUser()
    {
        return userName != null && userName.length() > 0;
    }

    public static DataResult isValidUsername(String name)
    {
        if (name.length() < 3)
            return new DataResult(false, "Please enter at least 3 letters for your name.");

        if (name.length() > 20)
            return new DataResult(false, "Please keep the name under 20 letters.");

        for (int i=0; i<name.length(); i++) {
            char c = name.charAt(i);
            if (!Character.isDigit(c) && !Character.isLetter(c) && c != ' ')
                return new DataResult(false, "Name should only contain letters and numbers");
        }

        return new DataResult(true, "");
    }

    /* Instance Management */
    private User() {
        this.userName = sharedPrefs.getString("UserName", "");
    }

    public static User getInstance() {
        if (sharedPrefs == null)
        {
            sharedPrefs = MyApplication.getAppContext().getSharedPreferences(MyApplication.getAppContext().getResources().getString(R.string.shared_prefs_user), Context.MODE_PRIVATE);
        }

        if (user == null)
        {
            user = new User();
        }

        return user;
    }
}




















    /*
    public User(String userName)
    {
        this.userName = userName;
    }

    public User(Intent intent)
    {
        this(intent.getBundleExtra("UserBundle"));
    }

    public User(Bundle bundle)
    {
        this(bundle.getString("UserName"));
    }

    public Bundle getBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putString("UserName", this.getUserName());
        return bundle;
    }

}
*/
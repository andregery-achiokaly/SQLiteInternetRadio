package com.example.alexander_topilskii.internetradio.presenters.interfaces;


import android.app.Activity;

public interface LoginPresenterInterface {
    void login(final String name, final String password, final Activity activity);

    void checkAutinfication(Activity activity);
}

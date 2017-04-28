package com.example.alexander_topilskii.internetradio.presenters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.inputmethod.InputMethodManager;

import com.example.alexander_topilskii.internetradio.Application;
import com.example.alexander_topilskii.internetradio.models.rest.PostModel;
import com.example.alexander_topilskii.internetradio.models.rest.RestApi;
import com.example.alexander_topilskii.internetradio.presenters.interfaces.LoginPresenterInterface;
import com.example.alexander_topilskii.internetradio.ui.activitys.main.MainActivity;
import com.example.alexander_topilskii.internetradio.ui.interfaces.LoginActivityView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.alexander_topilskii.internetradio.models.rest.PostModel.ERROR_RESPONSE;
import static com.example.alexander_topilskii.internetradio.models.rest.PostModel.SUCCESS_RESPONSE;
import static com.example.alexander_topilskii.internetradio.models.rest.PostModel.WELCOME_RESPONSE;

public class LoginPresenter extends MvpBasePresenter<LoginActivityView> implements LoginPresenterInterface {
    private static final String USER_NAME = "USER_NAME";
    private static final String IS_AUTHORIZATION = "IS_AUTHORIZATION";
    private static final String LOGIN_CAN_T_BE_EMPTY = "Login can't be empty!";
    private static final String PASSWORD_CAN_T_BE_EMPTY = "Password can't be empty!";

    @Inject
    RestApi restApi;

    public LoginPresenter() {
        Application.getComponent().inject(this);
    }

    @Override
    public void login(final String name, final String password, final Activity activity) {
        if (activity.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }

        if (getView() != null) {
            if (name.isEmpty()) getView().authorizationResult(LOGIN_CAN_T_BE_EMPTY);
            else {
                if (password.isEmpty()) getView().authorizationResult(PASSWORD_CAN_T_BE_EMPTY);
                else {
                    sendLoginResponse(name, password, activity);
                }
            }
        }
    }

    private void sendLoginResponse(final String name, final String password, final Context context) {
        restApi.login(name, password).enqueue(new Callback<PostModel>() {
            @Override
            public void onResponse(Call<PostModel> call, Response<PostModel> response) {
                if (response.body() != null && response.body().getSuccess().equals(SUCCESS_RESPONSE)) {
                    saveUserAndOpenActivity(context, name);
                } else {
                    deleteUserAndShowError(context);
                }
            }

            @Override
            public void onFailure(Call<PostModel> call, Throwable t) {
                deleteUserAndShowError(context);
            }
        });
    }

    private void deleteUserAndShowError(final Context context) {
        saveUser(false, context, "");
        if (getView() != null) getView().authorizationResult(ERROR_RESPONSE);
    }

    private void saveUserAndOpenActivity(final Context context, final String name) {
        saveUser(true, context, name);
        if (getView() != null) getView().authorizationResult(WELCOME_RESPONSE);
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    private void saveUser(final boolean isAuthorization, final Context context, final String name) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(IS_AUTHORIZATION, isAuthorization);
        editor.putString(USER_NAME, name);
        editor.apply();
    }

    @Override
    public void checkAutinfication(final Activity activity) {
        boolean isAuthorization = getUser(IS_AUTHORIZATION, activity);
        if (isAuthorization) {
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
            activity.finish();
        }
    }

    private boolean getUser(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, false);
    }
}

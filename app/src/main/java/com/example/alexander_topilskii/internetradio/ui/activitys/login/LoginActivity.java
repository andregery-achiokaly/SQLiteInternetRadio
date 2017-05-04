package com.example.alexander_topilskii.internetradio.ui.activitys.login;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alexander_topilskii.internetradio.R;
import com.example.alexander_topilskii.internetradio.presenters.LoginPresenter;
import com.example.alexander_topilskii.internetradio.ui.interfaces.LoginActivityView;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends MvpActivity<LoginActivityView, LoginPresenter> implements LoginActivityView {
    @BindView(R.id.input_login_edittext)
    EditText loginEditText;

    @BindView(R.id.input_password)
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);
        getPresenter().checkAutinfication(this);
    }

    @OnClick(R.id.login_btn)
    public void login() {
        String name = loginEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        getPresenter().login(name, password, this);
    }

    @Override
    public void authorizationResult(String errorMessage) {
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public LoginPresenter createPresenter() {
        return new LoginPresenter();
    }
}

package com.example.alexander_topilskii.internetradio.ui.interfaces;


import com.hannesdorfmann.mosby.mvp.MvpView;

public interface LoginActivityView extends MvpView {
    void authorizationResult(String errorMessage);
}

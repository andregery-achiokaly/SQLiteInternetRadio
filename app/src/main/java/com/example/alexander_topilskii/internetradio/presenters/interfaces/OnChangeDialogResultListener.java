package com.example.alexander_topilskii.internetradio.presenters.interfaces;


public interface OnChangeDialogResultListener {
    void onShareResult(String name, String source);
    void onDeleteResult(int id);
    void onEditResult(int id, String name, String source);
}
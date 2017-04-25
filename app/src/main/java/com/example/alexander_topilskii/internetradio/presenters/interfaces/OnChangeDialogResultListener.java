package com.example.alexander_topilskii.internetradio.presenters.interfaces;


import com.example.alexander_topilskii.internetradio.models.database.NoStationsException;

public interface OnChangeDialogResultListener {
    void onShareResult(int id);
    void onDeleteResult(int id) throws NoStationsException;
    void onEditResult(int id, String name, String source);
}
package com.example.alexander_topilskii.internetradio.models.database.interfaces;


public interface DataBaseManager extends DataBase{
    void deleteListeners(DataBaseChangedListener dataBaseChangeListener, ResultListener resultListener);
    void addChangeListener(DataBaseChangedListener dataBaseChangeListener);
    void addResultListener(ResultListener resultListener);
}

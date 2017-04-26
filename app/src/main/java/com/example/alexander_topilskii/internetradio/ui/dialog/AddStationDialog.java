package com.example.alexander_topilskii.internetradio.ui.dialog;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alexander_topilskii.internetradio.R;
import com.example.alexander_topilskii.internetradio.presenters.interfaces.OnAddDialogResultListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddStationDialog extends DialogFragment {
    public static final String TITLE_ADD_STATION = "Add station";
    public static final String ERROR_FIELDS_CAN_T_BE_EMPTY = "Error: fields can't be empty";
    @BindView(R.id.station_name)
    EditText stationNameEditText;

    @BindView(R.id.station_source)
    EditText stationSourceEditText;

    OnAddDialogResultListener onAddDialogResultListener;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(TITLE_ADD_STATION);
        View view = inflater.inflate(R.layout.add_station_dialog, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.save_station_btn)
    void saveStation() {
        String name = stationNameEditText.getText().toString();
        String source = stationSourceEditText.getText().toString();

        if (name.isEmpty() || source.isEmpty())
            Toast.makeText(getContext(), ERROR_FIELDS_CAN_T_BE_EMPTY, Toast.LENGTH_SHORT).show();
        else {
            onAddDialogResultListener.onAddResult(name, source);
            dismiss();
        }
    }

    public void setOnAddDialogResultListener(OnAddDialogResultListener onAddDialogResultListener) {
        this.onAddDialogResultListener = onAddDialogResultListener;
    }

    public static AddStationDialog newInstance() {
        return new AddStationDialog();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onAddDialogResultListener = null;
    }
}
package com.example.alexander_topilskii.internetradio.ui.dialog;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alexander_topilskii.internetradio.R;
import com.example.alexander_topilskii.internetradio.presenters.interfaces.OnEditStationDialogResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditStationDialog extends DialogFragment {
    public static final String TITLE_ADD_STATION = "Edit station";
    public static final String ERROR_FIELDS_CAN_T_BE_EMPTY = "Error: fields can't be empty";
    public static final String TAG_NAME_STATION = "TAG_NAME_STATION";
    public static final String TAG_SOURCE_STATION = "TAG_SOURCE_STATION";
    public static final String TAG_ID_STATION = "TAG_ID_STATION";
    @BindView(R.id.station_name)
    EditText stationNameEditText;

    @BindView(R.id.station_source)
    EditText stationSourceEditText;

    OnEditStationDialogResult onEditStationDialogResult;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(TITLE_ADD_STATION);
        View view = inflater.inflate(R.layout.add_station_dialog, container);
        ButterKnife.bind(this, view);
        stationNameEditText.setText(getArguments().getString(TAG_NAME_STATION));
        stationSourceEditText.setText(getArguments().getString(TAG_SOURCE_STATION));
        return view;
    }

    @OnClick(R.id.save_station_btn)
    void saveStation() {
        String name = stationNameEditText.getText().toString();
        String source = stationSourceEditText.getText().toString();

        if (name.isEmpty() || source.isEmpty())
            Toast.makeText(getContext(), ERROR_FIELDS_CAN_T_BE_EMPTY, Toast.LENGTH_SHORT).show();
        else {
            onEditStationDialogResult.onEditResult(getArguments().getInt(TAG_ID_STATION), name, source);
            dismiss();
        }
    }

    public void setOnChangeDialogResultListener(OnEditStationDialogResult onAddDialogResultListener) {
        this.onEditStationDialogResult = onAddDialogResultListener;
    }

    public static EditStationDialog newInstance(int id, String name, String source) {
        EditStationDialog fragment = new EditStationDialog();
        Bundle args = new Bundle();
        args.putInt(TAG_ID_STATION, id);
        args.putString(TAG_NAME_STATION, name);
        args.putString(TAG_SOURCE_STATION, source);
        fragment.setArguments(args);
        return fragment;
    }
}
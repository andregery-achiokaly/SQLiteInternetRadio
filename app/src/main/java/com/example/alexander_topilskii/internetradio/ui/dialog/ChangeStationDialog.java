package com.example.alexander_topilskii.internetradio.ui.dialog;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.alexander_topilskii.internetradio.R;
import com.example.alexander_topilskii.internetradio.presenters.interfaces.OnChangeDialogResultListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeStationDialog extends DialogFragment {
    public static final String TAG_STATION_ID = "STATION_ID";
    public static final String TAG_STATION_NAME = "STATION_NAME";
    public static final String TAG_STATION_SOURCE = "STATION_SOURCE";
    public static final String TITLE_STATION_DIALOG = "Station dialog";

    @BindView(R.id.editButton)
    protected Button editButton;

    @BindView(R.id.deleteButton)
    protected Button deleteButton;

    @BindView(R.id.shareButton)
    protected Button shareButton;

    OnChangeDialogResultListener onDialogResultListener;

    public static ChangeStationDialog newInstance(int id, String name, String source) {
        ChangeStationDialog f = new ChangeStationDialog();
        Bundle args = new Bundle();
        args.putInt(TAG_STATION_ID, id);
        args.putString(TAG_STATION_NAME, name);
        args.putString(TAG_STATION_SOURCE, source);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.station_dialog, container, false);
        ButterKnife.bind(this, view);
        setupTitle();
        editButton.setOnClickListener(v -> {
            onDialogResultListener.onEditResult(
                    getArguments().getInt(TAG_STATION_ID),
                    getArguments().getString(TAG_STATION_NAME),
                    getArguments().getString(TAG_STATION_SOURCE));
            dismiss();
        });

        deleteButton.setOnClickListener(v -> {
            onDialogResultListener.onDeleteResult(getArguments().getInt(TAG_STATION_ID));
            dismiss();
        });

        shareButton.setOnClickListener(v -> {
            onDialogResultListener.onShareResult(getArguments().getInt(TAG_STATION_ID));
            dismiss();
        });
        return view;
    }

    private void setupTitle() {
        this.getDialog().setTitle(TITLE_STATION_DIALOG);
        TextView tv = (TextView) this.getDialog().findViewById(android.R.id.title);
        if (tv != null) {
            tv.setSingleLine(false);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onDialogResultListener = null;
    }

    public void setOnDialogResultListener(OnChangeDialogResultListener listener) {
        this.onDialogResultListener = listener;
    }
}

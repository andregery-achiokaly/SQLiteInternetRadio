package com.example.alexander_topilskii.internetradio.ui.activitys.main;


import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class MainPermissionActivity extends MainActivity {
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(MainPermissionActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            presenter.setCanShowVizualizer(false);
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                ActivityCompat.requestPermissions(MainPermissionActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, WAVE_VIEW_RECORD_AUDIO_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, WAVE_VIEW_RECORD_AUDIO_PERMISSION);
            }
        } else presenter.setCanShowVizualizer(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WAVE_VIEW_RECORD_AUDIO_PERMISSION) {
                checkPermission();
        }
    }
}

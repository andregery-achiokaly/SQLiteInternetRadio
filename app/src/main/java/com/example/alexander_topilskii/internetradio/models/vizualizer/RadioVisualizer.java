package com.example.alexander_topilskii.internetradio.models.vizualizer;

import android.media.audiofx.Visualizer;

public class RadioVisualizer {
    private Visualizer visualizer;
    private VisualizerListener visualizerListener;
    private boolean canShow = false;

    public void setupVisualizerFxAndUI(int id, VisualizerListener visualizerListener) {
        this.visualizerListener = visualizerListener;
        if (id != -1) {
            if (visualizer != null) visualizer.release();
            visualizer = new Visualizer(id);
            visualizer.setEnabled(false);
            visualizer.setCaptureSize(16);
            visualizer.setDataCaptureListener(new AudioWaveDataCaptureListener(),
                    Visualizer.getMaxCaptureRate() / 2, true, false);
            visualizer.setEnabled(true);
        }
    }

    public void stop() {
        visualizerListener = null;
        if (visualizer != null) {
            visualizer.setEnabled(false);
            visualizer.release();
        }
    }

    public void setCanShow(boolean canShow) {
        this.canShow = canShow;
    }

    private class AudioWaveDataCaptureListener implements Visualizer.OnDataCaptureListener {
        public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
            if (visualizerListener != null && canShow) visualizerListener.updateVisualizer(bytes);
        }

        @Override
        public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int i) {

        }
    }
}

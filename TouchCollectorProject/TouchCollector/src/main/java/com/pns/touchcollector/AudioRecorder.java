/*
 * The application needs to have the permission to write to external storage
 * if the output file is written to the external storage, and also the
 * permission to record audio. These permissions must be set in the
 * application's AndroidManifest.xml file, with something like:
 *
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.RECORD_AUDIO" />
 *
 */
package com.pns.touchcollector;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;

import com.pns.touchcollector.InputCollection.DataCollector;

/** Record audio from the MIC audio source. Procedure:
 *
 *      AudioRecorder ar = new AudioRecorder();
 *      ar.startRecording();
 *      ar.stopRecording();
 *      String recordingFilename = ar.getRecording();
 *
 *      ar = new AudioRecorder();
 *      ...
 *
 *      */
public class AudioRecorder implements DataCollector<String> {
    private static final String LOG_TAG = "AudioRecorder";
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private String mFileName = null;
    private MediaRecorder mRecorder;

    private boolean recordingNow;
    private boolean startedRecording;

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    public void startRecording() {
        if (startedRecording)
            throw new IllegalStateException("This instance already made a recording.");
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed", e);
        }

        mRecorder.start();
        startedRecording = true;
        recordingNow = true;
    }

    public void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        recordingNow = false;
    }

    public AudioRecorder() {
        final long lNow = System.currentTimeMillis() / 1000L;
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/microphone_recording-" + AUDIO_SOURCE + "-" + lNow + ".3gp";
    }

    public String getRecording() {
        if (recordingNow) throw new IllegalStateException("Still recording audio");
        return mFileName;
    }

    public String getData() {
        return getRecording();
    }
}

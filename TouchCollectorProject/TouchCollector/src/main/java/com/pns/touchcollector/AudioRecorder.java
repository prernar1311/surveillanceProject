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
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.IOException;


public class AudioRecorder  {
    private static final String LOG_TAG = "AudioRecorder";
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private String mFileName = null;

    private boolean recording;
    private boolean startedRecording;

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
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

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        recordingNow = false;
    }

/*
 *    class RecordButton extends Button {
 *        boolean mStartRecording = true;
 *
 *        OnClickListener clicker = new OnClickListener() {
 *            public void onClick(View v) {
 *                onRecord(mStartRecording);
 *                if (mStartRecording) {
 *                    setText("Stop recording");
 *                } else {
 *                    setText("Start recording");
 *                }
 *                mStartRecording = !mStartRecording;
 *            }
 *        };
 *
 *        public RecordButton(Context ctx) {
 *            super(ctx);
 *            setText("Start recording");
 *            setOnClickListener(clicker);
 *        }
 *    }
 */

    public AudioRecorder() {
        final long lNow = System.currentTimeMillis() / 1000L;
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/microphone_recording-" + AUDIO_SOURCE + "-" + lNow + ".3gp";
    }

    public String getRecording() {
        if (recordingNow) throw new StillRecordingException();
        return mFileName;
    }

    public void onPause() {
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }

    public class StillRecordingException extends Exception {}
}

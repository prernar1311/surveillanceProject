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

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;

import com.pns.touchcollector.DataCollection.DataCollector;
import com.pns.touchcollector.DataCollection.SensorUnavailableException;

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
    private static final String FORMAT = ".aac";
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static AudioRecorder instance;
    private String baseFilename = null;
    private MediaRecorder mRecorder;

    private boolean recordingNow;
    private boolean startedRecording;

    private static int instances;

    private AudioRecorder() {
        baseFilename = Environment.getExternalStorageDirectory().getAbsolutePath();
        baseFilename += "/microphone_recording-" + AUDIO_SOURCE;
        instances++;
    }

    public static AudioRecorder getInstance() {
        if (instance == null) instance = new AudioRecorder();
        return instance;
    }

    public void startRecording(String s) throws SensorUnavailableException {
        if (recordingNow) throw new IllegalStateException("Already recording from microphone.");
        final long lNow = System.currentTimeMillis();
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mRecorder.setOutputFile(baseFilename + lNow + s + FORMAT);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
        //mRecorder.setAudioEncodingBitRate(BITRATE);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "MediaRecorder prepare() failed", e);
            return;
        }

        Log.i(LOG_TAG, "MediaRecorder succesfully prepared.");

        try {
            mRecorder.start();
        } catch (RuntimeException e) {
            String message = "MediaRecorder wouldn't start with " + instances + " instances.";
            throw new SensorUnavailableException(message, e);
        } finally {
            startedRecording = true;
            recordingNow = true;
        }
    }

    public void stopRecording() throws SensorUnavailableException {
        if (!recordingNow) {
            throw new IllegalStateException("Trying to stop recording, but was never started!");
        }
        try {
            mRecorder.stop();
        } catch (RuntimeException e) {
            String message = "MediaRecorder wouldn't stop with " + instances + " instances.";
            throw new SensorUnavailableException(message, e);
        } finally {
            mRecorder.release();
            mRecorder = null;
            recordingNow = false;
        }
    }

    public String getRecording() {
        if (recordingNow) throw new IllegalStateException("Still recording audio");
        return baseFilename;
    }

    public String getData() {
        return getRecording();
    }
}

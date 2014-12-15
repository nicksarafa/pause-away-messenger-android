package com.pauselabs.pause.listeners;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.services.PauseApplicationService;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by Passa on 12/12/14.
 */
public class SpeechListener implements RecognitionListener {

    private final String TAG = this.getClass().getSimpleName();

    @Inject AudioManager am;

    public static String lastResult;

    @Override
    public void onReadyForSpeech(Bundle params) {
//        am.setStreamMute(AudioManager.STREAM_MUSIC, true);


    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
//        PauseApplication.sr.startListening(getNewSpeechIntent());
    }

    @Override
    public void onError(int error) {
//        if ((error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT || error == SpeechRecognizer.ERROR_NO_MATCH))
//            PauseApplication.sr.startListening(getNewSpeechIntent());
//        else
//            Log.i(TAG,"error: " + error);

    }

    @Override
    public void onResults(Bundle results) {
        ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        lastResult = (String) data.get(0);

        Log.i(TAG, (String) data.get(0));

//        PauseApplication.sr.startListening(getNewSpeechIntent());
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    public static Intent getNewSpeechIntent() {
        Intent recognizeSpeechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizeSpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizeSpeechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
        recognizeSpeechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        return recognizeSpeechIntent;
    }
}

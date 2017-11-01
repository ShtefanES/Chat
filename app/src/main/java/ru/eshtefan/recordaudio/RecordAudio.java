package ru.eshtefan.recordaudio;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by eshtefan on 13.10.2017.
 */

public class RecordAudio extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //позволяет кешировать данные Firebase Realtime Database, что позволяет работать в offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

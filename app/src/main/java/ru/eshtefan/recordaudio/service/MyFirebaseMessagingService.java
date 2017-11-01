package ru.eshtefan.recordaudio.service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * MyFirebaseMessagingService сервис для взаимодействия с Firebase Messaging. Предоставляет функционал для автоматического отображения уведомлений.
 * Created by eshtefan on 06.10.2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String TAG = getClass().getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Проверка если сообщение является уведомлением
        if (remoteMessage.getNotification() != null) {
            Log.w(TAG, "From: " + remoteMessage.getFrom());
            Log.w(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        }
    }
}

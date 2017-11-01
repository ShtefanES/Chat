package ru.eshtefan.recordaudio.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import ru.eshtefan.recordaudio.dbLayer.Users;

/**
 * MyFirebaseInstanceIDService сервис для работы с Firebase Instance ID token при обнвлении(приложение удалило  Instance ID, приложение было восстановлено на новом устройстве, пользователь переустановил приложение, пользователь очистил данные приложения).
 * Created by eshtefan on 06.10.2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private final String LOG = getClass().getSimpleName();

    @Override
    public void onTokenRefresh() {
        // Получение обновленного InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.w(LOG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Передает обновленный FCM InstanceID token в Firebase Realtime Database.
     *
     * @param token новый FCM InstanceID token.
     */
    private void sendRegistrationToServer(String token) {
        Users users = new Users();
        users.updateUser(users.getCurrentUser(), token);
    }
}
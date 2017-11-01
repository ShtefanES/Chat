package ru.eshtefan.recordaudio.dbLayer;

import ru.eshtefan.recordaudio.commonData.model.dbModel.User;

/**
 * Интерфейс UserCallback предоставляет callback для для выполнения кода после получения объекта класса User из Firebase Realtime Database.
 * Created by eshtefan on 09.10.2017.
 */

public interface UserCallback {
    /**
     * Callback на получение объекта класса User из Firebase Realtime Database.
     *
     * @param user класс модель учетной записи пользователя.
     */
    void onUserGot(User user);
}

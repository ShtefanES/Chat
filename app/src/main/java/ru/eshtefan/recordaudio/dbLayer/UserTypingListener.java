package ru.eshtefan.recordaudio.dbLayer;

import ru.eshtefan.recordaudio.commonData.model.dbModel.User;

/**
 * Интерфейс UserTypingListener предоставляет callback для выполнения кода если наблюдаемый объект класса User изменил состояния набора текста.
 * Created by eshtefan on 17.10.2017.
 */

public interface UserTypingListener {
    /**
     * Callback на изменения состояния boolean is_typing в Firebase Realtime Database для соответствующего JSON объекта.
     *
     * @param user     объект класс модели учетной записи пользователя, используется для нахождения соответствующего JSON объекта в Firebase Realtime Database.
     * @param newState новое состояние, которое заменит старое состояние в Firebase Realtime Database.
     */
    void onChangedStTyping(User user, boolean newState);
}

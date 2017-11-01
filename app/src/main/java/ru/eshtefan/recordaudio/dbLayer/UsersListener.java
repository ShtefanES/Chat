package ru.eshtefan.recordaudio.dbLayer;

import ru.eshtefan.recordaudio.commonData.model.dbModel.User;

/**
 * Интерфейс UsersListener предоставляет callback для выполнения кода если произошло добавление нового пользователя в Firebase Realtime Database(при получении данных с бд вызывется как минимум один раз).
 * Created by eshtefan on 17.10.2017.
 */

public interface UsersListener {
    /**
     * Callback на  добавление нового пользователя в Firebase Realtime Database(при получении данных с бд вызывется как минимум один раз).
     * @param user объект класс модели учетной записи пользователя, полученнный из Firebase Realtime Database.
     */
    void onAddedUser(User user);

}

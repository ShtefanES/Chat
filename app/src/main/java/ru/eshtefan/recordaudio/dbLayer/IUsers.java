package ru.eshtefan.recordaudio.dbLayer;

import ru.eshtefan.recordaudio.commonData.model.dbModel.User;

/**
 * Интерфейс IUsers предоставляет операции для взаимодействия экзмляров класса User с Firebase Realtime Database.
 * Created by eshtefan on 09.10.2017.
 */

public interface IUsers {
    /**
     * Используя уникальный id пользователя получает соответствующий объект класса User из Firebase Realtime Database, затем  вызывает callback UserCallback для взаимодействия с объектом класаа User.
     *
     * @param userId       уникальный идентификатор пользователя.
     * @param userCallback предоставляет callback для выполнения кода после создания объекта класса User.
     */
    void getUser(String userId, UserCallback userCallback);

    /**
     * Устанавливает наблюдателя UserTypingListener для объекта класса User.
     *
     * @param user               объект класс модели учетной записи пользователя используемый, предмет наблюдения.
     * @param userTypingListener предоставляет callback для выполнения кода если наблюдаемый объект класса User изменил состояния набора текста.
     */
    void setUserListener(User user, UserTypingListener userTypingListener);

    /**
     * Устанавливает наблюдателя UsersListener на изменения в Firebase Realtime Database по сслыке refUsers.
     *
     * @param usersListener предоставляет callback для выполнения кода если произошло добавление нового пользователя в Firebase Realtime Database(при получении данных с бд вызывется как минимум один раз).
     * @param refUsers      ссылка в Firebase Realtime Database на JSON объект пользователя.
     */
    void setUsersListener(UsersListener usersListener, String refUsers);

    /**
     * Возвращает объект классса User который содержит учетный данные текущего авторизованного пользователя.
     *
     * @return объект класс модели учетной записи пользователя.
     */
    User getCurrentUser();

    /**
     * Добавлет объект класса User в Firebase Realtime Database.
     *
     * @param user объект класс модели учетной записи пользователя.
     */
    void addUser(User user);

    /**
     * Обновлет строку notification_token в Firebase Realtime Database для соответствующего JSON объекта.
     *
     * @param user                 объект класс модели учетной записи пользователя, используется для нахождения соответствующего JSON объекта в Firebase Realtime Database.
     * @param newNotificationToken новый notificationToken, который замен старый токен в Firebase Realtime Database.
     */
    void updateUser(User user, String newNotificationToken);

    /**
     * Обновляет boolean is_typing в Firebase Realtime Database для соответствующего JSON объекта.
     *
     * @param user           объект класс модели учетной записи пользователя, используется для нахождения соответствующего JSON объекта в Firebase Realtime Database.
     * @param newStateTyping новое состояние, которое заменит старое состояние в Firebase Realtime Database.
     */
    void updateUser(User user, boolean newStateTyping);
}

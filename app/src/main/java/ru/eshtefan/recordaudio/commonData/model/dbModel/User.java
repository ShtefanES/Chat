package ru.eshtefan.recordaudio.commonData.model.dbModel;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

/**
 * User - класс модель учетной записи пользователя используемый для парснга данных с базы данных.
 * Created by eshtefan on 09.10.2017.
 */

public class User {

    private String name;
    private String email;
    @PropertyName("notification_token")
    public String notificationToken;
    //isTyping - отражает состояния набора текста данным пользователем в данный момент времени
    @PropertyName("is_typing")
    public boolean isTyping;

    private String userId;

    /**
     * Конструктор по умолчанию, явное указание необходимо для корректного парсинга из Firebase Realtime Database.
     */
    public User() {
    }

    /**
     * Конструктор с параметрами для инициализации данных.
     *
     * @param userId            уникальный идентификатор пользователя.
     * @param name              имя пользователя.
     * @param email             электронная почта пользователя.
     * @param notificationToken уникальный токен, необходимый для отправки пуш-уведомлений на устройство.
     */
    public User(String userId, String name, String email, String notificationToken) {
        this.name = name;
        this.email = email;
        this.notificationToken = notificationToken;
        this.userId = userId;
        isTyping = false;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    //аннотация Exclude исулючает поле userId при отправки объекта User в Firebase Realtime Database.
    @Exclude
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

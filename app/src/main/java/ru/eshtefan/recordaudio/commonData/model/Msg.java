package ru.eshtefan.recordaudio.commonData.model;

import ru.eshtefan.recordaudio.commonData.model.dbModel.Message;
import ru.eshtefan.recordaudio.commonData.model.dbModel.User;

/**
 * Msg - класс модель предметной области сообщения.
 * Created by eshtefan on 10.10.2017.
 */

public class Msg extends Message {

    private User user;

    /**
     * Конструктор с параметрами для инициализации данных.
     *
     * @param message объект класс модели сообщения.
     * @param user    объект класс модели учетной записи пользователя.
     */
    public Msg(Message message, User user) {
        setParentFields(message);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    /**
     * Инициализирует поля родительского класса.
     *
     * @param message объект класс модели сообщения.
     */
    private void setParentFields(Message message) {
        setPayload(message.getPayload());
        userId = message.userId;
        setType(message.getType());
        setTimestamp(message.getTimestamp());
    }
}

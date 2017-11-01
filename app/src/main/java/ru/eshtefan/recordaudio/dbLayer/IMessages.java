package ru.eshtefan.recordaudio.dbLayer;

import ru.eshtefan.recordaudio.commonData.model.dbModel.Message;

/**
 * Интерфейс IMessages предоставляет операции для взаимодействия сообщений(Message) с Firebase Realtime Database.
 * Created by eshtefan on 01.10.2017.
 */

public interface IMessages {

    /**
     * Добавлет объект Message в Firebase Realtime Database.
     *
     * @param message объект класс модели сообщения используемый для парснга данных с базы данных.
     */
    void addMessage(Message message);
}

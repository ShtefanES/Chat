package ru.eshtefan.recordaudio.dbLayer;

import ru.eshtefan.recordaudio.commonData.model.dbModel.Message;

/**
 * Интерфейс IMsgs предоставляет операции для взаимодействия сообщений(Msg) с Firebase Realtime Database.
 * Created by eshtefan on 10.10.2017.
 */

public interface IMsgs {

    /**
     * Используя данные в объекте класса Message создает экземпляр класса  Msg и вызывает callback MsgCallback для взаимодействия с объектом класаа Msg.
     *
     * @param message     объект класс модели сообщения используемый для парснга данных с базы данных.
     * @param msgCallback предоставляет callback для выполнения кода после создания объекта класса Msg.
     */
    void getMsg(Message message, MsgCallback msgCallback);

}

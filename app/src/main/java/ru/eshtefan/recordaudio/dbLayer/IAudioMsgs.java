package ru.eshtefan.recordaudio.dbLayer;

import ru.eshtefan.recordaudio.commonData.model.Msg;
import ru.eshtefan.recordaudio.commonData.model.dbModel.AudioMessage;
import ru.eshtefan.recordaudio.commonData.model.dbModel.Message;

/**
 * Интерфейс IAudioMsgs предоставляет операции для взаимодействия аудио-сообщений(AudioMsg) с Firebase Realtime Database.
 * Created by eshtefan on 01.10.2017.
 */

public interface IAudioMsgs {

    /**
     * Получает AudioMsg из Firebase Realtime Database.
     *
     * @param msg              объект модели предметной области сообщения.
     * @param audioMsgCallback предоставляет callback для для выполнения кода после получения аудио-сообщения из Firebase Realtime Database.
     */
    void getAudioMsg(Msg msg, AudioMsgCallback audioMsgCallback);

    /**
     * По соответствующей ссылке в Firebase Realtime Database генерирует пустой объект с уникальным ключом, затем возвращает данный ключ.
     *
     * @param refAudioMessages ссылка в Firebase Realtime Database на JSON объект аудио-сообщения.
     * @return уникальный id подобъекта аудио-сообщения(конкретный объект аудио-сообщения).
     */
    String getAudioMessageKey(String refAudioMessages);

    /**
     * Добавлет AudioMsg в Firebase Realtime Database(записывет объекты audioMessage и message в базу данных).
     *
     * @param audioMessage объект класс модели аудио-сообщения используемый для парснга данных с базы данных.
     * @param message      объект класс модели сообщения используемый для парснга данных с базы данных.
     */
    void addAudioMsg(AudioMessage audioMessage, Message message);
}
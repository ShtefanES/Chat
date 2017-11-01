package ru.eshtefan.recordaudio.dbLayer;

import ru.eshtefan.recordaudio.commonData.model.AudioMsg;

/**
 * Интерфейс AudioMsgCallback предоставляет callback для для выполнения кода после получения аудио-сообщения из Firebase Realtime Database.
 * Created by eshtefan on 01.10.2017.
 */

public interface AudioMsgCallback {

    /**
     * Callback на получение аудио-сообщения из Firebase Realtime Database.
     *
     * @param audioMsg объект предметной области аудио-сообщения.
     */
    void onAudioMsgGot(AudioMsg audioMsg);
}

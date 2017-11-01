package ru.eshtefan.recordaudio.commonData.model;

import ru.eshtefan.recordaudio.commonData.model.dbModel.AudioMessage;

/**
 * AudioMsg - класс модель предметной области аудио-сообщения.
 * Created by eshtefan on 01.10.2017.
 */

public class AudioMsg extends Msg {

    private AudioMessage audioMessage;

    /**
     * Конструктор с параметрами для инициализации данных.
     *
     * @param msg          объект предметной области сообщения.
     * @param audioMessage объект класс модели аудио-сообщения.
     */
    public AudioMsg(Msg msg, AudioMessage audioMessage) {
        super(msg, msg.getUser());

        this.audioMessage = audioMessage;
    }

    public AudioMessage getAudioMessage() {
        return audioMessage;
    }
}
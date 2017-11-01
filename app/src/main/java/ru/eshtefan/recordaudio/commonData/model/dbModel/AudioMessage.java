package ru.eshtefan.recordaudio.commonData.model.dbModel;

import com.google.firebase.database.PropertyName;

/**
 * AudioMessage - класс модель аудио-сообщения используемый для парснга данных с базы данных.
 * Created by eshtefan on 18.09.2017.
 */

public class AudioMessage {

    private long duration;
    @PropertyName("audio_url")
    public String audioUrl;

    /**
     * Конструктор по умолчанию, явное указание необходимо для корректного парсинга из Firebase Realtime Database.
     */
    public AudioMessage() {

    }

    /**
     * Конструктор с параметрами для инициализации данных.
     *
     * @param audioUrl строка, содержащая url аудио-файла хранящегося в Firebase Storage.
     * @param duration длительность аудиозаписи в миллисекундах.
     */
    public AudioMessage(String audioUrl, long duration) {
        this.audioUrl = audioUrl;
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}

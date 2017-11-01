package ru.eshtefan.recordaudio.commonData.model.dbModel;

import android.support.annotation.IntDef;
import android.util.Log;

import com.google.firebase.database.PropertyName;

import java.util.Calendar;

/**
 * Message - класс модель сообщения используемый для парснга данных с базы данных.
 * Created by eshtefan on 19.09.2017.
 */

public class Message {

    private String payload;
    private int type;
    @PropertyName("user_id")
    public String userId;
    //timestamp - unix timestamp в миллисекундах отправки сообщения
    private long timestamp;

    /**
     * TYPE_TEXT_MSG - текстовое сообщение
     * TYPE_AUDIO_MSG - аудио-сообщение
     */
    @IntDef({MessageType.TYPE_TEXT_MSG, MessageType.TYPE_AUDIO_MSG})
    public @interface MessageType {
        int TYPE_TEXT_MSG = 1;
        int TYPE_AUDIO_MSG = 2;
    }

    /**
     * Конструктор с параметрами для инициализации данных.
     *
     * @param payload строка, содержащая текст сообщения для тестового сообщения или id аудио-сообщения в базе данных.
     * @param userId  уникальный идентификатор отправителя сообщения.
     * @param type    тип сообщения.
     */
    public Message(String payload, String userId, int type) {
        this.payload = payload;
        this.userId = userId;
        timestamp = Calendar.getInstance().getTimeInMillis();
        this.type = type;
    }

    /**
     * Конструктор по умолчанию, явное указание необходимо для корректного парсинга из Firebase Realtime Database.
     */
    public Message() {
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

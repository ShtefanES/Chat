package ru.eshtefan.recordaudio.dbLayer;

import ru.eshtefan.recordaudio.commonData.model.Msg;

/**
 * Интерфейс MsgCallback предоставляет callback для для выполнения кода после создания объекта класса Msg.
 * Created by eshtefan on 10.10.2017.
 */

public interface MsgCallback {
    /**
     * Callback на создания объекта класса Msg.
     *
     * @param msg
     */
    void onCreatingMsg(Msg msg);
}

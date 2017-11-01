package ru.eshtefan.recordaudio.handler;

/**
 * Интерфейс FabSrcObserver предоставляет callback-и для выполнения кода при смене drawable в объекте Floating Action Button, который отвечает за отправку текста и записи аудиофайла.
 * Created by eshtefan on 29.09.2017.
 */

public interface FabSrcObserver {
    /**
     * Callback если id drawable в Floating Action Button равен id drawable отправки текстового сообщения.
     */
    void onResIdEqualsSend();

    /**
     * Callback если id drawable в Floating Action Button равен id drawable записи аудио.
     */
    void onResIdEqualsRecord();
}

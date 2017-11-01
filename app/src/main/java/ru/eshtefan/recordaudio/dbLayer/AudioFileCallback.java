package ru.eshtefan.recordaudio.dbLayer;

/**
 * Интерфейс AudioFileCallback предоставляет callback для выполнения кода после загрузки аудиофайла в Firebase Storage.
 * Created by eshtefan on 02.10.2017.
 */

public interface AudioFileCallback {

    /**
     * Callback на успешную загрузку аудиофала в Firebase Storage.
     *
     * @param uri      audioUrl строка, содержащая url аудио-файла хранящегося в Firebase Storage.
     * @param duration длительность аудиозаписи в миллисекундах.
     */
    void OnSuccessUpload(String uri, long duration);
}

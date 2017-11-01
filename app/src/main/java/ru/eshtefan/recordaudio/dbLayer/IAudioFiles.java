package ru.eshtefan.recordaudio.dbLayer;

/**
 * Интерфейс IAudioFiles предоставляет операции для взаимодействия с Firebase Storage.
 * Created by eshtefan on 02.10.2017.
 */

public interface IAudioFiles {

    /**
     * Загружает аудилофайл в Firebase Storage.
     *
     * @param fullFilePath      путь до аудиофайла в локальном хранилище данных.
     * @param duration          длительность аудиозаписи в миллисекундах.
     * @param audioFileCallback предоставляет callback для выполнения кода после загрузки аудиофайла в Firebase Storage.
     */
    void uploadFile(String fullFilePath, long duration, AudioFileCallback audioFileCallback);
}

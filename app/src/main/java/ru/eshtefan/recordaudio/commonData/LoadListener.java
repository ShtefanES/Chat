package ru.eshtefan.recordaudio.commonData;

/**
 * Интерфейс LoadListener предоставляет методы, которые должны быть реализованы при выполнении долгих операция с сетью.
 * Created by eshtefan on 05.10.2017.
 */

public interface LoadListener {
    /**
     * callback вызовется при старте выполнения длительной операции.
     */
    void onStartLoad();

    /**
     * callback вызовется при ззавершении выполнения длительной операции.
     */
    void onFinishLoad();

    /**
     * callback вызовется при возникновении ошибки во время выполнения длительной операции.
     */
    void onFailedLoad();
}

package ru.eshtefan.recordaudio.handler;

/**
 * Интерфейс FabSrcObservation необходим для объекта надблюдения, предоставляет методы регистрции/отмены регистрации наблюдателей и сам метод уведомления наблюдателей.
 * Created by eshtefan on 29.09.2017.
 */

public interface FabSrcObservation {
    /**
     * Регистрирует наблюдателя.
     * @param fabSrcObserver объект наблюдателя, который подписывается на уведомления.
     */
    void registerObserver(FabSrcObserver fabSrcObserver);

    /**
     * Отменяетподписки на уведомления.
     * @param fabSrcObserver объект наблюдателя, который отписывается от уведомлений.
     */
    void removeObserver(FabSrcObserver fabSrcObserver);

    /**
     * Уведомлет наблюдателей о новом id drawable.
     * @param drawableId id drawable в Floating Action Button, отвечающей за отправку текста и запись аудио.
     */
    void notifyObservers(int drawableId);

}

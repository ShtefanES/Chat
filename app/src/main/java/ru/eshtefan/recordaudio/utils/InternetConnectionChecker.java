package ru.eshtefan.recordaudio.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * InternetConnectionChecker отвечает за проверку наличия подключения интернету.
 * Created by eshtefan on 15.10.2017.
 */

public class InternetConnectionChecker {
    /**
     * Интерфейс ConnectionListener предоставляет callback для выполнения кода в случае если при проверки подключения, подключение отсутствует.
     */
    public interface ConnectionListener {
        /**
         * Callback на отсутствие интернет-подключения.
         */
        void onOffline();
    }

    private Context mContext;
    private ConnectionListener connectionListener;

    /**
     * Конструктор инициализирует объект класса Context.
     *
     * @param mContext объект класса Context необходим для работы ConnectivityManager.
     */
    public InternetConnectionChecker(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Проверяет подключение, если нет подключение вызовет ссответствующий callback и вернет false, иначе вернет true.
     *
     * @return true если есть подключение, false при отсутствии подключения.
     */
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean checkResult = netInfo != null && netInfo.isConnectedOrConnecting();

        if (!checkResult && connectionListener != null) {
            connectionListener.onOffline();
            return false;
        }
        return true;
    }

    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }
}

package ru.eshtefan.recordaudio.handler;

import android.util.Log;

import ru.eshtefan.recordaudio.commonData.model.dbModel.User;
import ru.eshtefan.recordaudio.dbLayer.UserTypingListener;
import ru.eshtefan.recordaudio.dbLayer.Users;
import ru.eshtefan.recordaudio.dbLayer.UsersListener;
import ru.eshtefan.recordaudio.utils.FBReferences;

/**
 * TypingHandler предоставлет методы для работы с индикацией набора текста данным пользователем/собеседниками.
 * Created by eshtefan on 17.10.2017.
 */

public class TypingHandler {
    /**
     * Интерфейс TypingListener предоставляет callback-и для выполнения кода при старте набора текста/при завершении набора текста.
     */
    public interface TypingListener {
        /**
         * Callback на набор текст пользователей, имя которого передается в параметре.
         *
         * @param name имя пользователя, которвй начал набирать текст.
         */
        void onStartTyping(String name);

        /**
         * Callback на завершение набора текста.
         */
        void onStopTyping();
    }

    private final String LOG = getClass().getSimpleName();
    private TypingListener typingListener;

    /**
     * Конструктор добавлет слушателей в Firebase Realtime Database.
     */
    public TypingHandler() {
        addListeners();
    }

    /**
     * Добавляет слушателей на изменение поля isTyping  у всех пользователей в Firebase Realtime Database за исключением данного пользователя.
     */
    private void addListeners() {
        final Users users = new Users();
        final String currentUserId = users.getCurrentUser().getUserId();
        users.setUsersListener(new UsersListener() {
            @Override
            public void onAddedUser(User user) {
                if (!user.getUserId().equals(currentUserId)) {
                    //   Log.w(LOG, "id = " + user.getUserId());
                    users.setUserListener(user, new UserTypingListener() {
                        @Override
                        public void onChangedStTyping(User user, boolean newState) {
                            if (typingListener != null) {
                                if (newState) {
                                    typingListener.onStartTyping(user.getName());
                                } else {
                                    typingListener.onStopTyping();
                                }
                            }
                        }
                    });
                }
            }
        }, FBReferences.Database.REF_USERS);
    }

    public void setTypingListener(TypingListener typingListener) {
        this.typingListener = typingListener;
    }
}

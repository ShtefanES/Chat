package ru.eshtefan.recordaudio.utils;

/**
 * FBReferences предоставляет inner классы, которые содержат контстанты для части пути в url-ах Firebase Realtime Database и Firebase Storage.
 * Created by eshtefan on 27.09.2017.
 */

public class FBReferences {

    /**
     * Конструктор по умолчанию с модификатором доступа private, запрещает создавать экземпляры данного класса вне класса.
     */
    private FBReferences() {
    }

    /**
     * Storage inner класс  которые содержит контстанты для части пути в url-е Firebase Storage.
     */
    public static class Storage {

        public static final String CHILD_AUDIO = "Audio";
    }

    /**
     * Database inner класс  которые содержит контстанты для части пути в url-е Firebase Realtime Database.
     */
    public static class Database {
        public static final String REF_MESSAGES = "messages";
        public static final String REF_AUDIO_MESSAGES = "audio_messages";
        public static final String REF_USERS = "users";
        public static final String REF_USERS_CHILD_NOTIFICATION_TOKEN = "notification_token";
        public static final String REF_USERS_CHILD_IS_TYPING = "is_typing";
    }
}

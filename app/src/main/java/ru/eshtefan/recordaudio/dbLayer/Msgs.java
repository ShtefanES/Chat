package ru.eshtefan.recordaudio.dbLayer;

import ru.eshtefan.recordaudio.commonData.model.Msg;
import ru.eshtefan.recordaudio.commonData.model.dbModel.User;
import ru.eshtefan.recordaudio.commonData.model.dbModel.Message;

/**
 * Msgs предоставляет реализации операций для взаимодействия сообщенией(Msg) с Firebase Realtime Database.
 * Created by eshtefan on 10.10.2017.
 */

public class Msgs implements IMsgs {
    @Override
    public void getMsg(final Message message, final MsgCallback msgCallback) {
        new Users().getUser(message.userId, new UserCallback() {
            @Override
            public void onUserGot(User user) {
                Msg msg = new Msg(message, user);
                msgCallback.onCreatingMsg(msg);
            }
        });

    }
}

package ru.eshtefan.recordaudio.dbLayer;

import com.google.firebase.database.FirebaseDatabase;

import ru.eshtefan.recordaudio.commonData.model.dbModel.Message;
import ru.eshtefan.recordaudio.utils.FBReferences;

/**
 * Messages предоставляет реализации операций для взаимодействия сообщенией(Message) с Firebase Realtime Database.
 * Created by eshtefan on 01.10.2017.
 */

public class Messages implements IMessages {

    @Override
    public void addMessage(Message message) {
        FirebaseDatabase.getInstance()
                .getReference(FBReferences.Database.REF_MESSAGES)
                .push()
                .setValue(message);
    }
}

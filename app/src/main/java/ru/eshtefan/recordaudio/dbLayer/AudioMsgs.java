package ru.eshtefan.recordaudio.dbLayer;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.eshtefan.recordaudio.commonData.model.Msg;
import ru.eshtefan.recordaudio.commonData.model.dbModel.AudioMessage;
import ru.eshtefan.recordaudio.commonData.model.AudioMsg;
import ru.eshtefan.recordaudio.commonData.model.dbModel.Message;
import ru.eshtefan.recordaudio.utils.FBReferences;

/**
 * AudioMsgs предоставляет реализации операций для взаимодействия аудио-сообщенией(AudioMsg) с Firebase Realtime Database.
 * Created by eshtefan on 01.10.2017.
 */

public class AudioMsgs implements IAudioMsgs {
    @Override
    public void getAudioMsg(final Msg msg, final AudioMsgCallback audioMsgCallback) {
        String refAudioMessage = msg.getPayload();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FBReferences.Database.REF_AUDIO_MESSAGES);

        myRef.child(refAudioMessage).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AudioMessage audioMessage = dataSnapshot.getValue(AudioMessage.class);

                AudioMsg audioMsg = new AudioMsg(msg, audioMessage);
                audioMsgCallback.onAudioMsgGot(audioMsg);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", databaseError.toException());
            }
        });
    }

    @Override
    public String getAudioMessageKey(String refAudioMessages) {
        return FirebaseDatabase.getInstance()
                .getReference(refAudioMessages)
                .push().getKey();
    }

    @Override
    public void addAudioMsg(AudioMessage audioMessage, Message message) {
        String audioMessageKey = message.getPayload();

        //добавляем AudioMessage audioMessage в бд
        FirebaseDatabase.getInstance()
                .getReference(FBReferences.Database.REF_AUDIO_MESSAGES)
                .child(audioMessageKey).setValue(audioMessage);

        //добавляем Message message в бд
        new Messages().addMessage(message);
    }
}

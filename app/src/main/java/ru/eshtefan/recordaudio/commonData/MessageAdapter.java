package ru.eshtefan.recordaudio.commonData;

import android.support.annotation.IntDef;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.NoSuchElementException;

import ru.eshtefan.recordaudio.ChatFragment;
import ru.eshtefan.recordaudio.R;
import ru.eshtefan.recordaudio.commonData.model.dbModel.Message;
import ru.eshtefan.recordaudio.commonData.model.AudioMsg;
import ru.eshtefan.recordaudio.commonData.model.Msg;
import ru.eshtefan.recordaudio.dbLayer.AudioMsgCallback;
import ru.eshtefan.recordaudio.dbLayer.AudioMsgs;
import ru.eshtefan.recordaudio.dbLayer.MsgCallback;
import ru.eshtefan.recordaudio.dbLayer.Msgs;
import ru.eshtefan.recordaudio.utils.FBReferences;

/**
 * MessageAdapter - адаптер расширяющий FirebaseRecyclerAdapter для работы отображения списка сообщений в чате.
 * Created by eshtefan on 20.09.2017.
 */

public class MessageAdapter extends FirebaseRecyclerAdapter<Message, MessageViewHolder> {

    /**
     * TYPE_IN_TEXT - view type для элемента входящее текстовое сообщение
     * TYPE_OUT_TEXT - view type для элемента исходящее текстовое сообщение
     * TYPE_IN_AUDIO - view type для элемента входящее аудио-сообщение
     * TYPE_OUT_AUDIO - view type для элемента исходящее аудио-сообщение
     */
    @IntDef({MessageAdapter.MessageType.TYPE_IN_TEXT, MessageAdapter.MessageType.TYPE_OUT_TEXT, MessageAdapter.MessageType.TYPE_IN_AUDIO, MessageAdapter.MessageType.TYPE_OUT_AUDIO})
    public @interface MessageType {
        int TYPE_IN_TEXT = 10;
        int TYPE_OUT_TEXT = 11;
        int TYPE_IN_AUDIO = 20;
        int TYPE_OUT_AUDIO = 21;
    }

    /**
     * Интерфейс OnDataChangedListener является контрактом для слушателя на изменение(удаление, добавление, модтфикацию) содержимого в Firebase Realtime Database.
     */
    public interface OnDataChangedListener {
        /**
         * Callback вызывающийся при изменение(удаление, добавление, модтфикацию) содержимого в Firebase Realtime Database.
         */
        void onListItemsChange();
    }

    private OnDataChangedListener mOnDataChangedListener;
    private final String LOG = getClass().getSimpleName();
    private LoadListener mLoadListener;
    private ChatFragment chatFragment;

    /**
     * Конструктор.
     *
     * @param chatFragment ссылка на экземпляр класса ChatFragment, необходима для передачи в метод MessageViewHolder, где данная ссылка используется для инициализации слушателей.
     */
    public MessageAdapter(ChatFragment chatFragment) {
        //параметры Message.class - класс модель, 0 - id  layout элемента списка(0 тк id меняется в зависимости от view type), MessageViewHolder.class - класс view holder, FirebaseDatabase.getInstance().getReference(FBReferences.Database.REF_MESSAGES) - database reference
        super(Message.class, 0, MessageViewHolder.class, FirebaseDatabase.getInstance().getReference(FBReferences.Database.REF_MESSAGES));

        this.chatFragment = chatFragment;
    }

    @Override
    protected void populateViewHolder(final MessageViewHolder viewHolder, Message model, final int position) {
        //previousMessageDate - timestamp предыдущего сообщения
        final long previousMessageDate = (position == 0) ? 0 : getItem(position - 1).getTimestamp();
        int type = getItem(position).getType();
        switch (type) {

            case Message.MessageType.TYPE_TEXT_MSG:
                new Msgs().getMsg(model, new MsgCallback() {
                    @Override
                    public void onCreatingMsg(Msg msg) {
                        viewHolder.bindMessage(msg, previousMessageDate);
                    }
                });
                break;

            case Message.MessageType.TYPE_AUDIO_MSG:
                new Msgs().getMsg(model, new MsgCallback() {
                    @Override
                    public void onCreatingMsg(Msg msg) {
                        new AudioMsgs().getAudioMsg(msg, new AudioMsgCallback() {
                            @Override
                            public void onAudioMsgGot(AudioMsg audioMsg) {
                                viewHolder.bindAudioMessage(audioMsg, previousMessageDate, chatFragment);
                            }
                        });
                    }
                });
                break;
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //resourceId - id layout элемента списка
        int resourceId;

        switch (viewType) {
            case MessageType.TYPE_IN_TEXT:
                resourceId = R.layout.list_item_text_in;
                break;
            case MessageType.TYPE_OUT_TEXT:
                resourceId = R.layout.list_item_text_out;
                break;
            case MessageType.TYPE_IN_AUDIO:
                resourceId = R.layout.list_item_audio_in;
                break;
            case MessageType.TYPE_OUT_AUDIO:
                resourceId = R.layout.list_item_audio_out;
                break;
            default:
                throw new NoSuchElementException(String.format("%s type of message not found", String.valueOf(viewType)));
        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(resourceId, parent, false);

        return new MessageViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        //authorizedUserId - уникальный id текущего авторизованного пользователя
        String authorizedUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userId = getItem(position).userId;
        int type = getItem(position).getType();
        int itemViewType;

        switch (type) {
            case Message.MessageType.TYPE_TEXT_MSG:
                itemViewType = (authorizedUserId.equals(userId)) ? MessageType.TYPE_OUT_TEXT : MessageType.TYPE_IN_TEXT;
                break;
            case Message.MessageType.TYPE_AUDIO_MSG:
                itemViewType = (authorizedUserId.equals(userId)) ? MessageType.TYPE_OUT_AUDIO : MessageType.TYPE_IN_AUDIO;
                break;
            default:
                throw new NoSuchElementException(String.format("%s type of message not found", String.valueOf(type)));
        }
        return itemViewType;
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if (mOnDataChangedListener != null) {
            mOnDataChangedListener.onListItemsChange();
        }

        if (mLoadListener != null) {
            mLoadListener.onFinishLoad();
        }
    }

    @Override
    public void onCancelled(DatabaseError error) {
        super.onCancelled(error);
        Log.w(LOG, error.toString());
        if (mLoadListener != null) {
            mLoadListener.onFailedLoad();
        }
    }

    public void setmLoadListener(LoadListener mLoadListener) {
        this.mLoadListener = mLoadListener;
    }

    public void setmOnDataChangedListener(OnDataChangedListener mOnDataChangedListener) {
        this.mOnDataChangedListener = mOnDataChangedListener;
    }
}

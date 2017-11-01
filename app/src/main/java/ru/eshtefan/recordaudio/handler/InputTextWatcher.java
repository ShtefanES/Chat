package ru.eshtefan.recordaudio.handler;

import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.eshtefan.recordaudio.R;
import ru.eshtefan.recordaudio.commonData.model.dbModel.User;
import ru.eshtefan.recordaudio.dbLayer.Users;
import ru.eshtefan.recordaudio.view.MonitoringEditText;

/**
 * InputTextWatcher слушатель, который наблюдает за изменением текста в EditText.
 * Created by eshtefan on 29.09.2017.
 */

public class InputTextWatcher implements TextWatcher, FabSrcObservation, MonitoringEditText.OnCutCopyPasteListener {

    private final String LOG = getClass().getSimpleName();
    //количество миллисекунд через которое isTyping для текущего пользователь false
    private final int CHECK_PERIOD = 2000;
    private final int DRAWABLE_ID_SEND = R.drawable.ic_shortcut_send;
    private final int DRAWABLE_ID_RECORD = R.drawable.ic_shortcut_speak;

    private FloatingActionButton fabChat;

    private final AtomicBoolean isTypingAtomic = new AtomicBoolean(false);
    private Users users;
    private User currentUser;
    private Handler handler;

    private List<FabSrcObserver> fabSrcObservers;

    public Runnable runnableCheckTyping = new Runnable() {
        @Override
        public void run() {
            //     Log.w(LOG, "in runnable");

            if (isTypingAtomic.compareAndSet(true, false)) {
                users.updateUser(currentUser, isTypingAtomic.get());
            }
        }
    };

    /**
     * Конструктор инициализирует объект класса FloatingActionButton.
     *
     * @param fabChat объект FloatingActionButton, отвечающий за тправку текста и запись аудиофайла.
     */

    public InputTextWatcher(FloatingActionButton fabChat) {
        this.fabChat = fabChat;

        fabSrcObservers = new ArrayList<>();

        users = new Users();
        currentUser = users.getCurrentUser();
        handler = new Handler();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0) {
            notifyObservers(DRAWABLE_ID_RECORD);
        } else if (s.length() == 1) {
            notifyObservers(DRAWABLE_ID_SEND);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(s.toString()) && s.toString().trim().length() > 0) {
            //     Log.w(LOG, "length() > 0; remove, post");
            handler.removeCallbacks(runnableCheckTyping);
            handler.postDelayed(runnableCheckTyping, CHECK_PERIOD);
            if (isTypingAtomic.compareAndSet(false, true)) {
                //     Log.w(LOG, "typing started event…");

                //установить тригер набор текста для данного пользователя true
                users.updateUser(currentUser, isTypingAtomic.get());
            }

        } else if (s.toString().trim().length() == 0 && isTypingAtomic.compareAndSet(true, false)) {
            //   Log.w(LOG, "typing stopped event…");
            ////установить тригер набор текста для данного пользователя false
            users.updateUser(currentUser, isTypingAtomic.get());
        }
    }

    @Override
    public void registerObserver(FabSrcObserver fabSrcObserver) {
        if (!fabSrcObservers.contains(fabSrcObserver)) {
            fabSrcObservers.add(fabSrcObserver);
        }
    }

    @Override
    public void removeObserver(FabSrcObserver fabSrcObserver) {
        if (fabSrcObservers.contains(fabSrcObserver)) {
            fabSrcObservers.remove(fabSrcObserver);
        }
    }

    @Override
    public void notifyObservers(int drawableId) {
        for (FabSrcObserver observer : fabSrcObservers) {
            if (drawableId == DRAWABLE_ID_RECORD) {
                observer.onResIdEqualsRecord();
                fabChat.setImageResource(drawableId);
            } else if (drawableId == DRAWABLE_ID_SEND) {
                observer.onResIdEqualsSend();
                fabChat.setImageResource(drawableId);
            }
        }
    }

    @Override
    public void onCut() {

    }

    @Override
    public void onCopy() {

    }

    @Override
    public void onPaste() {
        notifyObservers(DRAWABLE_ID_SEND);
    }
}
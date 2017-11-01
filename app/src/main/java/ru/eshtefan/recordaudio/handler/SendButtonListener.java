package ru.eshtefan.recordaudio.handler;

import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import ru.eshtefan.recordaudio.commonData.model.dbModel.Message;
import ru.eshtefan.recordaudio.dbLayer.Messages;
import ru.eshtefan.recordaudio.utils.InternetConnectionChecker;

/**
 * SendButtonListener слушатель на кнопу отправить текст и кнопку "стрелка" на программной клавиатуре.
 * Created by eshtefan on 29.09.2017.
 */

public class SendButtonListener implements View.OnClickListener, FabSrcObserver, TextView.OnEditorActionListener {

    private final int MAX_LENGTH_MSG = 300;
    private EditText etInput;

    private boolean isBtnSend = false;
    private InternetConnectionChecker internetConnectionChecker;

    /**
     * Конструктор инициализирует объекты классов EditText, InternetConnectionChecker.
     *
     * @param etInput                   объекты классов EditText, необходим для обнуления ввода, после нажатия на кнопку "enter", а так же для получения текста, который необохдимо отправить.
     * @param internetConnectionChecker объект предоставлет методы для проверки интернет соединения, использует перед обращением в сеть.
     */
    public SendButtonListener(EditText etInput, InternetConnectionChecker internetConnectionChecker) {
        this.etInput = etInput;
        this.internetConnectionChecker = internetConnectionChecker;
    }

    /**
     * Записывает текст в Firebase Realtime Database.
     *
     * @param messageText текст который необходимо записать в Firebase Realtime Database.
     */
    private void sendTextMessage(String messageText) {
        Message message = new Message(messageText,
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                Message.MessageType.TYPE_TEXT_MSG);

        new Messages().addMessage(message);
    }

    /**
     * Проверяет текст на максимально допустимую длину, при привышении длины, возвращает подстроку, с максимально допустимой длиной строки.
     *
     * @param messageText текст который необходимо проверить.
     * @return возвращает всю строку если не превышена допустимая длина и возвращает подстраку если превышена.
     */
    private String checkMessageText(String messageText) {
        if (messageText.length() > MAX_LENGTH_MSG) {
            return messageText.substring(0, MAX_LENGTH_MSG);
        } else {
            return messageText;
        }
    }

    @Override
    public void onClick(View v) {

        internetConnectionChecker.isOnline();

        if (isBtnSend) {
            String checkedMessageText = checkMessageText(etInput.getText().toString());
            sendTextMessage(checkedMessageText);
            etInput.setText("");
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        //если нажата клавиша enter на клавиатуре
        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
            int sizeText = etInput.getText().toString().length();
            if (sizeText != 0) {
                String checkedMessageText = checkMessageText(etInput.getText().toString());
                sendTextMessage(checkedMessageText);
                etInput.setText("");
            }
        }
        return false;
    }

    @Override
    public void onResIdEqualsSend() {
        isBtnSend = true;
    }

    @Override
    public void onResIdEqualsRecord() {
        isBtnSend = false;
    }
}

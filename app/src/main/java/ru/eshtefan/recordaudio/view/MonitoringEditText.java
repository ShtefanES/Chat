package ru.eshtefan.recordaudio.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

/**
 * MonitoringEditText квстомный вью класс расширяющий класс EditText. Используется для определения действия во время событий вырезать/копировать/вставить в EditText.
 * Created by eshtefan on 14.10.2017.
 */

public class MonitoringEditText extends AppCompatEditText {

    /**
     * Интерфейс OnCutCopyPasteListener предоставляет callback-и для выполнения кода во время событий вырезать/копировать/вставить в EditText.
     */
    public interface OnCutCopyPasteListener {
        void onCut();

        void onCopy();

        void onPaste();
    }

    private OnCutCopyPasteListener mOnCutCopyPasteListener;

    public void setOnCutCopyPasteListener(OnCutCopyPasteListener listener) {
        mOnCutCopyPasteListener = listener;
    }

    /*
    Стандартные конструкторы для создания EditText.
     */
    public MonitoringEditText(Context context) {
        super(context);
    }

    public MonitoringEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MonitoringEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        boolean consumed = super.onTextContextMenuItem(id);

        switch (id) {
            case android.R.id.cut:
                onCut();
                break;
            case android.R.id.copy:
                onCopy();
                break;
            case android.R.id.paste:
                onPaste();
        }
        return consumed;
    }

    /**
     * Callback на событие - текст вырезан из EditText.
     */
    public void onCut() {
        if (mOnCutCopyPasteListener != null)
            mOnCutCopyPasteListener.onCut();
    }

    /**
     * Callback на событие - текст копирован в EditText.
     */
    public void onCopy() {
        if (mOnCutCopyPasteListener != null)
            mOnCutCopyPasteListener.onCopy();
    }

    /**
     * Callback на событие - текст вставлен в EditText.
     */
    public void onPaste() {
        if (mOnCutCopyPasteListener != null)
            mOnCutCopyPasteListener.onPaste();
    }
}

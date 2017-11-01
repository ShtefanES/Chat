package ru.eshtefan.recordaudio.commonData;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.eshtefan.recordaudio.ChatFragment;
import ru.eshtefan.recordaudio.R;
import ru.eshtefan.recordaudio.commonData.model.AudioMsg;
import ru.eshtefan.recordaudio.commonData.model.Msg;
import ru.eshtefan.recordaudio.handler.DateHandler;
import ru.eshtefan.recordaudio.handler.PlayBtnClickListener;

/**
 * MessageViewHolder - ViewHolder расширяющий RecyclerView.ViewHolder, который связывает модели сообщений с соответствующими view.
 * Created by eshtefan on 20.09.2017.
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {

    private final String LOG = getClass().getSimpleName();
    private View mView;

    /**
     * Конструктор.
     *
     * @param itemView макет элемента списка FirebaseRecyclerAdapter.
     */
    public MessageViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    /**
     * Связывает модель текстового сообщения с соответствующим макетом элемента, а так же опредлеяет нужно ли показывать дату-заголовок.
     *
     * @param msg                 класс модель предметной области сообщения.
     * @param previousMessageDate timestamp предыдущего сообщения.
     */
    public void bindMessage(Msg msg, long previousMessageDate) {
        DateHandler dateHandler = new DateHandler();

        TextView tvUserName = (TextView) mView.findViewById(R.id.tv_user_name);
        TextView tvTimestamp = (TextView) mView.findViewById(R.id.tv_timestamp);
        TextView tvPayload = (TextView) mView.findViewById(R.id.tv_payload);

        LinearLayout dateLayout = (LinearLayout) mView.findViewById(R.id.date_layout);
        TextView tvDate = (TextView) mView.findViewById(R.id.tv_date);

        dateLayout.setVisibility(View.GONE);
        tvDate.setText(dateHandler.getDateStr(msg.getTimestamp()));
        // Log.w(LOG, "previousMessageDate = " + previousMessageDate);

        if (!dateHandler.isDatesBelongToSameDay(msg.getTimestamp(), previousMessageDate)) {
            dateLayout.setVisibility(View.VISIBLE);
        }

        tvUserName.setText(msg.getUser().getName());
        tvTimestamp.setText(new SimpleDateFormat("H:mm").format(new Date(msg.getTimestamp())));
        tvPayload.setText(msg.getPayload());
    }

    /**
     * Связывает модель аудио-сообщения с соответствующим макетом элемента, а так же опредлеяет нужно ли показывать дату-заголовок.
     *
     * @param audioMsg            класс модель предметной области аудио-сообщения.
     * @param previousMessageDate timestamp предыдущего сообщения.
     * @param chatFragment        ссылка на экземпляр класса ChatFragment, необходима экземаляру PlayBtnClickListener.
     */
    public void bindAudioMessage(AudioMsg audioMsg, long previousMessageDate, ChatFragment chatFragment) {
        DateHandler dateHandler = new DateHandler();

        TextView tvUserName = (TextView) mView.findViewById(R.id.tv_user_name);
        TextView tvTimestamp = (TextView) mView.findViewById(R.id.tv_timestamp);
        ProgressBar progressAudio = (ProgressBar) mView.findViewById(R.id.progress_audio);
        TextView tvDuration = (TextView) mView.findViewById(R.id.tv_duration);
        ImageButton imgBtnStartAudio = (ImageButton) mView.findViewById(R.id.img_btn_start_audio);

        LinearLayout dateLayout = (LinearLayout) mView.findViewById(R.id.date_layout);
        TextView tvDate = (TextView) mView.findViewById(R.id.tv_date);

        dateLayout.setVisibility(View.GONE);
        tvDate.setText(dateHandler.getDateStr(audioMsg.getTimestamp()));
        // Log.w(LOG, "previousMessageDate = " + previousMessageDate);

        if (!dateHandler.isDatesBelongToSameDay(audioMsg.getTimestamp(), previousMessageDate)) {
            dateLayout.setVisibility(View.VISIBLE);
        }

        tvUserName.setText(audioMsg.getUser().getName());
        tvTimestamp.setText(new SimpleDateFormat("H:mm").format(new Date(audioMsg.getTimestamp())));
        tvDuration.setText(dateHandler.formatDurationAsMinAndSec(audioMsg.getAudioMessage().getDuration()));

        PlayBtnClickListener playBtnClickListener = new PlayBtnClickListener(audioMsg, mView, chatFragment);
        imgBtnStartAudio.setOnClickListener(playBtnClickListener);
    }
}

package ru.eshtefan.recordaudio.handler;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.io.IOException;

import ru.eshtefan.recordaudio.ChatFragment;
import ru.eshtefan.recordaudio.R;
import ru.eshtefan.recordaudio.commonData.model.AudioMsg;
import ru.eshtefan.recordaudio.commonData.LoadListener;
import ru.eshtefan.recordaudio.utils.InternetConnectionChecker;

/**
 * PlayBtnClickListener слушатель, который предоставлет методы для обработки воспроизведения аудиофайла, а также управлет поведение view элементов связанных с аудио-воспроизведением.
 * Created by eshtefan on 02.10.2017.
 */

public class PlayBtnClickListener implements View.OnClickListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    /**
     * Интерфейс PlaybackListener предоставляет callback-и для выполнения код при старте/паузе/возобновлении/завершении воспроизведения аудиофайла.
     */
    public interface PlaybackListener {
        void onContinued();

        void onPaused();

        void onStarted();

        void onStopped();
    }

    private final String LOG = getClass().getSimpleName();

    private String uri;
    private MediaPlayer mediaPlayer;
    private ImageButton imageButton;
    private View itemView;

    private Stopwatch stopwatch;
    private PlaybackListener mPlaybackListener;
    private LoadListener mLoadListener;

    private InternetConnectionChecker internetConnectionChecker;

    /**
     * Конструктор инициализирует необходимые поля.
     *
     * @param audioMsg     объект класса AudioMsg, предоставляет url в Firebase Storage где хранится аудиофайл.
     * @param itemView     view элемента списка FirebaseRecyclerAdapter, который содержит элементы аудио-сообщения.
     * @param chatFragment ссылка на экземпляр класса ChatFragment, является слушателем для проверки интернет соединения и слушателем на выполнение долгих операций с сетью.
     */
    public PlayBtnClickListener(AudioMsg audioMsg, View itemView, ChatFragment chatFragment) {
        uri = audioMsg.getAudioMessage().audioUrl;

        imageButton = (ImageButton) itemView.findViewById(R.id.img_btn_start_audio);
        this.itemView = itemView;

        internetConnectionChecker = new InternetConnectionChecker(chatFragment.getContext());
        internetConnectionChecker.setConnectionListener(chatFragment);

        mLoadListener = chatFragment;
    }

    @Override
    public void onClick(View v) {
        if (internetConnectionChecker.isOnline()) {
            if (mediaPlayer == null) {
                startPlayback();
            } else {
                if (mediaPlayer.isPlaying()) {
                    pausePlayback();
                } else {
                    continuePlayback();
                }
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        onFinishedPrepareMediaPlayer(imageButton);
        Log.d(LOG, "onPrepared");
        mp.start();

        stopwatch = new Stopwatch(itemView, mp.getDuration());
        setmPlaybackListener(stopwatch);

        if (mPlaybackListener != null) {
            mPlaybackListener.onStarted();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.w(LOG, "Error in MediaPlayer");
        if (mLoadListener != null) {
            mLoadListener.onFailedLoad();
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.w(LOG, "onCompletion");
        releaseMP();

        if (mPlaybackListener != null) {
            mPlaybackListener.onStopped();
        }
        stopwatch = null;
    }

    /**
     * Продолжает воспроизведения аудио.
     */
    private void continuePlayback() {
        mediaPlayer.start();

        if (mPlaybackListener != null) {
            mPlaybackListener.onContinued();
        }
    }

    /**
     * Приостанавливает воспроизведения аудио.
     */
    private void pausePlayback() {
        mediaPlayer.pause();

        if (mPlaybackListener != null) {
            mPlaybackListener.onPaused();
        }
    }

    /**
     * Начинает воспроизводить аудио.
     */
    private void startPlayback() {
        onStartPrepareMediaPlayer(imageButton);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        Log.w(LOG, "prepareAsync");
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);

        mediaPlayer.prepareAsync();
    }

    /**
     * Освобождение ресурсов объекта MediaPlayer.
     */
    private void releaseMP() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setmPlaybackListener(PlaybackListener mPlaybackListener) {
        this.mPlaybackListener = mPlaybackListener;
    }

    /**
     * Callback на начало подготовки объекта MediaPlayer.
     *
     * @param imageButton объект класса ImageButton позволяет сделать данную кнопку неактивной.
     */
    private void onStartPrepareMediaPlayer(ImageButton imageButton) {
        if (mLoadListener != null) {
            mLoadListener.onStartLoad();
            imageButton.setEnabled(false);
        }
    }

    /**
     * Callback на завершение подготовки объекта MediaPlayer.
     *
     * @param imageButton объект класса ImageButton позволяет сделать данную кнопку активной.
     */
    private void onFinishedPrepareMediaPlayer(ImageButton imageButton) {
        if (mLoadListener != null) {
            mLoadListener.onFinishLoad();
            imageButton.setEnabled(true);
        }
    }
}

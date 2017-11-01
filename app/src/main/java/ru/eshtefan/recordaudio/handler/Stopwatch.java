package ru.eshtefan.recordaudio.handler;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import ru.eshtefan.recordaudio.R;

/**
 * Stopwatch слушатель на воспроизведение аудиофайла, управлет поведение view элементов связанных с таймингом аудио-воспроизведением.
 * Created by eshtefan on 03.10.2017.
 */

public class Stopwatch implements PlayBtnClickListener.PlaybackListener {

    private final String LOG = getClass().getSimpleName();

    private Handler handler;
    private ProgressBar audioProgress;
    private TextView tvDuration;
    private ImageButton imageButton;
    private int duration;
    private String strDuration;

    private long progressBuffer = 0L;

    long millisecondTime, startTime, timeBuff, updateTime = 0L;
    private int seconds, minutes;

    //в отдельном потоке запускается секундомер, который суммирует миллисекунды, заполняет горизонтальный ProgressBar
    private Runnable runnableTime = new Runnable() {
        @Override
        public void run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime;
            updateTime = timeBuff + millisecondTime;
            //   Log.w(LOG,"millisecondTime =" + millisecondTime + " updateTime = "+ updateTime + " timeBuff= " + timeBuff);
            seconds = (int) (updateTime / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;
            //    Log.w(LOG,"minutes =" + minutes + " seconds= " + seconds);
            tvDuration.setText(String.format("%02d:%02d", minutes, seconds));

            //изменение уровня прогресс-бара
            progressBuffer = timeBuff + millisecondTime;
            audioProgress.setProgress((int) progressBuffer);
            //Log.w(LOG,"progressBuffer =" + progressBuffer + " timeBuff = "+ timeBuff + " millisecondTime= " + millisecondTime);

            if (updateTime >= duration) {
                tvDuration.setText(strDuration);
                audioProgress.setProgress(0);
                handler.removeCallbacks(this);
            } else {
                handler.postDelayed(this, 0);
            }
        }
    };

    /**
     * Конструктор инициализирует поле duration, а так же инициализирует view-объекты(tvDuration - длительность, audioProgress - шкала прогресса воспроизведения, imageButton - изображение кнопки), которые должны меняться при воспроизведении аудиофайла.
     *
     * @param itemView объект класса View, содержащий view-объекты которые должны меняться при воспроизведении аудиофайла
     * @param duration длительность записи в миллисекундах.
     */
    public Stopwatch(View itemView, int duration) {
        handler = new Handler();
        audioProgress = (ProgressBar) itemView.findViewById(R.id.progress_audio);
        tvDuration = (TextView) itemView.findViewById(R.id.tv_duration);
        imageButton = (ImageButton) itemView.findViewById(R.id.img_btn_start_audio);

        strDuration = tvDuration.getText().toString();
        this.duration = duration;

        audioProgress.setMax(duration);
    }

    @Override
    public void onContinued() {
        imageButton.setImageResource(android.R.drawable.ic_media_pause);
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnableTime, 0);
    }

    @Override
    public void onPaused() {
        imageButton.setImageResource(android.R.drawable.ic_media_play);
        handler.removeCallbacks(runnableTime);
        timeBuff += millisecondTime;
    }

    @Override
    public void onStarted() {
        imageButton.setImageResource(android.R.drawable.ic_media_pause);
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnableTime, 0);
    }

    @Override
    public void onStopped() {
        imageButton.setImageResource(android.R.drawable.ic_media_play);
    }
}

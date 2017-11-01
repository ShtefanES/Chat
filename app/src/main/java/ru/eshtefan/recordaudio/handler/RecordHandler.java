package ru.eshtefan.recordaudio.handler;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.Uri;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;

import ru.eshtefan.recordaudio.commonData.model.dbModel.AudioMessage;
import ru.eshtefan.recordaudio.commonData.LoadListener;
import ru.eshtefan.recordaudio.commonData.model.dbModel.Message;
import ru.eshtefan.recordaudio.dbLayer.AudioFileCallback;
import ru.eshtefan.recordaudio.dbLayer.AudioFiles;
import ru.eshtefan.recordaudio.dbLayer.AudioMsgs;
import ru.eshtefan.recordaudio.utils.FBReferences;
import ru.eshtefan.recordaudio.utils.InternetConnectionChecker;

/**
 * RecordHandler предоставлет методы для работы с записью звука, а также управлет поведение view элементов связанных с записью звука.
 * Created by eshtefan on 02.10.2017.
 */

public class RecordHandler implements View.OnTouchListener, View.OnLongClickListener, FabSrcObserver, MediaRecorder.OnInfoListener {
    /**
     * Интерфейс RecordListener предоставляет callback-и для выполнения кода ui кода, на действия с записью звука.
     */
    public interface RecordListener {
        /**
         * Callback на начало записи звука.
         */
        void onStartRecord();

        /**
         * Callback на прекращение загрузки файла в Firebase Storage по причине слишком корокой дилтельности записи.
         */
        void onTooShortRecord();

        /**
         * Callback на прекращение записи звука по причине слишком длительной продолжительности записи.
         */
        void onTooLongRecord();
    }

    private final String LOG = getClass().getSimpleName();
    // максимальное время записи звука
    private final int MAX_DURATION = 10000;
    //минимальное время записи звука, необходимое для того чтобы аудиофайл был сохранен на сервере
    private final int MIN_SEC_DURATION = 2000;

    private final String FILE_NAME = "my_audio_record.3gp";

    private String fullFilePath;

    private Context mContext;
    private MediaRecorder mediaRecorder;

    private boolean isLongClick = false;
    //указывает верно ли, что FAB сейчас выполняет функцию отправки текста
    private boolean isBtnSend = false;

    private LoadListener mLoadListener;
    private RecordListener recordListener;
    private View btnView;

    private InternetConnectionChecker internetConnectionChecker;

    /**
     * Конструктор инициализирует объекты InternetConnectionChecker, Context и создает строку, содержащую полный путь до создаваемого аудиофайла.
     *
     * @param mContext                  объект класса Context, необходим для получения пути внутреннего хранилища данных, а так же для работы объекта класса MediaMetadataRetriever.
     * @param internetConnectionChecker объект предоставлет методы для проверки интернет соединения, использует перед обращением в сеть.
     */
    public RecordHandler(Context mContext, InternetConnectionChecker internetConnectionChecker) {
        String filePath = mContext.getFilesDir().toString();
        fullFilePath = String.format("%s/%s", filePath, FILE_NAME);

        this.internetConnectionChecker = internetConnectionChecker;
        this.mContext = mContext;
    }

    public void setmLoadListener(LoadListener mLoadListener) {
        this.mLoadListener = mLoadListener;
    }

    public void setRecordListener(RecordListener recordListener) {
        this.recordListener = recordListener;
    }

    /**
     * Возвращает длительность записи аудиофайл.
     *
     * @param audioFilePath путь до аудиофайла.
     * @return длительность записи в миллисекундах.
     */
    private int getDuration(String audioFilePath) {
        File file = new File(audioFilePath);
        if (!file.exists()) {
            return -1;
        }
        Uri uri = Uri.parse(audioFilePath);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(mContext, uri);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int millSecond = Integer.parseInt(durationStr);
        return millSecond;
    }

    /**
     * Создает объект класса MediaRecorder и выполняет ено настройку(подготовка к записи звука).
     *
     * @param maxDuration  максимальное время записи звука в миллисекундах.
     * @param fullFilePath путь до создаваемого аудиофайла с учетом имени файла.
     * @return объект класса MediaRecorder готовый для выполнения записи звука.
     */
    private MediaRecorder getPreparedMediaRecorder(int maxDuration, String fullFilePath) {
        MediaRecorder mRecorder = new MediaRecorder();
        mRecorder.setOnInfoListener(this);

        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setMaxDuration(maxDuration);
        mRecorder.setOutputFile(fullFilePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG, "prepare() failed");
            return null;
        }
        return mRecorder;
    }

    /**
     * Прекращает запись звука, свобождает ресурсы обекта класса MediaRecorder.
     *
     * @param mRecorder    обекта класса MediaRecorder, который выполняет запиь звук.
     * @param fullFilePath путь до создаванного аудиофайла с учетом имени файла. Необходим для удаления файла в случае возникновения ошибки при выполнении данного метода.
     */
    private void stopRecording(MediaRecorder mRecorder, String fullFilePath) {
        try {
            mRecorder.stop();
        } catch (IllegalStateException ex) {
            File file = new File(fullFilePath);
            boolean deleted = file.delete();
            Log.w(LOG, "IllegalStateException - stop() called before start()\nis deleted = " + deleted);

        } catch (RuntimeException e) {
            File file = new File(fullFilePath);
            boolean deleted = file.delete();
            Log.w(LOG, "RuntimeException - stop() is called immediately after start()\nis deleted = " + deleted);
        } finally {
            mRecorder.release();
        }
    }

    /**
     * Загружает аудиофайл в Firebase Storage.
     *
     * @param fullFilePath путь до создаванного аудиофайла с учетом имени файла.
     * @param duration     длительность записи в миллисекундах.
     */
    private void uploadAudio(String fullFilePath, final long duration) {
        onStartLoadFile();

        new AudioFiles().uploadFile(fullFilePath, duration, new AudioFileCallback() {
            @Override
            public void OnSuccessUpload(String uri, long duration) {
                onFinishLoadFiile();
                writeAudioMessInDB(uri, duration);
            }
        });

    }

    /**
     * Записывает сведения об аудио-сообщении в Firebase Realtime Database.
     *
     * @param audioUri строка, содержащая url аудио-файла хранящегося в Firebase Storage.
     * @param duration длительность записи в миллисекундах.
     */
    private void writeAudioMessInDB(String audioUri, long duration) {
        AudioMsgs audioMsgs = new AudioMsgs();

        String audioMessageKey = audioMsgs.getAudioMessageKey(FBReferences.Database.REF_AUDIO_MESSAGES);

        AudioMessage audioMessage = new AudioMessage(audioUri, duration);
        Message message = new Message(audioMessageKey,
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                Message.MessageType.TYPE_AUDIO_MSG);

        audioMsgs.addAudioMsg(audioMessage, message);
    }

    /**
     * Callback на старт загрузки аудиофала в Firebase Storage.
     */
    private void onStartLoadFile() {
        if (mLoadListener != null) {
            mLoadListener.onStartLoad();
        }
    }

    /**
     * Callback на завершение загрузки аудиофала в Firebase Storage.
     */
    private void onFinishLoadFiile() {
        if (mLoadListener != null) {
            mLoadListener.onFinishLoad();
        }
    }

    /**
     * Прекращает запись звука, если длительность записи валидна, сохранит аудиофайл в Firebase Storage.
     *
     * @param view объект класса View, необходим для срабатывания вибросигнала.
     */
    private void stopRecordingAndUpload(View view) {
        if (mediaRecorder == null) {
            return;
        }
        stopRecording(mediaRecorder, fullFilePath);
        //вибрация
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        mediaRecorder = null;

        long duration = getDuration(fullFilePath);
        boolean isRecordShort = (duration < MIN_SEC_DURATION) ? true : false;
        if (!isRecordShort) {
            uploadAudio(fullFilePath, duration);
        } else {
            if (recordListener != null) {
                recordListener.onTooShortRecord();
            }
        }
    }

    /**
     * Начинает запись звука.
     *
     * @param view объект класса View, необходим для срабатывания вибросигнала.
     */
    private void startRecording(View view) {
        mediaRecorder = getPreparedMediaRecorder(MAX_DURATION, fullFilePath);
        if (mediaRecorder != null) {
            if (recordListener != null) {
                recordListener.onStartRecord();
            }
            //вибрация
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            mediaRecorder.start();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (isBtnSend || mediaRecorder != null || !internetConnectionChecker.isOnline()) {
            return false;
        }

        if (btnView == null) {
            btnView = v;
        }
        startRecording(v);
        isLongClick = true;

        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isBtnSend) {
            return false;
        }

        if (isLongClick && (event.getAction() == MotionEvent.ACTION_UP)) {
            stopRecordingAndUpload(v);
            isLongClick = false;
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

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            Log.w(LOG, "MAX DURATION");
            isLongClick = false;
            stopRecordingAndUpload(btnView);
            if (recordListener != null) {
                recordListener.onTooLongRecord();
            }
        }
    }
}

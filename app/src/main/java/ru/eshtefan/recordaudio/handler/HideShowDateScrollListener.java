package ru.eshtefan.recordaudio.handler;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import ru.eshtefan.recordaudio.commonData.MessageAdapter;

/**
 * HideShowDateScrollListener слушатель, который предоставляет callback при скроллинге RecyclerView. Задача слушателя определить определить какую дату показывать в верхней части экрана при скролле RecyclerView.
 * Created by eshtefan on 16.10.2017.
 */

public abstract class HideShowDateScrollListener extends RecyclerView.OnScrollListener {

    private final String LOG = "HSDateScrollListener";

    private Handler handler;
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private DateHandler dateHandler;
    //позиция элемента RecyclerView находящегося в самой верхней части экрана
    private int firstVisibleItem;
    //дата-заголовок для для предыдущего элемента RecyclerView
    private String previousDateStr = null;
    //установлено ли значение для вслплывающей даты
    private boolean isInitDateStr = false;
    private Runnable runnableSleep = new Runnable() {
        @Override
        public void run() {
            onDragged();
        }
    };

    /**
     * Конструктор инициализирует linearLayoutManager.
     *
     * @param linearLayoutManager необходим для получения позиции элемента RecyclerView, который находится в самой верхней части экрана.
     * @param messageAdapter      необходим для получения timstamp указанного элемента списка.
     */
    public HideShowDateScrollListener(LinearLayoutManager linearLayoutManager, MessageAdapter messageAdapter) {
        handler = new Handler();
        this.linearLayoutManager = linearLayoutManager;
        this.messageAdapter = messageAdapter;
        dateHandler = new DateHandler();
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        //если RecyclerView начал скролится и всплывающая дата уже установлена, удаляется callback handler(чтоб через не которое врема всплвающая дата не исчезла) затем callback на скроллинг
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING && isInitDateStr) {
            handler.removeCallbacks(runnableSleep);
            onDragging();
        }
        //если RecyclerView перестал скролится, секундное ожидание в другом потоке и callback на завершение скроллинга
        else if (newState == recyclerView.SCROLL_STATE_IDLE) {
            handler.postDelayed(runnableSleep, 1000);
        }
        // Log.w(LOG, "new State = " + newState);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        //при инициализвции listener всегда вызывается данный callback с dy = 0, причем ранее, чем будут получеы данные с RecyclerView.
        if (dy == 0) {
            return;
        }

        int previousFirstVisibleItem = firstVisibleItem;
        firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
        //   Log.w(LOG, "firstVisibleItem = " + firstVisibleItem + " previousFirstVisibleItem = " + previousFirstVisibleItem);

        //если элемент списка полностью просклроллен(позиция элемента RecyclerView находящегося в самой верхней части экрана изменилась)
        if (previousFirstVisibleItem != firstVisibleItem) {
            // Log.w(LOG, "firstVisibleItem = " + firstVisibleItem + " previousFirstVisibleItem = " + previousFirstVisibleItem);

            long timestamp = messageAdapter.getItem(firstVisibleItem).getTimestamp();
            String dateStr = dateHandler.getDateStr(timestamp);

            //если это первый скролинг, нет сведений о дате-заголовке для предыдущего элемента RecyclerView.
            if (previousDateStr == null) {
                //      Log.w(LOG, "previousDateStr = null");
                onChangedDay(dateStr);
                previousDateStr = dateStr;
                isInitDateStr = true;
                onDragging();
            }
            //если в предыдущем элементе списка дата-заголовок отличается от текущего(верхнего на экране), то устанавливается новое значение для всплывающей даты
            else if (!previousDateStr.equals(dateStr)) {
                onChangedDay(dateStr);
                previousDateStr = dateStr;
            }
        }
    }

    /**
     * Callback на завершение скроллинга.
     */
    public abstract void onDragged();

    /**
     * Callback на начало скроллинга.
     */
    public abstract void onDragging();

    /**
     * Callback при отличии даты-заголовка элемента RecyclerView находящегося в самой верхней части экрана, от даты-заголовка предыдущего элемента.
     *
     * @param dateStr значение даты-заголовка элемента RecyclerView находящегося в самой верхней части экрана.
     */
    public abstract void onChangedDay(String dateStr);
}

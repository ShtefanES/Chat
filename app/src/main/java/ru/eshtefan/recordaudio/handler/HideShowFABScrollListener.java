package ru.eshtefan.recordaudio.handler;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

/**
 * HideShowFABScrollListener слушатель, который предоставляет callback при скроллинге RecyclerView. Задача слушателя предоставить callback-и при скроллинге вверх или вниз.
 * Created by eshtefan on 04.10.2017.
 */

public abstract class HideShowFABScrollListener extends RecyclerView.OnScrollListener {

    private final String LOG = "HSFABScrollListener";
    private boolean isCalledOnScrolledDawn = false;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        //если скролинг вверх и тригер предыдущего скролинга вниз true
        if (dy > 0 && isCalledOnScrolledDawn) {
            onScrolledUp();
            isCalledOnScrolledDawn = false;
        }
        //если скролинг вниз и тригер предыдущего скролинга вниз false
        else if (dy < 0 && !isCalledOnScrolledDawn) {
            onScrolledDown();
            isCalledOnScrolledDawn = true;
        }
    }

    /**
     * Callback на скроллинг вверх.
     */
    public abstract void onScrolledUp();

    /**
     * Callback на скроллинг вниз.
     */
    public abstract void onScrolledDown();
}
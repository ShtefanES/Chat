package ru.eshtefan.recordaudio;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import ru.eshtefan.recordaudio.handler.HideShowDateScrollListener;
import ru.eshtefan.recordaudio.handler.HideShowFABScrollListener;
import ru.eshtefan.recordaudio.commonData.LoadListener;
import ru.eshtefan.recordaudio.commonData.MessageAdapter;
import ru.eshtefan.recordaudio.utils.InternetConnectionChecker;

/**
 * Created by eshtefan on 04.10.2017.
 */

public class ChatFragment extends Fragment implements MessageAdapter.OnDataChangedListener, LoadListener, InternetConnectionChecker.ConnectionListener {

    private final String LOG = getClass().getSimpleName();

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;

    private FloatingActionButton fabScroll;
    private SwipeRefreshLayout swipeRefreshContainer;
    private TextView tvPopUpDate;

    private InternetConnectionChecker internetConnectionChecker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        internetConnectionChecker = new InternetConnectionChecker(getContext());
        internetConnectionChecker.setConnectionListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        fabScroll = (FloatingActionButton) view.findViewById(R.id.fab_scroll);
        swipeRefreshContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_container);
        tvPopUpDate = (TextView) view.findViewById(R.id.tv_pop_up_date);

        setupSwipeRefresh();
        onStartLoad();
        setupRecylerView();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        internetConnectionChecker.isOnline();
    }

    /**
     * Настраивает для работы RecylerView, который содержит список ссобщения.
     */
    private void setupRecylerView() {
        messageAdapter = new MessageAdapter(this);
        messageAdapter.setmOnDataChangedListener(this);
        messageAdapter.setmLoadListener(this);

        recyclerView.setAdapter(messageAdapter);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    /**
     * Настраивает для работы SwipeRefresh, который отвечает за обновление данных при вертикальном swipe по экрану, а так же является индикатором долгих операций.
     */
    private void setupSwipeRefresh() {
        swipeRefreshContainer.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
        swipeRefreshContainer.setColorSchemeResources(android.R.color.white,
                R.color.colorPrimaryGreen,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark);
        swipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView();
            }
        });
    }

    /**
     * Обновляет данные в списке сообщений.
     */
    private void refreshView() {
        if (internetConnectionChecker.isOnline() && (messageAdapter != null)) {
            //обновление
            messageAdapter.cleanup();
            messageAdapter.startListening();
            fabScroll.setVisibility(View.GONE);
        }
    }

    public SwipeRefreshLayout getSwipeRefreshContainer() {
        return swipeRefreshContainer;
    }

    @Override
    public void onResume() {
        super.onResume();

        fabScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
                fabScroll.setVisibility(View.GONE);
            }
        });

        recyclerView.addOnScrollListener(new HideShowFABScrollListener() {
            @Override
            public void onScrolledDown() {
                fabScroll.setVisibility(View.VISIBLE);
            }

            @Override
            public void onScrolledUp() {
                fabScroll.setVisibility(View.GONE);
            }
        });

        recyclerView.addOnScrollListener(new HideShowDateScrollListener((LinearLayoutManager) recyclerView.getLayoutManager(), messageAdapter) {
            @Override
            public void onDragged() {
                tvPopUpDate.setVisibility(View.GONE);
            }

            @Override
            public void onDragging() {
                tvPopUpDate.setVisibility(View.VISIBLE);
            }

            @Override
            public void onChangedDay(String dateStr) {
                tvPopUpDate.setText(dateStr);
            }

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w(LOG, "onDestroy");
        if (messageAdapter != null) {
            messageAdapter.cleanup();
        }
    }

    @Override
    public void onListItemsChange() {
        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
        Log.w(LOG, "Items CHANGE");
    }

    @Override
    public void onStartLoad() {
        swipeRefreshContainer.setRefreshing(true);
    }

    @Override
    public void onFinishLoad() {
        swipeRefreshContainer.setRefreshing(false);
    }

    @Override
    public void onFailedLoad() {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) getView().findViewById(R.id.coordinator_layout);
        Snackbar.make(coordinatorLayout, getString(R.string.firebase_error), Snackbar.LENGTH_LONG).show();

        swipeRefreshContainer.setRefreshing(false);
    }

    @Override
    public void onOffline() {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) getView().findViewById(R.id.coordinator_layout);
        Snackbar.make(coordinatorLayout, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show();

        onFinishLoad();
    }
}

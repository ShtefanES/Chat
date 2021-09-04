package ru.eshtefan.recordaudio;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import ru.eshtefan.recordaudio.commonData.LoadListener;
import ru.eshtefan.recordaudio.handler.TypingHandler;
import ru.eshtefan.recordaudio.handler.InputTextWatcher;
import ru.eshtefan.recordaudio.handler.SendButtonListener;
import ru.eshtefan.recordaudio.handler.RecordHandler;
import ru.eshtefan.recordaudio.dbLayer.Users;
import ru.eshtefan.recordaudio.utils.InternetConnectionChecker;
import ru.eshtefan.recordaudio.view.MonitoringEditText;

public class ChatActivity extends AppCompatActivity implements LoadListener, TypingHandler.TypingListener, RecordHandler.RecordListener {
    //Its activity request code полезен для работы с методом onActivityResult
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final String LOG = getClass().getSimpleName();
    //высота action Bar, необоходима чтобы отобразить Toast в верхней части экрана, ниже границы tool bar
    private int actionBarHeight;

    private FloatingActionButton fabChat;
    private MonitoringEditText etInput;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ChatFragment chatFragment;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        //проверка на наличие на устройстве приложения Google Play Store
        if (checkPlayServices()) {
            // if user  null запуск login activity
            if (user == null) {
                startActivity(new Intent(ChatActivity.this, LoginActivity.class));
                finish();
            } else {
                initUI();

                InternetConnectionChecker internetConnectionChecker = new InternetConnectionChecker(this);
                internetConnectionChecker.setConnectionListener(chatFragment);

                InputTextWatcher inputTextWatcher = new InputTextWatcher(fabChat);
                etInput.addTextChangedListener(inputTextWatcher);
                etInput.setOnCutCopyPasteListener(inputTextWatcher);

                SendButtonListener sendButtonListener = new SendButtonListener(etInput, internetConnectionChecker);
                etInput.setOnEditorActionListener(sendButtonListener);
                inputTextWatcher.registerObserver(sendButtonListener);

                RecordHandler recordButtonListener = new RecordHandler(this, internetConnectionChecker);
                recordButtonListener.setmLoadListener(this);
                recordButtonListener.setRecordListener(this);
                inputTextWatcher.registerObserver(recordButtonListener);

                fabChat.setOnLongClickListener(recordButtonListener);
                fabChat.setOnTouchListener(recordButtonListener);
                fabChat.setOnClickListener(sendButtonListener);

                TypingHandler typingHandler = new TypingHandler();
                typingHandler.setTypingListener(this);

                actionBarHeight = calculateActionBarHeight();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        swipeRefreshLayout = chatFragment.getSwipeRefreshContainer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            deleteNotificationToken();
            auth.signOut();
            startActivity(new Intent(ChatActivity.this, LoginActivity.class));
            finish();
        }
        return true;
    }

    @Override
    public void onStartLoad() {
        swipeRefreshLayout.setRefreshing(true);
        Log.w(LOG, "onStartLoad");
        fabChat.setEnabled(false);
    }

    @Override
    public void onFinishLoad() {
        swipeRefreshLayout.setRefreshing(false);
        Log.w(LOG, "onFinishLoad");
        fabChat.setEnabled(true);
    }

    @Override
    public void onFailedLoad() {
        Log.w(LOG, "onFailedLoad");
    }

    /**
     * Проверяет устройство на наличие Google Play Services APK.
     * Если его нет, то отобразит диалог, который позволит пользователю загрузить APK
     * из Google Play Store или позволит разрешить его в системных настройках устройства.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(LOG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Обнавлет notificationToken(пустая строка)в Firebase Realtime Database для текущего пользователя.
     */
    private void deleteNotificationToken() {
        Users users = new Users();
        users.updateUser(users.getCurrentUser(), "");
    }

    /**
     * Вычисляет высоту action Bar, необоходима чтобы отобразить Toast в верхней части экрана, ниже границы tool bar.
     *
     * @return высоту ActionBar.
     */
    private int calculateActionBarHeight() {
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return 0;
    }

    /**
     * Инициализация ui объектов, расположенных на данном layout.
     */
    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        chatFragment = new ChatFragment();
        FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.replace(R.id.frm_layout, chatFragment);
        fTrans.commit();

        fabChat = (FloatingActionButton) findViewById(R.id.fab_chat);

        etInput = (MonitoringEditText) findViewById(R.id.et_input);
        //setHorizontallyScrolling и setMaxLines необходимы для корректного заполнения EditText объемным текстом
        etInput.setHorizontallyScrolling(false);
        etInput.setMaxLines(5);
    }

    @Override
    public void onStartTyping(String name) {
        setTitle(String.format("%s typing...", name));
    }

    @Override
    public void onStopTyping() {
        setTitle(getString(R.string.app_name));
    }

    @Override
    public void onStartRecord() {
        Toast toast = Toast.makeText(this, getString(R.string.start_record), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, actionBarHeight);
        toast.show();
    }

    @Override
    public void onTooShortRecord() {
        Toast toast = Toast.makeText(this, getString(R.string.record_too_short), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, actionBarHeight);
        toast.show();
    }

    @Override
    public void onTooLongRecord() {
        Toast toast = Toast.makeText(this, getString(R.string.record_too_long), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, actionBarHeight);
        toast.show();
    }
}

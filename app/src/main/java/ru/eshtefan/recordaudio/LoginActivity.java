package ru.eshtefan.recordaudio;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import ru.eshtefan.recordaudio.dbLayer.Users;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private FirebaseAuth mAuth;

    private EditText etEmail;
    private EditText etPassword;
    private ProgressBar pbProgressLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        initUI();
    }

    /**
     * Выполняет аутентификацию, если введенные учетные данные не содержатся в Firebase Authentication, то регистрирует и авторизовывает.
     */
    private void attemptLogin() {
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        if (!isEmailValid(email)) {
            Toast.makeText(LoginActivity.this, getString(R.string.email_failed), Toast.LENGTH_LONG).show();
            return;
        }
        if (!isPasswordValid(password)) {
            Toast.makeText(LoginActivity.this, getString(R.string.pass_failed), Toast.LENGTH_LONG).show();
            return;
        }

        pbProgressLogin.setVisibility(View.VISIBLE);

        //аутентификация пользователя
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pbProgressLogin.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            // произошла ошибка "неверный пользователь"
                            if (task.getException() instanceof com.google.firebase.auth.FirebaseAuthInvalidUserException) {
                                pbProgressLogin.setVisibility(View.VISIBLE);
                                //создание пользователя в Firebase Authentication
                                mAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                                                pbProgressLogin.setVisibility(View.GONE);
                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(getApplicationContext(), "Authentication failed!\n" + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                                } else {
                                                    Toast.makeText(getApplicationContext(), R.string.successfully_sign_up, Toast.LENGTH_LONG).show();

                                                    saveUser();
                                                    startActivity(new Intent(LoginActivity.this, ChatActivity.class));
                                                    finish();
                                                }
                                            }
                                        });

                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.auth_failed, task.getException().getMessage()), Toast.LENGTH_LONG).show();
                            }

                        } else {
                            updateNotificationToken();
                            Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                });
    }

    /**
     * Проверяет на валидность email.
     *
     * @param email строка, которая будет проверятся на валидность, как email.
     * @return true если строка email является адесом электронной почты, иначе false.
     */
    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.length() <= 25;
    }

    /**
     * Проверяет на валидность пароль.
     *
     * @param password строка, которая будет проверятся на валидность, как пароль.
     * @return true если результат проверки положителный и password можно считать стойким паролем, иначе false.
     */
    private boolean isPasswordValid(String password) {
        return password.length() >= 6 && password.length() <= 20;
    }

    /**
     * Инициализация ui объектов, расположенных на данном layout.
     */
    private void initUI() {
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);

        Button btnLogin = (Button) findViewById(R.id.btn_sign_in);
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        pbProgressLogin = (ProgressBar) findViewById(R.id.login_progress);
    }

    /**
     * Сохраняет данные авторизованного пользователя в Firebase Realtime Database.
     */
    private void saveUser() {
        Users users = new Users();
        users.addUser(users.getCurrentUser());
    }

    /**
     * Обнавлет notificationToken(notificationToken для данного устройства)в Firebase Realtime Database для текущего пользователя.
     */
    private void updateNotificationToken() {
        Users users = new Users();
        users.updateUser(users.getCurrentUser(), FirebaseInstanceId.getInstance().getToken());
    }
}


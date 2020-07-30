package ru.mvlikhachev.stopdrink.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.mvlikhachev.stopdrink.Model.User;
import ru.mvlikhachev.stopdrink.R;

public class LoginSignUpActivity extends AppCompatActivity {

    public static final String TAG = "LoginSignUpActivity";

    private TextInputLayout textInputEmail;
    private TextInputLayout textInputName;
    private TextInputLayout textInputPassword;
    private TextInputLayout textInputConfirmPassword;

    private Button loginSignUpButton;
    private TextView toggleLoginSignUpTextView;

    private boolean isLoginModeActive;

    // Firebase
    private FirebaseAuth auth;

    private FirebaseDatabase database;
    private DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);

        textInputEmail = findViewById(R.id.textInputEmail);
        textInputName = findViewById(R.id.textInputName);
        textInputPassword = findViewById(R.id.textInputPassword);
        textInputConfirmPassword = findViewById(R.id.textInputConfirmPassword);

        loginSignUpButton = findViewById(R.id.loginSignUpButton);
        toggleLoginSignUpTextView = findViewById(R.id.toggleLoginSignUpTextView);

        authorizationUi();
        
        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        usersDatabaseReference = database.getReference().child("users");

        // Если пользователь авторизован - сразу открыть мэйн активити
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginSignUpActivity.this, MainActivity.class));
        }
        
    }
    private boolean validateEmail() {

        String emailInput = textInputEmail
                .getEditText()
                .getText()
                .toString()
                .trim();

        if (emailInput.isEmpty()) {
            textInputEmail.setError("Введите email!");
            return false;
        } else {
            textInputEmail.setError("");
            return true;
        }
    }
    private boolean validateName() {

        String nameInput = textInputName
                .getEditText()
                .getText()
                .toString()
                .trim();

        if (nameInput.isEmpty()) {
            textInputName.setError("Введите Ваше имя!");
            return false;
        } else if (nameInput.length() > 10) {
            textInputName.setError("Имя должно быть меньше 15 символов!");
            return false;
        } else {
            textInputName.setError("");
            return true;
        }
    }

    private boolean validatePassword() {

        String passwordInput = textInputPassword
                .getEditText()
                .getText()
                .toString()
                .trim();

        if (passwordInput.isEmpty()) {
            textInputPassword.setError("Введите Ваше имя!");
            return false;
        } else if (passwordInput.length() > 7) {
            textInputPassword.setError("Пароль должно быть меньше 7 символов!");
            return false;
        }  else {
            textInputPassword.setError("");
            return true;
        }
    }

    private boolean validateConfirmPassword() {

        String passwordInput = textInputPassword
                .getEditText()
                .getText()
                .toString()
                .trim();
        String confirmPasswordInput = textInputConfirmPassword
                .getEditText()
                .getText()
                .toString()
                .trim();

        if (!passwordInput.equals(confirmPasswordInput)) {
            textInputPassword.setError("Пароли должны совпадать");
            return false;
        } else {
            textInputPassword.setError("");
            return true;
        }
    }

    private void registarionUi() {
        isLoginModeActive = false;
        loginSignUpButton.setText("Зарегистрироваться");
        toggleLoginSignUpTextView.setText("Или авторизуйтесь");
        textInputConfirmPassword.setVisibility(View.VISIBLE);
        textInputName.setVisibility(View.VISIBLE);
    }
    private void authorizationUi() {
        isLoginModeActive = true;
        loginSignUpButton.setText("Войти");
        toggleLoginSignUpTextView.setText("Или зарегистрируйтесь");
        textInputConfirmPassword.setVisibility(View.GONE);
        textInputName.setVisibility(View.GONE);
    }

    public void toggleLoginSignUp(View view) {
        if (isLoginModeActive) {
            registarionUi();
        } else {
            authorizationUi();
        }
    }

    public void loginSignUpUser(View view) {

        if (!validateEmail() | !validatePassword()) {
            return;
        }

        if (isLoginModeActive) { // Authorization
            auth.signInWithEmailAndPassword(
                    textInputEmail.getEditText().getText().toString().trim(),
                    textInputPassword.getEditText().getText().toString().trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = auth.getCurrentUser();
                                startActivity(new Intent(
                                        LoginSignUpActivity.this,
                                        MainActivity.class
                                ));
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.d(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginSignUpActivity.this, "Ошибка авторизации. \n Проверьте введенные данные!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else { // Registration

            if (!validateEmail() | !validateName() | !validatePassword() | !validateConfirmPassword()) {
                return;
            }


            auth.createUserWithEmailAndPassword(
                    textInputEmail.getEditText().getText().toString().trim(),
                    textInputPassword.getEditText().getText().toString().trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = auth.getCurrentUser();
                                createUser(user);
                                startActivity(new Intent(
                                        LoginSignUpActivity.this,
                                        MainActivity.class
                                ));
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.d(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(LoginSignUpActivity.this, "Registration failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private void createUser(FirebaseUser user) {

        User currentUser = new User();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        currentUser.setId(user.getUid());
        currentUser.setEmail(user.getEmail());
        currentUser.setName(textInputName.getEditText()
                                         .getText()
                                         .toString()
                                         .trim());
        currentUser.setDateWhenStopDrink(dateFormat.format(date));
        Log.d("testTime", " " + dateFormat.format(date));


        usersDatabaseReference.push().setValue(currentUser);
    }
}
package com.example.gethub.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.gethub.MainActivity;
import com.example.gethub.R;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private EditText etStudentId, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        etStudentId = findViewById(R.id.etStudentId);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> {
            String studentId = etStudentId.getText().toString();
            String password = etPassword.getText().toString();
            loginViewModel.login(studentId, password);
        });

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        loginViewModel.getLoginResult().observe(this, user -> {
            if (user != null) {
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                // Navigate to the main activity, passing the user object
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("LOGGED_IN_USER", user);
                startActivity(intent);
                finish(); // Prevent user from coming back to the login screen
            } else {
                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

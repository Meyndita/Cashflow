package com.dhanifudin.cashflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dhanifudin.cashflow.models.Session;
import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameInput;
    private EditText passwordInput;
    private Session session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        // binding komponen
        session = Application.getSession();

        if (session.isKeepUsername()) {
            usernameInput.setText(session.getUsername());
        }
    }

    // click pada tombol reset
    public void handleReset(View view) {
        usernameInput.setText("");
        passwordInput.setText("");
    }

    //click pada tombol login
    public void handleLogin(View view) {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        boolean success = session.validate(username, password);
        if (success) {
            if (session.isKeepUsername()) {
                session.setUsername(username);
            }
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Snackbar.make(view, "Authentication Failed", Snackbar.LENGTH_SHORT).show();
        }
    }
}

package com.example.secureprogrammanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt; // handles finger print and face detection
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executor; // interface that determines what thread asynchronous activity (such as callbacks) runs on

public class UnlockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_unlock_xml);

            BiometricAuthManager authManager = new BiometricAuthManager(this);

            authManager.authenticate(() -> {
                SessionManager.getInstance().unlock();
                startTimer();

                runOnUiThread(() -> {
                    Toast.makeText(this, "Vault Unlocked!", Toast.LENGTH_SHORT).show();
                    finish();

                    // 1. Explicitly start MainActivity
                    Intent intent = new Intent(UnlockActivity.this, MainActivity.class);
                    startActivity(intent);

                    finish(); // go back to MainActivity
                });
            }, this);
        }

    public void startTimer ()
    {
        VaultAlarmManager.scheduleVaultAutoLock(this);
    }
    }


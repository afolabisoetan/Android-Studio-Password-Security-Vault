package com.example.secureprogrammanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import javax.crypto.SecretKey;

public class AddEntryActivity extends AppCompatActivity {

    private EditText serviceField, usernameField, passwordField;

    private VaultDatabase db;
    private CryptoManager crypto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        serviceField = findViewById(R.id.serviceInput);
        usernameField = findViewById(R.id.usernameInput);
        passwordField = findViewById(R.id.passwordInput);
        Button saveButton = findViewById(R.id.saveButton);

        // Initialize database
        db = Room.databaseBuilder(getApplicationContext(),
                        VaultDatabase.class, "vault.db")
                .build();

        // Initialize CryptoManager
        KeyStoreManager ksm = new KeyStoreManager();
        try {
            SecretKey key = ksm.getOrCreateKey();
            crypto = new CryptoManager(key);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to initialize crypto", Toast.LENGTH_SHORT).show();
        }

        saveButton.setOnClickListener(v -> addVaultEntry());
    }

    private void addVaultEntry() {
        final String service = serviceField.getText().toString().trim();
        final String username = usernameField.getText().toString().trim();
        final String password = passwordField.getText().toString().trim();

        // Validate input
        if (service.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert in background thread
        new Thread(() -> {
            try {
                // Encrypt password
                CryptoManager.EncryptionResult result = crypto.encrypt(password.getBytes());

                // Create VaultEntry object
                VaultEntry entry = new VaultEntry(service, username, result.cipherText, result.iv);

                // Insert into database
                DatabaseService.insertVault(this, entry, input -> {
                                ; // dont need anything to run in the callback interface, since we are immediately going
                                 // to the main screen (where its onResume code will run)
                });

                // Update UI on main thread
                runOnUiThread(() -> {
                    Toast.makeText(this, "Password added successfully!", Toast.LENGTH_SHORT).show();
                    serviceField.setText("");
                    usernameField.setText("");
                    passwordField.setText("");

                    // Return RESULT_OK so MainActivity reloads the list (was successful if we got this far)
                    setResult(RESULT_OK);
                    finish();
                });

            } catch (Exception e) {
                e.printStackTrace();

                // Key timed out or some crypto failure
                runOnUiThread(() -> {
                    Toast.makeText(this,
                            "You timed out. Please reauthenticate to ensure it is still you",
                             Toast.LENGTH_LONG).show();
                });

                SessionGate.lockSession();

                // Finish the activity anyway → goes back to MainActivity after 5 seconds of alerting the user
                runOnUiThread(() -> new android.os.Handler().postDelayed(this::finish, 5000));



            }
        }).start();
    }
}

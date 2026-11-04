package com.example.secureprogrammanager;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.UserNotAuthenticatedException;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import com.google.android.material.button.MaterialButton;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int ADD_ENTRY_REQUEST = 100; // request code

    private RecyclerView recyclerView;
    private VaultAdapter adapter; // will be attached to recyclerView
    private VaultDatabase db;
    private CryptoManager crypto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Initialize CryptoManager
        KeyStoreManager ksm = new KeyStoreManager(); // the key used for encryption and decryption
        // since the KeyStoreManager only stores one key with one alias, all of the passwords will use
        // the same encryption/decryption key (meaning the basic process of scrambling these different passwords will
        // be the same across all passwords)
        try {
            SecretKey key = ksm.getOrCreateKey();
            crypto = new CryptoManager(key);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // initializes the recyclerView
        recyclerView = findViewById(R.id.vaultRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with empty list + crypto (for decryption of passwords) and attaches it to the recyclerVIew
        // includes lambda that will be stored in the Adapter’s functional interface instance variable so that whenever
        // its sole onDeleteClick function is called, this is the implementation that is ran for it
        adapter = new VaultAdapter(new ArrayList<>(), crypto, entry -> {
            // This code runs when 'Delete' is clicked in the row
            deleteVaultEntry(entry.id);
        });

        recyclerView.setAdapter(adapter);

        // go to AddEntryActivity when clicked
        @SuppressLint("WrongViewCast") MaterialButton add = findViewById(R.id.addEntryButton);
        add.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEntryActivity.class);
            startActivity(intent);
        });


        EditText searchInput = findViewById(R.id.searchInput);
        Button searchButton = findViewById(R.id.searchButton);
        Button lock = findViewById(R.id.Unlock);

        searchButton.setOnClickListener(v -> {
            String query = searchInput.getText().toString().trim();
            searchVault(query);
        });

        lock.setOnClickListener(v -> {
            lock();
        });


        // load vault entries initially
        DatabaseService.loadAll(this, results -> {
            adapter.setEntries(results);
        });
    }

    // runs everytime the activity becomes visible, including when returning from another activity
    @Override
    protected void onResume() {
        super.onResume();

        //  use the DatabaseService to load all items
        // allow the handler to run this implementation of the callback
        DatabaseService.loadAll(this, allEntries -> {


            int sessionStatus = SessionGate.enforce(this, allEntries, crypto);

            if (sessionStatus == 0) {
                // if locked, kick the user to the unlock screen
                Intent intent = new Intent(this, UnlockActivity.class);
                // add flags to prevent the user from hitting 'back' to get back here
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                // if unlocked, the UI can be updated
                adapter.setEntries(allEntries);
            }
        });
    }

    private void lock ()
    {
        // get the SessionGate to lock out the user (set locked to true)
       SessionGate.lockSession();
       startActivity(new Intent(this, UnlockActivity.class));

    }

    private void deleteVaultEntry(UUID entryId) {
        // pass in context and id to delete. Pass in implementation of the callback runnable
        DatabaseService.onDelete(this, entryId, newList -> {
            adapter.setEntries(newList);
        });
    }

    // Search method
    private void searchVault(String query) {
        DatabaseService.searchVault(this, query, results -> {
            adapter.setEntries(results); // occurs on the UI thread when the handler posts this to the
                                        // main thread implementation
        });
    }

}

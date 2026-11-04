package com.example.secureprogrammanager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.room.Room;
import java.util.List;
import java.util.UUID;


/**
    Service layer that separates data acquisition and querying from the activities. This allows the
    activities to have a focused responsibility of managing UI logic. Allows this service to have a
    a focused responsibility of accessing/updating the database
 **/
public class DatabaseService {

    private static VaultDatabase instance;

    // singleton provider for the database
    private static synchronized VaultDatabase getDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            VaultDatabase.class, "vault.db")
                    // Removed allowMainThreadQueries for safety
                    .build();
        }
        return instance;
    }

    // interface for callbacks
    public interface DatabaseCallback {
        void onDataReady(List<VaultEntry> entries);
    }

    // delete and refresh, run the runnable
    public static void onDelete(Context context, UUID entryId, DatabaseCallback callback) {
        VaultDatabase db = getDatabase(context);
        new Thread(() -> {
            db.vaultDao().deleteById(entryId);
            // Refresh the list after deletion
            List<VaultEntry> updatedList = db.vaultDao().getAll();
            returnResult(updatedList, callback);
        }).start();
    }

    // load all, run the runnable
    public static void loadAll(Context context, DatabaseCallback callback) {
        VaultDatabase db = getDatabase(context);
        new Thread(() -> {
            List<VaultEntry> results = db.vaultDao().getAll();
            returnResult(results, callback);
        }).start();
    }

    // search, run the runnable
    public static void searchVault(Context context, String query, DatabaseCallback callback) {
        VaultDatabase db = getDatabase(context);
        new Thread(() -> {
            List<VaultEntry> results = db.vaultDao().search(query);
            returnResult(results, callback);
        }).start();
    }

    public static void insertVault(Context context, VaultEntry entry, DatabaseCallback callback) {
        VaultDatabase db = getDatabase(context);
        new Thread(() -> {
            db.vaultDao().insert(entry);
            List<VaultEntry> results = db.vaultDao().getAll();
            returnResult(results, callback);
        }).start();
    }

    // helper to send results back to the main (UI) thread
    private static void returnResult(List<VaultEntry> results, DatabaseCallback callback) {
        if (callback != null) {
            new Handler(Looper.getMainLooper()).post(() -> callback.onDataReady(results));
            // creates a handler that sends a block of code for the main thread manager (Looper.getMainLooper()) to complete (the implementation of
            // the callback runnable's onDataReady method)

        }
    }
}
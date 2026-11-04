package com.example.secureprogrammanager;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.example.secureprogrammanager.VaultRepository;

public class VaultViewModel extends ViewModel {

    private final VaultRepository repository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public VaultViewModel(VaultRepository repository) {
        this.repository = repository;
    }

    public void addEntry(String service, String username, String password) {
        executor.execute(() -> repository.addVaultEntry(service, username, password));
    }
}


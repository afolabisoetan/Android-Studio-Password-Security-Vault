package com.example.secureprogrammanager;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.UUID;

// Entity that represents the user's saved password
@Entity(tableName = "vault_entries")
public class VaultEntry {
    @PrimaryKey
    @NonNull
    public UUID id; // automatically generated PK
    public String service; // service password is for
    public String username; // corresponding password for username
    public byte[] encryptedPassword; // encrypted password from CryptoManager
    public byte[] iv; // IV from cipher.getIV () (necessary for GCM authentication)

    // constructor to create a new entry
    public VaultEntry(String service, String username, byte[] encryptedPassword, byte[] iv) {
        this.id = UUID.randomUUID();
        this.service = service;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.iv = iv;
    }
}



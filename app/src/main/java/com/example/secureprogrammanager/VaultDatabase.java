package com.example.secureprogrammanager;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {VaultEntry.class}, version = 1)
public abstract class VaultDatabase extends RoomDatabase {
    public abstract VaultDao vaultDao(); // exposed data access object for querying this database
}


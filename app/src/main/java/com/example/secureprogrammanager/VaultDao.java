package com.example.secureprogrammanager;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import java.util.UUID;

// data access object that exposes methods for querying the underlying database (implemented by the VaultDatabase class
// since that is the class the Room database is built off of)
@Dao
public interface VaultDao {

    @Query("SELECT * FROM vault_entries WHERE service LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%'")
    List<VaultEntry> search(String query);

    @Query("DELETE FROM vault_entries WHERE id = :entryId")
    void deleteById(UUID entryId);

    @Insert
    void insert(VaultEntry entry);

    @Query("SELECT * FROM vault_entries")
    List<VaultEntry> getAll();
}




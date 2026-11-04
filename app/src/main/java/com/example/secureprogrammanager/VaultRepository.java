
package com.example.secureprogrammanager;

import com.example.secureprogrammanager.VaultDao;
import com.example.secureprogrammanager.VaultEntry;
import com.example.secureprogrammanager.CryptoManager;

/**
 * This class is mostly obsolete right now as its central role was to provide a layer of indirection
 *  (forcing the activities to call this object's addVaultEntry method in order to add an entry instead
 *  of that activity just directly accessing the dao and preforming an insertion itself). However, since
 *  we create a new thread for insertions on those activities, the level of indirection is not needed to
 *  relieve the UI thread since it is not used for the queries.
 */
public class VaultRepository {

    private final com.example.secureprogrammanager.VaultDao dao;
    private final com.example.secureprogrammanager.CryptoManager crypto;

    // receives the cryptoManager object and the dao
    public VaultRepository(com.example.secureprogrammanager.VaultDao  dao, com.example.secureprogrammanager.CryptoManager crypto) {
        this.dao = dao;
        this.crypto = crypto;
    }

    public void addVaultEntry(String service, String username, String password) {
        try {
            com.example.secureprogrammanager.CryptoManager.EncryptionResult result =
                    crypto.encrypt(password.getBytes()); // uses cryptoManager to encrypt the password passed in
            dao.insert(new com.example.secureprogrammanager.VaultEntry(service, username,
                    result.cipherText, result.iv)); // uses dao to insert entry with now encrypted password
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


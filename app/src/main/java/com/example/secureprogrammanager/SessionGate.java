package com.example.secureprogrammanager;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.UserNotAuthenticatedException;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

/**
 * modifies the SessionManager's locked status and separates these responsibilities from the activities/UI.
 * This creates a separate, focused layer designed to alter the SessionManager's lock and unlock status.
 */
public class SessionGate  {
    private SessionGate() {
    }

    public static int enforce(AppCompatActivity activity, List<VaultEntry> allEntries, CryptoManager crypto) {
        if (!allEntries.isEmpty()) {
            // tests to see if the key is still live. If not, set the SessionManager into locked state
            try {
                VaultEntry entry =  allEntries.get(0);
                byte[] decrypted = crypto.decrypt(entry.encryptedPassword, entry.iv);
            } catch (KeyPermanentlyInvalidatedException | UserNotAuthenticatedException e) {
                SessionManager.getInstance().lock(); // <-- lock the session if these exceptions occur
                return 0;
            } catch (Exception e) {
                SessionManager.getInstance().lock(); // lock the session if some unknown error occurs
                return 0;
            }
        }

        // if the vault session is not unlocked, takes you to unlockActivity for authentication
        if (!SessionManager.getInstance().isUnlocked()) {
            return 0;
        }
        return 1;
    }

    public static void lockSession ()
    {
        // locks if requested
        SessionManager.getInstance().lock();
    }
}

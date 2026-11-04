package com.example.secureprogrammanager;

/**
 * Manages application-level authentication state.
 * Cryptographic enforcement is handled by Android Keystore and GCM implementaion
 */
public final class SessionManager {

    // Singleton instance
    private static final SessionManager INSTANCE = new SessionManager();

    // Tracks whether the vault session is unlocked
    private boolean unlocked = false;

    // Prevent external instantiation by making constructor private
    private SessionManager() {}

    /**
     * Get the single SessionManager instance
     */
    public static SessionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Mark the vault session as unlocked
     * Called after successful biometric authentication
     */
    public void unlock() {
        unlocked = true;
    }

    /**
     * Lock the vault session
     * Called when app is backgrounded or timeout occurs
     */
    public void lock() {
        unlocked = false;
    }

    /**
     * Check if the vault is currently unlocked
     */
    public boolean isUnlocked() {
        return unlocked;
    }
}




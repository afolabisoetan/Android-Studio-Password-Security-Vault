package com.example.secureprogrammanager;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import java.security.KeyStore;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class KeyStoreManager {

    private static final String KEY_ALIAS = "vault_key"; // name to be given to the key generated

    public SecretKey getOrCreateKey() throws Exception {
        // (singleton) retrieves instance of the KeyStore class
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null); // loads the object if it is not already

        // if the keystore object does not yet contain a key with the alias, that means that the
        // key has not yet been generated
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            KeyGenerator generator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            // creates instance of the KeyGenerator

            KeyGenParameterSpec spec = null; // sets spec to null, instantiates it with the builder
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                spec = new KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT) // allows key to encrypt and decrypt
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM) // key must wait for GCM authentication
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE) // no padding during the encryption
                        .setUserAuthenticationRequired(true) // user must authenticate before the key is valid
                        .setUserAuthenticationParameters(30, KeyProperties.AUTH_BIOMETRIC_STRONG) // key relocks after 30 seconds (no longer can encrypt or decrypt)
                        .build();
            }


            generator.init(spec);
            generator.generateKey();
        }
        // if the key with that alias is already in the keystore, just return it
        return (SecretKey) keyStore.getKey(KEY_ALIAS, null);
    }
}



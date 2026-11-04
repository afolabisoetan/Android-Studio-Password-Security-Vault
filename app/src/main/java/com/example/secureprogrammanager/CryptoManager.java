package com.example.secureprogrammanager;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class CryptoManager {

    private final SecretKey key;

    public CryptoManager(SecretKey key) {
        this.key = key; // receives key that decrypts and encrypts the passwords (using AES)
        // since AES is symmetrical, the same key will be used for encryption and decryption
    }

    /**
    ENCRYPTION PROCESS:
     Based on the configuration of this CryptoManager object, it will encrypt the plaintext (the password)
     and add a 128 bit tag to the end of the plaintext (128 bits is the default produced). The key determines how the
     data will be encrypted (scrambled), which makes it important for that same key to be used in the decryption process.
     The 128 bit tag will also be used for GCM authentication.
     */
    public EncryptionResult encrypt(byte[] data) throws Exception {
        // retrieves Cipher object
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        // instantiates the Cipher object for encryption and passes in the associated key . Ensures encryption
        // method is AES, that GCM is used for authentication (making sure the encrypted cipher text has not been tampered
        // with ever creation), and makes sure filler bits are not used to create the tag
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return new EncryptionResult(cipher.doFinal(data), cipher.getIV()); // returns encryptionResult object
                                    // GCM tag for encryption defaults to 128 bits
    }


    /**
     DECRYPTION PROCESS:
     Based on the configuration of this CryptoManager object, the cipherText will be decrypted using the same key
     that was used to encrypt the original password. The GCMParameterSpec object ensures that the GCM has the correct
     tag length (knows how much of the encrypted message to slice off from the end as the tag) and the initialization vector
     (allows for unique encryption with the same key). GCM works by authenticating the current state of the encrypted message.
     It runs the encrypted message (minus the tag at the end) through a mathematical formula. The output is a tag. If the resulting
     output tag of that formula is the same tag that was originally attached to it, the decryption results are returned. If not, that
     means the data has been tampered with after encryption, therefore an exception will be thrown and the decrypted message will not be
     returned because it is inaccurate and not safe to do so. This process is called authentication.
     */
    public byte[] decrypt(byte[] cipherText, byte[] iv) throws Exception {
        // receive the cipherText to decrypt and the
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
        return cipher.doFinal(cipherText);
    }

    public static class EncryptionResult {
        public byte[] cipherText; // stores encrypted password
        public byte[] iv; // stores the return of cipher.getIV()

        public EncryptionResult(byte[] cipherText, byte[] iv) {
            this.cipherText = cipherText;
            this.iv = iv;
        }
    }
}


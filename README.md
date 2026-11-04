A security-focused Android password manager built with Java that provides hardware-backed credential protection using AES-GCM encryption, the Android KeyStore system, and biometric authentication.

Key Features:
- Uses the Android AndroidKeyStore system to generate and store AES keys

- Implements AES/GCM/NoPadding encryption with a 128-bit authentication tag to ensure confidential password storage and prevent data tampering.

- Biometric Authentication & Auto-Lock --> Integrates BiometricPrompt and KeyGenParameterSpec. As a result, keys automatically invalidate after a 30-second inactivity window, triggering session re-authentication on cryptographic operations.

- Encrypted Room Persistence --> Stores encrypted credentials along with unique Initialization Vectors (IVs) per entry using Room ORM and SQLite.

- Thread-Safe Operation --> Executes cryptographic functions and database persistence asynchronously on background threads. This keeps the UI smooth and responsive.

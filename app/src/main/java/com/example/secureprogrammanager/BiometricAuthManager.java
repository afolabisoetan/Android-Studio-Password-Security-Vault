package com.example.secureprogrammanager;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executor;

/**
 Currently obsolete because the unlock activity totally replaces its functionality. However, if for some
 unforeseen reason we need another activity to run a biometric authentication, then this helper becomes helpful
 in providing reusability of code for all of the activities that would be using it.
 */
public class BiometricAuthManager implements AuthenticationInterface{

    private final AppCompatActivity activity;

    public BiometricAuthManager(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void authenticate(Runnable onSuccess, Context context) {

        Executor executor = ContextCompat.getMainExecutor(activity); // asynchronous activity (biometric authentication,
        // toasts, etc) will run on main thread (in this case, they must to avoid crashes since these are inherently UI related activity)

        // creates a BiometricPrompt object with an anonymous authenticationCallback object.
        // Its method (name must exactly match onAuthenticationSucceeded and OnAuthenticationFailed for system to recognize) defines how the system will respond to successful,
        // biometric prompts. This is the method the system will call on successful authentications

        BiometricPrompt prompt = new BiometricPrompt(activity, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result); // default/base super call for all successful authentications
                        onSuccess.run(); // run the onSuccess runnable interface
                    }

                    public void onAuthenticationFailed ()
                    {
                        super.onAuthenticationFailed(); // default/base super call for all failed authentications
                        activity.runOnUiThread(() -> {
                            // show failure message
                            Toast.makeText(context, "Validation failed. Try again", Toast.LENGTH_SHORT).show();

                        });
                    }
                });

        // creates a prompt object which specifies the activity context, the thread to run call backs
        // (the thread stored in the executor variable --> main), and the AuthenticationCallback object which defines
        // what the system will do for each successful response. Also builds the visual of the prompt
        BiometricPrompt.PromptInfo promptInfo =
                new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Unlock Vault")
                        .setSubtitle("Authenticate to continue")
                        .setNegativeButtonText("Cancel")
                        .build();

        prompt.authenticate(promptInfo);
    }
}


package com.example.secureprogrammanager;

import android.content.Context;

public interface AuthenticationInterface {

    public void authenticate (Runnable successful, Context context);
}

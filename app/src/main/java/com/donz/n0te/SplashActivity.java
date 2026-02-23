package com.donz.n0te;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

// guider, do not have relative View.
public class SplashActivity extends SecureActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = getSharedPreferences(StringDef.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        if(!sp.getBoolean(StringDef.SP_KEY_IS_PW_SET, false)){
            // set password
            startActivity(new Intent(this, PwCreateActivity.class));
        }
        else{
            // enter password
            startActivity(new Intent(this, PwEnterActivity.class));
        }
        finish();   // necessary
    }
}

package com.donz.n0te;

import android.app.Application;
import android.util.Log;

import kotlin.jvm.Throws;

public class NoteApplication extends Application {
    private NoteSecureDBHelper dbHelper = null;

    @Override
    public void onCreate() {
        super.onCreate();

        // load cipher library before open database;
        try {
            // 在类第一次被访问时立即加载 native 库
            System.loadLibrary("sqlcipher");
        } catch (Error e) {
            Log.e("SQLCipher", "Native library failed to load: " + e.getMessage());
        } catch(Exception e){
            Log.e("SQLCipher", "Native library failed to load: " + e.getMessage());
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if(dbHelper != null){
            dbHelper.closeDBLink();
            dbHelper = null;
        }
    }

    public NoteSecureDBHelper getDbHelper(){
        return dbHelper;
    }

    // you must catch error or exception and deal it when password wrong.
    public void openDb(char[] pw){
        dbHelper = NoteSecureDBHelper.getInstance(this, pw);
    }

    public void setPwAndOpenDb(char[] pw){
        dbHelper = NoteSecureDBHelper.getInstance(this, pw);
    }
}

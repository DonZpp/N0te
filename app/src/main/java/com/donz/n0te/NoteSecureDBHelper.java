package com.donz.n0te;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;


import net.zetetic.database.DefaultDatabaseErrorHandler;
import net.zetetic.database.sqlcipher.SQLiteOpenHelper;
import net.zetetic.database.sqlcipher.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NoteSecureDBHelper extends SQLiteOpenHelper {
    private static NoteSecureDBHelper instance = null;
    private static final int DATABASE_VERSION = 1;
    private static final int DATABASE_MIN_VERSION = 1;
    public static final String TN_NOTE = "Notes";
    public static final String DB_NAME_NOTE = "Note.db";
    private SQLiteDatabase mWDB = null;
    private static final String TEST_PASSWORD = "123456";

    // we set password here, but we know if the password wrong when call openDBLink
    public static synchronized NoteSecureDBHelper getInstance(Context context, char[] pw) {
        if (instance == null) {
            instance = new NoteSecureDBHelper(context, pw);
            try{
                instance.openDbLink();
            }catch (Throwable e){
                instance = null;    // it's necessary to set the instance to 0
                throw e;
            }
        }
        return instance;
    }

    private NoteSecureDBHelper(Context context, char[] pw) {
        super(context, DB_NAME_NOTE, new String(pw)
                , null
                , DATABASE_VERSION
                , DATABASE_MIN_VERSION
                , new DefaultDatabaseErrorHandler()
                , null
                , true);
        Arrays.fill(pw, '0');   // erase password immediately.
    }

    // it's possible to throw exception or error if password is wrong.
    public void openDbLink() {
        if (mWDB == null || !mWDB.isOpen()) {
            mWDB = this.getWritableDatabase();
        }
    }

    public void closeDBLink() {
        if (mWDB != null && mWDB.isOpen()) {
            mWDB.close();
            mWDB = null;
        }
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String strCreateTable = "CREATE TABLE IF NOT EXISTS " + TN_NOTE + " (" +
                StringDef.KEY_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                StringDef.KEY_NOTE_TITLE + " TEXT," +
                StringDef.KEY_NOTE_CONTENT + " TEXT," +
                StringDef.KEY_NOTE_LAST_MODIFIED + " INTEGER);";
        sqLiteDatabase.execSQL(strCreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // may throw exception or error
    public void insert(NoteData sNoteData) {
        ContentValues sInsertValues = new ContentValues();
        sNoteData.setId(0);
        copyNoteData2ContentVal(sNoteData, sInsertValues);

        mWDB.insert(TN_NOTE, null, sInsertValues);
    }

    static private void copyCursor2NoteData(Cursor cursor, NoteData data){
        data.setId(cursor.getLong(0));
        data.setTitle(cursor.getString(1));
        data.setContent(cursor.getString(2));
        data.setLastModified(cursor.getLong(3));
    }

    // Attention, this function won't copy id!
    static private void copyNoteData2ContentVal(NoteData data, ContentValues vals){
        vals.put(StringDef.KEY_NOTE_TITLE, data.getTitle());
        vals.put(StringDef.KEY_NOTE_CONTENT, data.getContent());
        vals.put(StringDef.KEY_NOTE_LAST_MODIFIED, data.getLastModified());
    }

    public List<NoteData> queryAll(){
        List<NoteData> lstData = new ArrayList<NoteData>();
        // the most recently modified note are at the front.
        Cursor cursor = mWDB.query("SELECT * FROM " + TN_NOTE + " ORDER BY "+ StringDef.KEY_NOTE_LAST_MODIFIED +" DESC");
        while (cursor.moveToNext())
        {
            NoteData data = new NoteData();
            copyCursor2NoteData(cursor, data);
            lstData.add(data);
        }

        lstData.forEach(data->{
            Log.e("zeng", data.toString());
        });
        return lstData;
    }

    public NoteData queryViaTitle(String title){
        Cursor cursor = mWDB.query(TN_NOTE, null, "title=?", new String[]{title}, null, null, null);
        if(cursor.moveToNext()){
            NoteData data = new NoteData();
            copyCursor2NoteData(cursor, data);
            return data;
        }
        else{
            return null;
        }
    }

    public void modifyViaId(NoteData data){
        ContentValues values = new ContentValues();
        copyNoteData2ContentVal(data, values);
        values.put(StringDef.KEY_NOTE_ID, data.getId());    // copyNoteData2ContentVal won't copy id
        mWDB.update(TN_NOTE, values, "id=?", new String[]{Long.toString(data.getId())});
    }

    public void deleteNote(long id){
        mWDB.delete(TN_NOTE, "id=?", new String[]{Long.toString(id)});
    }

    /*public boolean isTitleExist(String title){
        return queryViaTitle(title) == null? false: true;
    }*/

}

package com.donz.n0te;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;




public class EditorActivity extends SecureActivity {

    static boolean bChanged = false;

    private EditText etTitle = null;
    private EditText etContent = null;
    private long iNoteId = 0;
    private String strMode = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // some init
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editor);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editor_main), (v, insets) -> {
            // 1. get height of system bar/ nav bar
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // get height of keyboard
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());

            // get the max value of both two
            int bottomPadding = Math.max(systemBars.bottom, ime.bottom);

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding);

            return insets;
        });

        com.donz.n0te.EditorActivity.bChanged = false;

        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);


        // handle intent, change activity according to intent of new or intent of modify.
        handleIntent();

        // logic when click complete/done button.
        findViewById(R.id.btn_edit_complete).setOnClickListener(new onCompleteListener(this));

        getOnBackPressedDispatcher().addCallback(this, new onCompleteListener(this));

        // monite the text change event
        etContent.addTextChangedListener(new ContentChangeWatcher());
        etTitle.addTextChangedListener(new ContentChangeWatcher());
    }


    private class ContentChangeWatcher implements TextWatcher{
        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            EditorActivity.bChanged = true;
        }
    }


    private void handleIntent(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        strMode = bundle.getString(StringDef.KEY_ACTIVITY_START_MODE);
        switch (strMode){
            case StringDef.VAL_EDITOR_ACTIVITY_MODE_NEW:
                // clear text
                etTitle.setText("");
                etContent.setText("");
                break;
            case StringDef.VAL_EDITOR_ACTIVITY_MODE_MODIFY:
                // show content
                etTitle.setText(bundle.getString(StringDef.KEY_NOTE_TITLE));
                etContent.setText(bundle.getString(StringDef.KEY_NOTE_CONTENT));
                //save the id of note, id be used when save note.
                iNoteId = bundle.getLong(StringDef.KEY_NOTE_ID);
                break;
            default:
                break;
        }
    }

    class onCompleteListener extends OnBackPressedCallback implements View.OnClickListener{
        private EditorActivity actvt;
        private com.donz.n0te.NoteSecureDBHelper dbHelper;
        public onCompleteListener(EditorActivity _editorActivity){
            super(true);
            actvt = _editorActivity;
        }

        private boolean Save(){
            // prepare data
            String strTitle = actvt.etTitle.getText().toString();
            String strContent = actvt.etContent.getText().toString();
            NoteData sData = new NoteData(strTitle, strContent);
            sData.setId(actvt.iNoteId); // iNoteId equals 0 when it's a new note.

            dbHelper = ((NoteApplication)getApplication()).getDbHelper();

            boolean bSuccess = false;
            try{
                if (strMode.equals(StringDef.VAL_EDITOR_ACTIVITY_MODE_NEW)) {
                    dbHelper.insert(sData);
                    bSuccess = true;
                }
                else if(strMode.equals(StringDef.VAL_EDITOR_ACTIVITY_MODE_MODIFY)){
                    dbHelper.modifyViaId(sData);
                    bSuccess = true;
                }
            }
            catch (Exception e){
                Toast.makeText(actvt, "保存异常" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            catch (Error err){
                Toast.makeText(actvt, "保存出错" + err.getMessage(), Toast.LENGTH_SHORT).show();
            }

            if (bSuccess)
            {
                Toast.makeText(actvt, "保存成功", Toast.LENGTH_SHORT).show();
            }
            return bSuccess;
        }

        @Override
        public void onClick(View v){
            boolean bSaveSuccess = false;
            if (EditorActivity.bChanged){
                if (Save()) {
                    // try save change, finish if successful.
                    finish();
                }
            }
            else {  // if there is no change, finish directly.
                finish();
            }
        }

        @Override
        public void handleOnBackPressed() {
            if (EditorActivity.bChanged){
                this.Save();
            }

            // finish whatever
            finish();
        }
    }
}

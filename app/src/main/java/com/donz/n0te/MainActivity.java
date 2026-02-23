package com.donz.n0te;

import android.content.Intent;
import android.os.Bundle;
import android.text.style.BulletSpan;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends SecureActivity{
    private RecyclerView recyclerView;
    private NoteItemAdapter adapter;
    private List<NoteData> lstNotes;

    public MainActivity(){
        super();
        lstNotes = new ArrayList<NoteData>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ///////////BASIC DEAL///////////////
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_new_note).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intentToEditor = new Intent(MainActivity.this, EditorActivity.class);
                intentToEditor.putExtra(StringDef.KEY_ACTIVITY_START_MODE, StringDef.VAL_EDITOR_ACTIVITY_MODE_NEW);
                startActivity(intentToEditor);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.note_list_view), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        /////////Init RecyclerView//////////
        InitRecyclerView();
    }

    private void InitRecyclerView(){
        recyclerView = findViewById(R.id.note_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // read lstNotes from database;
        loadNoteFromDb();

        // init adapter, deal click event
        adapter = new NoteItemAdapter(lstNotes, new NoteItemAdapter.OnItemClickListener(){
            private boolean isDelete = false;
            @Override
            public void onClick(NoteData noteData){
                Intent intentModifyNote = new Intent(MainActivity.this, EditorActivity.class);
                intentModifyNote.putExtra(StringDef.KEY_ACTIVITY_START_MODE, StringDef.VAL_EDITOR_ACTIVITY_MODE_MODIFY);
                intentModifyNote.putExtra(StringDef.KEY_NOTE_ID,  noteData.getId());
                intentModifyNote.putExtra(StringDef.KEY_NOTE_TITLE, noteData.getTitle());
                intentModifyNote.putExtra(StringDef.KEY_NOTE_CONTENT, noteData.getContent());
                startActivity(intentModifyNote);
            }

            @Override
            public void onClickDelBtn(NoteItemAdapter _adapter, NoteData data, int iPosition){
                isDelete = false;
                new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                        .setTitle("CONFIRM")
                        .setMessage("Once the notes are deleted, they cannot be retrieved. Are you sure you want to delete them?") // 提示内容
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, which) -> {
                            ((NoteApplication)getApplication()).getDbHelper().deleteNote(data.getId());
                            lstNotes.remove(iPosition);
                            _adapter.notifyItemRemoved(iPosition);
                            _adapter.notifyItemRangeChanged(iPosition, lstNotes.size() - iPosition);
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });

        recyclerView.setAdapter(adapter);
    }

    // read data from database, and save data to lstNotes;
    // the text show in the activity was come from lstNotes
    // so you should call the funciton while the activity was activate;
    private void loadNoteFromDb(){
        lstNotes.clear();
        lstNotes.addAll(((NoteApplication)getApplication()).getDbHelper().queryAll());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNoteFromDb();
        adapter.notifyDataSetChanged();
    }
}
package com.donz.n0te;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteItemAdapter extends RecyclerView.Adapter<NoteItemAdapter.NoteViewHolder> {

    private List<NoteData> lstNotes;
    private OnItemClickListener fListener;

    public NoteItemAdapter(List<NoteData> _lstNotes, OnItemClickListener _listener){
        this.lstNotes = _lstNotes;
        this.fListener = _listener;
    }

    @Override
    public NoteItemAdapter.NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteItemAdapter.NoteViewHolder(view, 0);
    }

    @Override
    public void onBindViewHolder(NoteItemAdapter.NoteViewHolder holder, int position) {
        NoteData currentNote = lstNotes.get(position);
        holder.getTvTitle().setText(currentNote.getTitle());
        // TODO if we get all content, we cost too much resources,
        //  it's better to generate content_overview and save it to database
        holder.getTvContent().setText(currentNote.getContent());

        holder.itemView.setOnClickListener(v->{
            if (fListener!=null){
                fListener.onClick(currentNote);
            }
        });

        holder.getDelBtn().setOnClickListener(v->{
            if (fListener!=null){
                fListener.onClickDelBtn(this, currentNote, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return lstNotes.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTitle;
        private TextView tvContent;
        private Button btnDel;

        public NoteViewHolder(@NonNull View itemView, long _iIndex) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_recy_view_title);
            tvContent = itemView.findViewById(R.id.tv_recy_view_content);
            btnDel = itemView.findViewById(R.id.btn_delete_note);
        }

        public TextView getTvTitle(){
            return tvTitle;
        }

        public TextView getTvContent(){
            return tvContent;
        }

        public Button getDelBtn(){return btnDel;}

    }

    public interface OnItemClickListener{
        void onClick(NoteData data);
        void onClickDelBtn(NoteItemAdapter adapter, NoteData data, int iPosition);
    }
}

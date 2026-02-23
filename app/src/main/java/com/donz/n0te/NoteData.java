package com.donz.n0te;

public class NoteData {
    private String strTitle = null;
    private String strContent = null;
    private long iLastModified = 0;

    // this value used when note was modified by user and need update
    // , in other word, when the data was read from db(already inserted) the value was valid.
    // When you want to insert a new note to db, just let sqlite generate the id.
    private long id = 0;

    NoteData(){
        iLastModified = System.currentTimeMillis();
    }

    NoteData(String _strTitle, String _strContent){
        iLastModified = System.currentTimeMillis();
        this.strTitle = _strTitle;
        this.strContent = _strContent;
    }

    public void setTitle(String strTitle) {
        this.strTitle = strTitle;
    }

    public void setContent(String strContent) {
        this.strContent = strContent;
    }

    public void setLastModified(long _iLastModified) {
        this.iLastModified = _iLastModified;
    }

    public void updateLastModified(){ iLastModified = System.currentTimeMillis(); }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return strTitle;
    }

    public String getContent() {
        return strContent;
    }

    public long getLastModified() {
        return iLastModified;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "NoteData{" +
                "strTitle='" + strTitle + '\'' +
                ", strContent='" + strContent + '\'' +
                ", iCreatedAt=" + iLastModified +
                ", id=" + id +
                '}';
    }
}

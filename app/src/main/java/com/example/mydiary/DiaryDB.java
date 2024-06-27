package com.example.mydiary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DiaryDB extends SQLiteOpenHelper {
    public static final String TABLE_NAME= "Diary_table";
    public static final String ID="_id";
    public static final String DATE_TIME="Date_Time";
    public static final String TITLE_NAME="Title";
    public static final String CONTANTS_NAME="Contents";

    private static DiaryDB mInstance;
    private DiaryDB(@Nullable Context context) {
        super(context, "Diary.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT,%s TEXT,%s TEXT)",TABLE_NAME,ID,DATE_TIME,TITLE_NAME,CONTANTS_NAME));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s",TITLE_NAME));
        onCreate(db);
    }

    public static DiaryDB getInstance(Context context){
        if(mInstance==null){
            mInstance=new DiaryDB(context);
        }
        return mInstance;
    }
}

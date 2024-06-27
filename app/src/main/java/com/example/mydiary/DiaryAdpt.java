package com.example.mydiary;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class DiaryAdpt extends CursorAdapter {

    public DiaryAdpt(Context context, Cursor c) {
        super(context, c,false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView title=view.findViewById(R.id.title);
        TextView Date=view.findViewById(R.id.dateTime);
        title.setText(cursor.getString(cursor.getColumnIndexOrThrow(DiaryDB.TITLE_NAME)));
        Date.setText(cursor.getString(cursor.getColumnIndexOrThrow(DiaryDB.DATE_TIME)));
    }
}

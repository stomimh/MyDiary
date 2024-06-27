package com.example.mydiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DiaryActivity extends AppCompatActivity {
   private EditText mTitle;
   private  EditText mContents;
   private long mId = -1;
   private boolean mKeyevent=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        mTitle=findViewById(R.id.Title_Edit);
        mContents=findViewById(R.id.Contests_Edit);
        View.OnKeyListener Key=new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(mKeyevent ==false)
                    mKeyevent=true;
                return false;
            }
        };

        mTitle.setOnKeyListener(Key);
        mContents.setOnKeyListener(Key);
        Intent intent= getIntent();
        if(intent !=null){
            mId=intent.getLongExtra("ID",-1);
            mTitle.setText(intent.getStringExtra(DiaryDB.TITLE_NAME));
            mContents.setText(intent.getStringExtra(DiaryDB.CONTANTS_NAME));
        }
    }



    @Override
    public void onBackPressed() {
        ContentValues values=new ContentValues();
        LocalDateTime DataTime=LocalDateTime.now();
        values.put(DiaryDB.DATE_TIME,DataTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일(E) a hh시 mm분")));
        values.put(DiaryDB.TITLE_NAME,mTitle.getText().toString());
        values.put(DiaryDB.CONTANTS_NAME,mContents.getText().toString());

        SQLiteDatabase db=DiaryDB.getInstance(this).getWritableDatabase();
        if(mId == -1){
            long IdCount= db.insert(DiaryDB.TABLE_NAME,null,values);
            if(IdCount == -1){
                Toast.makeText(this,"저장에 실패 했습니다.",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"저장 되었습니다.",Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
            }
        }else{
            if(mKeyevent){
                int count= db.update(DiaryDB.TABLE_NAME,values,DiaryDB.ID+" = "+mId,null);
                if(count ==0){
                    Toast.makeText(this,"수정되지 않았슺니다.",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this,"수정되었습니다.",Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                }
            }
        }
        super.onBackPressed();
    }
}
package com.example.mydiary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.service.autofill.FieldClassification;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class MainActivity extends AppCompatActivity {
     private DiaryAdpt madpt;
    private int REQUEST=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView list=findViewById(R.id.ListView);
        madpt=new DiaryAdpt(this,getCursor());
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        list.setAdapter(madpt);
        findViewById(R.id.fab_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityIfNeeded(new Intent(MainActivity.this, DiaryActivity.class),REQUEST);
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,DiaryActivity.class);
                Cursor cursor=(Cursor) parent.getAdapter().getItem(position);
                intent.putExtra("ID",id);
                intent.putExtra(DiaryDB.TITLE_NAME,cursor.getString(cursor.getColumnIndexOrThrow(DiaryDB.TITLE_NAME)));
                intent.putExtra(DiaryDB.CONTANTS_NAME,cursor.getString(cursor.getColumnIndexOrThrow(DiaryDB.CONTANTS_NAME)));
                startActivityIfNeeded(intent,REQUEST);
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
               new AlertDialog.Builder(MainActivity.this)
                       .setTitle("삭제 메시지")
                       .setMessage("정말로 삭제 하시겠습니까")
                       .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               SQLiteDatabase db=DiaryDB.getInstance(MainActivity.this).getWritableDatabase();
                              int count= db.delete(DiaryDB.TABLE_NAME,DiaryDB.ID+"="+id,null);
                              if(count==0){
                                  Toast.makeText(MainActivity.this,"삭제하지 못했습니다.",Toast.LENGTH_SHORT).show();
                              }else {
                                  Toast.makeText(MainActivity.this,"삭제햐였습니다.",Toast.LENGTH_SHORT).show();
                                  madpt.swapCursor(getCursor());
                              }
                           }
                       })
                       .setNegativeButton("취소",null)
                       .show();

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.backup) {
            DbBackup();
            return true;
        }else if(item.getItemId()==R.id.restore){
            DbRestore();
            return true;
        }else if(item.getItemId()==R.id.delete){
            DiaryDB.getInstance(this).getWritableDatabase().delete(DiaryDB.TABLE_NAME,null,null);
            madpt.swapCursor(getCursor());
        }
        return super.onOptionsItemSelected(item);
    }

    private Cursor getCursor() {
        SQLiteDatabase db=DiaryDB.getInstance(this).getReadableDatabase();
        Cursor cursor=db.query(DiaryDB.TABLE_NAME,null,null,null,null,null,DiaryDB.ID +" DESC");
        return cursor;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST && resultCode==RESULT_OK){
            madpt.swapCursor(getCursor());
        }
    }

    private void DbBackup(){
      try{
          File sd=Environment.getExternalStorageDirectory();
          File data=Environment.getDataDirectory();
         // if(sd.canWrite()){
              File CurrentDB=new File(data,"/data/com.example.mydiary/databases/Diary.db");
              File BackDB=new File(sd,"/Download/Diary.db");
              FileChannel src=new FileInputStream(CurrentDB).getChannel();
              FileChannel dst=new FileOutputStream(BackDB).getChannel();
              dst.transferFrom(src,0,src.size());
              src.close();
              dst.close();
              Toast.makeText(this,"DB를 백업했습니다.",Toast.LENGTH_SHORT).show();
         // }

      }catch(Exception e){
          Toast.makeText(this,"DB를 백업하는데 문제가 발생했습니다.",Toast.LENGTH_SHORT).show();
      }
    }
    private void DbRestore(){
        try{
            File sd=Environment.getExternalStorageDirectory();
            File data=Environment.getDataDirectory();
           // if(sd.canWrite()) {
                File CurrentDB = new File(data, "/Download/Diary.db");
                File RestoreDB= new File(sd, "/data/com.example.mydiary/databases/Diary.db");
                FileChannel src= new FileInputStream(CurrentDB).getChannel();
                FileChannel dst=new FileOutputStream(RestoreDB).getChannel();
                dst.transferFrom(src,0,src.size());
                src.close();
                dst.close();
                Toast.makeText(this, "DB를 복원 했습니다.", Toast.LENGTH_SHORT).show();
          //  }

        }catch (Exception e){
            Toast.makeText(this, "DB를 복원하지 못 했습니다.", Toast.LENGTH_SHORT).show();
        }
    }
    


}
package com.example.note;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    ListView lvNoteList;
    ArrayAdapter<String> adapter;
    ArrayList<String> dataList;

    MyDBHelper myDB;
    SQLiteDatabase sqlDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvNoteList = (ListView) findViewById(R.id.lvNoteList);
        dataList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, dataList);
        lvNoteList.setAdapter(adapter);

        myDB = new MyDBHelper(this);
        sqlDB = myDB.getReadableDatabase();// 데이터를 읽어
        Cursor cursor;
        cursor = sqlDB.rawQuery("select * from noteTBL;",null);

        //movetonext 데이터를 하나씩 읽어 온다.
        //movetonext() 다음 데이터가 없으면 false를 반환한다.
        // false이면 while 반복문은 중단된다.
        while (cursor.moveToNext()){
            // table 0번째가 title, 1번째가 note;
            String title = cursor.getString(0);

            //데이터에 값을 추가한다.
            dataList.add(title);
        }
        //값이 저장하고 리스트뷰를 갱신한다.
        adapter.notifyDataSetChanged();

        // 아이템을 선택했을 때
        lvNoteList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 수정 액티비티로 이동
                    Intent uintent = new Intent(getApplicationContext(), UpdateNoteActivity.class);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //리스트를 꾹 눌렀을 때 삭제하기
        lvNoteList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //선택한 아이템의 db 삭제
                // title 은 리스트 가져온다.
                // title을 가지온다. 어디서? 데이터에서 근데 선택한 positon(index)에서...
                String title = dataList.get(position);
                //sqlDB.execSQL("delete from noteTBL where title = '" + title +"'");
                sqlDB.execSQL("delete from noteTBL where title = '" + title + "'");
                // 선택한아이템 데이터 삭제
                dataList.remove(position);
                //adapter에게 변경되었음을 알려 리스트뷰에게 갱신하게 만든다.
                adapter.notifyDataSetChanged();

                Toast.makeText(getApplicationContext(), "메모가 삭제되었습니다", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        Log.i("note", "inflate menu note");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addItem:
                Log.i("note", "click addItem");
                dlgAddNote();
                break;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String title, note;
            title = data.getStringExtra("Title");
            note = data.getStringExtra("Note");

            //데이터 추가
            dataList.add(title);
            //db에 저장
            // title, note, 둘다 저장
            sqlDB = myDB.getWritableDatabase();
            sqlDB.execSQL("insert into noteTBL values('" +
                    title + "','" +
                    note + "');");
            Toast.makeText(getApplicationContext(), "메모가 저장되었습니다", Toast.LENGTH_SHORT).show();
        }
    }




    void dlgAddNote() {
        Log.i("note", "inflate dlg");
        final View dlgView = (View) View.inflate(getApplicationContext(), R.layout.dialog_addnote, null);
//        LinearLayout line1 = dlgView.findViewById(R.id.line1);
        Button btnNote = dlgView.findViewById(R.id.btnNote);
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog m;
        dlg.setView(dlgView);
        m = dlg.show();
        //
        btnNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), AddNoteActivity.class);
                // startActitvityForResult은 AddNoteActivity 클래스로 갔다가 올 수 있다.
                // startActitvity 일 경우에는 갔다가 올 수는 없다.
                // 올 때 onActivityResult로 온다.
                startActivityForResult(mIntent, 0);
                m.dismiss();

            }
        });
    }

    // DB명
    class MyDBHelper extends SQLiteOpenHelper {
        public MyDBHelper(Context context){
            super(context, "NoteDB", null, 1);
        }
        // Table 명
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE noteTBL (title TEXT, note TEXT);");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists noteTBL");
            onCreate(db); // if exists - 존재하다면
            // noteTBL 존재한다면 삭제한다.
            // 삭제 후 db 생성
        }
    }
}

class MyNoteData{
    String title;
    String note;
}

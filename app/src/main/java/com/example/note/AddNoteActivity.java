package com.example.note;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class AddNoteActivity extends AppCompatActivity {

    EditText edtTitle, edtNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("노트 추가");
        setContentView(R.layout.activity_add_note);
        edtTitle = (EditText)findViewById(R.id.edtTitle);
        edtNote = (EditText)findViewById(R.id.edtNote);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveItem:
                Log.i("note", "click saveItem");
                saveData();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    void saveData(){
        String title, note;
        Intent outIntent = new Intent(getApplicationContext(), AddNoteActivity.class);

        title = edtTitle.getText().toString();
        note = edtNote.getText().toString();

        outIntent.putExtra("Title", title);
        outIntent.putExtra("Note", note);
        // outIntent에 값(title, note)을 담는다.
        setResult(RESULT_OK, outIntent);


        //addNoteAcitivity 종료한다.
        finish();
    }
}

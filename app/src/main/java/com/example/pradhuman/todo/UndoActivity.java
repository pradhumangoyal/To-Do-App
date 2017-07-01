package com.example.pradhuman.todo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UndoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_undo);
        Intent i = getIntent();
        long id = i.getLongExtra(IntentConstants.ID,-1);
        ToDoOpenHelper toDoOpenHelper = new ToDoOpenHelper(this);
        SQLiteDatabase database = toDoOpenHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + ToDoOpenHelper.TO_DO_TABLE_NAME_TWO + " where " + ToDoOpenHelper.TO_DO_ID + "='" + id + "'", null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        String desc = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_DESC));
        String time = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_TIME));
        String title = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_TITLE));
        String category = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_CATEGORY));
        String priority = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_PRIORITY));
        String date = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_DATE));
        TextView prio = (TextView)findViewById(R.id.addShowUndoActivitPriority);
        TextView dateV = (TextView)findViewById(R.id.addShowUndoActivityDate);
        TextView descV = (TextView) findViewById(R.id.addShowUndoActivityEditDesc);
        TextView cat = (TextView) findViewById(R.id.addShowUndoActivityCategory);
        TextView timeV = (TextView) findViewById(R.id.addShowUndoActivityTime) ;
        TextView tit = (TextView) findViewById(R.id.addShowUndoActivityEditTitle);
        prio.setText(priority);
        dateV.setText(date);
        descV.setText(desc);
        cat.setText(category);
        timeV.setText(time);
        tit.setText(title);
        Button b = (Button)findViewById(R.id.addShowUndoActivitySubmitButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
}

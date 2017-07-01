package com.example.pradhuman.todo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class ShowUndoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_undo);
        final ArrayList undoList = new ArrayList();
        undoAdapter undo_adapter = new undoAdapter(this,undoList);
        ToDoOpenHelper toDoOpenHolder = new ToDoOpenHelper(ShowUndoActivity.this);
        ListView listView = (ListView) findViewById(R.id.showUndoListView);
        listView.setAdapter(undo_adapter);
        undoList.clear();
        SQLiteDatabase database = toDoOpenHolder.getReadableDatabase();
        Cursor cursor = database.query(ToDoOpenHelper.TO_DO_TABLE_NAME_TWO,null,null,null,null,null,null);
        while(cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_TITLE));
            String date = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_DATE));
            String description = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_DESC));
            String time = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_TIME));
            long id = cursor.getLong(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_ID));
            int priority = cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_PRIORITY));
            String category = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_CATEGORY));
            int isChecked = cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_IS_CHECKED));
            ToDo obj = new ToDo(title,description,time,priority,category,id,date,isChecked);
            undoList.add(obj);
        }
        undo_adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ShowUndoActivity.this,UndoActivity.class);
                ToDo object = (ToDo)undoList.get(i);
                intent.putExtra(IntentConstants.ID,object.getId());
                startActivity(intent);
            }
        });
    }
}

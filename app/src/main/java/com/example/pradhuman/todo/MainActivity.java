package com.example.pradhuman.todo;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnListButtonClickedListener{

    ArrayList<ToDo> toDoList;
    ListView listView;
    ToDoAdapter listadapter,undoAdapter;
    ArrayList<ToDo> undoToDoList;
    //public ItemTouchHelper itemTouchHelper;
    int justRemovedPos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toDoList = new ArrayList<ToDo>();
        undoToDoList = new ArrayList<ToDo>();
        listView = (ListView) findViewById(R.id.contentMainListView);
        listadapter = new ToDoAdapter(this, toDoList);
      //  listadapter.setOnListSwipedListener(this);
        listadapter.setOnListButtonClickedListener(this);
        listView.setAdapter(listadapter);
        /*
        showAllImage = (ImageView)findViewById(R.id.allToDO);
        showAllImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setAdapter(listadapter);
            }
        });*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AddActivity.class);
                startActivityForResult(intent,2);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(MainActivity.this,AddActivity.class);
                intent.putExtra(IntentConstants.POSITION,i);
                intent.putExtra(IntentConstants.ID,toDoList.get(i).getId());
                startActivityForResult(intent,2);

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final int pos = i;
                final View Viewview = view;
                builder.setTitle("Delete");
                builder.setCancelable(false);
                builder.setMessage("Are you sure to delete??");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ToDo obj = new ToDo(toDoList.get(pos).getTitle(),toDoList.get(pos).getDescription(),toDoList.get(pos).getTime()
                                ,toDoList.get(pos).getPriority(),toDoList.get(pos).getCategory(),toDoList.get(pos).getId(),toDoList.get(pos).getDate(),toDoList.get(pos).isChecked());

                        undoToDoList.add(obj);
                        justRemovedPos = pos;
                        toDoList.remove(pos);
                        removeDatabase(obj.getId(),ToDoOpenHelper.TO_DO_TABLE_NAME);
                        listadapter.notifyDataSetChanged();
                        snackBarShow(Viewview);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
                /*
                return true*/
            }
        });
        updateList();
        SwipeListViewTouchListener touchListener = new SwipeListViewTouchListener(
                listView,
                new SwipeListViewTouchListener.OnSwipeCallback() {
                    @Override
                    public void onSwipeLeft(
                            ListView listView, int[] reverseSortedPositions)
                    {
                        int pos = reverseSortedPositions[0];
                        ToDo obj = toDoList.get(pos);
                        undoToDoList.add(obj);
                        addObjToDatabase(obj,ToDoOpenHelper.TO_DO_TABLE_NAME_TWO);
                        toDoList.remove(pos);
                        justRemovedPos = pos;
                        removeDatabase(obj.getId(),ToDoOpenHelper.TO_DO_TABLE_NAME);
                        listadapter.notifyDataSetChanged();
                        snackBarShow(listView);
                        //onLeftSwipe
                    }
                    @Override
                    public void onSwipeRight(ListView listView, int[] reverseSortedPositions)
                    {
                        //onRightSwipe

                        int pos = reverseSortedPositions[0];
                        ToDo obj = toDoList.get(pos);
                        undoToDoList.add(obj);
                        addObjToDatabase(obj,ToDoOpenHelper.TO_DO_TABLE_NAME_TWO);
                        toDoList.remove(pos);
                        justRemovedPos = pos;
                        removeDatabase(obj.getId(),ToDoOpenHelper.TO_DO_TABLE_NAME);
                        listadapter.notifyDataSetChanged();
                        snackBarShow(listView);
                    }
                },true, // example : left action = dismiss
                true); // example : right action without dismiss animation
        listView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listView.setOnScrollListener(touchListener.makeScrollListener());

    }

    private void updateList() {
        ToDoOpenHelper toDoOpenHolder = new ToDoOpenHelper(MainActivity.this);
        toDoList.clear();
        SQLiteDatabase database = toDoOpenHolder.getWritableDatabase();
        Cursor cursor = database.query(ToDoOpenHelper.TO_DO_TABLE_NAME,null,null,null,null,null,null);
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
            toDoList.add(obj);
        }
        listadapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
         if(id==R.id.action_showUndo){
            Intent i = new Intent(MainActivity.this,ShowUndoActivity.class);
            startActivity(i);
        }
        else if(id==R.id.contactUs){
            Intent i =new Intent();
            i.setAction(Intent.ACTION_SENDTO);
            Uri uri = Uri.parse("mailto:goyalpradhuman21@gmail.com");
            i.putExtra(Intent.EXTRA_SUBJECT,"Contact Developer");
            i.setData(uri);
            startActivity(i);
        }
        else if(id==R.id.aboutUs){
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("https://www.codingninjas.in");
            i.setData(uri);

            startActivity(i);
        }
        else if(id==R.id.birthdayCategory){
             updateListAccordingToCateg("Birthday");
         }
         else if(id==R.id.workCategory){
            updateListAccordingToCateg("Work");
         }
         else if(id == R.id.homeCategory){
             updateListAccordingToCateg("Home");
         }
         else if(id==R.id.allCategory){
             updateList();
         }
        else if(id == R.id.priorityP){
             updateListPriority();
         }else if(id == R.id.priorityA){
             updateList();
         }
        return super.onOptionsItemSelected(item);
    }

    private void updateListPriority() {
        ToDoOpenHelper toDoOpenHolder = new ToDoOpenHelper(MainActivity.this);
        toDoList.clear();
        SQLiteDatabase database = toDoOpenHolder.getWritableDatabase();
        String[] rank = new String[]{ ToDoOpenHelper.TO_DO_PRIORITY,ToDoOpenHelper.TO_DO_ID,
                ToDoOpenHelper.TO_DO_DATE,
                ToDoOpenHelper.TO_DO_TIME,
                ToDoOpenHelper.TO_DO_TITLE,
                ToDoOpenHelper.TO_DO_CATEGORY,
                ToDoOpenHelper.TO_DO_IS_CHECKED,
                ToDoOpenHelper.TO_DO_DESC};
        Cursor cursor = database.query(ToDoOpenHelper.TO_DO_TABLE_NAME,rank,null,null,null,null,ToDoOpenHelper.TO_DO_PRIORITY);
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
            toDoList.add(obj);
        }
        listadapter.notifyDataSetChanged();
    }


    private void updateListAccordingToCateg(String reqiredCategory){
        ToDoOpenHelper toDoOpenHolder = new ToDoOpenHelper(MainActivity.this);
        toDoList.clear();
        SQLiteDatabase database = toDoOpenHolder.getWritableDatabase();
        Cursor cursor = database.query(ToDoOpenHelper.TO_DO_TABLE_NAME,null,null,null,null,null,null);
        while(cursor.moveToNext()){
            String category = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_CATEGORY));
            if(!category.equals(reqiredCategory))
                continue;
            String title = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_TITLE));
            String date = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_DATE));
            String description = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_DESC));
            String time = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_TIME));
            long id = cursor.getLong(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_ID));
            int priority = cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_PRIORITY));
            //String category = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_CATEGORY));
            int isChecked = cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_IS_CHECKED));
            ToDo obj = new ToDo(title,description,time,priority,category,id,date,isChecked);
            toDoList.add(obj);
        }
        listadapter.notifyDataSetChanged();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==2&&resultCode!=6)
        {
            long id = data.getLongExtra(IntentConstants.ID,-1);
            ToDoOpenHelper toDoOpenHolder = new ToDoOpenHelper(this);
            SQLiteDatabase database = toDoOpenHolder.getWritableDatabase();
            //Toast.makeText(this,id+"",Toast.LENGTH_SHORT).show();
            Cursor cursor = database.rawQuery("select * from " + ToDoOpenHelper.TO_DO_TABLE_NAME + " where " + ToDoOpenHelper.TO_DO_ID + "='" + id + "'", null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            String desc = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_DESC));
            String time = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_TIME));
            String title = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_TITLE));
            String category = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_CATEGORY));
            int priority = cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_PRIORITY));
            String date = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_DATE));

            if(resultCode==33) // Insert
            {
                ToDo obj = new ToDo(title,desc,time,priority,category,id,date,0);
                toDoList.add(obj);

            }
            else if(resultCode==44)
            {
                int pos = data.getIntExtra(IntentConstants.POSITION,-1);
                toDoList.get(pos).setCategory(category);
                toDoList.get(pos).setChecked(0);
                toDoList.get(pos).setDate(date);
                toDoList.get(pos).setDescription(desc);
                toDoList.get(pos).setTime(time);
                toDoList.get(pos).setPriority(priority);
                updateList();
            }
            listadapter.notifyDataSetChanged();
        }
    }

    @Override
    public void listButtonClicked(View v, int pos) {
        ToDo obj = toDoList.get(pos);
        undoToDoList.add(obj);
        addObjToDatabase(obj,ToDoOpenHelper.TO_DO_TABLE_NAME_TWO);
        toDoList.remove(pos);
        justRemovedPos = pos;
        removeDatabase(obj.getId(),ToDoOpenHelper.TO_DO_TABLE_NAME);
        listadapter.notifyDataSetChanged();
        snackBarShow(v);
    }
    public void removeDatabase(long id,String TABLE_NAME)
    {
        ToDoOpenHelper toDoOpenHolder = new ToDoOpenHelper(this);
        SQLiteDatabase database = toDoOpenHolder.getWritableDatabase();
        database.delete(TABLE_NAME,ToDoOpenHelper.TO_DO_ID +"="+id,null);
    }
    public void snackBarShow(View v){
        Snackbar.make(v, "Click To Undo", Snackbar.LENGTH_LONG)
                       .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                undoItem();
                            }
                        }).setActionTextColor(Color.RED).show();
    }

    void undoItem(){
        if(undoToDoList.isEmpty())
            return;
        ToDo obj = undoToDoList.get(undoToDoList.size()-1);
        addObjToDatabase(obj,ToDoOpenHelper.TO_DO_TABLE_NAME);
        removeDatabase(undoToDoList.get(undoToDoList.size()-1).getId(),ToDoOpenHelper.TO_DO_TABLE_NAME_TWO);
        undoToDoList.remove(undoToDoList.size()-1);
        toDoList.add(justRemovedPos,obj);
        listadapter.notifyDataSetChanged();
    }
    void addObjToDatabase(ToDo obj, String TABLE_NAME){
        ContentValues cv = new ContentValues();
        cv.put(ToDoOpenHelper.TO_DO_TITLE,obj.getTitle());
        cv.put(ToDoOpenHelper.TO_DO_CATEGORY, obj.getCategory());
        cv.put(ToDoOpenHelper.TO_DO_DESC, obj.getDescription());
        cv.put(ToDoOpenHelper.TO_DO_IS_CHECKED, obj.isChecked());
        cv.put(ToDoOpenHelper.TO_DO_PRIORITY,obj.getPriority());
        cv.put(ToDoOpenHelper.TO_DO_TIME,obj.getPriority());
        cv.put(ToDoOpenHelper.TO_DO_DATE,obj.getDate());
        cv.put(ToDoOpenHelper.TO_DO_ID,obj.getId());;
        ToDoOpenHelper toDoOpenHolder = new ToDoOpenHelper(this);
        SQLiteDatabase database = toDoOpenHolder.getWritableDatabase();
        database.insert(TABLE_NAME,null,cv);
    }

   /* @Override
    public void listViewSwiped(View v, int pos) {
        listButtonClicked(v,pos);
    }*/
}

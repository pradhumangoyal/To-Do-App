package com.example.pradhuman.todo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.security.acl.Group;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnListButtonClickedListener,NavigationView.OnNavigationItemSelectedListener {
 //   MainActivity refrence = MainActivity.this;
    ArrayList<ToDo> toDoList;
    ListView listView;
    ToDoAdapter listadapter, undoAdapter;
    ArrayList<ToDo> undoToDoList;
    ImageView imageViewInvisible;
    /*AlarmManager alarmManager;
    Intent alarmIntent;*/
    //public ItemTouchHelper itemTouchHelper;
    int justRemovedPos;
    ToDo justDeletedObj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageViewInvisible = (ImageView) findViewById(R.id.imageViewTodoIn);
        toDoList = new ArrayList<ToDo>();
        undoToDoList = new ArrayList<ToDo>();
        listView = (ListView) findViewById(R.id.contentMainListView);
        listadapter = new ToDoAdapter(this, toDoList);
        //  listadapter.setOnListSwipedListener(this);
        listadapter.setOnListButtonClickedListener(this);
        listView.setAdapter(listadapter);
       /* alarmManager = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
        alarmIntent =  new Intent(MainActivity.this,AlarmReceiver.class);*/
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
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(intent, 2);
            }
        });

        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                intent.putExtra(IntentConstants.POSITION, i);
                intent.putExtra(IntentConstants.ID, toDoList.get(i).getId());
                startActivityForResult(intent, 2);

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, final View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final int pos = i;
                final View Viewview = view;
                builder.setTitle("Delete");
                builder.setCancelable(false);
                builder.setMessage("Are you sure to delete??");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this,"Task Deleted",Toast.LENGTH_SHORT).show();
                        final ToDo justDeletedObj = new ToDo(toDoList.get(pos).getTitle(), toDoList.get(pos).getDescription(), toDoList.get(pos).getTime()
                                , toDoList.get(pos).getPriority(), toDoList.get(pos).getCategory(), toDoList.get(pos).getId(), toDoList.get(pos).getDate(), toDoList.get(pos).isChecked());

                        justRemovedPos = pos;
                        toDoList.remove(pos);
                        deleteAlarm(justDeletedObj);
                        removeDatabase(justDeletedObj.getId(), ToDoOpenHelper.TO_DO_TABLE_NAME);
                        listadapter.notifyDataSetChanged();
                        Snackbar.make(view, "Click To Undo", Snackbar.LENGTH_LONG)
                                .setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        toDoList.add(justRemovedPos,justDeletedObj);
                                        listadapter.notifyDataSetChanged();
                                        insertAlarm(justDeletedObj);
                                        Toast.makeText(MainActivity.this,"Task Restored",Toast.LENGTH_SHORT).show();
                                    }
                                }).setActionTextColor(Color.RED).show();
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
                            ListView listView, int[] reverseSortedPositions) {
                        int pos = reverseSortedPositions[0];
                        ToDo obj = toDoList.get(pos);
                        Toast.makeText(MainActivity.this,"Task Deleted",Toast.LENGTH_SHORT).show();
                        undoToDoList.add(obj);
                        addObjToDatabase(obj, ToDoOpenHelper.TO_DO_TABLE_NAME_TWO);
                        toDoList.remove(pos);
                        justRemovedPos = pos;
                        removeDatabase(obj.getId(), ToDoOpenHelper.TO_DO_TABLE_NAME);
                        listadapter.notifyDataSetChanged();
                        snackBarShow(listView);
                        //onLeftSwipe
                    }

                    @Override
                    public void onSwipeRight(ListView listView, int[] reverseSortedPositions) {
                        //onRightSwipe

                        int pos = reverseSortedPositions[0];
                        Toast.makeText(MainActivity.this,"Task Completed",Toast.LENGTH_SHORT).show();
                        final ToDo justDeletedObj = new ToDo(toDoList.get(pos).getTitle(), toDoList.get(pos).getDescription(), toDoList.get(pos).getTime()
                                , toDoList.get(pos).getPriority(), toDoList.get(pos).getCategory(), toDoList.get(pos).getId(), toDoList.get(pos).getDate(), toDoList.get(pos).isChecked());

                        justRemovedPos = pos;
                        toDoList.remove(pos);
                        deleteAlarm(justDeletedObj);
                        removeDatabase(justDeletedObj.getId(), ToDoOpenHelper.TO_DO_TABLE_NAME);
                        listadapter.notifyDataSetChanged();
                        Snackbar.make(listView, "Click To Undo", Snackbar.LENGTH_LONG)
                                .setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        toDoList.add(justRemovedPos,justDeletedObj);
                                        listadapter.notifyDataSetChanged();
                                        insertAlarm(justDeletedObj);
                                        Toast.makeText(MainActivity.this,"Task Restored",Toast.LENGTH_SHORT).show();
                                    }
                                }).setActionTextColor(Color.RED).show();
                    }
                }, true, // example : left action = dismiss
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
        //Cursor cursor = database.query(ToDoOpenHelper.TO_DO_TABLE_NAME, null, null, null, null, null, null);
        String[] rank = new String[]{ToDoOpenHelper.TO_DO_DATE, ToDoOpenHelper.TO_DO_TIME,
                ToDoOpenHelper.TO_DO_PRIORITY,
                ToDoOpenHelper.TO_DO_ID,
                ToDoOpenHelper.TO_DO_TITLE,
                ToDoOpenHelper.TO_DO_CATEGORY,
                ToDoOpenHelper.TO_DO_IS_CHECKED,
                ToDoOpenHelper.TO_DO_DESC};
        Cursor cursor = database.query(ToDoOpenHelper.TO_DO_TABLE_NAME, rank, null, null, null, null, ToDoOpenHelper.TO_DO_DATE);

        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_TITLE));
            String date = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_DATE));
            String description = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_DESC));
            String time = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_TIME));
            long id = cursor.getLong(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_ID));
            int priority = cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_PRIORITY));
            String category = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_CATEGORY));
            int isChecked = cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_IS_CHECKED));
            ToDo obj = new ToDo(title, description, time, priority, category, id, date, isChecked);
            toDoList.add(obj);
        }
        listadapter.notifyDataSetChanged();
        checkEmpty();
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
*/
/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_showUndo) {
            Intent i = new Intent(MainActivity.this, ShowUndoActivity.class);
            startActivity(i);
        } else if (id == R.id.contactUs) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_SENDTO);
            Uri uri = Uri.parse("mailto:goyalpradhuman21@gmail.com");
            i.putExtra(Intent.EXTRA_SUBJECT, "Contact Developer");
            i.setData(uri);
            startActivity(i);
        } else if (id == R.id.aboutUs) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("https://www.codingninjas.in");
            i.setData(uri);

            startActivity(i);
        } else if (id == R.id.birthdayCategory) {
            updateListAccordingToCateg("Birthday");
        } else if (id == R.id.workCategory) {
            updateListAccordingToCateg("Work");
        } else if (id == R.id.homeCategory) {
            updateListAccordingToCateg("Home");
        } else if (id == R.id.allCategory) {
            updateList();
        } else if (id == R.id.priorityP) {
            updateListPriority();
        } else if (id == R.id.priorityA) {
            updateList();
        } else if (id == R.id.removeAll) {
            int size = toDoList.size();
            for (int pos = 0; pos < size; pos++) {
                ToDo obj = toDoList.get(pos);
                undoToDoList.add(obj);
                addObjToDatabase(obj, ToDoOpenHelper.TO_DO_TABLE_NAME_TWO);
                toDoList.remove(pos);
                justRemovedPos = pos;
                removeDatabase(obj.getId(), ToDoOpenHelper.TO_DO_TABLE_NAME);
            }
            checkEmpty();
            listadapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }*/

    private void updateListPriority() {
        ToDoOpenHelper toDoOpenHolder = new ToDoOpenHelper(MainActivity.this);
        toDoList.clear();
        SQLiteDatabase database = toDoOpenHolder.getWritableDatabase();
        String[] rank = new String[]{ToDoOpenHelper.TO_DO_PRIORITY, ToDoOpenHelper.TO_DO_ID,
                ToDoOpenHelper.TO_DO_DATE,
                ToDoOpenHelper.TO_DO_TIME,
                ToDoOpenHelper.TO_DO_TITLE,
                ToDoOpenHelper.TO_DO_CATEGORY,
                ToDoOpenHelper.TO_DO_IS_CHECKED,
                ToDoOpenHelper.TO_DO_DESC};
        Cursor cursor = database.query(ToDoOpenHelper.TO_DO_TABLE_NAME, rank, null, null, null, null, ToDoOpenHelper.TO_DO_PRIORITY);
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_TITLE));
            String date = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_DATE));
            String description = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_DESC));
            String time = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_TIME));
            long id = cursor.getLong(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_ID));
            int priority = cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_PRIORITY));
            String category = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_CATEGORY));
            int isChecked = cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_IS_CHECKED));
            ToDo obj = new ToDo(title, description, time, priority, category, id, date, isChecked);
            toDoList.add(obj);
        }
        listadapter.notifyDataSetChanged();
    }


    private void updateListAccordingToCateg(String reqiredCategory) {
        ToDoOpenHelper toDoOpenHolder = new ToDoOpenHelper(MainActivity.this);
        toDoList.clear();
        SQLiteDatabase database = toDoOpenHolder.getWritableDatabase();
        Cursor cursor = database.query(ToDoOpenHelper.TO_DO_TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String category = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_CATEGORY));
            if (!category.equals(reqiredCategory))
                continue;
            String title = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_TITLE));
            String date = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_DATE));
            String description = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_DESC));
            String time = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_TIME));
            long id = cursor.getLong(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_ID));
            int priority = cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_PRIORITY));
            //String category = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_CATEGORY));
            int isChecked = cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_IS_CHECKED));
            ToDo obj = new ToDo(title, description, time, priority, category, id, date, isChecked);
            toDoList.add(obj);
        }
        listadapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2 && resultCode != 6) {
            long id = data.getLongExtra(IntentConstants.ID, -1);
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

            if (resultCode == 33) // Insert
            {

                ToDo obj = new ToDo(title, desc, time, priority, category, id, date, 0);
                toDoList.add(obj);
                insertAlarm(obj);
            } else if (resultCode == 44) {
                int pos = data.getIntExtra(IntentConstants.POSITION, -1);
                ToDo obj = toDoList.get(pos);
                deleteAlarm(obj);
                toDoList.get(pos).setCategory(category);
                toDoList.get(pos).setChecked(0);
                toDoList.get(pos).setDate(date);
                toDoList.get(pos).setDescription(desc);
                toDoList.get(pos).setTime(time);
                toDoList.get(pos).setPriority(priority);
                updateList();
                insertAlarm(obj);
            }
            listadapter.notifyDataSetChanged();
        }
        checkEmpty();
    }

    @Override
    public void listButtonClicked(View v, int pos) {
        Toast.makeText(MainActivity.this,"Task Completed",Toast.LENGTH_SHORT).show();
        ToDo obj = toDoList.get(pos);
        deleteAlarm(obj);
        undoToDoList.add(obj);
        addObjToDatabase(obj, ToDoOpenHelper.TO_DO_TABLE_NAME_TWO);
        toDoList.remove(pos);
        checkEmpty();
        justRemovedPos = pos;
        removeDatabase(obj.getId(), ToDoOpenHelper.TO_DO_TABLE_NAME);
        listadapter.notifyDataSetChanged();
        snackBarShow(v);

    }

    public void removeDatabase(long id, String TABLE_NAME) {
        ToDoOpenHelper toDoOpenHolder = new ToDoOpenHelper(this);
        SQLiteDatabase database = toDoOpenHolder.getWritableDatabase();
        database.delete(TABLE_NAME, ToDoOpenHelper.TO_DO_ID + "=" + id, null);
        checkEmpty();
    }

    public void snackBarShow(View v) {
        Snackbar.make(v, "Click To Undo", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        undoItem();
                    }
                }).setActionTextColor(Color.RED).show();
    }

    void undoItem() {
        if (undoToDoList.isEmpty())
            return;
        ToDo obj = undoToDoList.get(undoToDoList.size() - 1);
        insertAlarm(obj);
        addObjToDatabase(obj, ToDoOpenHelper.TO_DO_TABLE_NAME);
        removeDatabase(undoToDoList.get(undoToDoList.size() - 1).getId(), ToDoOpenHelper.TO_DO_TABLE_NAME_TWO);
        undoToDoList.remove(undoToDoList.size() - 1);
        toDoList.add(justRemovedPos, obj);
        listadapter.notifyDataSetChanged();
        checkEmpty();
        Toast.makeText(MainActivity.this,"Task Restored",Toast.LENGTH_SHORT).show();
    }

    void addObjToDatabase(ToDo obj, String TABLE_NAME) {
        ContentValues cv = new ContentValues();
        cv.put(ToDoOpenHelper.TO_DO_TITLE, obj.getTitle());
        cv.put(ToDoOpenHelper.TO_DO_CATEGORY, obj.getCategory());
        cv.put(ToDoOpenHelper.TO_DO_DESC, obj.getDescription());
        cv.put(ToDoOpenHelper.TO_DO_IS_CHECKED, obj.isChecked());
        cv.put(ToDoOpenHelper.TO_DO_PRIORITY, obj.getPriority());
        cv.put(ToDoOpenHelper.TO_DO_TIME, obj.getTime());
        cv.put(ToDoOpenHelper.TO_DO_DATE, obj.getDate());
        cv.put(ToDoOpenHelper.TO_DO_ID, obj.getId());
        ;
        ToDoOpenHelper toDoOpenHolder = new ToDoOpenHelper(this);
        SQLiteDatabase database = toDoOpenHolder.getWritableDatabase();
        database.insert(TABLE_NAME, null, cv);
    }

    /* @Override
     public void listViewSwiped(View v, int pos) {
         listButtonClicked(v,pos);
     }*/
    void checkEmpty() {
        if (toDoList.isEmpty())
            imageViewInvisible.setVisibility(View.VISIBLE);
        else
            imageViewInvisible.setVisibility(View.INVISIBLE);
    }

    public void insertAlarm(ToDo obj) {
        Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        alarmIntent.putExtra("__id__",obj.getId());
        AlarmManager alarmManager = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, (int) obj.getId(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long myEpoch = 0;
        try {
            myEpoch = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).parse(obj.getDate() + " " + obj.getTime() + ":00").getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //  Log.d("HelloData","I am here"+ myEpoch + " "+  System.currentTimeMillis());
        alarmManager.set(AlarmManager.RTC, myEpoch, pendingIntent);

    }

    private void deleteAlarm(ToDo obj) {
        Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
       // alarmIntent.putExtra("idbro",obj.getId());
        AlarmManager alarmManager = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, (int) obj.getId(), alarmIntent, PendingIntent.FLAG_NO_CREATE);
        //  Log.d("HelloData","I am here"+ myEpoch + " "+  System.currentTimeMillis());
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_showUndo) {
            Intent i = new Intent(MainActivity.this, ShowUndoActivity.class);
            startActivity(i);
        } else if (id == R.id.contactUs) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_SENDTO);
            Uri uri = Uri.parse("mailto:goyalpradhuman21@gmail.com");
            i.putExtra(Intent.EXTRA_SUBJECT, "Contact Developer");
            i.setData(uri);
            startActivity(i);
        } else if (id == R.id.aboutUs) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("https://www.codingninjas.in");
            i.setData(uri);

            startActivity(i);
        } else if (id == R.id.birthdayCategory) {
            updateListAccordingToCateg("Birthday");
        } else if (id == R.id.workCategory) {
            updateListAccordingToCateg("Work");
        } else if (id == R.id.homeCategory) {
            updateListAccordingToCateg("Home");
        } else if (id == R.id.allCategory) {
            updateList();
        } else if (id == R.id.priorityP) {
            updateListPriority();
        } else if (id == R.id.priorityA) {
            updateList();
        } else if (id == R.id.removeAll) {
            int size = toDoList.size();
            for (int pos = 0; pos < size; pos++) {
                ToDo obj = toDoList.get(pos);
                undoToDoList.add(obj);
                addObjToDatabase(obj, ToDoOpenHelper.TO_DO_TABLE_NAME_TWO);
                toDoList.remove(pos);
                justRemovedPos = pos;
                removeDatabase(obj.getId(), ToDoOpenHelper.TO_DO_TABLE_NAME);
            }
            checkEmpty();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        listadapter.notifyDataSetChanged();
        return true;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*public void onClickViewByCategory(MenuItem item) {
        if(findViewById(R.id.hideMeCategory).getVisibility()==View.VISIBLE)
            findViewById(R.id.hideMeCategory).setVisibility(View.INVISIBLE);
        else
            findViewById(R.id.hideMeCategory).setVisibility(View.VISIBLE);
    }

    public void onClickSortBy(MenuItem item) {
        if(findViewById(R.id.hideMeSortBy).getVisibility()==View.VISIBLE){
            findViewById(R.id.hideMeSortBy).setVisibility(View.INVISIBLE);
        }else
            findViewById(R.id.hideMeSortBy).setVisibility(View.VISIBLE);
    }

    public void onClickHelpMe(MenuItem item) {
        if(findViewById(R.id.HideMeHelpMe).getVisibility()==View.VISIBLE)
            findViewById(R.id.HideMeHelpMe).setVisibility(View.INVISIBLE);
        else
            findViewById(R.id.HideMeHelpMe).setVisibility(View.INVISIBLE);
    }*/
}

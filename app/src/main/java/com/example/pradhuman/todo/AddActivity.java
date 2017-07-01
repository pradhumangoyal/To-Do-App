package com.example.pradhuman.todo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddActivity extends AppCompatActivity {

    String[] category = {"Work",
            "Home",
            "Birthday"};
    String[] priority = {"1", "2", "3"};
    EditText titleEditText;
    EditText descEditText;
    EditText dateEditText;
    EditText timeEditText;
    Button button;
    long epoch;
    long second;
    //String dateFormatter = "EEE MMM, YYYY"
    long id;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        final Spinner spinnerCategory = (Spinner) findViewById(R.id.addActivityCategory);
        ArrayAdapter catAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, category);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);
        final Spinner spinnerPriority = (Spinner) findViewById(R.id.addActivitPriority);
        ArrayAdapter priorityAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, priority);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);
        titleEditText = (EditText) findViewById(R.id.addActivityEditTitle);
        descEditText = (EditText) findViewById(R.id.addActivityEditDesc);
        dateEditText = (EditText) findViewById(R.id.addActivityDate);
        timeEditText = (EditText) findViewById(R.id.addActivityTime);
        button = (Button) findViewById(R.id.addActivitySubmitButton);
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        if(bundle!=null){
            id = (long)bundle.get(IntentConstants.ID);
            position = (int)bundle.get(IntentConstants.POSITION);}
        else
            id = -1;
        if (id != -1) {
            Log.i("i==-1", "yes");
            ToDoOpenHelper toDoOpenHolder = new ToDoOpenHelper(this);
            SQLiteDatabase database = toDoOpenHolder.getWritableDatabase();
            Cursor cursor = database.rawQuery("select * from " + ToDoOpenHelper.TO_DO_TABLE_NAME + " where " + ToDoOpenHelper.TO_DO_ID + "='" + id + "'", null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            String desc = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_DESC));
            String time = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_TIME));
            String title = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_TITLE));
            String category = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_CATEGORY));
            String priority = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_PRIORITY));
            String date = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TO_DO_DATE));
            titleEditText.setText(title);
            ArrayAdapter myAdp = (ArrayAdapter) spinnerCategory.getAdapter();
            int spinnerPosition = myAdp.getPosition(category);
            spinnerCategory.setSelection(spinnerPosition);
            ArrayAdapter Adp = (ArrayAdapter) spinnerPriority.getAdapter();
            int prPosition = Adp.getPosition(priority);
            spinnerPriority.setSelection(prPosition);
            descEditText.setText(desc);
            dateEditText.setText(date);
            timeEditText.setText(time);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleEditText.getText().toString();
                String category = spinnerCategory.getSelectedItem().toString();
                String priority = spinnerPriority.getSelectedItem().toString();
                String date = dateEditText.getText().toString();
                String time = timeEditText.getText().toString();
                String desc = descEditText.getText().toString();
                boolean flag = false;
                Log.e("title.trim", title.trim());
                if (title.trim().isEmpty()) {
                    Log.e("title.trim", title.trim());
                    titleEditText.setError("Title can't be Empty!");
                    flag = true;
                }
                if (time.trim().isEmpty()) {
                    timeEditText.setError("Time can't be Empty!");
                    flag = true;
                }
                if (date.trim().isEmpty()) {
                    dateEditText.setError("Date Can't be Empty");
                    flag = true;
                }
                if (flag)
                    return;
                int priorit = Integer.parseInt(priority);
                ToDoOpenHelper toDoOpenHelper = new ToDoOpenHelper(AddActivity.this);
                SQLiteDatabase db = toDoOpenHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put(ToDoOpenHelper.TO_DO_TITLE,title);
                cv.put(ToDoOpenHelper.TO_DO_CATEGORY, category);
                cv.put(ToDoOpenHelper.TO_DO_DESC, desc);
                cv.put(ToDoOpenHelper.TO_DO_IS_CHECKED, 0);
                cv.put(ToDoOpenHelper.TO_DO_PRIORITY,priorit);
                cv.put(ToDoOpenHelper.TO_DO_TIME,time);
                cv.put(ToDoOpenHelper.TO_DO_DATE,date);
                if (id == -1) {
                    long __id = db.insert(ToDoOpenHelper.TO_DO_TABLE_NAME,null,cv);
                    Intent in = new Intent();
                    in.putExtra(IntentConstants.ID,__id);
                    setResult(33,in);
                    finish();
                   // startActivity(in);

                } else {
                    db.update(ToDoOpenHelper.TO_DO_TABLE_NAME,cv,ToDoOpenHelper.TO_DO_ID + "=" +id,null );
                    Intent i = new Intent();
                    i.putExtra(IntentConstants.POSITION,position);
                    i.putExtra(IntentConstants.ID,id);
                    setResult(44,i);
                    finish();
                }
            }
        });

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar newCalendar = Calendar.getInstance();
                int month = newCalendar.get(Calendar.MONTH);  // Current month
                int year = newCalendar.get(Calendar.YEAR);   // Current year
                showDatePicker(AddActivity.this, year, month, 1);
            }
        });
        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        timeEditText.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
    }


    public void showDatePicker(Context context, int initialYear, int initialMonth, int initialDay) {

        // Creating datePicker dialog object
        // It requires context and listener that is used when a date is selected by the user.

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    //This method is called when the user has finished selecting a date.
                    // Arguments passed are selected year, month and day
                    @Override
                    public void onDateSet(DatePicker datepicker, int year, int month, int day) {

                        // To get epoch, You can store this date(in epoch) in database
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day);
                        epoch = calendar.getTime().getTime();
                        // Setting date selected in the edit text
                        dateEditText.setText(day + "/" + (month + 1) + "/" + year);
                    }
                }, initialYear, initialMonth, initialDay);

        //Call show() to simply show the dialog
        datePickerDialog.show();

    }

    @Override
    public void onBackPressed() {
        setResult(6);
        finish();
    }
}


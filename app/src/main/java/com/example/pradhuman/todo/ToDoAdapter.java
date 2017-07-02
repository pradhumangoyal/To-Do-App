package com.example.pradhuman.todo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;

/**
 * Created by Pradhuman on 29-06-2017.
 */

public class ToDoAdapter extends ArrayAdapter  {
    Context context;
    ArrayList<ToDo> arrayList;

    OnListButtonClickedListener listener;
  // OnListSwipedListener listenerSwipe;
    void setOnListButtonClickedListener(OnListButtonClickedListener listener){
        this.listener = listener;
    }/*
    void setOnListSwipedListener(OnListSwipedListener listenerSwipe){
        this.listenerSwipe = listenerSwipe;
    }
*/

    public ToDoAdapter(@NonNull Context context, ArrayList<ToDo> toDoArrayList) {
        super(context, 0);
        this.context = context;
        this.arrayList = toDoArrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }
    static class ToDoViewHolder {
        TextView titleTextView;
        TextView dateTextView;
        TextView timeTextView;
        Button button;
        ImageView imageView;
        ToDoViewHolder(TextView titleTextView, TextView dateTextView,TextView timeTextView,Button button,ImageView imageView){
            this.titleTextView = titleTextView;
            this.dateTextView = dateTextView;
            this.timeTextView = timeTextView;
            this.button = button;
            this.imageView = imageView;
        }
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item,null);
            TextView titleTextView = convertView.findViewById(R.id.listItemTitleView);
            TextView dateTextView = convertView.findViewById(R.id.listItemDateView);
            TextView timeTextView = convertView.findViewById(R.id.listItemTimeView);
            Button button = (Button) convertView.findViewById(R.id.listItemDoneButton);
            ImageView imageView = (ImageView)convertView.findViewById(R.id.image_view);
            ToDoViewHolder toDoViewHolder = new ToDoViewHolder(titleTextView,dateTextView,timeTextView,button,imageView);
            convertView.setTag(toDoViewHolder);
        }
       // float _xSwipe1,_xSwipe2;
        ToDo temp = arrayList.get(position);
        ToDoViewHolder toDoViewHolder =  (ToDoViewHolder) convertView.getTag();
        toDoViewHolder.dateTextView.setText(temp.getDate());
        toDoViewHolder.timeTextView.setText(temp.getTime());
        toDoViewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.listButtonClicked(v, position);

            }
        });
        /*final View v = convertView;
        if(convertView==null)
            Toast.makeText(context,"ConvertView Null!",Toast.LENGTH_SHORT).show();
        if(v==null)
            Toast.makeText(context,"V null",Toast.LENGTH_SHORT).show();
        convertView.setOnTouchListener(new OnSwipeTouchListener(){
            @Override
            public void onSwipeLeft() {
                Toast.makeText(context,"Hello",Toast.LENGTH_SHORT).show();
                if(listenerSwipe!=null)
                    listenerSwipe.listViewSwiped(v,position);
            }
        });*/
        TextDrawable drawable = TextDrawable.builder().beginConfig()
                .textColor(Color.WHITE)
                .useFont(Typeface.DEFAULT)
                .toUpperCase()
                .endConfig()
                .buildRound(temp.getTitle().substring(0, 1), temp.color);
        toDoViewHolder.imageView.setImageDrawable(drawable);
        toDoViewHolder.titleTextView.setText(temp.getTitle());
        return convertView;
    }

}
interface OnListButtonClickedListener{
    void listButtonClicked(View v, int pos);
}
/*
interface OnListSwipedListener{
    void listViewSwiped(View v, int pos);
}*/

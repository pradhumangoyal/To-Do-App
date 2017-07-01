package com.example.pradhuman.todo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;

/**
 * Created by Pradhuman on 01-07-2017.
 */

public class undoAdapter extends ArrayAdapter {
    Context context;
    ArrayList<ToDo> arrayList;

    public undoAdapter(@NonNull Context context, ArrayList<ToDo> toDoArrayList) {
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
        ImageView imageView;
        ToDoViewHolder(TextView titleTextView, TextView dateTextView,TextView timeTextView,ImageView imageView){
            this.titleTextView = titleTextView;
            this.dateTextView = dateTextView;
            this.timeTextView = timeTextView;
            this.imageView = imageView;
        }
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.undo_list,null);
            TextView titleTextView = convertView.findViewById(R.id.listItemTitleView);
            TextView dateTextView = convertView.findViewById(R.id.listItemDateView);
            TextView timeTextView = convertView.findViewById(R.id.listItemTimeView);
            ImageView imageView = (ImageView)convertView.findViewById(R.id.image_view2);
            undoAdapter.ToDoViewHolder toDoViewHolder = new undoAdapter.ToDoViewHolder(titleTextView,dateTextView,timeTextView,imageView);
            convertView.setTag(toDoViewHolder);
        }
        // float _xSwipe1,_xSwipe2;
        ToDo temp = arrayList.get(position);
        undoAdapter.ToDoViewHolder toDoViewHolder =  (undoAdapter.ToDoViewHolder) convertView.getTag();
        toDoViewHolder.dateTextView.setText(temp.getDate());
        toDoViewHolder.timeTextView.setText(temp.getTime());
        toDoViewHolder.titleTextView.setText(temp.getTitle());
        TextDrawable drawable = TextDrawable.builder().beginConfig()
                .textColor(Color.WHITE)
                .useFont(Typeface.DEFAULT)
                .toUpperCase()
                .endConfig()
                .buildRound(temp.getTitle().substring(0, 1), temp.color);
        toDoViewHolder.imageView.setImageDrawable(drawable);
        return convertView;
    }
}

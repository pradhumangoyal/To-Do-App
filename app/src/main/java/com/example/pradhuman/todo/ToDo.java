package com.example.pradhuman.todo;

import com.amulyakhare.textdrawable.util.ColorGenerator;

/**
 * Created by Pradhuman on 29-06-2017.
 */

public class ToDo {
    private String title;
    private String description;
    private String date;
    private String time;
    private int priority;
    private String category;
    private long id;
    private int isChecked;
    int color;

    public ToDo(String title, String description, String time, int priority, String category, long id, String date,int isChecked) {
        this.title = title;
        this.description = description;
        this.time = time;
        this.priority = priority;
        this.category = category;
        this.id = id;
        this.date = date;
        this.isChecked = isChecked;
        this.color = ColorGenerator.MATERIAL.getRandomColor();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int isChecked() {
        return isChecked;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setChecked(int checked) {
        isChecked = checked;
    }

}

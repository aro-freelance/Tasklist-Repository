package com.aro.tasklist.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "task_table")
public class Task {
    /*
    This is our task table

    note that all of these parameters will be columns.
    The column info tag is used when we want to change the name
    that the column will have from the default name which we gave it here to something else
    */

    @ColumnInfo(name="task_id")
    @PrimaryKey(autoGenerate = true)
    public long taskId;

    public String task;

    public Priority priority;

    @ColumnInfo(name = "due_date")
    public Date dueDate;

    @ColumnInfo(name="created_at")
    public Date dateCreated;

    @ColumnInfo(name="is_done")
    public Boolean isDone;

    public String category;


    public Task(){}
//
    public Task(String task, Priority priority, Date dueDate, Date cateCreated, Boolean isDone, String category) {
        this.task = task;
        this.priority = priority;
        this.dueDate = dueDate;
        this.dateCreated = cateCreated;
        this.isDone = isDone;
        this.category = category;
    }


    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @NonNull
    @Override
    public String toString(){
        return "Task{ " +
                "taskId = " + taskId +
                ", task(string) = " + task +
                ", priority = " + priority +
                ", dueDate = " + dueDate +
                ", dateCreated = " + dateCreated +
                ", isDone = " + isDone +
                ", category = " + category +
                "}";
    }

}

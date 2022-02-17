package com.aro.tasklist.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.aro.tasklist.data.TodoisterRepository;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    public static TodoisterRepository repository;
    public final LiveData<List<Task>> allTasks;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TodoisterRepository(application);
        allTasks = repository.getTaskList();
    }

    //create
    public static void insert(Task task) {repository.insert(task);}

    //read
    public LiveData<Task> get(long id) {return repository.get(id); }

    //read all
    public LiveData<List<Task>> getAllTasks() {return allTasks;}

    //update
    public static void update(Task task) {repository.update(task);}

    //delete
    public static void delete(Task task) {repository.delete(task);}

}

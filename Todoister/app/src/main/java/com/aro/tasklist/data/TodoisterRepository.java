package com.aro.tasklist.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.aro.tasklist.model.Task;
import com.aro.tasklist.util.TaskRoomDatabase;

import java.util.List;

public class TodoisterRepository {
    private final TaskDao taskDao;
    private final LiveData<List<Task>> allTasks;

    public TodoisterRepository(Application application) {
        TaskRoomDatabase database = TaskRoomDatabase.getDatabase(application);
        this.taskDao = database.taskDao();
        this.allTasks = taskDao.getAllTasks();
    }

    //create
    public void insert(Task task){
        TaskRoomDatabase.databaseWriterExecutor.execute(()-> taskDao.insertTask(task));
    }

    //read
    public LiveData<Task> get(long id){
        return taskDao.get(id);
    }

    //read all
    public LiveData<List<Task>> getTaskList(){
        return allTasks;
    }

    //update
    public void update(Task task){
        TaskRoomDatabase.databaseWriterExecutor.execute(()-> taskDao.update(task));
    }

    //delete
    public void delete(Task task){
        TaskRoomDatabase.databaseWriterExecutor.execute(()-> taskDao.delete(task));
    }

}

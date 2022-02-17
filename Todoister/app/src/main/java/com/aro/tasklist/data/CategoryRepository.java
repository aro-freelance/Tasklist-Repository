package com.aro.tasklist.data;

import android.app.Application;

import androidx.lifecycle.LiveData;


import com.aro.tasklist.model.Category;
import com.aro.tasklist.util.TaskRoomDatabase;

import java.util.List;

public class CategoryRepository {

    private final CategoryDao categoryDao;
    private final LiveData<List<Category>> allCategories;

    public CategoryRepository(Application application) {
        TaskRoomDatabase database = TaskRoomDatabase.getDatabase(application);
        this.categoryDao = database.categoryDao();
        this.allCategories = categoryDao.getAllCategories();
    }

    //create
    public void insert(Category category){
        TaskRoomDatabase.databaseWriterExecutor.execute(()-> categoryDao.insertCategory(category));
    }

    //read
    public LiveData<Category> get(long id){
        return categoryDao.get(id);
    }

    //read all
    public LiveData<List<Category>> getCategoryList(){
        return allCategories;
    }

    //update
    public void update(Category category){
        TaskRoomDatabase.databaseWriterExecutor.execute(()-> categoryDao.update(category));
    }

    //delete
    public void delete(Category category){
        TaskRoomDatabase.databaseWriterExecutor.execute(()-> categoryDao.delete(category));
    }
}

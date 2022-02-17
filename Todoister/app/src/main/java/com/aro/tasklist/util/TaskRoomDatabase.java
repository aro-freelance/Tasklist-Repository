package com.aro.tasklist.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.aro.tasklist.data.CategoryDao;
import com.aro.tasklist.data.TaskDao;
import com.aro.tasklist.model.Category;
import com.aro.tasklist.model.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(
        version = 3,
        entities = {Task.class, Category.class}

)
@TypeConverters({Converter.class})
public abstract class TaskRoomDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "todoister_database";
    public static final int NUMBER_OF_THREADS = 4;
    public static volatile TaskRoomDatabase INSTANCE;
    public static final ExecutorService databaseWriterExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    public abstract TaskDao taskDao();
    public abstract CategoryDao categoryDao();

    //this is called by addCallback when the database is first created
    public static final RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    databaseWriterExecutor.execute(()->{
                        // invoke Dao and write
                        TaskDao taskDao = INSTANCE.taskDao();
                        taskDao.deleteAll(); //clear the database when it is initially created

                        CategoryDao categoryDao = INSTANCE.categoryDao();
                        categoryDao.deleteAll();
                    });
                }
            };

    //constructor (singleton)
    public static TaskRoomDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (TaskRoomDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TaskRoomDatabase.class, DATABASE_NAME)
                            .addCallback(sRoomDatabaseCallback)
                            .addMigrations(MIGRATION_1_2)
                            .addMigrations(MIGRATION_2_3)
                            .build();
                }
            }
        }

        return INSTANCE;
    }


    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE task_table ADD COLUMN category TEXT");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS category_table " +
                    "(`categoryId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`categoryName` TEXT)");

        }
    };




}

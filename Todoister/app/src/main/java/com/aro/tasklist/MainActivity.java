package com.aro.tasklist;

import android.os.Bundle;

import com.aro.tasklist.adapter.OnItemClickedListener;
import com.aro.tasklist.adapter.RecyclerViewAdapter;
import com.aro.tasklist.model.Category;
import com.aro.tasklist.model.CategoryViewModel;
import com.aro.tasklist.model.SharedViewModel;
import com.aro.tasklist.model.Task;
import com.aro.tasklist.model.TaskViewModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnItemClickedListener, AdapterView.OnItemSelectedListener {



    //this activity displays the list of tasks and listens for the user to click one for edits or add a new one

    private Spinner spinner;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private BottomSheetFragment bottomSheetFragment;
    private SharedViewModel sharedViewModel;

    private Button deleteCatButton;
    private TextView emptyCatTextView;

    private String categoryString;
    private Category category;

    private List<Task> currentTaskList;
    private List<Task> fullTaskList;

    private List<String> categories;

    private boolean loadSpinnerFromDelete = false;

    private CategoryViewModel categoryViewModel;

    /*
    Room database structure:

    The tables (entities) are accessed by the Dao.

    The repository gets the data from the Dao.

    The Dao and entities are controlled by the RoomDatabase.

    The view model holds the live data for the UI by getting it from the repository.

    The UI interface gets the live data fro the view model.


    In this project:
    Task is the entity.
    TaskDao is the Dao.
    TaskRoomDatabase is the RoomDatabase.
    TodoisterRepository is the repository.
    TaskViewModel is the Android view model. It has the CRUD functionality.
    SharedViewModel is a view model which controls selecting items for edits.

    The UI interface is the activities and fragments.

     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        spinner = findViewById(R.id.spinner_main_categories);
        deleteCatButton = findViewById(R.id.delete_empty_cat_buton);
        emptyCatTextView = findViewById(R.id.empty_cat_textview);


        bottomSheetFragment = new BottomSheetFragment();
        ConstraintLayout constraintLayout = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior = BottomSheetBehavior.from(constraintLayout);
        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.STATE_HIDDEN);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        currentTaskList = new ArrayList<>();
        fullTaskList = new ArrayList<>();
        categories = new ArrayList<>();

        TaskViewModel taskViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication()).create(TaskViewModel.class);
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        categoryViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication()).create(CategoryViewModel.class);

        categoryViewModel.getAllCategories().observe(this, this::getCategories);


        taskViewModel.getAllTasks().observe(this, tasks -> {

            fullTaskList = tasks;


            //populate the currentTaskList with the tasks of the chosen category
            setCurrentTaskList(fullTaskList);


            sortByDate(currentTaskList);


            recyclerViewAdapter = new RecyclerViewAdapter(currentTaskList, this);
            recyclerView.setAdapter(recyclerViewAdapter);

        });

        spinner.setOnItemSelectedListener(this);

        floatingActionButton.setOnClickListener(this::floatingActionButtonMethod);

        deleteCatButton.setOnClickListener(this::deleteCatButtonMethod);


        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void deleteCategory(){
        Category catToDelete = category;


        LiveData<List<Category>> listOfAllCategories = categoryViewModel.getAllCategories();

        for (int i = 0; i < Objects.requireNonNull(listOfAllCategories.getValue()).size(); i++) {

            Category currentCategory = listOfAllCategories.getValue().get(i);

            //if the category that we want to delete (by name) is the same name as
            // the category we are looking at currently in the list
            // then remove it
            if(currentCategory.categoryName.equals(catToDelete.categoryName)){

                //remove from the list populating the spinner
                categories.remove(categoryString);

                //remove from the database
                CategoryViewModel.delete(currentCategory);

            }

        }
    }

    private void deleteCatButtonMethod(View view) {

        BottomSheetDialog deleteCatDialog = new BottomSheetDialog(this);

        deleteCatDialog.setContentView(R.layout.delete_cat_dialog);

        Button deleteCatButton = deleteCatDialog.findViewById(R.id.delete_cat_button);
        Button backCatButton = deleteCatDialog.findViewById(R.id.delete_cat_dialog_back_button);

        deleteCatDialog.show();


        assert deleteCatButton != null;
        deleteCatButton.setOnClickListener(view1 -> {
            deleteCategory();
            deleteCatDialog.dismiss();
        });

        assert backCatButton != null;
        backCatButton.setOnClickListener(view12 -> deleteCatDialog.dismiss());

        deleteCatDialog.setOnCancelListener(dialogInterface -> deleteCatDialog.dismiss());

        deleteCatDialog.setOnDismissListener(dialogInterface -> deleteCatDialog.dismiss());



    }

    public void getCategories(List<Category> categoryList){

        for (int i = 0; i < categoryList.size(); i++) {

            Category mCategory = categoryList.get(i);
            String categoryString = mCategory.getCategoryName();

            if(!categories.contains(categoryString)){
                categories.add(categoryString);
            }

        }


        if(!categories.contains("To Do List")){
            categories.add("To Do List");
            Category toDoCat = new Category("To Do List");

            if(!Objects.requireNonNull(categoryViewModel.getAllCategories().getValue()).contains(toDoCat)){
                CategoryViewModel.insert(toDoCat);
            }
        }
        if(!categories.contains("Completed")){
            categories.add("Completed");
            Category completedCat = new Category("Completed");
            if(!Objects.requireNonNull(categoryViewModel.getAllCategories().getValue()).contains(completedCat)){
                CategoryViewModel.insert(completedCat);
            }
        }

        //use the array to populate the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //set the spinner to default

        if(loadSpinnerFromDelete){
            spinner.setSelection(categories.indexOf("Completed")); // the number here is correct

            loadSpinnerFromDelete = false;

            //Log.d("test","spinner pos: dismiss " + categories.indexOf("Completed"));
        }
        else{
            spinner.setSelection(0);
        }

    }


    private void setCurrentTaskList(List<Task> tasks){

        currentTaskList.clear();


        //set up a list of tasks for the currently selected category
        for (int i = 0; i < tasks.size(); i++) {

            Task task = tasks.get(i);


            if(task.getCategory() == null){
                task.setCategory("To Do List");
                TaskViewModel.update(task);
            }

            if(task.getCategory().equals(categoryString)){
                currentTaskList.add(task);

            }

        }

        if(currentTaskList.size() > 0){

            deleteCatButton.setVisibility(View.GONE);
            emptyCatTextView.setVisibility(View.GONE);
        }
        else{

            if(categoryString != null){
                //if the empty category is not one of the default cats show empty cat views
                if(!categoryString.equals("To Do List") && !categoryString.equals("Completed")){
                    deleteCatButton.setVisibility(View.VISIBLE);
                    emptyCatTextView.setVisibility(View.VISIBLE);
                }

                // if the empty cat is To Do List show the empty cat text (so user can see their tasks are done)
                // but don't show delete
                else if (categoryString.equals("To Do List")){
                    deleteCatButton.setVisibility(View.GONE);
                    emptyCatTextView.setVisibility(View.VISIBLE);
                }

                //empty category is the completed list... set text to something?
                else{
                    //todo set text to something to prompt user to finish a task?
                    deleteCatButton.setVisibility(View.GONE);
                    emptyCatTextView.setVisibility(View.GONE);

                }
            }
        }
    }


    private void sortByDate(List<Task> tasks){
        tasks.sort(Comparator.comparing(task -> task.dueDate));

    }

    private void floatingActionButtonMethod(View view) {
        showBottomSheetDialog();

    }

    private void showBottomSheetDialog() {
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }


    @Override
    public void onTaskClicked(int position, Task task) {

        sharedViewModel.selectItem(task);
        sharedViewModel.setEdit(true);

        showBottomSheetDialog();

    }

    @Override
    public void onTaskRadioButtonClicked(Task task) {

        if(!task.getCategory().equals("Completed")){

            task.setCategory("Completed");
            TaskViewModel.update(task);




        }

        //if the currently task is marked as completed
        else{

            //todo show a dialog asking if the user wants to delete the task
            BottomSheetDialog deleteDialog = new BottomSheetDialog(this);
            deleteDialog.setContentView(R.layout.delete_confirmation_dialog);

            Button deleteButton = deleteDialog.findViewById(R.id.delete_button);
            Button backButton = deleteDialog.findViewById(R.id.delete_dialog_back_button);

            deleteDialog.show();

            assert deleteButton != null;
            deleteButton.setOnClickListener(view -> {
                TaskViewModel.delete(task);

                loadSpinnerFromDelete = true;

                deleteDialog.dismiss();

            });

            assert backButton != null;
            backButton.setOnClickListener(view -> deleteDialog.dismiss());

            deleteDialog.setOnDismissListener(dialogInterface -> deleteDialog.dismiss());

            deleteDialog.setOnCancelListener(dialogInterface -> deleteDialog.dismiss());

        }

    }


    //spinner item selected
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        categoryString = parent.getItemAtPosition(pos).toString();

        category = new Category(categoryString);

        setCurrentTaskList(fullTaskList);

        sortByDate(currentTaskList);
        recyclerViewAdapter = new RecyclerViewAdapter(currentTaskList, this);
        recyclerView.setAdapter(recyclerViewAdapter);




    }

    //spinner nothing selected
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
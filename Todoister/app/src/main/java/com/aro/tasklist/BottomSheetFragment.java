package com.aro.tasklist;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.aro.tasklist.model.Category;
import com.aro.tasklist.model.CategoryViewModel;
import com.aro.tasklist.model.Priority;
import com.aro.tasklist.model.SharedViewModel;
import com.aro.tasklist.model.Task;
import com.aro.tasklist.model.TaskViewModel;
import com.aro.tasklist.util.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class BottomSheetFragment extends BottomSheetDialogFragment implements AdapterView.OnItemSelectedListener{

    //this fragment lets the user make a new task or edit an existing task

    private EditText taskEditText;
    private ImageButton calendarButton;
    private ImageButton priorityButton;
    private ImageButton saveButton;
    private CalendarView calendarView;
    private Group calendarGroup;

    private ImageButton categoryButton;
    private Group categoryGroup;
    private Spinner categorySpinner;
    private EditText categoryEditText;
    private Button categoryEnterButton;

    private Chip todayChip;
    private Chip tomorrowChip;
    private Chip nextWeekChip;

    private RadioGroup priorityRadioGroup;
    private Priority priority;

    private RadioButton highRadioButton;
    private RadioButton medRadioButton;
    private RadioButton lowRadioButton;

    private Date dueDate;
    Calendar calendar = Calendar.getInstance();

    private SharedViewModel sharedViewModel;
    private boolean isEdit = false;

    private String category;
    private ArrayList<String> categories;


    private CategoryViewModel categoryViewModel;

    public BottomSheetFragment() { }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);

        taskEditText = view.findViewById(R.id.enter_todo_edit_text);
        calendarButton = view.findViewById(R.id.calendar_button);
        priorityButton = view.findViewById(R.id.priority_todo_button);
        saveButton = view.findViewById(R.id.save_todo_button);
        calendarView = view.findViewById(R.id.calendar_view);
        calendarGroup = view.findViewById(R.id.calendar_group);
        todayChip = view.findViewById(R.id.today_chip);
        tomorrowChip = view.findViewById(R.id.tomorrow_chip);
        nextWeekChip = view.findViewById(R.id.next_week_chip);
        highRadioButton = view.findViewById(R.id.radioButton_high);
        medRadioButton = view.findViewById(R.id.radioButton_med);
        lowRadioButton = view.findViewById(R.id.radioButton_low);

        categoryButton = view.findViewById(R.id.category_button);
        categoryGroup = view.findViewById(R.id.category_group);
        categorySpinner = view.findViewById(R.id.category_spinner_bottom_sheet);
        categoryEditText = view.findViewById(R.id.category_edit_text_bottom_sheet);
        categoryEnterButton = view.findViewById(R.id.new_category_button_bottom_sheet);


        priorityRadioGroup = view.findViewById(R.id.radioGroup_priority);

       categoryViewModel = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()).create(CategoryViewModel.class);

        categories = new ArrayList<>();

        categoryViewModel.getAllCategories().observe(this, this::getCategories);



        categoryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!TextUtils.isEmpty(categoryEditText.getText().toString().trim())){
                    categoryEnterButton.setVisibility(View.VISIBLE);
                }
            }
        });


        categorySpinner.setOnItemSelectedListener(this);

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        if(sharedViewModel.getSelectedItem().getValue() != null){
            isEdit = sharedViewModel.getIsEdit();
            Task task = sharedViewModel.getSelectedItem().getValue();
            taskEditText.setText(task.getTask());
        }
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
        }

        categories.remove("Completed");

        //use the array to populate the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        saveButton.setOnClickListener(this::saveButtonMethod);
        calendarButton.setOnClickListener(this::calendarButtonMethod);
        calendarView.setOnDateChangeListener(this::calendarViewMethod);
        todayChip.setOnClickListener(this::todayChipMethod);
        tomorrowChip.setOnClickListener(this::tomorrowChipMethod);
        nextWeekChip.setOnClickListener(this::nextWeekChipMethod);
        priorityButton.setOnClickListener(this::priorityButtonMethod);
        highRadioButton.setOnClickListener(this::highRadioButtonMethod);
        medRadioButton.setOnClickListener(this::medRadioButtonMethod);
        lowRadioButton.setOnClickListener(this::lowRadioButtonMethod);

        categoryButton.setOnClickListener(this::categoryButtonMethod);
        categoryEnterButton.setOnClickListener(this::categoryEnterButtonMethod);

        categoryEnterButton.setVisibility(View.GONE);

        categoryEditText.setOnKeyListener((view1, keyCode, keyEvent) -> {
            if(keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                switch(keyCode){
                    //if the enter key is pressed when the title text is being edited close the soft keyboard
                    case KeyEvent.KEYCODE_ENTER:
                        Utils.hideKeyboard(categoryEditText);

                        //act as if the user has pushed the enter button on the UI and get their input
                        getUserInputCategory();

                }
            }

            return false;
        });

    }

    private void getUserInputCategory(){
        String categoryText = categoryEditText.getText().toString().trim();
        if(!TextUtils.isEmpty(categoryText)){

            //add the user input to the array
            categories.add(categoryText);

            //use the array to populate the spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(adapter);

            //set the spinner to have the newest item selected
            categorySpinner.setSelection(categories.size() - 1);

            //and just to be safe set the category to the user input now
            category = categoryText;

            //reset the edit text
            categoryEditText.setText("");
        }
        else{
            Toast.makeText(getContext(), "Category field empty. Enter a new category.", Toast.LENGTH_SHORT).show();
        }
        categoryEnterButton.setVisibility(View.GONE);
    }

    private void categoryEnterButtonMethod(View view) {

        getUserInputCategory();
        categoryEnterButton.setVisibility(View.GONE);
    }

    private void categoryButtonMethod(View view) {
        Utils.hideKeyboard(view);
        calendarGroup.setVisibility(View.GONE);
        priorityRadioGroup.setVisibility(View.GONE);
        categoryGroup.setVisibility(
                categoryGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE
        );

    }

    private void lowRadioButtonMethod(View view) {
        priority = Priority.LOW;
    }

    private void medRadioButtonMethod(View view) {
        priority = Priority.MEDIUM;
    }

    private void highRadioButtonMethod(View view) {
        priority = Priority.HIGH;
    }

    private void priorityButtonMethod(View view) {
        Utils.hideKeyboard(view);
        calendarGroup.setVisibility(View.GONE);
        categoryGroup.setVisibility(View.GONE);
        priorityRadioGroup.setVisibility(
                priorityRadioGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE
        );

    }

    private void nextWeekChipMethod(View view) {
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.add(Calendar.DAY_OF_YEAR, 7);
        calendar = newCalendar;
        dueDate = calendar.getTime();
        calendarGroup.setVisibility(
                calendarGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE
        );
        nextWeekChip.setChipBackgroundColor(ColorStateList.valueOf(Color.GREEN));
        todayChip.setChipBackgroundColor(ColorStateList.valueOf(Color.LTGRAY));
        tomorrowChip.setChipBackgroundColor(ColorStateList.valueOf(Color.LTGRAY));
    }
    private void tomorrowChipMethod(View view) {
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar = newCalendar;
        dueDate = calendar.getTime();
        calendarGroup.setVisibility(
                calendarGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE
        );
        tomorrowChip.setChipBackgroundColor(ColorStateList.valueOf(Color.GREEN));
        todayChip.setChipBackgroundColor(ColorStateList.valueOf(Color.LTGRAY));
        nextWeekChip.setChipBackgroundColor(ColorStateList.valueOf(Color.LTGRAY));
    }
    private void todayChipMethod(View view) {
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.add(Calendar.DAY_OF_YEAR, 0);
        calendar = newCalendar;
        dueDate = calendar.getTime();
        calendarGroup.setVisibility(
                calendarGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE
        );
        todayChip.setChipBackgroundColor(ColorStateList.valueOf(Color.GREEN));
        tomorrowChip.setChipBackgroundColor(ColorStateList.valueOf(Color.LTGRAY));
        nextWeekChip.setChipBackgroundColor(ColorStateList.valueOf(Color.LTGRAY));

    }

    private void calendarViewMethod(CalendarView thisCalendarView, int year, int month, int day) {
        calendar.clear();
        calendar.set(year, month, day);
        dueDate = calendar.getTime();
    }
    private void calendarButtonMethod(View view) {
        Utils.hideKeyboard(view);
        categoryGroup.setVisibility(View.GONE);
        priorityRadioGroup.setVisibility(View.GONE);
        calendarGroup.setVisibility(
                calendarGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE
        );


    }
    private void saveButtonMethod(View view) {

        String taskText = taskEditText.getText().toString().trim();

        //Set default values
        if (TextUtils.isEmpty(taskText)) {
            taskText = "";
        }
        if (dueDate == null) {
            if (isEdit) {
                dueDate = Objects.requireNonNull(sharedViewModel.getSelectedItem().getValue()).getDueDate();
            } else {
                dueDate = Calendar.getInstance().getTime();
            }
        }
        if (priority == null) {
            if (isEdit) {
                priority = Objects.requireNonNull(sharedViewModel.getSelectedItem().getValue()).getPriority();
            } else {
                priority = Priority.LOW;
            }

        }

        if(category == null){

            if (isEdit) {
                category = Objects.requireNonNull(sharedViewModel.getSelectedItem().getValue()).getCategory();
            } else {
                category = "To Do List";
            }

        }

        //these should not be null now because I am filling them with defaults above. But check to be safe is fine

        Task task = new Task(taskText, priority, dueDate,
                    Calendar.getInstance().getTime(), false, category);


        Category newCategory = new Category(category);

        if(isEdit){
            Task taskToUpdate = sharedViewModel.getSelectedItem().getValue();
            assert taskToUpdate != null;
            taskToUpdate.setTask(taskText);
            taskToUpdate.setDateCreated(Calendar.getInstance().getTime());
            taskToUpdate.setPriority(priority);
            taskToUpdate.setDueDate(dueDate);
            taskToUpdate.setDone(false);
            taskToUpdate.setCategory(category);
            TaskViewModel.update(taskToUpdate);
            isEdit = false;

        }
        else{
            TaskViewModel.insert(task);

            //if the category doesn't exist in the category database yet, add it
            if(!Objects.requireNonNull(categoryViewModel.getAllCategories().getValue()).contains(newCategory)){
                CategoryViewModel.insert(newCategory);
            }

        }

        reset();

        if(this.isVisible()){
            dismiss();
        }

    }

    private void reset(){
        taskEditText.setText("");
        lowRadioButton.setChecked(false);
        medRadioButton.setChecked(false);
        highRadioButton.setChecked(false);
    }


    //user selects something in the category spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        category = parent.getItemAtPosition(pos).toString();

    }

    //nothing selected in category spinner
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
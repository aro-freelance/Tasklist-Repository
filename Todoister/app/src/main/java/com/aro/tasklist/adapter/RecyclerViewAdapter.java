package com.aro.tasklist.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.aro.tasklist.R;
import com.aro.tasklist.model.Task;
import com.aro.tasklist.util.Utils;
import com.google.android.material.chip.Chip;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final List<Task> taskList;
    private final OnItemClickedListener itemClickedListener;



    //constructor
    public RecyclerViewAdapter(List<Task> taskList, OnItemClickedListener onItemClickedListener) {
        this.taskList = taskList;
        itemClickedListener = onItemClickedListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //get the task at the current position
        Task task = taskList.get(position);
        String formattedDate = Utils.formatDate(task.getDueDate());

        //override the color when something is clicked
        ColorStateList colorStateList = new ColorStateList( new int[][]{
                new int[]{-android.R.attr.state_enabled},
                new int[]{android.R.attr.state_enabled}
        },
                new int[]{
                        Color.LTGRAY, //disabled color
                        Utils.priorityColor(task)

                });


        //set the task data to the views
        holder.taskText.setText(task.getTask());
        holder.dueDateChip.setText(formattedDate);

        //set color based on priority
        holder.dueDateChip.setTextColor(Utils.priorityColor(task));
        holder.dueDateChip.setChipIconTint(colorStateList);
        holder.radioButton.setButtonTintList(colorStateList);

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //set up views from the row layout
        public AppCompatRadioButton radioButton;
        public AppCompatTextView taskText;
        public Chip dueDateChip;
        OnItemClickedListener onItemClickedListener;

        //constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.todo_radio_button);
            taskText = itemView.findViewById(R.id.todo_row_textview);
            dueDateChip = itemView.findViewById(R.id.todo_row_chip);
            this.onItemClickedListener = itemClickedListener;

            itemView.setOnClickListener(this);
            radioButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int id = view.getId();
            Task currentTask = taskList.get(getAdapterPosition());

            //if the user clicks the whole row open it for edits
            if(id == R.id.todo_row_layout){
                onItemClickedListener.onTaskClicked(getAdapterPosition(), currentTask);
            }
            //if the user clicks the radio button... this is handled in main activity. Move to completed. Or if in completed prompt delete.
            else if (id == R.id.todo_radio_button){
                onItemClickedListener.onTaskRadioButtonClicked(currentTask);
            }

        }
    }
}

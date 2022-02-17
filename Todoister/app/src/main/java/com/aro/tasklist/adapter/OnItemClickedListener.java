package com.aro.tasklist.adapter;

import com.aro.tasklist.model.Task;

public interface OnItemClickedListener {

    void onTaskClicked(int position, Task task);
    void onTaskRadioButtonClicked(Task task);

}

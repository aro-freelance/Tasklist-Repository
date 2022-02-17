package com.aro.tasklist.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {

    /*
    This will hold data so that we can move it between activities and fragments.
     */

    private final MutableLiveData<Task> selectedItem = new MutableLiveData<>();
    private boolean isEdit;

    public boolean getIsEdit() {
        return isEdit;
    }

    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

    //setter
    public void selectItem(Task task){
        selectedItem.setValue(task);
    }

    //getter
    public LiveData<Task> getSelectedItem(){
        return selectedItem;
    }



}

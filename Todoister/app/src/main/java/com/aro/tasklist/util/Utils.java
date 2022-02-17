package com.aro.tasklist.util;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.aro.tasklist.model.Priority;
import com.aro.tasklist.model.Task;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    /*
     This is a class with helper functions that may be used in multiple activities and classes
     */

    public static String formatDate(Date date){
        //simple date format patterns can be found here: https://developer.android.com/reference/java/text/SimpleDateFormat
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d"); //Sat, Sep 4

        return simpleDateFormat.format(date);
    }

    public static void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static int priorityColor(Task task) {
        int color;
        if(task.getPriority() == Priority.HIGH){
            //red
            color = Color.argb(200, 201, 21, 23);
        }else if (task.getPriority() == Priority.MEDIUM){
            //yellow191, 143, 10
            color = Color.argb(200, 191, 143, 10);
        } else {
            //blue
            color = Color.argb(200, 51, 181, 129);
        }
        return color;
    }
}

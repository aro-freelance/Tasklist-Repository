package com.aro.tasklist.util;

import androidx.room.TypeConverter;

import com.aro.tasklist.model.Priority;

import java.util.Date;

public class Converter {

    //convert timestamp stored in Database back into a Date
    @TypeConverter
    public static Date timestampToDate (Long value){
        //if the value we received is null return null, else create a new Date with the value we are receiving
        return value == null ? null : new Date(value);

    }

    //convert Date to Timestamp to store in the db
    @TypeConverter
    public static Long dateToTimestamp(Date date){
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Priority stringToPriority(String string){
        return string == null ? null : Priority.valueOf(string);
    }

    @TypeConverter
    public static String priorityToString(Priority priority){
        return priority == null ? null : priority.toString(); // if this doesn't work use priority.name();
    }



}

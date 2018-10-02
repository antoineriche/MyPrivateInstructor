package com.antoineriche.privateinstructor.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabase extends SQLiteOpenHelper {

    public static final int VERSION = 3;
    public static final String DB_NAME = "private-instructor";

    public MyDatabase(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DB_NAME, factory, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(getClass().getSimpleName(), "onCreate");
        sqLiteDatabase.execSQL(new CourseTable().getCreationString());
        sqLiteDatabase.execSQL(new PupilTable().getCreationString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if(newVersion > oldVersion) {
            Log.d(getClass().getSimpleName(), "onUpgrade");
            sqLiteDatabase.execSQL(new PupilTable().getCreationString());
        }
    }

}

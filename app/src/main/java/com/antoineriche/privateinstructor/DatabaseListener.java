package com.antoineriche.privateinstructor;

import android.database.sqlite.SQLiteDatabase;

public interface DatabaseListener {

    int READABLE = 0;
    int WRITABLE = 1;

    SQLiteDatabase getDatabase();
}

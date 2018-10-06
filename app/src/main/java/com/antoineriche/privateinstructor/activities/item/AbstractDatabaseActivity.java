package com.antoineriche.privateinstructor.activities.item;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.antoineriche.privateinstructor.DatabaseListener;
import com.antoineriche.privateinstructor.database.MyDatabase;

public abstract class AbstractDatabaseActivity extends AppCompatActivity implements DatabaseListener {

    private SQLiteDatabase mDatabase;
    private MyDatabase mMyDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyDB = new MyDatabase(getApplicationContext(), null);
        mDatabase = isDatabaseWritable() ? mMyDB.getWritableDatabase() : mMyDB.getReadableDatabase();
    }

    protected abstract boolean isDatabaseWritable();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMyDB.close();
    }

    @Override
    public SQLiteDatabase getDatabase() {
        return mDatabase;
    }
}

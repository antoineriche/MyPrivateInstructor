package com.antoineriche.privateinstructor.services;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.antoineriche.privateinstructor.beans.DatabaseItem;
import com.antoineriche.privateinstructor.beans.Location;
import com.antoineriche.privateinstructor.database.LocationTable;
import com.antoineriche.privateinstructor.database.MyDatabase;

import java.io.Serializable;

public class DatabaseIntentService extends IntentService {

    private static final String TAG = DatabaseIntentService.class.getSimpleName();

    public static final String DB_EXTRAS = "DB_EXTRAS";

    public DatabaseIntentService() {
        super(DatabaseIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MyDatabase db = new MyDatabase(this, null);
        dispatchOperation(DatabaseOperation.fromBundle(intent.getBundleExtra(DB_EXTRAS)));
    }

    private void dispatchOperation(DatabaseOperation dbOperation){
        Log.e(TAG, dbOperation.toString());
        MyDatabase mDb = new MyDatabase(this, null);
        SQLiteDatabase sql = dbOperation.needWritingRights ?
                mDb.getWritableDatabase() : mDb.getReadableDatabase();

        switch(dbOperation.operation){
            case GET:
                break;
            case INSERT:
                if(dbOperation.itemClass.equals(Location.class)){
                    LocationTable.insertLocation(sql, (Location) dbOperation.item);
                }
                break;
            case REMOVE:
                break;
            case UPDATE:
                if(dbOperation.itemClass.equals(Location.class)){
                    Location location = (Location) dbOperation.item;
                    LocationTable.updateLocationWithUuid(sql, location.getUuid(), location);
                }
                break;
        }
    }

    public static class DatabaseOperation implements Serializable {

        private static final String DB_CLASS = "DB_CLASS";
        private static final String DB_ITEM = "DB_ITEM";
        private static final String DB_OPERATION = "DB_OPERATION";

        public enum Operation {
            INSERT, UPDATE, REMOVE, GET
        }

        Operation operation;
        DatabaseItem item;
        Class itemClass;
        boolean needWritingRights;

        public static Bundle toBundle(Operation operation, DatabaseItem item, Class itemClass){
            Bundle args = new Bundle();
            args.putSerializable(DB_OPERATION, operation);
            args.putSerializable(DB_ITEM, item);
            args.putSerializable(DB_CLASS, itemClass);
            return args;
        }

        static DatabaseOperation fromBundle(Bundle bundle){
            return new DatabaseOperation((Operation) bundle.get(DB_OPERATION),
                    (DatabaseItem) bundle.get(DB_ITEM), (Class) bundle.get(DB_CLASS));
        }

        DatabaseOperation(Operation operation, DatabaseItem item, Class itemClass) {
            this.operation = operation;
            this.item = item;
            this.itemClass = itemClass;
            this.needWritingRights = !operation.equals(Operation.GET);
        }

        @Override
        public String toString() {
            return "DatabaseOperation{" +
                    "operation=" + operation +
                    ", item=" + item +
                    ", itemClass=" + itemClass +
                    ", needWritingRights=" + needWritingRights +
                    '}';
        }
    }

}

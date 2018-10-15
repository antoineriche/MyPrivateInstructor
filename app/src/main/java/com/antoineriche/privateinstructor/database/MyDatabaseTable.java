package com.antoineriche.privateinstructor.database;

import java.util.Locale;

public abstract class MyDatabaseTable {
    protected abstract String getCreationString();
    protected abstract String getTableName();

    private String getDeletionString(){
        return String.format(Locale.FRANCE, "DROP TABLE %s;", getTableName());
    }

}

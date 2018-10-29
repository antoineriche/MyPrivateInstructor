package com.antoineriche.privateinstructor.beans;

import java.util.Locale;
import java.util.Map;

public interface DatabaseItem {

    long getId();

    Map<String, Object> toMap();

    boolean equals(Object o);

    void setUuid(String pUuid);

    default String generateDatabaseId(){
        return String.format(Locale.FRANCE, "%04d", getId());
    }
}

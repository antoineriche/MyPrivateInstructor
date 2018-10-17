package com.antoineriche.privateinstructor;

public interface DatabaseItemListener {

    Object getItemFromDb(long pId);
    void updateItem(long mItemId, Object pNewItem);
    void saveItem(Object pNewItem);
    void removeItem(long mItemId);
}

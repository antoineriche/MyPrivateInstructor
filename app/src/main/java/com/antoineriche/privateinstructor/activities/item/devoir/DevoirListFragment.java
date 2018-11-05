package com.antoineriche.privateinstructor.activities.item.devoir;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.antoineriche.privateinstructor.activities.item.AbstractFragmentList;
import com.antoineriche.privateinstructor.activities.item.AbstractItemActivity;
import com.antoineriche.privateinstructor.activities.item.course.CourseActivity;
import com.antoineriche.privateinstructor.adapters.EventItemAdapter;
import com.antoineriche.privateinstructor.beans.DatabaseItem;
import com.antoineriche.privateinstructor.beans.Devoir;
import com.antoineriche.privateinstructor.database.DevoirTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DevoirListFragment extends AbstractFragmentList {

    public DevoirListFragment() {
    }

    public static DevoirListFragment newInstance() {
        return new DevoirListFragment();
    }

    @Override
    protected List<DatabaseItem> order(List<DatabaseItem> pItemsToOrder) {
        pItemsToOrder.sort(Comparator.comparing(d -> -((Devoir) d).getDate()));
        return pItemsToOrder;
    }
    @Override
    protected Class<? extends Activity> getAddingActivity() {
        return DevoirActivity.class;
    }

    @Override
    protected List<DatabaseItem> getItemsFromDB(SQLiteDatabase database) {
        return new ArrayList<>(DevoirTable.getAllDevoirs(database));
    }

    @Override
    protected RecyclerView.Adapter initAdapter(List pListItems, FragmentListListener pListener) {
        return new EventItemAdapter(getActivity(), pListItems, Devoir.class, pEventItem -> {
            Bundle args = new Bundle();
            args.putLong(AbstractItemActivity.ARG_ITEM_ID, ((Devoir) pEventItem).getId());
            pListener.goToDetailsActivity(DevoirActivity.class, args);
        });
    }

    @Override
    protected void refreshRecyclerViewData(List<DatabaseItem> pNewData) {
        ((EventItemAdapter) getAdapter()).refreshList(pNewData);
    }
} // 146 - 79

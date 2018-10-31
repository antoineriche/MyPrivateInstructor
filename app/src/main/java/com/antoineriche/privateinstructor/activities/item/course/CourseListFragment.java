package com.antoineriche.privateinstructor.activities.item.course;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;

import com.antoineriche.privateinstructor.activities.item.AbstractFragmentList;
import com.antoineriche.privateinstructor.adapters.EventItemAdapter;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.beans.DatabaseItem;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.database.DevoirTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CourseListFragment extends AbstractFragmentList {

    public CourseListFragment() {
    }

    public static CourseListFragment newInstance() {
        return new CourseListFragment();
    }

    @Override
    protected Class<? extends Activity> getAddingActivity() {
        return CourseActivity.class;
    }

    @Override
    protected List<DatabaseItem> order(List<DatabaseItem> pItemsToOrder) {
        pItemsToOrder.sort(Comparator.comparing(c -> -((Course) c).getDate()));
        return pItemsToOrder;
    }

    @Override
    protected List<DatabaseItem> getItemsFromDB(SQLiteDatabase database) {
        return new ArrayList<>(CourseTable.getAllCourses(database));
    }

    @Override   //FIXME: bug with list item <DatabaseItem>
    protected RecyclerView.Adapter initAdapter(List pListItems, FragmentListListener pListener) {
        return new EventItemAdapter(getActivity(), pListItems, Course.class, pListener);
    }

    @Override
    protected void refreshRecyclerViewData(List<DatabaseItem> pNewData) {
        ((EventItemAdapter) getAdapter()).refreshList(pNewData);
    }

} // 142 - 82

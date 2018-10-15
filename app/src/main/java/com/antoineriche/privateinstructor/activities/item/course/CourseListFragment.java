package com.antoineriche.privateinstructor.activities.item.course;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.item.AbstractFragmentList;
import com.antoineriche.privateinstructor.activities.item.AbstractItemActivity;
import com.antoineriche.privateinstructor.activities.item.pupil.AbstractPupilSubDetailsFragment;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.database.CourseTable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class CourseListFragment extends AbstractFragmentList {

    public CourseListFragment() {
    }

    public static CourseListFragment newInstance() {
        return new CourseListFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.action_order) {
            getItems().sort(Comparator.comparing(Course::getDate));
            refreshListItems();
        }

        return true;
    }

    @Override
    protected Class<? extends Activity> getAddingActivity() {
        return AbstractItemActivity.CourseActivity.class;
    }

    @Override
    protected List<Course> getItemsFromDB(SQLiteDatabase database) {
        return CourseTable.getAllCourses(database);
    }

    @Override
    protected String title() {
        return "Mes cours";
    }

    @Override
    protected RecyclerView.Adapter getAdapter(List pListItems, FragmentListListener pListener) {
        return new RecyclerViewCourseAdapter(pListItems, pListener);
    }

    @Override
    public List<Course> getItems() {
        return ((List<Course>) super.getItems());
    }

    public class RecyclerViewCourseAdapter extends RecyclerView.Adapter<RecyclerViewCourseAdapter.CourseViewHolder> {

        private List<Course> mCourses;
        private FragmentListListener mListener;

        RecyclerViewCourseAdapter(List<Course> mCourses, FragmentListListener mListener) {
            this.mCourses = mCourses;
            this.mListener = mListener;
        }

        @NonNull
        @Override
        public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_rv_courses, parent, false);
            return new CourseViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull CourseViewHolder courseHolder, int position) {
            final Course course = mCourses.get(position);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(course.getDate());

            courseHolder.tvTime.setText(course.getFriendlyTimeSlot());
            courseHolder.tvDate.setText(
                    new SimpleDateFormat("dd/MM/yy", Locale.FRANCE).format(calendar.getTimeInMillis()));

            courseHolder.tvChapter.setText(course.getPupil().getFullName());
            courseHolder.tvComment.setText(course.getFriendlyTimeSlot());
            courseHolder.ivICourseState.setImageDrawable(course.getStateIcon(getContext()));

            courseHolder.cvCell.setOnClickListener(view -> {
                Bundle args = new Bundle();
                args.putLong(AbstractItemActivity.ARG_ITEM_ID, getItems().get(position).getId());
                mListener.seeItemDetails(AbstractItemActivity.CourseActivity.class, args);
            });
        }

        @Override
        public int getItemCount() {
            return this.mCourses.size();
        }

        class CourseViewHolder extends RecyclerView.ViewHolder {
            CardView cvCell;
            TextView tvDate, tvTime, tvChapter, tvComment;
            ImageView ivICourseState;

            CourseViewHolder(View itemView) {
                super(itemView);
                cvCell = itemView.findViewById(R.id.cv_course_cell);
                tvDate = itemView.findViewById(R.id.tv_course_date);
                tvTime = itemView.findViewById(R.id.tv_course_time);
                tvChapter = itemView.findViewById(R.id.tv_course_chapter);
                tvComment = itemView.findViewById(R.id.tv_course_comment);
                ivICourseState = itemView.findViewById(R.id.iv_course_state);
            }
        }
    }
}

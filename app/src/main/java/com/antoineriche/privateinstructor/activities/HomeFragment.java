package com.antoineriche.privateinstructor.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.antoineriche.privateinstructor.DatabaseListener;
import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.database.CourseTable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {

    DatabaseListener mDBListener;
    private int mCurrentWeekOffset = 0;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(){
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.cv_next_week).setOnClickListener(v -> {
            mCurrentWeekOffset++;
            setUpView(mCurrentWeekOffset);
        });

        view.findViewById(R.id.cv_previous_week).setOnClickListener(v -> {
            mCurrentWeekOffset--;
            setUpView(mCurrentWeekOffset);
        });

        view.findViewById(R.id.cv_current_week).setOnClickListener(v -> {
            mCurrentWeekOffset = 0;
            setUpView(mCurrentWeekOffset);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("Accueil");
        setUpView(mCurrentWeekOffset);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DatabaseListener) {
            mDBListener = (DatabaseListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement DatabaseListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDBListener = null;
    }

    private RecyclerView getRecyclerView(){
        return getView().findViewById(R.id.rv_list_day_week);
    }

    //FIXME: use AsyncTask
    private void setUpRecyclerView(List<Course> pWeekCourses) {
        RecyclerView rv = getRecyclerView();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new WeekAdapter(mCurrentWeekOffset, pWeekCourses));
    }

    private void setUpView(int pWeekOffset){
        List<Course> weekCourses = CourseTable.getCoursesForWeekOffset(mDBListener.getDatabase(), pWeekOffset);
        setUpRecyclerView(weekCourses);

        int courseCount = weekCourses.size();
        ((TextView) getView().findViewById(R.id.tv_week_courses)).setText(String.format(Locale.FRANCE, "%d", courseCount));

        double money = weekCourses.stream().mapToDouble(c -> c.getMoney()).sum();
        ((TextView) getView().findViewById(R.id.tv_week_money)).setText(String.format(Locale.FRANCE, "%.02f", money));

        //TODO
        int devoirCount = 0;
        ((TextView) getView().findViewById(R.id.tv_week_devoirs)).setText(String.format(Locale.FRANCE, "%d", devoirCount));
    }

    private class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.DayViewHolder> {

        class DayViewHolder extends RecyclerView.ViewHolder {
            TextView tvCourses, tvDayNumber, tvDayMonth;
            LinearLayout llCourseContainer, llDateLayout, llCell;

            DayViewHolder(View rowView) {
                super(rowView);
                tvDayNumber = rowView.findViewById(R.id.tv_day_number);
                tvDayMonth = rowView.findViewById(R.id.tv_day_month);
                tvCourses = rowView.findViewById(R.id.tv_day_courses);
                llCourseContainer = rowView.findViewById(R.id.ll_course_container);
                llDateLayout = rowView.findViewById(R.id.ll_date);
                llCell = rowView.findViewById(R.id.ll_cell);
            }
        }

        private final Calendar mCalendar;
        private final List<Course> mCourses;

        WeekAdapter(int pWeekOffset, List<Course> pCourses) {
            this.mCalendar = Calendar.getInstance();
            this.mCalendar.add(Calendar.WEEK_OF_YEAR, pWeekOffset);
            this.mCourses = pCourses;
        }

        @NonNull
        @Override
        public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_lv_day, parent, false);
            return new DayViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
            mCalendar.set(Calendar.DAY_OF_WEEK, mCalendar.getFirstDayOfWeek() + position);

            if (Calendar.getInstance().get(Calendar.DAY_OF_YEAR) == mCalendar.get(Calendar.DAY_OF_YEAR)) {
                holder.llDateLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.unthemAccent));
                holder.tvDayMonth.setTextColor(ContextCompat.getColor(getContext(), R.color.themPrimaryDark));
                holder.tvDayNumber.setTextColor(ContextCompat.getColor(getContext(), R.color.themPrimaryDark));
            }

            holder.tvDayNumber.setText(String.format(Locale.FRANCE, "%02d", mCalendar.get(Calendar.DAY_OF_MONTH)));
            holder.tvDayMonth.setText(
                    new SimpleDateFormat("MMM", Locale.FRANCE).format(mCalendar.getTimeInMillis()));

            List<Course> courses = mCourses.stream().filter(course -> {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(course.getDate());
                return cal.get(Calendar.DAY_OF_WEEK) == mCalendar.get(Calendar.DAY_OF_WEEK);
            }).collect(Collectors.toList());

            String courseContent = null;
            if(!courses.isEmpty()){
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    courseContent = String.join("\n", courses.stream().map(course ->
                            String.format(Locale.FRANCE, "%s : %s", course.getPupil().getFullName(), course.getFriendlyTimeSlot()))
                            .collect(Collectors.toList()));
                } else {
                    courseContent = courses.stream().map(course ->
                            String.format(Locale.FRANCE, "%s : %s\n", course.getPupil().getFullName(), course.getFriendlyTimeSlot()))
                            .collect(Collectors.toList()).toString();
                }
            }

            holder.tvCourses.setText(courseContent);
        }

        @Override
        public int getItemCount() {
            return 7;
        }
    }
}

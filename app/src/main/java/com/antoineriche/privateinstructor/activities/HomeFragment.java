package com.antoineriche.privateinstructor.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.antoineriche.privateinstructor.DatabaseListener;
import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.customviews.ItemCounterView;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.utils.CourseUtils;
import com.antoineriche.privateinstructor.utils.DateUtils;
import com.antoineriche.privateinstructor.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class HomeFragment extends AbstractDatabaseFragment {

    private int mCurrentWeekOffset = 0;
    private CountDownTimer mCountDownNextCourse;

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

        Course next = CourseTable.getNextCourse(mListener.getDatabase());
        ItemCounterView icvCountDown = getView().findViewById(R.id.icv_count_down_course);
        if(next != null) {
            long remainingTime = next.getDate() - System.currentTimeMillis();
            scheduleCountDown(remainingTime, icvCountDown);
        } else {
            icvCountDown.setUpView("-");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mCountDownNextCourse != null){
            mCountDownNextCourse.start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mCountDownNextCourse != null){
            mCountDownNextCourse.cancel();
        }
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
        List<Course> weekCourses = CourseTable.getCoursesForWeekOffset(mListener.getDatabase(), pWeekOffset);
        setUpRecyclerView(weekCourses);

        int courseCount = weekCourses.size();
        ((ItemCounterView) getView().findViewById(R.id.icv_week_courses)).setUpView(String.valueOf(courseCount));

        double money = CourseUtils.extractMoneySum(weekCourses);
        ((ItemCounterView) getView().findViewById(R.id.icv_week_money)).setUpView(StringUtils.formatDouble(money));

        //TODO
        int devoirCount = 0;
        ((ItemCounterView) getView().findViewById(R.id.icv_week_devoirs)).setUpView(String.valueOf(devoirCount));
    }

    //FIXME AsyncTask
    private void scheduleCountDown(long pMilliseconds, ItemCounterView pICVDisplay){
        Log.e(getClass().getSimpleName(), "scheduleCountDown");
        mCountDownNextCourse = new CountDownTimer(pMilliseconds, 1000) {
            @Override
            public void onTick(long milliseconds) {
                pICVDisplay.setUpView(DateUtils.formatRemainingTime(milliseconds));
            }

            @Override
            public void onFinish() {
                pICVDisplay.setUpView("-");
            }
        };
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

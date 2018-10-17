package com.antoineriche.privateinstructor.customviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MyCalendarView extends LinearLayout {

    private static int currentMonthOffset = 0;
    private final MyCalendarViewAdapter adapter;

    public MyCalendarView(Context context) {
        this(context, null);
    }

    public MyCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.adapter = new MyCalendarViewAdapter(new Date());
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.custom_view_calendar, this);
        findViewById(R.id.iv_next_month).setOnClickListener(v -> { currentMonthOffset++; refreshCalendar(currentMonthOffset);});
        findViewById(R.id.iv_previous_month).setOnClickListener(v -> { currentMonthOffset--; refreshCalendar(currentMonthOffset);});

        RecyclerView rv = findViewById(R.id.rv_month_days);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 7));
        rv.setAdapter(this.adapter);

        refreshCalendar(currentMonthOffset);
    }

    private void refreshCalendar(int currentMonthOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, currentMonthOffset);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        this.adapter.updateFirstDayOfMonth(calendar.getTime());

        ((TextView) findViewById(R.id.tv_month_label)).setText(new SimpleDateFormat("MMMM yyyy", Locale.FRANCE).format(calendar.getTime()));
    }

    private void setDetailsVisible(boolean pVisible){
        findViewById(R.id.ll_calendar_details).setVisibility(pVisible ? VISIBLE : GONE);
    }

    public void setListCourses(List<Course> courses){
        this.adapter.setListCourses(courses);
    }

    public void setCalenderClickListener(MyCalendarClickListener pListener){
        this.adapter.addCalendarClickListener(pListener);
    }

    public void hideDetails(){
        setDetailsVisible(false);
    }

    public void showDetails(){
        setDetailsVisible(true);
    }

    public boolean areDetailsVisible(){
        return findViewById(R.id.ll_calendar_details).getVisibility() == VISIBLE;
    }

    public void cleanSelection(){
        this.adapter.selectedDate = null;
        this.adapter.notifyDataSetChanged();
    }

    public void updateCourseCount(long pCourseCount){
        if(areDetailsVisible()) {
            ((TextView) findViewById(R.id.tv_course_count)).setText(String.format(Locale.FRANCE, "%d cours", pCourseCount));
        }
    }

    /**
     * MyCalendarView adapter
     */
    class MyCalendarViewAdapter extends RecyclerView.Adapter<MyCalendarViewAdapter.ViewHolder> {

        private Date firstDayOfMonth;
        private int currentMonth;
        private List<Course> courses;
        private List<Course> monthlyCourses;
        private MyCalendarClickListener mClickListener;
        private Date selectedDate;

        private int getDayCountInMonth(){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(firstDayOfMonth);
            return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        private int getBeforeDayOffset(){
            Calendar calendar = Calendar.getInstance(Locale.FRENCH);
            int firstOfWeek = calendar.getFirstDayOfWeek();
            calendar.setTime(this.firstDayOfMonth);
            int current = calendar.get(Calendar.DAY_OF_WEEK);
            return Math.abs(current - firstOfWeek);
        }

        private int getAfterDayOffset(){
            Calendar calendar = Calendar.getInstance(Locale.FRENCH);
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            calendar.add(Calendar.DAY_OF_WEEK, -1);

            int lastOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            calendar.setTime(this.firstDayOfMonth);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

            int current = calendar.get(Calendar.DAY_OF_WEEK);

            return lastOfWeek != current ? Math.abs(7 + lastOfWeek - current) : 0;
        }

        /**
         *
         */
        // Method called to update all the calendar view
        private void updateFirstDayOfMonth(Date pDate){
            this.firstDayOfMonth = pDate;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(pDate);
            this.currentMonth = calendar.get(Calendar.MONTH);
            this.monthlyCourses = getMonthlyCourses(this.firstDayOfMonth);
            updateCourseCount(this.monthlyCourses.size());
            notifyDataSetChanged();
        }
        /**
         *
         */

        MyCalendarViewAdapter(Date pDate) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(pDate);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            this.firstDayOfMonth = calendar.getTime();
            this.currentMonth = calendar.get(Calendar.MONTH);
            this.courses = new ArrayList<>();
            this.monthlyCourses = new ArrayList<>();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_rv_calendar_day, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyCalendarViewAdapter.ViewHolder holder, int position) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(this.firstDayOfMonth);
            calendar.add(Calendar.DAY_OF_YEAR, position - getBeforeDayOffset());

            holder.tvDayNumber.setText(String.format(Locale.FRANCE, "%d", calendar.get(Calendar.DAY_OF_MONTH)));
            List<Course> dailyCourses = monthlyCourses.stream().filter(course -> course.isTheGoodDay(calendar.getTime())).collect(Collectors.toList());

            if(!dailyCourses.isEmpty()) {
                holder.tvCourseCounter.setText(String.format(Locale.FRANCE, " x%d", dailyCourses.size()));
                holder.ivCourse.setVisibility(VISIBLE);
            } else {
                holder.tvCourseCounter.setText(null);
                holder.ivCourse.setVisibility(INVISIBLE);
            }

            //FIXME
            holder.tvDevoirCounter.setText(null);
            holder.ivDevoir.setVisibility(INVISIBLE);

            holder.rlDayCell.setOnClickListener(v -> {
                if(mClickListener != null){
                    mClickListener.clickOnDay(calendar.getTime(), dailyCourses);
                }
            });

            if (DateUtils.isToday(calendar.getTime())){
                holder.rlDayCell.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.unthem100));
            } else if ( calendar.get(Calendar.MONTH) == currentMonth ){
                holder.rlDayCell.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bkg_calendar_day));
            } else {
                holder.rlDayCell.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey200));
                holder.rlDayCell.setOnClickListener(null);
            }
        }

        @Override
        public int getItemCount() {
            return getDayCountInMonth() + getBeforeDayOffset() + getAfterDayOffset();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDayNumber, tvCourseCounter, tvDevoirCounter;
            ImageView ivDevoir, ivCourse;
            RelativeLayout rlDayCell;

            ViewHolder(View itemView) {
                super(itemView);
                tvDayNumber = itemView.findViewById(R.id.tv_day_number);
                tvCourseCounter = itemView.findViewById(R.id.tv_course_counter);
                tvDevoirCounter = itemView.findViewById(R.id.tv_devoir_counter);
                ivDevoir = itemView.findViewById(R.id.iv_devoirs);
                ivCourse = itemView.findViewById(R.id.iv_courses);
                rlDayCell = itemView.findViewById(R.id.rl_day_cell);
            }
        }

        void setListCourses(List<Course> pCourses){
            this.courses = pCourses;
            this.monthlyCourses = getMonthlyCourses(this.firstDayOfMonth);
            updateCourseCount(this.monthlyCourses.size());
            notifyDataSetChanged();
        }

        void addCalendarClickListener(MyCalendarClickListener mListener){
            this.mClickListener = mListener;
        }

        private List<Course> getMonthlyCourses(Date pFirstDayOfMonth){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(pFirstDayOfMonth);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            return this.courses.stream().filter(course -> course.isBetweenDates(
                    DateUtils.getFirstSecond(this.firstDayOfMonth),
                    DateUtils.getLastSecond(calendar.getTime())
            )).collect(Collectors.toList());
        }
    }

    /**
     * MyCalendarView interface
     */
    public interface MyCalendarClickListener {
        void clickOnDay(Date pDate, List<Course> pCourses);
    }
}

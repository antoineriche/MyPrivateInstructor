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
import com.antoineriche.privateinstructor.beans.Devoir;
import com.antoineriche.privateinstructor.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MyCalendarView extends LinearLayout {

    private static final String TAG = MyCalendarView.class.getSimpleName();

    private static int currentMonthOffset = 0;
    private Date firstDayOfMonth;
    private MyCalendarViewAdapter adapter;
    protected MyCalendarClickListener mListener;
    protected List<Course> mCourses = new ArrayList<>();
    protected List<Devoir> mDevoirs = new ArrayList<>();

    public MyCalendarView(Context context) {
        this(context, null);
    }

    public MyCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.custom_view_calendar, this);
        findViewById(R.id.iv_next_month).setOnClickListener(v -> { currentMonthOffset++; refreshCalendar(currentMonthOffset);});
        findViewById(R.id.iv_previous_month).setOnClickListener(v -> { currentMonthOffset--; refreshCalendar(currentMonthOffset);});

        this.adapter = new MyCalendarViewAdapter(new Date());
        RecyclerView rv = findViewById(R.id.rv_month_days);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 7));
        rv.setAdapter(this.adapter);

        refreshCalendar(currentMonthOffset);
    }

    private void refreshCalendar(int currentMonthOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, currentMonthOffset);
        this.firstDayOfMonth = DateUtils.getFirstDayOfMonth(calendar.getTime());
        this.adapter.refreshView();
        ((TextView) findViewById(R.id.tv_month_label)).setText(new SimpleDateFormat("MMMM yyyy", Locale.FRANCE).format(calendar.getTime()));
    }

    public void setUpCalendar(List<Course> courses, List<Devoir> devoirs, MyCalendarClickListener pListener){
        this.mListener = pListener;
        this.mCourses = courses;
        this.mDevoirs = devoirs;
        this.adapter.refreshView();
    }

    public void cleanSelection(){
        this.adapter.selectedDate = null;
        this.adapter.notifyDataSetChanged();
    }


    /**
     * MyCalendarView adapter
     */
    class MyCalendarViewAdapter extends RecyclerView.Adapter<MyCalendarViewAdapter.ViewHolder> {

        private int currentMonth;
        private List<Course> monthlyCourses;
        private List<Devoir> monthlyDevoirs;
        private Date selectedDate;

        //FIXME
        private int getBeforeDayOffset(){
            Calendar calendar = Calendar.getInstance(Locale.FRENCH);
            int firstOfWeek = calendar.getFirstDayOfWeek();
            calendar.setTime(firstDayOfMonth);
            int current = calendar.get(Calendar.DAY_OF_WEEK);
            return Math.abs(current - firstOfWeek);
        }

        //FIXME
        private int getAfterDayOffset(){
            Calendar calendar = Calendar.getInstance(Locale.FRENCH);
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            calendar.add(Calendar.DAY_OF_WEEK, -1);

            int lastOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            calendar.setTime(firstDayOfMonth);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

            int current = calendar.get(Calendar.DAY_OF_WEEK);

            return lastOfWeek != current ? Math.abs(7 + lastOfWeek - current) : 0;
        }

        /**
         * Method called to update all the calendar view
         */
        void refreshView(){
            updateMonthlyLists();
            notifyDataSetChanged();

            if(mListener != null) {
                mListener.monthChanged(firstDayOfMonth, this.monthlyCourses, this.monthlyDevoirs);
            }
        }

        private void updateMonthlyLists() {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(firstDayOfMonth);
            currentMonth = calendar.get(Calendar.MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

            this.monthlyCourses = mCourses.stream().filter(c -> c.isBetweenDates(
                    DateUtils.getFirstSecondOfTheDay(firstDayOfMonth),
                    DateUtils.getLastSecondOfTheDay(calendar.getTime())
            )).collect(Collectors.toList());

            this.monthlyDevoirs = mDevoirs.stream().filter(d -> d.isBetweenDates(
                    DateUtils.getFirstSecondOfTheDay(firstDayOfMonth),
                    DateUtils.getLastSecondOfTheDay(calendar.getTime())
            )).collect(Collectors.toList());
        }

        MyCalendarViewAdapter(Date pDate) {
            this.currentMonth = DateUtils.getMonthIndex(pDate);
            this.monthlyCourses = new ArrayList<>();
            this.monthlyDevoirs = new ArrayList<>();
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
            calendar.setTime(firstDayOfMonth);
            calendar.add(Calendar.DAY_OF_YEAR, position - getBeforeDayOffset());

            holder.tvDayNumber.setText(String.format(Locale.FRANCE, "%d", calendar.get(Calendar.DAY_OF_MONTH)));
            List<Course> dailyCourses = monthlyCourses.stream().filter(course -> course.isTheGoodDay(calendar.getTime())).collect(Collectors.toList());
            List<Devoir> dailyDevoirs = monthlyDevoirs.stream().filter(devoir -> devoir.isTheGoodDay(calendar.getTime())).collect(Collectors.toList());

            if(!dailyCourses.isEmpty()) {
                holder.tvCourseCounter.setText(String.format(Locale.FRANCE, " x%d", dailyCourses.size()));
                holder.ivCourse.setVisibility(VISIBLE);
            } else {
                holder.tvCourseCounter.setText(null);
                holder.ivCourse.setVisibility(GONE);
            }

            if(!dailyDevoirs.isEmpty()) {
                holder.tvDevoirCounter.setText(String.format(Locale.FRANCE, " x%d", dailyDevoirs.size()));
                holder.ivDevoir.setVisibility(VISIBLE);
            } else {
                holder.tvDevoirCounter.setText(null);
                holder.ivDevoir.setVisibility(GONE);
            }

            holder.rlDayCell.setOnClickListener(v -> {
                if(mListener != null){
                    mListener.clickOnDay(calendar.getTime(), dailyCourses, dailyDevoirs);
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
            return DateUtils.getDayCountOfMonth(firstDayOfMonth) + getBeforeDayOffset() + getAfterDayOffset();
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

    }

    /**
     * MyCalendarView interface
     */
    public interface MyCalendarClickListener {
        void clickOnDay(Date pDate, List<Course> pCourses, List<Devoir> pDevoirs);
        void monthChanged(Date pFirstDayOfMonth, List<Course> pCourses, List<Devoir> pDevoirs);
    }
}

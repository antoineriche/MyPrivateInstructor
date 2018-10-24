package com.antoineriche.privateinstructor.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.antoineriche.privateinstructor.DatabaseListener;
import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.customviews.ItemCounterView;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.customviews.MyCalendarView;
import com.antoineriche.privateinstructor.utils.CourseUtils;
import com.antoineriche.privateinstructor.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment implements MyCalendarView.MyCalendarClickListener {

    DatabaseListener mDBListener;
    CardView cvDetails;

    public CalendarFragment() {
    }

    public static CalendarFragment newInstance(){
        return new CalendarFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("Calendrier");
        setUpCalendarView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cvDetails = view.findViewById(R.id.cv_day_details);
        cvDetails.setVisibility(View.GONE);
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

    private void setUpCalendarView() {
        //FIXME use AsyncTask
        List<Course> c = CourseTable.getAllCourses(mDBListener.getDatabase());
        ((MyCalendarView) getView().findViewById(R.id.mcv)).setListCourses(c);
        ((MyCalendarView) getView().findViewById(R.id.mcv)).setCalenderClickListener(this);
    }

    @Override
    public void clickOnDay(Date pDate, List<Course> pCourses) {
        cvDetails.setVisibility(View.VISIBLE);

        pCourses.sort(Comparator.comparingLong(Course::getDate));

        ((TextView) cvDetails.findViewById(R.id.tv_day_details_title)).setText(StringUtils.capitalizeFirstChar(
                new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRANCE).format(pDate)));

        cvDetails.findViewById(R.id.iv_day_details_close).setOnClickListener(v -> cvDetails.setVisibility(View.GONE));

        if(pCourses.isEmpty()){
            ((TextView) cvDetails.findViewById(R.id.tv_day_details_no_course)).setText("Pas de cours");
            cvDetails.findViewById(R.id.tv_day_details_no_course).setVisibility(View.VISIBLE);
            cvDetails.findViewById(R.id.rv_day_details_courses).setVisibility(View.GONE);
        } else {
            cvDetails.findViewById(R.id.tv_day_details_no_course).setVisibility(View.GONE);
            RecyclerView rv = cvDetails.findViewById(R.id.rv_day_details_courses);
            rv.setVisibility(View.VISIBLE);
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            rv.setAdapter(new RecyclerViewDailyCourseAdapter(pCourses));
        }
    }

    @Override
    public void monthChanged(Date pFirstDayOfMonth, List<Course> pCourses) {
        int courseCount = pCourses.size();
        double monthCountMean = CourseTable.getMonthlyCourseCountMean(mDBListener.getDatabase());
        ((ItemCounterView) getView().findViewById(R.id.icv_course)).setUpView(courseCount, monthCountMean);

        double money = CourseUtils.extractMoneySum(pCourses);
        double monthMoneyMean = CourseTable.getMonthlyMoneyMean(mDBListener.getDatabase());
        ((ItemCounterView) getView().findViewById(R.id.icv_devoir)).setUpView(0, 2);

        ((ItemCounterView) getView().findViewById(R.id.icv_money)).setUpView(money, monthMoneyMean);
    }

    public static class RecyclerViewDailyCourseAdapter extends RecyclerView.Adapter<RecyclerViewDailyCourseAdapter.CourseViewHolder> {

        private List<Course> mCourses;

        RecyclerViewDailyCourseAdapter(List<Course> mCourses) {
            this.mCourses = mCourses;
        }

        @NonNull
        @Override
        public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_rv_daily_courses, parent, false);
            return new CourseViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
            final Course course = mCourses.get(position);
            holder.tvCoursePupil.setText(course.getPupil().getFullName());
            holder.tvCourseTime.setText(course.getFriendlyTimeSlot());
        }

        @Override
        public int getItemCount() {
            return this.mCourses.size();
        }

        class CourseViewHolder extends RecyclerView.ViewHolder {
            CardView cvCell;
            TextView tvCourseTime, tvCoursePupil;

            CourseViewHolder(View itemView) {
                super(itemView);
                cvCell = itemView.findViewById(R.id.cv_course_cell);
                tvCourseTime = itemView.findViewById(R.id.tv_course_time);
                tvCoursePupil = itemView.findViewById(R.id.tv_course_pupil);
            }
        }
    }
}

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
import com.antoineriche.privateinstructor.beans.DatabaseItem;
import com.antoineriche.privateinstructor.beans.Devoir;
import com.antoineriche.privateinstructor.customviews.ItemCounterView;
import com.antoineriche.privateinstructor.customviews.MyCalendarView;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.database.DevoirTable;
import com.antoineriche.privateinstructor.utils.CourseUtils;
import com.antoineriche.privateinstructor.utils.DateUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CalendarFragment extends Fragment implements MyCalendarView.MyCalendarClickListener {

    DatabaseListener mDBListener;
    CardView cvDetails;

    public CalendarFragment() {
    }

    public static CalendarFragment newInstance() {
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
        MyCalendarView mcv = getView().findViewById(R.id.mcv);
        mcv.setUpCalendar(CourseTable.getAllCourses(mDBListener.getDatabase()),
                DevoirTable.getAllDevoirs(mDBListener.getDatabase()), this);
    }

    @Override
    public void clickOnDay(Date pDate, List<Course> pCourses, List<Devoir> pDevoirs) {
        cvDetails.setVisibility(View.VISIBLE);

        List<DatabaseItem> courses = new ArrayList<>();
        List<DatabaseItem> devoirs = new ArrayList<>();
        pCourses.sort(Comparator.comparingLong(Course::getDate));
        pDevoirs.sort(Comparator.comparingLong(Devoir::getDate));

        ((TextView) cvDetails.findViewById(R.id.tv_day_details_title)).setText(DateUtils.getFriendlyDate(pDate));

        cvDetails.findViewById(R.id.iv_day_details_close).setOnClickListener(v -> cvDetails.setVisibility(View.GONE));
        //FIXME
        if (pCourses.isEmpty()) {
            ((TextView) cvDetails.findViewById(R.id.tv_day_details_no_course)).setText("Pas de cours");
            cvDetails.findViewById(R.id.tv_day_details_no_course).setVisibility(View.VISIBLE);
            cvDetails.findViewById(R.id.rv_day_details_courses).setVisibility(View.GONE);
        } else {
            courses.addAll(pCourses);
            cvDetails.findViewById(R.id.tv_day_details_no_course).setVisibility(View.GONE);
            RecyclerView rv = cvDetails.findViewById(R.id.rv_day_details_courses);
            rv.setVisibility(View.VISIBLE);
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            rv.setAdapter(new DailyItemAdapter(courses));
        }

        if (pDevoirs.isEmpty()) {
            ((TextView) cvDetails.findViewById(R.id.tv_day_details_no_devoir)).setText("Pas de devoir");
            cvDetails.findViewById(R.id.tv_day_details_no_devoir).setVisibility(View.VISIBLE);
            cvDetails.findViewById(R.id.rv_day_details_devoirs).setVisibility(View.GONE);
        } else {
            devoirs.addAll(pDevoirs);
            cvDetails.findViewById(R.id.tv_day_details_no_devoir).setVisibility(View.GONE);
            RecyclerView rv = cvDetails.findViewById(R.id.rv_day_details_devoirs);
            rv.setVisibility(View.VISIBLE);
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            rv.setAdapter(new DailyItemAdapter(devoirs));
        }
    }

    @Override
    public void monthChanged(Date pFirstDayOfMonth, List<Course> pCourses, List<Devoir> pDevoirs) {
        int courseCount = pCourses.size();
        double monthCourseCountMean = CourseTable.getMonthlyCourseCountMean(mDBListener.getDatabase());
        ((ItemCounterView) getView().findViewById(R.id.icv_course)).setUpView(courseCount, monthCourseCountMean);

        int devoirCount = pDevoirs.size();
        double monthDevoirCountMean = DevoirTable.getMonthlyDevoirCountMean(mDBListener.getDatabase());
        ((ItemCounterView) getView().findViewById(R.id.icv_devoir)).setUpView(devoirCount, monthDevoirCountMean);

        double money = CourseUtils.extractMoneySum(pCourses);
        double monthMoneyMean = CourseTable.getMonthlyMoneyMean(mDBListener.getDatabase());
        ((ItemCounterView) getView().findViewById(R.id.icv_money)).setUpView(money, monthMoneyMean);
    }

    public static class DailyItemAdapter extends RecyclerView.Adapter<DailyItemAdapter.DailyItemHolder> {

        private List<DatabaseItem> mItems;

        DailyItemAdapter(List<DatabaseItem> pItems) {
            this.mItems = pItems;
        }

        @NonNull
        @Override
        public DailyItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_rv_daily_items, parent, false);
            return new DailyItemHolder(v);
        }

        @Override
        public int getItemCount() {
            if (!this.mItems.isEmpty()) {
                DatabaseItem item = this.mItems.get(0);
                return (item instanceof Course || item instanceof Devoir) ? this.mItems.size() : 0;
            } else {
                return 0;
            }
        }

        @Override
        public void onBindViewHolder(@NonNull DailyItemHolder holder, int position) {
            final DatabaseItem item = mItems.get(position);
            if (item instanceof Course) {
                holder.tvItemDetails.setText(((Course) item).getPupil().getFullName());
                holder.tvItemLabel.setText(((Course) item).getFriendlyTimeSlot());
            } else if (item instanceof Devoir) {
                holder.tvItemLabel.setText(((Devoir) item).getPupil().getFullName());
                holder.tvItemDetails.setVisibility(View.GONE);
            }
        }

        class DailyItemHolder extends RecyclerView.ViewHolder {
            TextView tvItemLabel, tvItemDetails;

            DailyItemHolder(View itemView) {
                super(itemView);
                tvItemLabel = itemView.findViewById(R.id.tv_daily_item_label);
                tvItemDetails = itemView.findViewById(R.id.tv_daily_item_details);
            }
        }
    }
}

package com.antoineriche.privateinstructor.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.beans.EventItem;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.utils.CourseUtils;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MoneyFragment extends AbstractDatabaseFragment {

    public MoneyFragment() {
    }

    public static MoneyFragment newInstance(){
        return new MoneyFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_money, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvTotalMoney = view.findViewById(R.id.tv_money_total);
        TextView tvValidatedMoney = view.findViewById(R.id.tv_money_validated);
        TextView tvWaitingMoney = view.findViewById(R.id.tv_money_waiting);
        TextView tvForeseenMoney = view.findViewById(R.id.tv_money_foreseen);

        List<Course> allCourses = CourseTable.getAllCourses(mListener.getDatabase());


        double validated = CourseUtils.extractMoneySum(
                allCourses.stream().filter(c -> c.getState() == EventItem.VALIDATED).collect(Collectors.toList()));
        double foreseen = CourseUtils.extractMoneySum(
                allCourses.stream().filter(c -> c.getState() == EventItem.FORESEEN).collect(Collectors.toList()));
        double waiting = CourseUtils.extractMoneySum(
                allCourses.stream().filter(c -> c.getState() == EventItem.WAITING_FOR_VALIDATION).collect(Collectors.toList()));

        tvTotalMoney.setText(String.format(Locale.FRANCE, "%.02f €", (validated + foreseen + waiting)));
        tvValidatedMoney.setText(String.format(Locale.FRANCE, "%.02f €", validated));
        tvWaitingMoney.setText(String.format(Locale.FRANCE, "%.02f €", waiting));
        tvForeseenMoney.setText(String.format(Locale.FRANCE, "%.02f €", foreseen));
    }
}

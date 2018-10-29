package com.antoineriche.privateinstructor.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.utils.CourseUtils;

import java.util.Locale;

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

        TextView tvMoney = view.findViewById(R.id.tv_money_total);

        double money = CourseUtils.extractMoneySum(CourseTable.getAllCourses(mListener.getDatabase()));
        tvMoney.setText(String.format(Locale.FRANCE, "%.02f €", money));
    }
}

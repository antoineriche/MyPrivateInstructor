package com.antoineriche.privateinstructor.activities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.database.CourseTable;

import java.util.Locale;

public class ToImplementFragment extends Fragment {

    private static final String SECTION_NAME = "section";

    private String mSection;

    public ToImplementFragment() {
    }

    public static ToImplementFragment newInstance(String pSection) {
        ToImplementFragment fragment = new ToImplementFragment();
        Bundle args = new Bundle();
        args.putString(SECTION_NAME, pSection);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSection = getArguments().getString(SECTION_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_to_implement, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((TextView) getView().findViewById(R.id.tv_section_to_implement)).setText(String.format(Locale.FRANCE, "%s is not implemeted yet", mSection));
        getActivity().setTitle(getClass().getSimpleName());
    }
}

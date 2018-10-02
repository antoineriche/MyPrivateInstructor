package com.antoineriche.privateinstructor.activities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

    private ToImplementFragmentInteractionListener mListener;

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
        View view =  inflater.inflate(R.layout.fragment_to_implement, container, false);
        ((TextView) view.findViewById(R.id.tv_section_to_implement)).setText(String.format(Locale.FRANCE, "%s is not implemeted yet", mSection));
        view.findViewById(R.id.fab_return).setOnClickListener(v -> onButtonPressed());


        ((TextView) view.findViewById(R.id.tv_section_to_implement)).append(
                String.format(Locale.FRANCE, "\n%d courses",
                        CourseTable.getAllCourses(mListener.getDatabase()).size())
        );

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction(mSection);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ToImplementFragmentInteractionListener) {
            mListener = (ToImplementFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface ToImplementFragmentInteractionListener {
        void onFragmentInteraction(String pSection);
        SQLiteDatabase getDatabase();
    }
}

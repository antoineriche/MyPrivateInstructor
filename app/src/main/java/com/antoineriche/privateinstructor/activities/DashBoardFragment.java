package com.antoineriche.privateinstructor.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.adapters.EventItemAdapter;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.beans.Devoir;
import com.antoineriche.privateinstructor.beans.EventItem;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.database.DevoirTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class DashBoardFragment extends AbstractDatabaseFragment {

    private final static String TAG = "DashBoardFragment";

    private EventItemAdapter mCoursesAdapter, mDevoirsAdapter;
    private DashBoardListener mDashboardListener;

    public DashBoardFragment(){}

    public static DashBoardFragment newInstance(){
        return new DashBoardFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCoursesAdapter = new EventItemAdapter(getActivity(), new ArrayList<>(), Course.class, this::openDialog);
        mDevoirsAdapter = new EventItemAdapter(getActivity(), new ArrayList<>(), Devoir.class, this::openDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        refreshView(getView());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof DashBoardListener){
            mDashboardListener = (DashBoardListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement DashBoardListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDashboardListener = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.cv_toggle_uncompleted_courses).setOnClickListener(v -> {
            view.findViewById(R.id.rv_uncompleted_courses).setVisibility(
                    view.findViewById(R.id.rv_uncompleted_courses).getVisibility() == View.VISIBLE ?
                            View.GONE : View.VISIBLE);
        });

        view.findViewById(R.id.cv_toggle_uncompleted_devoirs).setOnClickListener(v -> {
            view.findViewById(R.id.rv_uncompleted_devoirs).setVisibility(
                    view.findViewById(R.id.rv_uncompleted_devoirs).getVisibility() == View.VISIBLE ?
                            View.GONE : View.VISIBLE);
        });

        RecyclerView rv = view.findViewById(R.id.rv_uncompleted_courses);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(mCoursesAdapter);

        RecyclerView rv2 = view.findViewById(R.id.rv_uncompleted_devoirs);
        rv2.setLayoutManager(new LinearLayoutManager(getContext()));
        rv2.setAdapter(mDevoirsAdapter);

        refreshView(view);
    }


    private void openDialog(EventItem pEventItem){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.comment_form, null);

        EditText etChapter = dialogView.findViewById(R.id.et_course_chapter);
        EditText etComment = dialogView.findViewById(R.id.et_course_comment);

        if(pEventItem instanceof Course){
            etChapter.setText(((Course) pEventItem).getChapter());
            etComment.setText(((Course) pEventItem).getComment());
        } else if(pEventItem instanceof Devoir){
            etChapter.setText(((Devoir) pEventItem).getChapter());
            etComment.setText(((Devoir) pEventItem).getComment());
        }

        dialogBuilder.setView(dialogView)
                .setTitle(pEventItem.getFriendlyDate())
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Enregistrer", (dialogInterface, i) ->
                        updateComment(pEventItem, etChapter.getText().toString(), etComment.getText().toString()))
                .show();
    }

    private void updateComment(EventItem pEventItem, String pChapter, String pComment){
        if(pEventItem instanceof Course){
            Course course = (Course) pEventItem;
            course.setChapter(pChapter);
            course.setComment(pComment);
            CourseTable.updateCourse(mListener.getDatabase(), course.getId(), course);
            mDashboardListener.onItemComplete(course.getUuid());
        } else if(pEventItem instanceof Devoir){
            Devoir devoir = (Devoir) pEventItem;
            devoir.setChapter(pChapter);
            devoir.setComment(pComment);
            DevoirTable.updateDevoir(mListener.getDatabase(), devoir.getId(), devoir);
            mDashboardListener.onItemComplete(devoir.getUuid());
        }
        refreshView(getView());
    }

    private void refreshView(View view){
        List<Course> mUncompletedCourses = CourseTable.getAllCourses(mListener.getDatabase()).stream().filter(c -> !c.isComplete()).collect(Collectors.toList());
        List<Devoir> mUncompletedDevoirs = DevoirTable.getAllDevoirs(mListener.getDatabase()).stream().filter(d -> !d.isComplete()).collect(Collectors.toList());

        mCoursesAdapter.refreshList(new ArrayList<>(mUncompletedCourses));
        mDevoirsAdapter.refreshList(new ArrayList<>(mUncompletedDevoirs));

        ((TextView) view.findViewById(R.id.tv_uncompleted_courses)).setText(String.format(Locale.FRANCE, "%d cours incomplets", mUncompletedCourses.size()));
        ((TextView) view.findViewById(R.id.tv_uncompleted_devoirs)).setText(String.format(Locale.FRANCE, "%d devoirs incomplets", mUncompletedDevoirs.size()));
    }

    interface DashBoardListener {
        void onItemComplete(String pItemUuid);
    }
}

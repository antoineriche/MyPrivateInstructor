package com.antoineriche.privateinstructor.activities.item.course;

import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.item.AbstractDetailsItemFragment;
import com.antoineriche.privateinstructor.activities.item.AbstractItemActivity;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.beans.Pupil;

import java.io.File;
import java.util.Date;
import java.util.Locale;

public class CourseDetailsFragment extends AbstractDetailsItemFragment {

    public CourseDetailsFragment() {
    }

    public static CourseDetailsFragment newInstance(long pCourseId) {
        CourseDetailsFragment fragment = new CourseDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(AbstractItemActivity.ARG_ITEM_ID, pCourseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.details_course, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Course course = getItem();
        menu.findItem(R.id.action_course_cancel).setVisible(course.getState() != Course.CANCELED);
        menu.findItem(R.id.action_course_validate).setVisible(course.getState() != Course.VALIDATED);
        menu.findItem(R.id.action_course_waiting).setVisible(course.getState() != Course.WAITING_FOT_VALIDATION);
//        menu.findItem(R.id.action_course_foreseen).setVisible(course.getState() != Course.FORESEEN && new Date().before(new Date(course.getDate())));
        menu.findItem(R.id.action_course_foreseen).setVisible(course.getState() != Course.FORESEEN);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_course_cancel:
                getDbItemListener().updateItem(getItemId(), updateState(Course.CANCELED));
                break;
            case R.id.action_course_validate:
                getDbItemListener().updateItem(getItemId(), updateState(Course.VALIDATED));
                break;
            case R.id.action_course_waiting:
                getDbItemListener().updateItem(getItemId(), updateState(Course.WAITING_FOT_VALIDATION));
                break;
            case R.id.action_course_foreseen:
                getDbItemListener().updateItem(getItemId(), updateState(Course.FORESEEN));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int layout() {
        return R.layout.course_details;
    }

    @Override
    protected void fillViewWithItem(View pView, Object pItem) {
        Course course = (Course) pItem;
        Pupil pupil = course.getPupil();
        ((TextView) pView.findViewById(R.id.tv_course_date)).setText(
                String.format(Locale.FRANCE, "%s\n%s", course.getFriendlyDate(), course.getFriendlyTimeSlot()));

        // PUPIL
        if(TextUtils.isEmpty(pupil.getImgPath()) || !new File(pupil.getImgPath()).exists()) {
            Drawable pImg = pupil.getGender() == Pupil.GENDER_MALE ?
                    ContextCompat.getDrawable(getActivity(), R.drawable.man) :
                    ContextCompat.getDrawable(getActivity(), R.drawable.woman);

            ((ImageView) pView.findViewById(R.id.iv_course_pupil_pix)).setImageDrawable(pImg);
        } else {
            ((ImageView) pView.findViewById(R.id.iv_course_pupil_pix)).setImageBitmap(BitmapFactory.decodeFile(pupil.getImgPath()));
        }

        ((TextView) pView.findViewById(R.id.tv_course_pupil_name)).setText(pupil.getFullName());
        ((TextView) pView.findViewById(R.id.tv_course_pupil_class_level)).setText(pupil.getFriendlyClassLevel(getContext()));

        ((TextView) pView.findViewById(R.id.tv_course_status)).setText(course.getFriendlyStatus(getContext()));
        ((TextView) pView.findViewById(R.id.tv_course_money)).setText(String.format(Locale.FRANCE, "%.02f €", course.getMoney()));

        ((TextView) pView.findViewById(R.id.tv_course_chapter)).setText(course.getChapter());
        ((TextView) pView.findViewById(R.id.tv_course_comment)).setText(course.getComment());
    }

    @Override
    protected String deletionDialogMessage() {
        return getString(R.string.dialog_delete_course);
    }

    @Override
    protected Course getItem() {
        return (Course) super.getItem();
    }

    private Course updateState(int pNewState){
        Course course = getItem();
        course.setState(pNewState);
        return course;
    }
}

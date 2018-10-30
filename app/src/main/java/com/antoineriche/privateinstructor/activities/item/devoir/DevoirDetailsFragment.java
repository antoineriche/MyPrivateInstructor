package com.antoineriche.privateinstructor.activities.item.devoir;

import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import com.antoineriche.privateinstructor.beans.Devoir;
import com.antoineriche.privateinstructor.beans.Pupil;

import java.io.File;
import java.util.Locale;

public class DevoirDetailsFragment extends AbstractDetailsItemFragment {

    public DevoirDetailsFragment() {
    }

    public static DevoirDetailsFragment newInstance(long pCourseId) {
        DevoirDetailsFragment fragment = new DevoirDetailsFragment();
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
        //TODO rename
        inflater.inflate(R.menu.details_course, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Devoir devoir = getItem();
        menu.findItem(R.id.action_course_cancel).setVisible(devoir.getState() != Course.CANCELED);
        menu.findItem(R.id.action_course_validate).setVisible(devoir.getState() != Course.VALIDATED);
        menu.findItem(R.id.action_course_waiting).setVisible(devoir.getState() != Course.WAITING_FOR_VALIDATION);
//        menu.findItem(R.id.action_course_foreseen).setVisible(course.getState() != Course.FORESEEN && new Date().before(new Date(course.getDate())));
        menu.findItem(R.id.action_course_foreseen).setVisible(devoir.getState() != Course.FORESEEN);
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
                getDbItemListener().updateItem(getItemId(), updateState(Course.WAITING_FOR_VALIDATION));
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
        return R.layout.devoir_details;
    }

    @Override
    protected void fillViewWithItem(View pView, Object pItem) {
        Devoir devoir = (Devoir) pItem;
        Pupil pupil = devoir.getPupil();
        StringBuilder strBHeader = new StringBuilder();
        strBHeader.append(devoir.getFriendlyDate()).append("\n");
        strBHeader.append(devoir.getFriendlyType(getContext()));
        strBHeader.append(" | ").append(devoir.getFriendlyDuration());
        ((TextView) pView.findViewById(R.id.tv_devoir_date)).setText(strBHeader.toString());

        // PUPIL
        if(TextUtils.isEmpty(pupil.getImgPath()) || !new File(pupil.getImgPath()).exists()) {
            Drawable pImg = pupil.getGender() == Pupil.GENDER_MALE ?
                    ContextCompat.getDrawable(getActivity(), R.drawable.man) :
                    ContextCompat.getDrawable(getActivity(), R.drawable.woman);

            ((ImageView) pView.findViewById(R.id.iv_devoir_pupil_pix)).setImageDrawable(pImg);
        } else {
            ((ImageView) pView.findViewById(R.id.iv_devoir_pupil_pix)).setImageBitmap(BitmapFactory.decodeFile(pupil.getImgPath()));
        }

        ((TextView) pView.findViewById(R.id.tv_devoir_pupil_name)).setText(pupil.getFullName());
        ((TextView) pView.findViewById(R.id.tv_devoir_pupil_class_level)).setText(pupil.getFriendlyClassLevel(getContext()));

        ((TextView) pView.findViewById(R.id.tv_devoir_status)).setText(devoir.getFriendlyStatus(getContext()));
        ((TextView) pView.findViewById(R.id.tv_devoir_mark)).setText(String.format(Locale.FRANCE, "%.02f/%.02f", devoir.getMark(), devoir.getMaxMark()));

        ((TextView) pView.findViewById(R.id.tv_devoir_chapter)).setText(devoir.getChapter());
        ((TextView) pView.findViewById(R.id.tv_devoir_comment)).setText(devoir.getComment());
    }

    @Override
    protected String deletionDialogMessage() {
        return getString(R.string.dialog_delete_devoir);
    }

    @Override
    protected Devoir getItem() {
        return (Devoir) super.getItem();
    }

    private Devoir updateState(int pNewState){
        Devoir devoir = getItem();
        devoir.setState(pNewState);
        return devoir;
    }
}

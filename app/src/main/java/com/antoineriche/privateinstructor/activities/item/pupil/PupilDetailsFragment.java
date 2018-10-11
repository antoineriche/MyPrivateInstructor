package com.antoineriche.privateinstructor.activities.item.pupil;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.item.AbstractDetailsItemFragment;
import com.antoineriche.privateinstructor.activities.item.AbstractItemActivity;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.antoineriche.privateinstructor.database.PupilTable;

import java.io.File;
import java.util.Locale;

public class PupilDetailsFragment extends AbstractDetailsItemFragment {

    public PupilDetailsFragment() {
    }

    public static PupilDetailsFragment newInstance(long pCourseId) {
        PupilDetailsFragment fragment = new PupilDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(AbstractItemActivity.ARG_ITEM_ID, pCourseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected Pupil getItemFromDB(SQLiteDatabase pDatabase, long pId) {
        return PupilTable.getPupilWithId(pDatabase, pId);
    }

    @Override
    protected boolean deleteItemFromDB(SQLiteDatabase pDatabase, long pId) {
        return PupilTable.removePupilWithID(pDatabase, pId);
    }

    @Override
    protected int layout() {
        return R.layout.pupil_details;
    }

    @Override
    protected void fillViewWithItem(View pView, Object pItem) {
        Pupil pupil = (Pupil) pItem;

        if(TextUtils.isEmpty(pupil.getImgPath()) || !new File(pupil.getImgPath()).exists()) {
            Drawable pImg = pupil.getGender() == Pupil.GENDER_MALE ?
                    ContextCompat.getDrawable(getActivity(), R.drawable.man) :
                    ContextCompat.getDrawable(getActivity(), R.drawable.woman);

            ((ImageView) pView.findViewById(R.id.iv_pupil_pix)).setImageDrawable(pImg);
        } else {
            ((ImageView) pView.findViewById(R.id.iv_pupil_pix)).setImageBitmap(BitmapFactory.decodeFile(pupil.getImgPath()));
        }


        ((TextView) pView.findViewById(R.id.tv_pupil_name)).setText(pupil.getFullName());
        ((TextView) pView.findViewById(R.id.tv_pupil_class_level)).setText(""+pupil.getClassLevel());

        ((TextView) pView.findViewById(R.id.tv_pupil_address)).setText(pupil.getAddress());
        ((TextView) pView.findViewById(R.id.tv_pupil_phone)).setText(pupil.getPhone());
        ((TextView) pView.findViewById(R.id.tv_pupil_parent_phone)).setText(pupil.getParentPhone());

        ((TextView) pView.findViewById(R.id.tv_pupil_hourly_price)).setText(String.format(Locale.FRANCE, "%.02f €", pupil.getHourlyPrice()));
        ((TextView) pView.findViewById(R.id.tv_pupil_course_frequency)).setText(""+pupil.getFrequency());
        ((TextView) pView.findViewById(R.id.tv_pupil_payment_type)).setText(""+pupil.getPaymentType());
    }

    @Override
    protected String title(Object pItem) {
        return String.format(Locale.FRANCE, "Élève %03d", ((Pupil) pItem).getId());
    }
}

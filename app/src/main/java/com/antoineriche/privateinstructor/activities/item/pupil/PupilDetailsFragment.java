package com.antoineriche.privateinstructor.activities.item.pupil;

import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.ToImplementFragment;
import com.antoineriche.privateinstructor.activities.item.AbstractDetailsItemFragment;
import com.antoineriche.privateinstructor.activities.item.AbstractItemActivity;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.database.PupilTable;

import java.io.File;

public class PupilDetailsFragment extends AbstractDetailsItemFragment {

    public final static String PUPIL = "pupil";

    public final static String TAB_GENERAL = "general";
    public final static String TAB_COURSES = "courses";
    public final static String TAB_DEVOIRS = "devoirs";
    public final static String TAB_MAP = "map";
    public final static String TAB_EVOLUTION = "evolution";

    public PupilDetailsFragment() {
    }

    public static PupilDetailsFragment newInstance(long pPupilId) {
        PupilDetailsFragment fragment = new PupilDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(AbstractItemActivity.ARG_ITEM_ID, pPupilId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected Pupil getItemFromDB(SQLiteDatabase pDatabase, long pId) {
        Pupil pupil = PupilTable.getPupilWithId(pDatabase, pId);
        pupil.setCourses(CourseTable.getCoursesForPupil(pDatabase, pId));
        return pupil;
    }

    @Override
    protected boolean deleteItemFromDB(SQLiteDatabase pDatabase, long pId) {
        Pupil p = getItemFromDB(pDatabase, pId);
        if(!TextUtils.isEmpty(p.getImgPath()) && new File(p.getImgPath()).exists()){
            if(new File(p.getImgPath()).delete()){
                return PupilTable.removePupilWithID(pDatabase, pId);
            }
        } else {
            return PupilTable.removePupilWithID(pDatabase, pId);
        }
        return false;
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

        TabLayout tabLayout = pView.findViewById(R.id.tl_pupil_details);
        setUpTabLayout(tabLayout, pupil);

        ((TextView) pView.findViewById(R.id.tv_pupil_name)).setText(pupil.getFirstname());
    }

    @Override
    protected String deletionDialogMessage() {
        return getString(R.string.dialog_delete_pupil);
    }

    protected void loadFragment(Fragment pFragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_pupil_details, pFragment).commit();
    }

    private void setUpTabLayout(TabLayout pTabLayout, Pupil pupil){
        TabLayout.Tab tab;
        ColorStateList myList;

        int[][] states = new int[][] {new int[] { android.R.attr.state_selected}, new int[] {-android.R.attr.state_selected}};
        int[] colors = new int[] {getContext().getColor(R.color.unthemAccent), getContext().getColor(R.color.white)};
        myList = new ColorStateList(states, colors);

        tab = pTabLayout.newTab();
        tab = setUpTab(tab, R.drawable.baseline_person_white_48, TAB_GENERAL, myList);
        pTabLayout.addTab(tab);

        tab = pTabLayout.newTab();
        tab = setUpTab(tab, R.drawable.baseline_public_white_48, TAB_MAP, myList);
        pTabLayout.addTab(tab);

        tab = pTabLayout.newTab();
        tab = setUpTab(tab, R.drawable.ic_book_open_page_variant_white_48dp, TAB_COURSES, myList);
        pTabLayout.addTab(tab);

        tab = pTabLayout.newTab();
        tab = setUpTab(tab, R.drawable.ic_assignment_white_48dp, TAB_DEVOIRS, myList);
        pTabLayout.addTab(tab);

        tab = pTabLayout.newTab();
        tab = setUpTab(tab, R.drawable.ic_trending_up_white_48dp, TAB_EVOLUTION, myList);
        pTabLayout.addTab(tab);

        pTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment;
                if (TAB_GENERAL.equals(tab.getTag())){
                    fragment = AbstractPupilSubDetailsFragment.PupilGeneralDetailsFragment.newInstance(pupil);
                } else if (TAB_COURSES.equals(tab.getTag())){
                    fragment = AbstractPupilSubDetailsFragment.PupilCoursesDetailsFragment.newInstance(pupil);
                } else if (TAB_DEVOIRS.equals(tab.getTag())){
                    fragment = ToImplementFragment.newInstance("Pupil devoirs");
                } else if (TAB_MAP.equals(tab.getTag())){
                    fragment = AbstractPupilSubDetailsFragment.PupilMapDetailsFragment.newInstance(pupil);
                } else if (TAB_EVOLUTION.equals(tab.getTag())){
                    fragment = ToImplementFragment.newInstance("Pupil evolution");
                } else {
                    fragment = AbstractPupilSubDetailsFragment.PupilGeneralDetailsFragment.newInstance(pupil);
                }
                loadFragment(fragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabSelected(tab);
            }
        });

        pTabLayout.getTabAt(0).select();
    }

    private TabLayout.Tab setUpTab(TabLayout.Tab tab, int resDrawable, String pTag, ColorStateList pColorStateList){
        Drawable drawable = ContextCompat.getDrawable(getContext(), resDrawable);
        DrawableCompat.setTintList(drawable, pColorStateList);
        tab.setIcon(drawable);
        tab.setTag(pTag);
        return tab;
    }
}

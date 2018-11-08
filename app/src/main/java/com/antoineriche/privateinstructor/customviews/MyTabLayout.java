package com.antoineriche.privateinstructor.customviews;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;

import com.antoineriche.privateinstructor.R;

public class MyTabLayout extends TabLayout implements TabLayout.BaseOnTabSelectedListener {

    private MyTabLayoutListener mListener;

    public MyTabLayout(Context context) {
        this(context, null);
    }

    public MyTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setBackgroundColor(getContext().getColor(R.color.them900));
        addOnTabSelectedListener(this);
    }

    public void addTab(String pTitle, String pTag){
        Tab tab = newTab();
        tab.setText(pTitle);
        tab.setTag(pTag);
        addTab(tab);
    }

    public void addTab(int pResDrawable, String pTag){
        Tab tab = newTab();
        Drawable drawable = getContext().getDrawable(pResDrawable);
        DrawableCompat.setTintList(drawable, getResources().getColorStateList(R.color.img_tint_selector, null));
        tab.setIcon(drawable);tab.setIcon(pResDrawable);
        tab.setTag(pTag);
        addTab(tab);
    }

    public void addTabListener(MyTabLayoutListener pListener){
        mListener = pListener;
    }

    @Nullable
    @Override
    public Tab getTabAt(int index) {
        Tab tab = super.getTabAt(index);
        onTabSelected(tab);
        return tab;
    }

    @Override
    public void onTabSelected(Tab tab) {
        if(mListener != null){
            mListener.onTabSelected(tab.getTag().toString());
        }
    }

    @Override
    public void onTabUnselected(Tab tab) {

    }

    @Override
    public void onTabReselected(Tab tab) {
        onTabSelected(tab);
    }

    public interface MyTabLayoutListener {
        void onTabSelected(String tabTag);
    }
}

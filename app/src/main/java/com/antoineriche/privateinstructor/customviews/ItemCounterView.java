package com.antoineriche.privateinstructor.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.utils.StringUtils;

import java.util.Locale;

public class ItemCounterView extends CardView {

    private double mCount;
    private double mMean = -1;
    private Drawable mIcon;

    private boolean showDetails, showIndicator;

    public ItemCounterView(Context context) {
        this(context, null);
    }

    public ItemCounterView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemCounterView);

        showDetails = typedArray.getBoolean(R.styleable.ItemCounterView_showDetails, true);
        showIndicator = typedArray.getBoolean(R.styleable.ItemCounterView_showIndicator, true);

        setIcon(typedArray.getResourceId(R.styleable.ItemCounterView_srcImg,  R.drawable.baseline_close_white_48));

        typedArray.recycle();

        init();
    }

    private void init(){
        inflate(getContext(), R.layout.custom_view_item_counter, this);

        if(isInEditMode()){
            setUpView(12, 4);
        }

        setViewVisibility(findViewById(R.id.tv_item_counter_count_2), showDetails);
        setViewVisibility(findViewById(R.id.iv_item_counter_count_indicator), showIndicator);
    }

    public void setIcon(int resDrawable){
        mIcon = ContextCompat.getDrawable(getContext(), resDrawable);
    }

    public void setCount(double pCount){
        mCount = pCount;
    }

    public void setMean(double pMean){
        mMean = pMean;
    }

    public void setViewVisibility(View view, boolean visible){
        view.setVisibility(visible ? VISIBLE : GONE);
    }

    public void showDetails(){
        setViewVisibility(findViewById(R.id.tv_item_counter_count_2), true);
    }

    public void hideDetails(){
        setViewVisibility(findViewById(R.id.tv_item_counter_count_2), false);
    }

    public void showIndicator(){
        setViewVisibility(findViewById(R.id.iv_item_counter_count_indicator), true);
    }

    public void hideIndicator(){
        setViewVisibility(findViewById(R.id.iv_item_counter_count_indicator), false);
    }

    private Drawable getIndicator(double pCount, double pMean){
        Drawable drawable;
        if(pCount == pMean) {
            drawable = null;
        } else {
            drawable = pCount > pMean ? ContextCompat.getDrawable(getContext(), R.drawable.baseline_expand_less_white_48)
                    : ContextCompat.getDrawable(getContext(), R.drawable.baseline_expand_more_white_48);
            drawable.setTint(pCount > pMean ? getContext().getColor(R.color.green500) : getContext().getColor(R.color.red500));
        }
        return drawable;
    }

    public void setUpView(double pCount, double pMean){
        setCount(pCount);
        setMean(pMean);

        ((ImageView) findViewById(R.id.iv_item_counter)).setImageDrawable(mIcon);

        String countStr = mCount % 1 == 0 ? String.valueOf(((int) mCount)) : StringUtils.formatDouble(mCount);
        ((TextView) findViewById(R.id.tv_item_counter_count_1)).setText(countStr);

        ((TextView) findViewById(R.id.tv_item_counter_count_2)).setText(
                String.format(Locale.FRANCE, "(%s)", StringUtils.formatDouble(mMean)));

        Drawable img = getIndicator(mCount, mMean);
        if (img == null) {
            findViewById(R.id.iv_item_counter_count_indicator).setVisibility(GONE);
        } else {
            ((ImageView) findViewById(R.id.iv_item_counter_count_indicator)).setImageDrawable(img);
        }
    }

    public void setUpView(String pTitle){
        ((ImageView) findViewById(R.id.iv_item_counter)).setImageDrawable(mIcon);
        ((TextView) findViewById(R.id.tv_item_counter_count_1)).setText(pTitle);
        hideDetails();
        hideIndicator();
    }
}

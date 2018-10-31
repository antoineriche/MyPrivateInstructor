package com.antoineriche.privateinstructor.beans;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.utils.DateUtils;
import com.google.firebase.database.Exclude;

import java.util.Date;

public interface EventItem {

    int FORESEEN = 0;
    int VALIDATED = 1;
    int WAITING_FOR_VALIDATION = 2;
    int CANCELED = 3;

    int DURATION_1H = 60;
    int DURATION_1H30 = 90;
    int DURATION_2H = 120;

    long getDate();
    void setDate(long pDateInMillis);
    int getState();
    void setState(int pState);
    int getDuration();
    void setDuration(int pDuration);

    @Exclude
    default String getFriendlyStatus(Context context){
        return context.getResources().getStringArray(R.array.event_states)[getState()];
    }

    @Exclude
    default Drawable getStateIcon(Context context){
        Drawable drawable;
        switch(getState()){
            case FORESEEN:
                drawable = context.getDrawable(R.drawable.baseline_schedule_white_48);
                drawable.setTint(context.getColor(R.color.themPrimaryDark));
                break;
            case WAITING_FOR_VALIDATION:
                drawable = context.getDrawable(R.drawable.baseline_hourglass_empty_white_48);
                drawable.setTint(context.getColor(R.color.themPrimaryDark));
                break;
            case VALIDATED:
                drawable = context.getDrawable(R.drawable.baseline_check_circle_white_48);
                drawable.setTint(context.getColor(R.color.green500));
                break;
            case CANCELED:
                drawable = context.getDrawable(R.drawable.baseline_highlight_off_white_48);
                drawable.setTint(context.getColor(R.color.red500));
                break;
            default:
                drawable = context.getDrawable(R.drawable.baseline_hourglass_empty_white_48);
                drawable.setTint(context.getColor(R.color.themPrimaryDark));
                break;
        }
        return drawable;
    }

    default boolean isTheGoodDay(Date pDate){
        return isBetweenDates(DateUtils.getFirstSecondOfTheDay(pDate), DateUtils.getLastSecondOfTheDay(pDate));
    }

    default boolean isBetweenDates(Date pStartDate, Date pEndDate){
        Date courseDate = new Date(this.getDate());
        return courseDate.after(pStartDate) && courseDate.before(pEndDate);
    }

    @Exclude
    default String getFriendlyDate(){
        return DateUtils.getFriendlyDate(getDate());
    }

    @Exclude
    default String getShortDate(){
        return DateUtils.getShortDate(getDate());
    }
}

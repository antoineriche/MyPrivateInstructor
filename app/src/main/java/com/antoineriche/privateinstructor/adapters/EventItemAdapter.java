package com.antoineriche.privateinstructor.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.item.AbstractFragmentList;
import com.antoineriche.privateinstructor.activities.item.AbstractItemActivity;
import com.antoineriche.privateinstructor.activities.item.course.CourseActivity;
import com.antoineriche.privateinstructor.activities.item.devoir.DevoirActivity;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.beans.Devoir;
import com.antoineriche.privateinstructor.beans.EventItem;

import java.util.ArrayList;
import java.util.List;

public class EventItemAdapter extends RecyclerView.Adapter<EventItemAdapter.EventItemHolder> {

    private Context mContext;
    private List<EventItem> mEventItems;
    private AbstractFragmentList.FragmentListListener mListener;
    private Class mClass;

    public EventItemAdapter(Context pContext, List<EventItem> mItems, Class pClass, AbstractFragmentList.FragmentListListener pListener) {
        this.mEventItems = new ArrayList<>(mItems);
        this.mContext = pContext;
        this.mListener = pListener;
        this.mClass = pClass;
    }

    @NonNull
    @Override
    public EventItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_rv_event_item, viewGroup, false);
        return new EventItemHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventItemHolder eventItemHolder, int position) {
        EventItem event = mEventItems.get(position);
        eventItemHolder.tvDate.setText(event.getShortDate());
        eventItemHolder.ivEventState.setImageDrawable(event.getStateIcon(mContext));

        Bundle args = new Bundle();
        Class activityClass = null;

        if(mClass.equals(Course.class)){
            Course course = (Course) event;
            eventItemHolder.tvDate2.setText(course.getFriendlyTimeSlot());
            eventItemHolder.tvEventLabel.setText(course.getPupil().getFullName());
            eventItemHolder.tvEventDetails.setText(course.getChapter());
            eventItemHolder.tvEventDetails.setVisibility(!TextUtils.isEmpty(course.getChapter()) ? View.VISIBLE : View.GONE);
            args.putLong(AbstractItemActivity.ARG_ITEM_ID, course.getId());
            activityClass = CourseActivity.class;
        } else if(mClass.equals(Devoir.class)){
            Devoir devoir = (Devoir) event;
            eventItemHolder.tvDate2.setText(devoir.getFriendlyDuration());
            eventItemHolder.tvEventLabel.setText(devoir.getPupil().getFullName());
            StringBuilder strB = new StringBuilder(devoir.getFriendlyType(mContext));
            if(!TextUtils.isEmpty(devoir.getChapter())){ strB.append(" | ").append(devoir.getChapter()); }
            eventItemHolder.tvEventDetails.setText(strB.toString());
            args.putLong(AbstractItemActivity.ARG_ITEM_ID, devoir.getId());
            activityClass = DevoirActivity.class;
        }

        Class finalActivityClass = activityClass;
        eventItemHolder.cvEventCell.setOnClickListener(view -> {
            if(mListener != null) { mListener.goToDetailsActivity(finalActivityClass, args); }
        });
    }

    @Override
    public int getItemCount() {
        if(mClass.equals(Course.class) || mClass.equals(Devoir.class)){
            return mEventItems.size();
        } else {
            return 0;
        }
    }

    public void refreshList(List list){
        this.mEventItems.clear();
        this.mEventItems.addAll(list);
        notifyDataSetChanged();
    }

    class EventItemHolder extends RecyclerView.ViewHolder {
        CardView cvEventCell;
        TextView tvDate, tvDate2, tvEventLabel, tvEventDetails;
        ImageView ivEventState;

        EventItemHolder(View itemView) {
            super(itemView);
            cvEventCell = itemView.findViewById(R.id.cv_event_cell);
            tvDate = itemView.findViewById(R.id.tv_event_date);
            tvDate2 = itemView.findViewById(R.id.tv_event_date_2);
            tvEventLabel = itemView.findViewById(R.id.tv_event_label);
            tvEventDetails = itemView.findViewById(R.id.tv_event_details);
            ivEventState = itemView.findViewById(R.id.iv_event_state);
        }
    }
}

package com.antoineriche.privateinstructor.activities.item.devoir;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.item.AbstractFragmentList;
import com.antoineriche.privateinstructor.activities.item.AbstractItemActivity;
import com.antoineriche.privateinstructor.beans.Devoir;
import com.antoineriche.privateinstructor.database.DevoirTable;
import com.antoineriche.privateinstructor.utils.DateUtils;

import java.util.Comparator;
import java.util.List;

public class DevoirListFragment extends AbstractFragmentList {

    public DevoirListFragment() {
    }

    public static DevoirListFragment newInstance() {
        return new DevoirListFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.action_order) {
            getItems().sort(Comparator.comparing(Devoir::getDate));
            refreshListItems();
        }

        return true;
    }

    @Override
    protected Class<? extends Activity> getAddingActivity() {
        return DevoirActivity.class;
    }

    @Override
    protected List<Devoir> getItemsFromDB(SQLiteDatabase database) {
        return DevoirTable.getAllDevoirs(database);
    }

    @Override
    protected String title() {
        return "Mes devoirs";
    }

    @Override
    protected RecyclerView.Adapter getAdapter(List pListItems, FragmentListListener pListener) {
        return new RecyclerViewCourseAdapter(pListItems, pListener);
    }

    @Override
    public List<Devoir> getItems() {
        return ((List<Devoir>) super.getItems());
    }

    class RecyclerViewCourseAdapter extends RecyclerView.Adapter<RecyclerViewCourseAdapter.CourseViewHolder> {

        private List<Devoir> mDevoirs;
        private FragmentListListener mListener;

        RecyclerViewCourseAdapter(List<Devoir> pDevoirs, FragmentListListener mListener) {
            this.mDevoirs = pDevoirs;
            this.mListener = mListener;
        }

        @NonNull
        @Override
        public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_rv_courses, parent, false);
            return new CourseViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull CourseViewHolder courseHolder, int position) {
            final Devoir devoir = mDevoirs.get(position);

            courseHolder.tvDate.setText(DateUtils.getShortDate(devoir.getDate()));
            courseHolder.tvTime.setText(devoir.getFriendlyDuration());

            courseHolder.tvChapter.setText(devoir.getPupil().getFullName());
            courseHolder.tvComment.setText(devoir.getChapter());

            StringBuilder strB = new StringBuilder();
            strB.append(devoir.getFriendlyType(getContext()));

            if(!TextUtils.isEmpty(devoir.getChapter())){
                strB.append(" | ").append(devoir.getChapter());
            }

            courseHolder.tvComment.setText(strB);
            courseHolder.ivICourseState.setImageDrawable(devoir.getStateIcon(getContext()));

            courseHolder.cvCell.setOnClickListener(view -> {
                Bundle args = new Bundle();
                args.putLong(AbstractItemActivity.ARG_ITEM_ID, devoir.getId());
                if(mListener != null) {
                    mListener.goToDetailsActivity(DevoirActivity.class, args);
                }
            });
        }

        @Override
        public int getItemCount() {
            return this.mDevoirs.size();
        }

        class CourseViewHolder extends RecyclerView.ViewHolder {
            CardView cvCell;
            TextView tvDate, tvTime, tvChapter, tvComment;
            ImageView ivICourseState;

            CourseViewHolder(View itemView) {
                super(itemView);
                cvCell = itemView.findViewById(R.id.cv_course_cell);
                tvDate = itemView.findViewById(R.id.tv_course_date);
                tvTime = itemView.findViewById(R.id.tv_course_time);
                tvChapter = itemView.findViewById(R.id.tv_course_chapter);
                tvComment = itemView.findViewById(R.id.tv_course_comment);
                ivICourseState = itemView.findViewById(R.id.iv_course_state);
            }
        }
    }
}

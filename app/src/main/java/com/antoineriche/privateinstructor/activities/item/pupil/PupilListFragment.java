package com.antoineriche.privateinstructor.activities.item.pupil;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.item.AbstractFragmentList;
import com.antoineriche.privateinstructor.activities.item.AbstractItemActivity;
import com.antoineriche.privateinstructor.beans.DatabaseItem;
import com.antoineriche.privateinstructor.beans.Devoir;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.antoineriche.privateinstructor.database.PupilTable;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PupilListFragment extends AbstractFragmentList {

    public PupilListFragment() {
    }

    public static PupilListFragment newInstance() {
        return new PupilListFragment();
    }

    @Override
    protected Class<? extends Activity> getAddingActivity() {
        return PupilActivity.class;
    }

    @Override
    protected List<DatabaseItem> order(List<DatabaseItem> pItemsToOrder) {
        pItemsToOrder.sort(Comparator.comparing(DatabaseItem::getId));
        return pItemsToOrder;
    }

    @Override
    protected List<DatabaseItem> getItemsFromDB(SQLiteDatabase database) {
        return new ArrayList<>(PupilTable.getAllPupils(database));
    }

    @Override
    protected RecyclerView.Adapter initAdapter(List pListItems, FragmentListListener pListener) {
        return new RecyclerViewPupilAdapter(pListItems, pListener);
    }


    public class RecyclerViewPupilAdapter extends RecyclerView.Adapter<RecyclerViewPupilAdapter.PupilViewHolder> {

        private List<Pupil> mPupils;
        private FragmentListListener mListener;

        RecyclerViewPupilAdapter(List<Pupil> mPupils, FragmentListListener mListener) {
            this.mPupils = mPupils;
            this.mListener = mListener;
        }

        @NonNull
        @Override
        public PupilViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_rv_pupils, parent, false);
            return new PupilViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull PupilViewHolder pupilHolder, int position) {
            final Pupil pupil = mPupils.get(position);

            pupilHolder.tvPupilName.setText(pupil.getFullName());
            pupilHolder.tvPupilClass.setText(pupil.getFriendlyClassLevel(getContext()));

            if (TextUtils.isEmpty(pupil.getImgPath()) || !new File(pupil.getImgPath()).exists()) {
                Drawable pImg = pupil.getGender() == Pupil.GENDER_MALE ?
                        ContextCompat.getDrawable(getContext(), R.drawable.man) :
                        ContextCompat.getDrawable(getContext(), R.drawable.woman);

                pupilHolder.ivPupilPix.setImageDrawable(pImg);
            } else {
                pupilHolder.ivPupilPix.setImageBitmap(BitmapFactory.decodeFile(pupil.getImgPath()));
            }

            pupilHolder.tvCall.setOnClickListener(view -> Toast.makeText(getContext(), "Appeler " + pupil.getFullName(), Toast.LENGTH_SHORT).show());
            pupilHolder.tvGoTo.setOnClickListener(view -> Toast.makeText(getContext(), "Aller chez " + pupil.getFullName(), Toast.LENGTH_SHORT).show());
            pupilHolder.cvCell.setOnClickListener(view -> {
                Bundle args = new Bundle();
                args.putLong(AbstractItemActivity.ARG_ITEM_ID, pupil.getId());
                mListener.goToDetailsActivity(PupilActivity.class, args);
            });
        }

        @Override
        public int getItemCount() {
            return this.mPupils.size();
        }

        class PupilViewHolder extends RecyclerView.ViewHolder {
            CardView cvCell;
            TextView tvPupilName, tvPupilClass, tvCall, tvGoTo;
            ImageView ivPupilPix;

            PupilViewHolder(View itemView) {
                super(itemView);
                cvCell = itemView.findViewById(R.id.cv_pupil_cell);
                tvPupilName = itemView.findViewById(R.id.tv_pupil_name);
                tvPupilClass = itemView.findViewById(R.id.tv_pupil_class_level);
                ivPupilPix = itemView.findViewById(R.id.iv_pupil_pix);
                tvCall = itemView.findViewById(R.id.tv_action_call);
                tvGoTo = itemView.findViewById(R.id.tv_action_go_to);
            }
        }
    }
}

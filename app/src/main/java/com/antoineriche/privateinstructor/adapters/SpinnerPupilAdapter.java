package com.antoineriche.privateinstructor.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.beans.Pupil;

import java.io.File;
import java.util.List;

public class SpinnerPupilAdapter extends BaseAdapter {

    List<Pupil> pupils;
    Context context;

    public SpinnerPupilAdapter(Context pContext, List<Pupil> pPupils) {
        this.pupils = pPupils;
        this.context = pContext;
    }

    @Override
    public int getCount() {
        return pupils.size();
    }

    @Override
    public Pupil getItem(int position) {
        return pupils.get(position);
    }

    @Override
    public long getItemId(int position) {
        return pupils.get(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        Pupil pupil = pupils.get(position);

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_spinner_pupil, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tvPupilName.setText(pupils.get(position).getFullName());

        if(TextUtils.isEmpty(pupil.getImgPath()) || !new File(pupil.getImgPath()).exists()) {
            Drawable pImg = pupil.getGender() == Pupil.GENDER_MALE ?
                    ContextCompat.getDrawable(context, R.drawable.man) :
                    ContextCompat.getDrawable(context, R.drawable.woman);

            holder.ivPupilPix.setImageDrawable(pImg);
        } else {
            holder.ivPupilPix.setImageBitmap(BitmapFactory.decodeFile(pupil.getImgPath()));
        }

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return pupils.isEmpty();
    }

    public int getIndexSelection(long pPupilId){
        Pupil a = pupils.stream().filter(p -> p.getId() == pPupilId).findFirst().orElse(new Pupil());
        return pupils.indexOf(a);
    }

    public int getIndexSelection(String pPupilUuid){
        Pupil a = pupils.stream().filter(p -> p.getUuid().equals(pPupilUuid)).findFirst().orElse(new Pupil());
        return pupils.indexOf(a);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvPupilName;
        ImageView ivPupilPix;

        ViewHolder(View itemView) {
            super(itemView);
            tvPupilName = itemView.findViewById(R.id.tv_pupil_name);
            ivPupilPix = itemView.findViewById(R.id.iv_pupil_pix);
        }
    }
}

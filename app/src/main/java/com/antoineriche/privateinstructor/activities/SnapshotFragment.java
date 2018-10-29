package com.antoineriche.privateinstructor.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.beans.Snapshot;
import com.antoineriche.privateinstructor.services.FirebaseIntentService;
import com.antoineriche.privateinstructor.services.SnapshotService;
import com.antoineriche.privateinstructor.utils.DateUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SnapshotFragment extends Fragment  {

    String TAG = SnapshotFragment.class.getSimpleName();

    public static class MyReceiver extends BroadcastReceiver {
        public static final String SNAPSHOT_FRAGMENT = SnapshotService.class.getName();
        private final WeakReference<SnapshotFragment> mFragment;

        MyReceiver(SnapshotFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra(FirebaseIntentService.FIB_GET_LAST_SNAPSHOT_DATE)) {
                mFragment.get().fillSnapshotDates(intent.getLongExtra(FirebaseIntentService.FIB_GET_LAST_SNAPSHOT_DATE, 0));
            } else if(intent.hasExtra(FirebaseIntentService.FIB_GET_SNAPSHOTS)) {
                mFragment.get().setSnapshots((List<Snapshot>) intent.getSerializableExtra(FirebaseIntentService.FIB_GET_SNAPSHOTS));
            } else if(intent.hasExtra(FirebaseIntentService.FIB_REPLACE_DATABASE)) {
                if(intent.getBooleanExtra(FirebaseIntentService.FIB_REPLACE_DATABASE, false)){
                    mFragment.get().getLastSnapshotDate();
                    mFragment.get().getSnapshots();
                } else {
                    Log.e(mFragment.get().TAG, "Error while updating snapshots");
                }
            }
        }
    }

    private MyReceiver mReceiver;
    private List<Snapshot> mSnapshots;

    public SnapshotFragment() {
    }

    public static SnapshotFragment newInstance() {
        return new SnapshotFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReceiver = new MyReceiver(this);
        mSnapshots = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(MyReceiver.SNAPSHOT_FRAGMENT);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_snapshot, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("Snapshots");

        getRecyclerView().setLayoutManager(new LinearLayoutManager(getContext()));
        setUpRecyclerView();

        getLastSnapshotDate();
        getSnapshots();
    }

    private void setUpRecyclerView(){
        getRecyclerView().setAdapter(getAdapter(mSnapshots));
    }
    private void refreshList(List<Snapshot> pSnapshots){
        this.mSnapshots.clear();
        this.mSnapshots.addAll(pSnapshots);
        refreshAdapterItems();
    }

    private RecyclerView getRecyclerView(){
        return getView().findViewById(R.id.rv_snapshots);
    }
    protected void refreshAdapterItems(){
        getRecyclerView().getAdapter().notifyDataSetChanged();
    }
    protected SnapshotAdapter getAdapter(List pListItems){
        return new SnapshotAdapter(pListItems);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((TextView) view.findViewById(R.id.tv_course_snapshot_frequency)).setText(String.valueOf(7));
    }

    private void getLastSnapshotDate(){
        getView().findViewById(R.id.pb_getting_snapshot_dates).setVisibility(View.VISIBLE);
        Intent intent = new Intent(getActivity(), FirebaseIntentService.class);
        intent.putExtra(FirebaseIntentService.FIB_TASKS, new String[]{FirebaseIntentService.FIB_GET_LAST_SNAPSHOT_DATE});
        getActivity().startService(intent);
    }

    private void getSnapshots(){
        getView().findViewById(R.id.pb_getting_snapshot).setVisibility(View.VISIBLE);
        Intent intent = new Intent(getActivity(), FirebaseIntentService.class);
        intent.putExtra(FirebaseIntentService.FIB_TASKS, new String[]{FirebaseIntentService.FIB_GET_SNAPSHOTS});
        getActivity().startService(intent);
    }

    private void fillSnapshotDates(long pLastSnapshotDate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(pLastSnapshotDate);
        calendar.add(Calendar.DAY_OF_YEAR, 7);

        ((TextView) getView().findViewById(R.id.tv_course_last_snapshot)).setText(DateUtils.getShortDate(pLastSnapshotDate));
        ((TextView) getView().findViewById(R.id.tv_course_next_snapshot)).setText(DateUtils.getShortDate(calendar.getTime()));
        getView().findViewById(R.id.pb_getting_snapshot_dates).setVisibility(View.GONE);
    }

    private void setSnapshots(List<Snapshot> pSnapshots){
        Log.e(TAG, pSnapshots.size() + " snapshots found");
        refreshList(pSnapshots);
        getView().findViewById(R.id.pb_getting_snapshot).setVisibility(View.GONE);
    }

    private void openChangeDatabaseDialog(Snapshot pSnapshot){
        Drawable drawable = getContext().getDrawable(R.drawable.baseline_warning_white_48);
        drawable.setTint(getContext().getColor(R.color.unthem500));
        new AlertDialog.Builder(getContext())
                .setIcon(drawable)
                .setTitle("Changement de base de données")
                .setMessage(getString(R.string.dialog_update_database))
                .setNegativeButton("Annuler", null)
                .setNeutralButton("Ne pas conserver une copie", (dialog, which) -> updateDatabase(pSnapshot, false))
                .setPositiveButton("Conserver une copie de la base", (dialog, which) -> updateDatabase(pSnapshot, true))
                .show();
    }

    private void updateDatabase(Snapshot pSnapshot, boolean createCopy){
        Intent intent = new Intent(getActivity(), FirebaseIntentService.class);
        intent.putExtra(FirebaseIntentService.FIB_TASKS, new String[]{FirebaseIntentService.FIB_REPLACE_DATABASE});
        intent.putExtra(FirebaseIntentService.FIB_NEW_DATABASE, pSnapshot.getName());
        intent.putExtra(FirebaseIntentService.FIB_CREATE_SNAPSHOT, createCopy);
        getActivity().startService(intent);
    }



    class SnapshotAdapter extends RecyclerView.Adapter<SnapshotAdapter.SnapshotViewHolder> {

        private List<Snapshot> mSnapshots;

        SnapshotAdapter(List<Snapshot> pSnapshots) {
            this.mSnapshots = pSnapshots;
        }

        @NonNull
        @Override
        public SnapshotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_rv_snapshots, parent, false);
            return new SnapshotViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SnapshotViewHolder snapshotHolder, int position) {
            final Snapshot snapshot = mSnapshots.get(position);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(snapshot.getDate());

            snapshotHolder.tvDate.setText(DateUtils.getShortDate(snapshot.getDate()));
            snapshotHolder.tvTime.setText(DateUtils.getFriendlyHour(snapshot.getDate()));

            snapshotHolder.tvName.setText(snapshot.getName());
            snapshotHolder.tvCoursesCount.setText(String.valueOf(snapshot.getCourses().size()));
            snapshotHolder.tvPupilsCount.setText(String.valueOf(snapshot.getPupils().size()));
            snapshotHolder.tvLocationsCount.setText(String.valueOf(snapshot.getLocations().size()));

            snapshotHolder.ivSnapshotState.setVisibility(View.GONE);
            snapshotHolder.cvCell.setOnClickListener(view -> openChangeDatabaseDialog(snapshot));
        }

        @Override
        public int getItemCount() {
            return this.mSnapshots.size();
        }

        class SnapshotViewHolder extends RecyclerView.ViewHolder {
            CardView cvCell;
            TextView tvDate, tvTime, tvName, tvPupilsCount, tvCoursesCount, tvLocationsCount;
            ImageView ivSnapshotState;

            SnapshotViewHolder(View itemView) {
                super(itemView);
                cvCell = itemView.findViewById(R.id.cv_snapshot_cell);
                tvDate = itemView.findViewById(R.id.tv_snapshot_date);
                tvTime = itemView.findViewById(R.id.tv_snapshot_time);
                tvName = itemView.findViewById(R.id.tv_snapshot_name);
                tvCoursesCount = itemView.findViewById(R.id.tv_snapshot_courses_count);
                tvPupilsCount = itemView.findViewById(R.id.tv_snapshot_pupils_count);
                tvLocationsCount = itemView.findViewById(R.id.tv_snapshot_locations_count);
                ivSnapshotState = itemView.findViewById(R.id.iv_snapshot_state);
            }
        }

    }

}

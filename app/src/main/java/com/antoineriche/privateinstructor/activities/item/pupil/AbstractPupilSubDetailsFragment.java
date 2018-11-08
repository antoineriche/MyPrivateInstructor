package com.antoineriche.privateinstructor.activities.item.pupil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.beans.Devoir;
import com.antoineriche.privateinstructor.beans.EventItem;
import com.antoineriche.privateinstructor.beans.Location;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.antoineriche.privateinstructor.customviews.GraphicView;
import com.antoineriche.privateinstructor.database.LocationTable;
import com.antoineriche.privateinstructor.database.MyDatabase;
import com.antoineriche.privateinstructor.utils.PreferencesUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public abstract class AbstractPupilSubDetailsFragment extends Fragment {

    private Pupil mPupil;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPupil = (Pupil) getArguments().getSerializable(PupilDetailsFragment.PUPIL);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layout(), container, false);
        fillView(view, mPupil);
        return view;
    }

    protected abstract int layout();

    protected abstract void fillView(View pView, Pupil pPupil);

    protected Pupil getPupil() {
        return mPupil;
    }

    public static class PupilGeneralDetailsFragment extends AbstractPupilSubDetailsFragment {


        public PupilGeneralDetailsFragment() {
        }

        public static PupilGeneralDetailsFragment newInstance(Pupil pPupil) {
            PupilGeneralDetailsFragment fragment = new PupilGeneralDetailsFragment();
            Bundle args = new Bundle();
            args.putSerializable(PupilDetailsFragment.PUPIL, pPupil);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        protected void fillView(View pView, Pupil pPupil) {

            ((TextView) pView.findViewById(R.id.tv_pupil_class_level)).setText(pPupil.getFriendlyClassLevel(getContext()));

            ((TextView) pView.findViewById(R.id.tv_pupil_hourly_price)).setText(
                    String.format(Locale.FRANCE, "%s €", pPupil.getFriendlyHourlyPrice()));
            ((TextView) pView.findViewById(R.id.tv_pupil_payment_type)).setText(pPupil.getFriendlyPaymentType(getContext()));
            ((TextView) pView.findViewById(R.id.tv_pupil_course_frequency)).setText(pPupil.getFriendlyFrequency(getContext()));

            ((TextView) pView.findViewById(R.id.tv_pupil_phone)).setText(pPupil.getPhone());
            ((TextView) pView.findViewById(R.id.tv_pupil_parent_phone)).setText(pPupil.getParentPhone());
        }

        @Override
        protected int layout() {
            return R.layout.pupil_details_general;
        }
    }

    public static class PupilCoursesDetailsFragment extends AbstractPupilSubDetailsFragment {

        public PupilCoursesDetailsFragment() {
        }

        public static PupilCoursesDetailsFragment newInstance(Pupil pPupil) {
            PupilCoursesDetailsFragment fragment = new PupilCoursesDetailsFragment();
            Bundle args = new Bundle();
            args.putSerializable(PupilDetailsFragment.PUPIL, pPupil);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        protected int layout() {
            return R.layout.pupil_details_courses;
        }

        @Override
        protected void fillView(View pView, Pupil pPupil) {
            RecyclerView rv = pView.findViewById(R.id.rv_pupil_courses);
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            rv.setAdapter(new PupilEventItemAdapter(getActivity(), new ArrayList<>(pPupil.getCourses())));

            pView.findViewById(R.id.tv_no_course).setVisibility(pPupil.getCourses().isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    public static class PupilDevoirsDetailsFragment extends AbstractPupilSubDetailsFragment {

        public PupilDevoirsDetailsFragment() {
        }

        public static PupilDevoirsDetailsFragment newInstance(Pupil pPupil) {
            PupilDevoirsDetailsFragment fragment = new PupilDevoirsDetailsFragment();
            Bundle args = new Bundle();
            args.putSerializable(PupilDetailsFragment.PUPIL, pPupil);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        protected int layout() {
            return R.layout.pupil_details_devoirs;
        }

        @Override
        protected void fillView(View pView, Pupil pPupil) {
            RecyclerView rv = pView.findViewById(R.id.rv_pupil_devoirs);
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            rv.setAdapter(new PupilEventItemAdapter(getActivity(), new ArrayList<>(pPupil.getDevoirs())));

            pView.findViewById(R.id.tv_no_devoir).setVisibility(pPupil.getDevoirs().isEmpty() ? View.VISIBLE : View.GONE);

            GraphicView graphic =  pView.findViewById(R.id.graphicView);
            fillGraphicView(graphic, pPupil.getDevoirs());
            graphic.setTitle("Résultats");

            pView.findViewById(R.id.btn_pupil_devoir_list).setOnClickListener(v -> {
                pView.findViewById(R.id.rl_pupil_list_devoirs).setVisibility(View.VISIBLE);
                pView.findViewById(R.id.cv_pupil_evolution_devoirs).setVisibility(View.GONE);
            });

            pView.findViewById(R.id.btn_pupil_devoir_evolution).setOnClickListener(v -> {
                if(pView.findViewById(R.id.cv_pupil_evolution_devoirs).getVisibility() != View.VISIBLE) {
                    pView.findViewById(R.id.cv_pupil_evolution_devoirs).setVisibility(View.VISIBLE);
                    pView.findViewById(R.id.rl_pupil_list_devoirs).setVisibility(View.GONE);
                }
            });
        }

        private void fillGraphicView(GraphicView graphView, List<Devoir> pDevoirs){
            pDevoirs.sort(Comparator.comparing(Devoir::getDate));
            Float[] marks = new Float[pDevoirs.size()];

            for(int i = 0 ; i < pDevoirs.size() ; i++){
                Devoir dev = pDevoirs.get(i);
                marks[i] = dev.getMaxMark() != 0 ?  (float) dev.getMark() * 20f / (float) dev.getMaxMark() : 0f;
            }

            graphView.setValues(marks, true);
        }
    }

    public static class PupilMapDetailsFragment extends AbstractPupilSubDetailsFragment
            implements OnMapReadyCallback {

//        private static final LatLng DEFAULT_HOME = new LatLng(44.836260, -0.602890);

        public PupilMapDetailsFragment() {
        }

        public static PupilMapDetailsFragment newInstance(Pupil pPupil) {
            PupilMapDetailsFragment fragment = new PupilMapDetailsFragment();
            Bundle args = new Bundle();
            args.putSerializable(PupilDetailsFragment.PUPIL, pPupil);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        @Override
        protected int layout() {
            return R.layout.pupil_details_map;
        }

        @Override
        protected void fillView(View pView, Pupil pPupil) {
            ((TextView) pView.findViewById(R.id.tv_pupil_address)).setText(pPupil.getLocation() != null ?
                    pPupil.getLocation().getAddress() : "unknown location");
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            initMap(googleMap);

            List<Marker> markers = new ArrayList<>();
            Pupil pupil = getPupil();

            if (pupil.getLocation() != null) {
                markers.add(newMarker(googleMap, pupil.getFullName(), pupil.getLocation().getLatLng()));
            }

            String locationUuid = PreferencesUtils.getStringPreferences(getContext(), getString(R.string.pref_user_location));
            if (!TextUtils.isEmpty(locationUuid)) {
                SQLiteDatabase sql = new MyDatabase(getContext(), null).getReadableDatabase();
                Location location = LocationTable.getLocationWithUuid(sql, locationUuid);
                markers.add(newMarker(googleMap, "User home", location.getLatLng()));
            }

            zoomMap(googleMap, markers);
        }

        protected void initMap(GoogleMap googleMap) {
            googleMap.getUiSettings().setCompassEnabled(false);
            googleMap.getUiSettings().setScrollGesturesEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setTiltGesturesEnabled(false);
            googleMap.getUiSettings().setZoomGesturesEnabled(false);
            googleMap.getUiSettings().setRotateGesturesEnabled(false);
        }

        private Marker newMarker(GoogleMap googleMap, String pLabel, LatLng pLatLng) {
            return googleMap.addMarker(new MarkerOptions().position(pLatLng).title(pLabel));
        }

        private void zoomMap(GoogleMap googleMap, List<Marker> markers) {
            CameraUpdate cameraUpdate = null;

            if (markers.size() > 1) {
                LatLngBounds.Builder b = new LatLngBounds.Builder();
                for (Marker m : markers) {
                    b.include(m.getPosition());
                }
                LatLngBounds bounds = b.build();

                cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 250);
            } else if (markers.size() == 1) {
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(markers.get(0).getPosition(), 15f);
            }

            if(cameraUpdate != null) {
                googleMap.animateCamera(cameraUpdate);
            }
        }

    }


    public static class PupilEventItemAdapter extends RecyclerView.Adapter<PupilEventItemAdapter.PupilEventItemHolder> {

        private Context mContext;
        private List<EventItem> mEventItems;

        public PupilEventItemAdapter(Context pContext, List<EventItem> mItems) {
            this.mEventItems = new ArrayList<>(mItems);
            this.mContext = pContext;
        }

        @NonNull
        @Override
        public PupilEventItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_rv_event_item, viewGroup, false);
            return new PupilEventItemHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull PupilEventItemHolder pupilEeventItemHolder, int position) {
            EventItem event = mEventItems.get(position);
            pupilEeventItemHolder.tvDate.setText(event.getShortDate());
            pupilEeventItemHolder.ivEventState.setImageDrawable(event.getStateIcon(mContext));
            if(event instanceof Course){
                Course course = (Course) event;
                pupilEeventItemHolder.tvDate2.setText(course.getFriendlyTimeSlot());
                pupilEeventItemHolder.tvEventLabel.setText(course.getChapter());
                pupilEeventItemHolder.tvEventDetails.setText(course.getComment());
                pupilEeventItemHolder.tvEventDetails.setVisibility(!TextUtils.isEmpty(course.getComment()) ? View.VISIBLE : View.GONE);
            } else if(event instanceof Devoir){
                Devoir devoir = (Devoir) event;
                pupilEeventItemHolder.tvDate2.setText(devoir.getFriendlyDuration());
                pupilEeventItemHolder.tvEventLabel.setText(devoir.getChapter());
                pupilEeventItemHolder.tvEventDetails.setText(devoir.getDetails(mContext));
            }
        }

        @Override
        public int getItemCount() {
            if (!this.mEventItems.isEmpty()) {
                EventItem item = this.mEventItems.get(0);
                return (item instanceof Course || item instanceof Devoir) ? this.mEventItems.size() : 0;
            } else {
                return 0;
            }
        }

        class PupilEventItemHolder extends RecyclerView.ViewHolder {
            CardView cvEventCell;
            TextView tvDate, tvDate2, tvEventLabel, tvEventDetails;
            ImageView ivEventState;

            PupilEventItemHolder(View itemView) {
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

} // 356 - 245

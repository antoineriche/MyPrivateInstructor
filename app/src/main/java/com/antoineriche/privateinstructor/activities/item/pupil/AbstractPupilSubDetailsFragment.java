package com.antoineriche.privateinstructor.activities.item.pupil;

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
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

            ((TextView) pView.findViewById(R.id.tv_pupil_last_name)).setText(pPupil.getLastname());
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
            rv.setAdapter(new CourseAdapter(pPupil.getCourses()));

            pView.findViewById(R.id.tv_no_course).setVisibility(pPupil.getCourses().isEmpty() ? View.VISIBLE : View.GONE);
        }

        public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

            private List<Course> mCourses;

            CourseAdapter(List<Course> mCourses) {
                this.mCourses = mCourses;
            }

            @NonNull
            @Override
            public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_rv_courses, parent, false);
                return new CourseViewHolder(v);
            }

            @Override
            public void onBindViewHolder(@NonNull CourseViewHolder courseHolder, int position) {
                final Course course = mCourses.get(position);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(course.getDate());

                courseHolder.tvTime.setText(course.getFriendlyTimeSlot());
                courseHolder.tvDate.setText(
                        new SimpleDateFormat("dd/MM/yy", Locale.FRANCE).format(calendar.getTimeInMillis()));

                courseHolder.tvChapter.setText(course.getChapter());
                courseHolder.tvComment.setText(course.getComment());
                courseHolder.ivICourseState.setImageDrawable(course.getStateIcon(getContext()));
            }

            @Override
            public int getItemCount() {
                return this.mCourses.size();
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

    public static class PupilMapDetailsFragment extends AbstractPupilSubDetailsFragment
            implements OnMapReadyCallback {

        private GoogleMap mGoogleMap;
        private static final LatLng DEFAULT_HOME = new LatLng(44.836260, -0.602890);

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
            Log.e("MapDetails", "onMapReady");
            initMap(googleMap);

            List<Marker> markers = new ArrayList<>();

            if (getPupil().getLocation() != null) {
                markers.add(newMarker(googleMap, getPupil().getFullName(), getPupil().getLocation().getLatLng()));
            }

            //FIXME
            if (true) {
                markers.add(newMarker(googleMap, "User home", DEFAULT_HOME));
            }

            zoomMap(googleMap, markers);


//            if(getPupil().getLocation() != null){
//                LatLng pupilHome = getPupil().getLocation().getLatLng();
//
//                markers.add(mGoogleMap.addMarker(new MarkerOptions().position(pupilHome).title(getPupil().getFullName())));
//


//                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(pupilHome));
//                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(16f));
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

            googleMap.animateCamera(cameraUpdate);
        }

    }

}

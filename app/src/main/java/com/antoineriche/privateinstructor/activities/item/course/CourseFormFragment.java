package com.antoineriche.privateinstructor.activities.item.course;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.item.AbstractFormItemFragment;
import com.antoineriche.privateinstructor.activities.item.AbstractItemActivity;
import com.antoineriche.privateinstructor.adapters.SpinnerPupilAdapter;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.database.PupilTable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CourseFormFragment extends AbstractFormItemFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Calendar mCalendar;

    public CourseFormFragment() {
    }

    public static CourseFormFragment newInstance(long pPupilId) {
        CourseFormFragment fragment = new CourseFormFragment();
        Bundle args = new Bundle();
        args.putLong(AbstractItemActivity.ARG_ITEM_ID, pPupilId);
        args.putBoolean(AbstractItemActivity.ARG_ITEM_EDITION, true);
        fragment.setArguments(args);
        return fragment;
    }

    public static CourseFormFragment newInstance() {
        return new CourseFormFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCalendar = Calendar.getInstance();
    }

    @Override
    protected Course extractItemFromView(View view) throws IllegalArgumentException {
        Course c;
        Pupil p = ((Pupil) ((Spinner) view.findViewById(R.id.spinner_pupils)).getSelectedItem());

        //FIXME
        if (mItemId != -1) {
            c = getItemFromDB(mListener.getDatabase(), mItemId);
            mCalendar.setTimeInMillis(c.getDate());
        } else {
            c = new Course();
        }

        if(getString(R.string.unknown_date).contentEquals(((TextView) view.findViewById(R.id.tv_course_date)).getText())){
            throw new IllegalArgumentException("Invalid date");
        } else if(getString(R.string.unknown_hour).contentEquals(((TextView) view.findViewById(R.id.tv_course_hour)).getText())){
            throw new IllegalArgumentException("Invalid hour");
        } else {
            c.setDate(mCalendar.getTimeInMillis());
            c.setState(new Date().after(new Date(c.getDate())) ? Course.WAITING_FOT_VALIDATION : Course.FORESEEN);
        }

        // MONEY
        try {
            c.setMoney(Double.parseDouble(((EditText) view.findViewById(R.id.et_course_money)).getText().toString()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid money");
        }

        // DURATION
        switch (((RadioGroup) view.findViewById(R.id.rg_course_duration)).getCheckedRadioButtonId()) {
            case R.id.rb_course_duration_60:
                c.setDuration(Course.DURATION_1H);
                break;
            case R.id.rb_course_duration_90:
                c.setDuration(Course.DURATION_1H30);
                break;
            case R.id.rb_course_duration_120:
                c.setDuration(Course.DURATION_2H);
                break;
            default:
                c.setDuration(Course.DURATION_1H);
                break;
        }

        // CHAPTER
        c.setChapter(((EditText) view.findViewById(R.id.et_course_chapter)).getText().toString());

        // COMMENT
        c.setComment(((EditText) view.findViewById(R.id.et_course_comment)).getText().toString());

        c.setPupilID(p.getId());
        return c;
    }

    @Override
    protected void fillViewWithItem(View view, Object item) {
        Course course = (Course) item;
        ((EditText) view.findViewById(R.id.et_course_money)).setText(
                String.format(Locale.FRANCE, "%.2f", (course.getMoney())).replace(",", "."));

        ((TextView) view.findViewById(R.id.tv_course_hour)).setText(new SimpleDateFormat("HH:mm", Locale.FRANCE)
                .format(course.getDate()));
        ((TextView) view.findViewById(R.id.tv_course_date)).setText(new SimpleDateFormat("EEEE dd MM yyyy", Locale.FRANCE)
                .format(course.getDate()));

        ((EditText) view.findViewById(R.id.et_course_chapter)).setText(course.getChapter());
        ((EditText) view.findViewById(R.id.et_course_comment)).setText(course.getComment());

        Spinner spinner = view.findViewById(R.id.spinner_pupils);
        int index = ((SpinnerPupilAdapter) spinner.getAdapter()).getIndexSelection(course.getPupilID());
        spinner.setSelection(index);

        switch (course.getDuration()) {
            case Course.DURATION_1H:
                ((RadioButton) view.findViewById(R.id.rb_course_duration_60)).setChecked(true);
                break;
            case Course.DURATION_1H30:
                ((RadioButton) view.findViewById(R.id.rb_course_duration_90)).setChecked(true);
                break;
            case Course.DURATION_2H:
                ((RadioButton) view.findViewById(R.id.rb_course_duration_120)).setChecked(true);
                break;
            default:
                ((RadioButton) view.findViewById(R.id.rb_course_duration_60)).setChecked(true);
                break;
        }
    }

    @Override
    protected void initView(View view) {
        List<Pupil> pupils = PupilTable.getAllPupils(mListener.getDatabase());
        Spinner spinner = view.findViewById(R.id.spinner_pupils);
        spinner.setAdapter(new SpinnerPupilAdapter(getActivity(), pupils));

        view.findViewById(R.id.cv_course_pick_date).setOnClickListener(v -> pickDate());
        view.findViewById(R.id.cv_course_pick_hour).setOnClickListener(v -> pickHour());
    }

    @Override
    protected void cleanView(View view) {
        ((EditText) view.findViewById(R.id.et_course_money)).setText(null);
        ((EditText) view.findViewById(R.id.et_course_chapter)).setText(null);
        ((EditText) view.findViewById(R.id.et_course_comment)).setText(null);
        ((TextView) view.findViewById(R.id.tv_course_date)).setText(getString(R.string.unknown_date));
        ((TextView) view.findViewById(R.id.tv_course_hour)).setText(getString(R.string.unknown_hour));
        ((RadioButton) view.findViewById(R.id.rb_course_duration_60)).setChecked(true);
    }


    @Override
    protected Course getItemFromDB(SQLiteDatabase database, long mItemId) {
        return CourseTable.getCourseWithId(database, mItemId);
    }

    @Override
    protected int layout() {
        return R.layout.course_form;
    }

    public void pickDate(){
        new DatePickerDialog(getActivity(), this,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void pickHour(){
        new TimePickerDialog(getActivity(), this,
                mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE), true).show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, monthOfYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        ((TextView)getView().findViewById(R.id.tv_course_date)).setText(new SimpleDateFormat("dd MM yyyy", Locale.FRANCE)
                .format(mCalendar.getTime()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalendar.set(Calendar.MINUTE, minute);
        ((TextView)getView().findViewById(R.id.tv_course_hour)).setText(new SimpleDateFormat("HH:mm", Locale.FRANCE)
                .format(mCalendar.getTime()));
    }
}

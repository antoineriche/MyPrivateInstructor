package com.antoineriche.privateinstructor.activities.item.devoir;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
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
import com.antoineriche.privateinstructor.beans.Devoir;
import com.antoineriche.privateinstructor.beans.EventItem;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.antoineriche.privateinstructor.database.DevoirTable;
import com.antoineriche.privateinstructor.database.PupilTable;
import com.antoineriche.privateinstructor.utils.DateUtils;
import com.antoineriche.privateinstructor.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DevoirFormFragment extends AbstractFormItemFragment implements
        DatePickerDialog.OnDateSetListener {

    private Calendar mCalendar;

    public DevoirFormFragment() {
    }

    public static DevoirFormFragment newInstance(long pPupilId) {
        DevoirFormFragment fragment = new DevoirFormFragment();
        Bundle args = new Bundle();
        args.putLong(AbstractItemActivity.ARG_ITEM_ID, pPupilId);
        args.putBoolean(AbstractItemActivity.ARG_ITEM_EDITION, true);
        fragment.setArguments(args);
        return fragment;
    }

    public static DevoirFormFragment newInstance() {
        return new DevoirFormFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCalendar = Calendar.getInstance();
    }

    @Override
    protected Devoir extractItemFromView(View view) throws IllegalArgumentException {
        Devoir d;
        Pupil p = ((Pupil) ((Spinner) view.findViewById(R.id.spinner_pupils)).getSelectedItem());

        //FIXME
        if (mItemId != -1) {
            d = getItemFromDB(mListener.getDatabase(), mItemId);
        } else {
            d = new Devoir();
        }

        if(getString(R.string.unknown_date).contentEquals(((TextView) view.findViewById(R.id.tv_devoir_date)).getText())){
            throw new IllegalArgumentException("Invalid date");
        } else {
            d.setDate(mCalendar.getTimeInMillis());
            d.setState(new Date().after(new Date(d.getDate())) ? Course.WAITING_FOR_VALIDATION : Course.FORESEEN);
        }

        // MONEY
        try {
            Editable editMark = ((EditText) view.findViewById(R.id.et_devoir_mark)).getText();
            Editable editMaxMark = ((EditText) view.findViewById(R.id.et_devoir_max_mark)).getText();

            if(!TextUtils.isEmpty(editMark) && !TextUtils.isEmpty(editMaxMark)){
                if(Double.parseDouble(editMark.toString()) < 0){
                    throw new IllegalArgumentException("Invalid mark");
                } else if(Double.parseDouble(editMaxMark.toString()) < 0){
                    throw new IllegalArgumentException("Invalid max mark");
                } else if (Double.parseDouble(editMark.toString()) > Double.parseDouble(editMaxMark.toString())){
                    throw new IllegalArgumentException("Mark can not be higher than max mark");
                } else {
                    d.setMark(Double.parseDouble(editMark.toString()));
                    d.setMaxMark(Double.parseDouble(editMaxMark.toString()));
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid marks");
        }

        //DURATION
        switch (((RadioGroup) view.findViewById(R.id.rg_devoir_duration)).getCheckedRadioButtonId()) {
            case R.id.rb_devoir_duration_60:
                d.setDuration(EventItem.DURATION_1H);
                break;
            case R.id.rb_devoir_duration_90:
                d.setDuration(EventItem.DURATION_1H30);
                break;
            case R.id.rb_devoir_duration_120:
                d.setDuration(EventItem.DURATION_2H);
                break;
            default:
                d.setDuration(EventItem.DURATION_1H);
                break;
        }

        // TYPE
        switch (((RadioGroup) view.findViewById(R.id.rg_devoir_type)).getCheckedRadioButtonId()) {
            case R.id.rb_devoir_type_dm:
                d.setType(Devoir.DM);
                break;
            case R.id.rb_devoir_type_dst:
                d.setType(Devoir.DST);
                break;
            case R.id.rb_devoir_type_interrogation:
                d.setType(Devoir.INTERROGATION);
                break;
            default:
                d.setType(Devoir.DST);
                break;
        }

        // CHAPTER
        d.setChapter(((EditText) view.findViewById(R.id.et_devoir_chapter)).getText().toString());

        // COMMENT
        d.setComment(((EditText) view.findViewById(R.id.et_devoir_comment)).getText().toString());

        d.setPupilUuid(p.getUuid());
        return d;
    }

    @Override
    protected void fillViewWithItem(View view, Object item) {
        Devoir devoir = (Devoir) item;

        Spinner spinner = view.findViewById(R.id.spinner_pupils);
        int index = ((SpinnerPupilAdapter) spinner.getAdapter()).getIndexSelection(devoir.getPupilUuid());
        spinner.setSelection(index);

        ((TextView) view.findViewById(R.id.tv_devoir_date)).setText(DateUtils.getFriendlyDate(devoir.getDate()));

        ((EditText) view.findViewById(R.id.et_devoir_chapter)).setText(devoir.getChapter());
        ((EditText) view.findViewById(R.id.et_devoir_comment)).setText(devoir.getComment());

        switch (devoir.getDuration()) {
            case Devoir.DURATION_1H:
                ((RadioButton) view.findViewById(R.id.rb_devoir_duration_60)).setChecked(true);
                break;
            case Devoir.DURATION_1H30:
                ((RadioButton) view.findViewById(R.id.rb_devoir_duration_90)).setChecked(true);
                break;
            case Devoir.DURATION_2H:
                ((RadioButton) view.findViewById(R.id.rb_devoir_duration_120)).setChecked(true);
                break;
            default:
                ((RadioButton) view.findViewById(R.id.rb_devoir_duration_60)).setChecked(true);
                break;
        }

        switch (devoir.getType()) {
            case Devoir.DM:
                ((RadioButton) view.findViewById(R.id.rb_devoir_type_dm)).setChecked(true);
                break;
            case Devoir.DST:
                ((RadioButton) view.findViewById(R.id.rb_devoir_type_dst)).setChecked(true);
                break;
            case Devoir.INTERROGATION:
                ((RadioButton) view.findViewById(R.id.rb_devoir_type_interrogation)).setChecked(true);
                break;
            default:
                ((RadioButton) view.findViewById(R.id.rb_devoir_type_dst)).setChecked(true);
                break;
        }

        ((EditText) view.findViewById(R.id.et_devoir_mark)).setText(StringUtils.formatDouble(devoir.getMark()));
        ((EditText) view.findViewById(R.id.et_devoir_max_mark)).setText(StringUtils.formatDouble(devoir.getMaxMark()));

        mCalendar.setTimeInMillis(devoir.getDate());
    }

    @Override
    protected void initView(View view) {
        List<Pupil> pupils = PupilTable.getAllPupils(mListener.getDatabase());
        Spinner spinner = view.findViewById(R.id.spinner_pupils);
        spinner.setAdapter(new SpinnerPupilAdapter(getActivity(), pupils));


        view.findViewById(R.id.cv_devoir_pick_date).setOnClickListener(v -> pickDate());
    }

    @Override
    protected void cleanView(View view) {
        mCalendar.setTime(new Date());
        ((EditText) view.findViewById(R.id.et_devoir_mark)).setText(null);
        ((EditText) view.findViewById(R.id.et_devoir_max_mark)).setText(null);
        ((EditText) view.findViewById(R.id.et_devoir_comment)).setText(null);
        ((EditText) view.findViewById(R.id.et_devoir_chapter)).setText(null);
        ((TextView) view.findViewById(R.id.tv_devoir_date)).setText(getString(R.string.unknown_date));
        ((RadioButton) view.findViewById(R.id.rb_devoir_type_dst)).setChecked(true);
        ((RadioButton) view.findViewById(R.id.rb_devoir_duration_60)).setChecked(true);
    }

    @Override
    protected Devoir getItemFromDB(SQLiteDatabase database, long mItemId) {
        return DevoirTable.getDevoirWithId(database, mItemId);
    }

    @Override
    protected int layout() {
        return R.layout.devoir_form;
    }

    public void pickDate(){
        new DatePickerDialog(getActivity(), this, mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, monthOfYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        ((TextView)getView().findViewById(R.id.tv_devoir_date)).setText(DateUtils.getFriendlyDate(mCalendar.getTime()));
    }
}

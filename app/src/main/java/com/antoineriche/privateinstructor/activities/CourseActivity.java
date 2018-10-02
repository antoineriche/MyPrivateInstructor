package com.antoineriche.privateinstructor.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;

public class CourseActivity extends AppCompatActivity {

    public static final String ARG_COURSE_ID = "course-id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_activity);

        if(getIntent().getExtras() != null){
            ((TextView)findViewById(R.id.tv_course_txt)).setText("ID: "+getIntent().getExtras().getInt(ARG_COURSE_ID));
        }
    }
}

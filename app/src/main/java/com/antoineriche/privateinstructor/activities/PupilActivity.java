package com.antoineriche.privateinstructor.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;

public class PupilActivity extends AppCompatActivity {

    public static final String ARG_PUPIL_ID = "pupil-id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pupil_activity);

        if(getIntent().getExtras() != null){
            ((TextView)findViewById(R.id.tv_pupil_txt)).setText("ID: "+getIntent().getExtras().getInt(ARG_PUPIL_ID));
        }
    }
}

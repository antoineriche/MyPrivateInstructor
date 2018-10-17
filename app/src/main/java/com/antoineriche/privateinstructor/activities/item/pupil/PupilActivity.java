package com.antoineriche.privateinstructor.activities.item.pupil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.item.AbstractItemActivity;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.database.PupilTable;

import java.io.File;

public class PupilActivity extends AbstractItemActivity {

    @Override
    protected Fragment getEditionFragment(long pItemId) {
        return PupilFormFragment.newInstance(pItemId);
    }

    @Override
    protected Fragment getDetailsFragment(long pItemId) {
        return PupilDetailsFragment.newInstance(pItemId);
    }

    @Override
    protected Fragment getAddingFragment() {
        return PupilFormFragment.newInstance();
    }

    @Override
    protected long insertItemInDatabase(Object pItem) {
        return PupilTable.insertPupil(getDatabase(), (Pupil) pItem);
    }

    @Override
    protected void updateItemInDatabase(long mItemId, Object pItem) {
        PupilTable.updatePupil(getDatabase(), mItemId, (Pupil) pItem);
    }

    @Override
    protected boolean removeItemFromDatabase(long mItemId) {
        Pupil p = getItemFromDb(mItemId);
        if(!TextUtils.isEmpty(p.getImgPath()) && new File(p.getImgPath()).exists()){
            if(new File(p.getImgPath()).delete()){
                return PupilTable.removePupilWithID(getDatabase(), mItemId);
            }
        } else {
            return PupilTable.removePupilWithID(getDatabase(), mItemId);
        }
        return false;
    }

    @Override
    public Pupil getItemFromDb(long pId) {
        Pupil pupil = PupilTable.getPupilWithId(getDatabase(), pId);
        pupil.setCourses(CourseTable.getCoursesForPupil(getDatabase(), pId));
        return pupil;
    }
}

package com.antoineriche.privateinstructor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.item.AbstractDatabaseActivity;
import com.antoineriche.privateinstructor.activities.item.AbstractFragmentList;
import com.antoineriche.privateinstructor.activities.item.course.CourseListFragment;
import com.antoineriche.privateinstructor.activities.item.pupil.PupilListFragment;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.beans.Location;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.database.LocationTable;
import com.antoineriche.privateinstructor.database.PupilTable;
import com.antoineriche.privateinstructor.services.CourseCheckingService;
import com.antoineriche.privateinstructor.services.FirebaseIntentService;
import com.antoineriche.privateinstructor.services.SnapshotService;

import java.util.List;
import java.util.UUID;

public class IndexActivity extends AbstractDatabaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AbstractFragmentList.FragmentListListener {

    private static final String TAG = IndexActivity.class.getSimpleName();

    Fragment mCurrentFragment;
    NavigationView mNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        DrawerLayout mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView  = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        initMenu(mNavigationView.getMenu());
        onNavigationItemSelected(mNavigationView.getMenu().getItem(0));
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, CourseCheckingService.class));
//        startService(new Intent(this, SnapshotService.class));
        Intent intent = new Intent(this, FirebaseIntentService.class);
        intent.putExtra(FirebaseIntentService.FIB_TASKS, new String[]{FirebaseIntentService.FIB_CHECK_SNAPSHOT, FirebaseIntentService.FIB_CHECK_SYNCHRONIZATION});
        startService(intent);
    }

    @Override
    protected boolean isDatabaseWritable() {
        return false;
    }

    //FIXME Regex is an ugly way to do the trick
    private void initMenu(Menu pMenu) {

        String[] sections = getResources().getStringArray(R.array.drawer_sections);

        View actionView = pMenu.findItem(R.id.nav_home).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_home_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[0].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[0].split(";")[1]);

        actionView = pMenu.findItem(R.id.nav_pupil).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_supervisor_account_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[1].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[1].split(";")[1]);

        actionView = pMenu.findItem(R.id.nav_course).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_book_open_page_variant_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[2].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[2].split(";")[1]);

        actionView = pMenu.findItem(R.id.nav_devoir).getActionView();
        actionView.setEnabled(false);
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_assignment_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[3].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[3].split(";")[1]);

        actionView = pMenu.findItem(R.id.nav_calendar).getActionView();
        actionView.setEnabled(false);
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_today_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[5].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[5].split(";")[1]);

        actionView = pMenu.findItem(R.id.nav_money).getActionView();
        actionView.setEnabled(false);
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_account_balance_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[6].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[6].split(";")[1]);

        actionView = pMenu.findItem(R.id.nav_snapshots).getActionView();
        actionView.setEnabled(false);
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.baseline_storage_white_48));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[7].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[7].split(";")[1]);

        actionView = pMenu.findItem(R.id.nav_stats).getActionView();
        actionView.setEnabled(false);
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_equalizer_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[8].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[8].split(";")[1]);

        actionView = pMenu.findItem(R.id.nav_settings).getActionView();
        actionView.setEnabled(false);
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_menu_manage));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[9].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[9].split(";")[1]);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!(mCurrentFragment instanceof HomeFragment)) {
            onNavigationItemSelected(mNavigationView.getMenu().getItem(0));
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment pFragment;

        switch (item.getItemId()) {
            case R.id.nav_home:
                pFragment = HomeFragment.newInstance();
                break;
            case R.id.nav_pupil:
                pFragment = PupilListFragment.newInstance();
                break;
            case R.id.nav_course:
                pFragment = CourseListFragment.newInstance();
                break;
            case R.id.nav_calendar:
                pFragment = CalendarFragment.newInstance();
                break;
            case R.id.nav_money:
                pFragment = MoneyFragment.newInstance();
                break;
            case R.id.nav_settings:
                pFragment = SettingsFragment.newInstance();
                break;
            case R.id.nav_snapshots:
                pFragment = SnapshotFragment.newInstance();
                break;
            default:
                pFragment = ToImplementFragment.newInstance("");
                break;
        }

        loadFragment(pFragment);
        item.setChecked(true);

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void goToAddingActivity(Class pActivity) {
        startActivity(new Intent(this, pActivity));
    }

    @Override
    public void goToDetailsActivity(Class pActivity, Bundle pBundle) {
        Intent intent = new Intent(this, pActivity);
        intent.putExtras(pBundle);
        startActivity(intent);
    }

    private void loadFragment(Fragment pFragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, pFragment).commit();
        mCurrentFragment = pFragment;
    }
}

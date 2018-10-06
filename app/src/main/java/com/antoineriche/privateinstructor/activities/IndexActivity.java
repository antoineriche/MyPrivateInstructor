package com.antoineriche.privateinstructor.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.item.AbstractDatabaseActivity;
import com.antoineriche.privateinstructor.activities.item.AbstractFragmentList;

public class IndexActivity extends AbstractDatabaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AbstractFragmentList.FragmentListListener {

    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private NavigationView mNavView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavView = (NavigationView) findViewById(R.id.nav_view);
        mNavView.setNavigationItemSelectedListener(this);
        initMenu(mNavView.getMenu());
    }

    @Override
    protected boolean isDatabaseWritable() {
        return false;
    }

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
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_assignment_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[3].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[3].split(";")[1]);

        actionView = pMenu.findItem(R.id.nav_evolution).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_trending_up_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[4].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[4].split(";")[1]);

        actionView = pMenu.findItem(R.id.nav_calendar).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_today_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[5].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[5].split(";")[1]);

        actionView = pMenu.findItem(R.id.nav_money).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_account_balance_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[6].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[6].split(";")[1]);

        actionView = pMenu.findItem(R.id.nav_stats).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_equalizer_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[7].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[7].split(";")[1]);

        actionView = pMenu.findItem(R.id.nav_settings).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_menu_manage));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[8].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[8].split(";")[1]);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.index, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment pFragment;

        switch (item.getItemId()) {
            case R.id.nav_calendar:
                pFragment = ToImplementFragment.newInstance("Calendar");
                break;
            case R.id.nav_pupil:
                pFragment = AbstractFragmentList.PupilListFragment.newInstance();
                break;
            case R.id.nav_course:
                pFragment = AbstractFragmentList.CourseListFragment.newInstance();
                break;
            default:
                pFragment = ToImplementFragment.newInstance("Home");
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, pFragment).commit();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void addItem(Class pActivity) {
        startActivity(new Intent(this, pActivity));
    }

    @Override
    public void seeItemDetails(Class pActivity, Bundle pBundle) {
        Intent intent = new Intent(this, pActivity);
        intent.putExtras(pBundle);
        startActivity(intent);
        Log.e("seeItemDetails", pBundle.toString());
    }
}

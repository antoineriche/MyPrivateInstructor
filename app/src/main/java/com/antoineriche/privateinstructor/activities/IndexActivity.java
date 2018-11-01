package com.antoineriche.privateinstructor.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.item.AbstractDatabaseActivity;
import com.antoineriche.privateinstructor.activities.item.AbstractFragmentList;
import com.antoineriche.privateinstructor.activities.item.course.CourseListFragment;
import com.antoineriche.privateinstructor.activities.item.devoir.DevoirListFragment;
import com.antoineriche.privateinstructor.activities.item.pupil.PupilListFragment;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.database.DevoirTable;
import com.antoineriche.privateinstructor.services.CourseCheckingService;
import com.antoineriche.privateinstructor.services.FirebaseIntentService;

import java.lang.ref.WeakReference;

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
        Intent intent = new Intent(this, FirebaseIntentService.class);
        intent.putExtra(FirebaseIntentService.FIB_TASKS, new String[]{FirebaseIntentService.FIB_CHECK_SNAPSHOT, FirebaseIntentService.FIB_CHECK_SYNCHRONIZATION});
        startService(intent);

        new GetDashboardNotifications(new WeakReference<>(this)).execute();
    }

    @Override
    protected boolean isDatabaseWritable() {
        return false;
    }

    //FIXME Regex is an ugly way to do the trick
    private void initMenu(Menu pMenu) {

        String[] sections = getResources().getStringArray(R.array.drawer_sections);
        int index = 0;

        View actionView = pMenu.findItem(R.id.nav_home).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_home_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[index].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[index].split(";")[1]);

        index++;
        actionView = pMenu.findItem(R.id.nav_dashboard).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.baseline_dashboard_white_48));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[index].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[index].split(";")[1]);
        actionView.findViewById(R.id.menu_badge).setVisibility(View.VISIBLE);
        ((TextView) actionView.findViewById(R.id.menu_badge)).setText("2");

        index++;
        actionView = pMenu.findItem(R.id.nav_pupil).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_supervisor_account_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[index].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[index].split(";")[1]);

        index++;
        actionView = pMenu.findItem(R.id.nav_course).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_book_open_page_variant_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[index].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[index].split(";")[1]);

        index++;
        actionView = pMenu.findItem(R.id.nav_devoir).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_assignment_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[index].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[index].split(";")[1]);

        index++;
        actionView = pMenu.findItem(R.id.nav_calendar).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_today_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[index].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[index].split(";")[1]);

        index++;
        actionView = pMenu.findItem(R.id.nav_money).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_account_balance_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[index].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[index].split(";")[1]);

        index++;
        actionView = pMenu.findItem(R.id.nav_snapshots).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.baseline_storage_white_48));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[index].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[index].split(";")[1]);

        index++;
        actionView = pMenu.findItem(R.id.nav_stats).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_equalizer_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[index].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[index].split(";")[1]);

        index++;
        actionView = pMenu.findItem(R.id.nav_settings).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_menu_manage));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[index].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[index].split(";")[1]);
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

        String title;
        Fragment pFragment;

        switch (item.getItemId()) {
            case R.id.nav_home:
                pFragment = HomeFragment.newInstance();
                title = "Accueil";
                break;
            case R.id.nav_dashboard:
                pFragment = ToImplementFragment.newInstance("Dashboard");
                title = "Dashboard";
                break;
            case R.id.nav_pupil:
                pFragment = PupilListFragment.newInstance();
                title = "Mes élèves";
                break;
            case R.id.nav_course:
                pFragment = CourseListFragment.newInstance();
                title = "Mes cours";
                break;
            case R.id.nav_devoir:
                pFragment = DevoirListFragment.newInstance();
                title = "Mes devoirs";
                break;
            case R.id.nav_calendar:
                pFragment = CalendarFragment.newInstance();
                title = "Calendrier";
                break;
            case R.id.nav_money:
                pFragment = MoneyFragment.newInstance();
                title = "Argent";
                break;
            case R.id.nav_settings:
                pFragment = SettingsFragment.newInstance();
                title = "Paramètres";
                break;
            case R.id.nav_snapshots:
                pFragment = SnapshotFragment.newInstance();
                title = "Snapshots";
                break;
            default:
                pFragment = ToImplementFragment.newInstance("");
                title = "ToImplementFragment";
                break;
        }

        loadFragment(pFragment);
        setTitle(title);
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

    private void updateDashboardNotif(Integer integer){
        MenuItem a = mNavigationView.getMenu().findItem(R.id.nav_dashboard);
        ((TextView) a.getActionView().findViewById(R.id.menu_badge)).setText(""+integer);
    }

    public static class GetDashboardNotifications extends AsyncTask<Void, Void, Integer> {

        WeakReference<IndexActivity> mActivity;

        GetDashboardNotifications(WeakReference<IndexActivity> pActivity) {
            this.mActivity = pActivity;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            SQLiteDatabase db = this.mActivity.get().getDatabase();

            long uncompletedDevoirs = DevoirTable.getAllDevoirs(db).stream().filter(d -> !d.isComplete()).count();
            long uncompletedCours = CourseTable.getAllCourses(db).stream().filter(c -> !c.isComplete()).count();

            return (int) (uncompletedCours + uncompletedDevoirs);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            this.mActivity.get().updateDashboardNotif(integer);
        }
    }
}

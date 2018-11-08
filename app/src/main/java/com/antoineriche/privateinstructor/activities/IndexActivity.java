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
import com.antoineriche.privateinstructor.activities.settings.SettingsFragment;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.database.DevoirTable;
import com.antoineriche.privateinstructor.dialogs.LogInDialog;
import com.antoineriche.privateinstructor.services.CourseCheckingService;
import com.antoineriche.privateinstructor.services.FirebaseIntentService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.ref.WeakReference;

public class IndexActivity extends AbstractDatabaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AbstractFragmentList.FragmentListListener, DashboardFragment.DashboardListener,
        FirebaseAuth.AuthStateListener {

    private static final String TAG = IndexActivity.class.getSimpleName();

    Fragment mCurrentFragment;
    NavigationView mNavigationView;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        FirebaseApp.initializeApp(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();

        DrawerLayout mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView  = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        initNavigationView(mNavigationView);
        onNavigationItemSelected(mNavigationView.getMenu().getItem(0));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(this);
        mFirebaseUser = mAuth.getCurrentUser();
        refreshNavigationMenu(mFirebaseUser);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, CourseCheckingService.class));

        Intent intent = new Intent(this, FirebaseIntentService.class);
        intent.putExtra(FirebaseIntentService.FIB_TASKS, new String[]{FirebaseIntentService.FIB_SCHEDULE_SNAPSHOT, FirebaseIntentService.FIB_CHECK_SYNCHRONIZATION});
        startService(intent);

        new GetDashboardNotifications(new WeakReference<>(this)).execute();
    }

    @Override
    protected boolean isDatabaseWritable() {
        return false;
    }

    //FIXME Regex is an ugly way to do the trick
    private void initNavigationView(NavigationView pNavigationView) {

        // Deal with header
//        View header = mNavigationView.getHeaderView(0);
//        if(mFirebaseUser != null)
//        header.findViewById(R.id.fab_log_in).setOnClickListener(v -> {
//            mAuth.createUserWithEmailAndPassword("riche.ant@gmail.com", "Rhomer91").addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                @Override
//                public void onComplete(@NonNull Task<AuthResult> task) {
//                    if (task.isSuccessful()) {
//                        // Sign in success, update UI with the signed-in user's information
//                        Log.e(TAG, "signInWithEmail:success");
//                        mFirebaseUser = mAuth.getCurrentUser();
//                    } else {
//                        // If sign in fails, display a message to the user.
//                        Log.e(TAG, "signInWithEmail:failure", task.getException());
//                        Toast.makeText(IndexActivity.this, "Authentication failed.",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        });

        // Deal with menu
        Menu menu = pNavigationView.getMenu();
        String[] sections = getResources().getStringArray(R.array.drawer_sections);
        View actionView;

        int index = 0;
        actionView = menu.findItem(R.id.nav_home).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_home_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[index].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[index].split(";")[1]);

        index++;
        actionView = menu.findItem(R.id.nav_dashboard).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.baseline_dashboard_white_48));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[index].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[index].split(";")[1]);
        actionView.findViewById(R.id.menu_badge).setVisibility(View.VISIBLE);
        ((TextView) actionView.findViewById(R.id.menu_badge)).setText("2");

        index++;
        actionView = menu.findItem(R.id.nav_pupil).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_supervisor_account_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[index].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[index].split(";")[1]);

        index++;
        actionView = menu.findItem(R.id.nav_course).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_book_open_page_variant_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[index].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[index].split(";")[1]);

        index++;
        actionView = menu.findItem(R.id.nav_devoir).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_assignment_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[index].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[index].split(";")[1]);

        index++;
        actionView = menu.findItem(R.id.nav_calendar).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_today_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[index].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[index].split(";")[1]);

        index++;
        actionView = menu.findItem(R.id.nav_money).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_account_balance_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[index].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[index].split(";")[1]);

        index++;
        actionView = menu.findItem(R.id.nav_snapshots).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.baseline_storage_white_48));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[index].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[index].split(";")[1]);

        index++;
        actionView = menu.findItem(R.id.nav_stats).getActionView();
        ((ImageView) actionView.findViewById(R.id.menu_icon)).setImageDrawable(getDrawable(R.drawable.ic_equalizer_white_48dp));
        ((TextView) actionView.findViewById(R.id.menu_label)).setText(sections[index].split(";")[0]);
        ((TextView) actionView.findViewById(R.id.menu_details)).setText(sections[index].split(";")[1]);

        index++;
        actionView = menu.findItem(R.id.nav_settings).getActionView();
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
                pFragment = DashboardFragment.newInstance();
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
        ((TextView) a.getActionView().findViewById(R.id.menu_badge)).setText(String.valueOf(integer));
    }

    @Override
    public void onItemComplete(String pItemUuid) {
        new GetDashboardNotifications(new WeakReference<>(this)).execute();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if(firebaseAuth.getCurrentUser() != null){
            mFirebaseUser = firebaseAuth.getCurrentUser();
        } else {
            mFirebaseUser = null;
            if(mCurrentFragment instanceof SnapshotFragment){
                onNavigationItemSelected(mNavigationView.getMenu().getItem(0));
            }
        }
        refreshNavigationMenu(mFirebaseUser);
    }

    private void refreshNavigationMenu(FirebaseUser user){
        boolean userConnected = user != null;

        if(userConnected){
            mNavigationView.getHeaderView(0).findViewById(R.id.fab_log_in).setOnClickListener(
                    v -> mAuth.signOut());
        } else {
            mNavigationView.getHeaderView(0).findViewById(R.id.fab_log_in).setOnClickListener(
                    v -> new LogInDialog(this, mAuth, this::refreshNavigationMenu).show());
        }

        mNavigationView.getMenu().findItem(R.id.nav_snapshots).setVisible(userConnected);
        ((TextView) mNavigationView.getHeaderView(0).findViewById(R.id.textView))
                .setText(userConnected ? "Connected as " + user.getEmail() : "Not connected");


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

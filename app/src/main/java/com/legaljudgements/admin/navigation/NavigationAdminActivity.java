package com.legaljudgements.admin.navigation;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.legaljudgements.R;
import com.legaljudgements.Utils.Constants;
import com.legaljudgements.Utils.Utility;
import com.legaljudgements.admin.dashboard.view.DashboardFragment;
import com.legaljudgements.admin.judgements.model.JudgementModel;
import com.legaljudgements.admin.judgements.view.JudgementFragment;
import com.legaljudgements.admin.judgements.view.CreateJudgementActivity;
import com.legaljudgements.admin.dashboard.view.UserFragment;
import com.legaljudgements.admin.membership.controller.MyListAdapter;
import com.legaljudgements.admin.membership.model.HeaderInfo;
import com.legaljudgements.admin.membership.view.CreateMembershipActivity;
import com.legaljudgements.admin.membership.view.ManageMembershipFragment;
import com.legaljudgements.login.SplashActivity;

import java.util.ArrayList;

public class NavigationAdminActivity extends AppCompatActivity implements MyListAdapter.AdapterCallback, View.OnClickListener {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;

    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    Boolean search = false;


    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_DASHBOARD = "dashboard";
    private static final String TAG_USERS = "users";
    private static final String TAG_ARTICLES = "articles";
    private static final String TAG_MEMBERSHIP = "membership";

    public static String CURRENT_TAG = TAG_DASHBOARD;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private ManageMembershipFragment membershipFragment;
    private JudgementFragment judgementFragment;
    private UserFragment userFragment;
    private String strJudgementId;
    private boolean jdg_tag = false;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Utility.addPreferences(getApplicationContext(), Constants.LoginCheck, true);
        Utility.addPreferences(getApplicationContext(), Constants.isLawyer, false);
        Utility.addPreferences(getApplicationContext(), Constants.isAdmin, true);
        strJudgementId = getIntent().getStringExtra("id");
        jdg_tag = getIntent().getBooleanExtra("tag", false);
        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();*/
        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles_admin);

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            if (strJudgementId == null) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_DASHBOARD;
                loadHomeFragment();
            } else {
                if (jdg_tag) {
                    navItemIndex = 2;
                    CURRENT_TAG = TAG_ARTICLES;
                } else {
                    navItemIndex = 1;
                    CURRENT_TAG = TAG_USERS;
                }
                loadHomeFragment();
            }

        }
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
        txtName.setText("Admin");
        txtWebsite.setText("www.legaljudgments.co.in");

    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            toggleFab();
            toggleMenu();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                strJudgementId = null;
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();


    }

    private void toggleMenu() {

    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // dashboard
                DashboardFragment dashboardFragment = new DashboardFragment();
                return dashboardFragment;

            case 1:
                // dashboard
                userFragment = new UserFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", strJudgementId);
                userFragment.setArguments(bundle);
                return userFragment;
            case 2:
                // articles
                judgementFragment = new JudgementFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("flagged", false);
                bundle2.putString("id", strJudgementId);
                judgementFragment.setArguments(bundle2);
                return judgementFragment;
            case 3:
                // membership packages
                membershipFragment = new ManageMembershipFragment();
                return membershipFragment;

            default:
                return new UserFragment();
        }
    }

    public void logoutDialog(Activity activity) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.common_dialog_layout);
        TextView tv_message = (TextView) dialog.findViewById(R.id.common_dialog_message);
        tv_message.setText("Are you sure you want to logout");
        TextView tv_ok = (TextView) dialog.findViewById(R.id.common_dialog_ok);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceId = Utility.getPreferences(getApplicationContext(), Constants.DEVICE_ID);
                Utility.clearPreferenceData(getApplicationContext());
                Utility.addPreferences(getApplicationContext(), Constants.DEVICE_ID, deviceId);
                Utility.addPreferences(getApplicationContext(), Constants.LoginCheck, false);
                Utility.addPreferences(getApplicationContext(), Constants.isAdmin, false);
                Utility.addPreferences(getApplicationContext(), Constants.isLawyer, false);
                dialog.dismiss();
                startActivity(new Intent(NavigationAdminActivity.this, SplashActivity.class));
                finishAffinity();


            }
        });
        dialog.show();

    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_dashboard:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_DASHBOARD;
                        break;
                    case R.id.nav_users:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_USERS;
                        break;
                    case R.id.nav_articles:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_ARTICLES;
                        break;
                    case R.id.nav_membership_packages:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_MEMBERSHIP;
                        break;
                    case R.id.nav_logout:
                        logoutDialog(NavigationAdminActivity.this);
                        break;

                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_DASHBOARD;
                loadHomeFragment();
                return;
            } else {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (navItemIndex == 2 || navItemIndex == 1) {
            getMenuInflater().inflate(R.menu.keyword_search, menu);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                if (!search)
                    item.setIcon(R.drawable.cross);
                else
                    item.setIcon(R.drawable.search);

                search = !search;
                if (navItemIndex == 1)
                    userFragment.setSearchView();
                else
                    judgementFragment.setSearchView();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex == 2 || navItemIndex == 3)
            fab.show();
        else
            fab.hide();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                switch (navItemIndex) {
                    case 2:
                        startActivityForResult(new Intent(NavigationAdminActivity.this, CreateJudgementActivity.class), 200);
                        break;
                    case 3:
                        startActivityForResult(new Intent(NavigationAdminActivity.this, CreateMembershipActivity.class), 100);
                        break;
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String tag = data.getStringExtra("tag");
            switch (requestCode) {

                case 100:
                    if (tag.equals("add")) {
                        ArrayList<HeaderInfo> membershipList = data.getParcelableArrayListExtra("data");
                        membershipFragment.updateAllMembership(membershipList);
                    } else if (tag.equals("edit")) {
                        HeaderInfo header = data.getParcelableExtra("data");
                        membershipFragment.updateSingleMembership(header);
                    }
                    break;

                case 200:
                    if (tag.equals("add")) {
                        judgementFragment.getJudgements();
                    } else if (tag.equals("edit")) {
                        int pos = data.getIntExtra("pos", -1);
                        if (pos >= 0) {
                            JudgementModel judgementDetail = (JudgementModel) data.getSerializableExtra("data");
                            judgementFragment.updateItem(pos, judgementDetail);
                        }
                    } else if (tag.equals("delete")) {
                        int pos = data.getIntExtra("pos", -1);
                        if (pos >= 0) {
                            judgementFragment.deleteItem(pos);
                        }
                    }
                    break;
            }


        }

    }

    @Override
    public void onMethodCallback(int groupPosition) {

    }
}
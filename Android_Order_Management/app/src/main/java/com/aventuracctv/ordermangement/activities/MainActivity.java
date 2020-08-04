package com.aventuracctv.ordermangement.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.aventuracctv.ordermangement.R;
import com.aventuracctv.ordermangement.data.LoginClass;
import com.aventuracctv.ordermangement.data.User;
import com.aventuracctv.ordermangement.fragments.ArchiveViewFragment;
import com.aventuracctv.ordermangement.fragments.FragmentDrawer;
import com.aventuracctv.ordermangement.fragments.LiveViewFragment;
import com.aventuracctv.ordermangement.interfaces.LoginCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements FragmentDrawer.FragmentDrawerListener, LoginCallback {

    LoginClass login;
    Toolbar mToolbar;
    FragmentDrawer drawerFragment;

    private static final int liveView = 0;
    private static final int archiveView = 1;
    private static final int fragmentCount = archiveView + 1;
    private Fragment[] fragments = new Fragment[fragmentCount];
    private String[] fragmentTAGS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);
        String strUserName = SP.getString("username", "NONE");
        String strPassword = SP.getString("password", "NONE");

        if (!strUserName.equals("NONE")) {
            login = new LoginClass();
            login.LoginClass(this, strUserName, strPassword);
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        fragments[liveView] = getSupportFragmentManager().findFragmentById(R.id.liveViewFragment);
        fragments[archiveView] = getSupportFragmentManager().findFragmentById(R.id.archiveViewFragment);
        fragments[liveView] = new LiveViewFragment();
        fragments[archiveView] = new ArchiveViewFragment();
        fragmentTAGS = new String[]{"liveViewFragment", "archiveViewFragment"};

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // display the first navigation drawer view on app launch

        displayView(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_login:
            {
                UserLoginActivity userLoginActivity = new UserLoginActivity(this);
                userLoginActivity.show();
                //Intent intent = new Intent(this, UserLoginActivity.class);
                //this.startActivity(intent);
                return true;
            }
            case R.id.action_register:
            {
                Intent intent = new Intent(this, UserRegistrationActivity.class);      //from UserRegistrationActivity
                this.startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        String title = getString(R.string.app_name);

        switch (position)
        {
            case 0:
                showFragment(liveView);
                break;
            case 1:
                showFragment(archiveView);
                break;
            case 2:
                showApp("aventura.calc.harddrivecalculator");
                break;
            case 3:
                showApp("com.aventuracctv.aventurafibercalculator");
                break;
            case 4:
                showApp("com.aventuracctv.aventurarangecalculator");
                break;
            case 5:
                showApp("com.aventuracctv.aventuravoltagedropcalculator");
                break;
        }
        getSupportActionBar().setTitle(title);
    }

    private void showFragment(int fragmentIndex) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        if (fm.findFragmentByTag(fragmentTAGS[fragmentIndex]) == null) {
            transaction.add(R.id.container_body, fragments[fragmentIndex], fragmentTAGS[fragmentIndex]);
        }

        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
            } else if (fm.findFragmentByTag(fragmentTAGS[i]) != null) {
                transaction.addToBackStack(null);
                transaction.hide(fragments[i]);
            }
        }

        transaction.commit();
    }

    private void showApp(String packageName) {
        if (isAppInstalled(packageName)) {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
            startActivity(launchIntent);
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        }
    }

    public boolean isAppInstalled(String packageName){
        List<ApplicationInfo> packages;
        PackageManager pm = getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals(packageName)) return true;
        }
        return false;
    }

    @Override
    public void login (User user) {

    }

}

package id.technobit.smsforwarder.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import id.technobit.smsforwarder.R;
import id.technobit.smsforwarder.base.BaseActivity;
import id.technobit.smsforwarder.fragment.MessageStatusFragment;
import id.technobit.smsforwarder.fragment.WhitelistFragment;
import id.technobit.smsforwarder.util.MainFragmentControl;

public class MainActivity extends BaseActivity implements MainFragmentControl, NavigationView.OnNavigationItemSelectedListener {

    Bundle savedInstanceState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("SMS Forwarder");
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_SMS)) {
                if (savedInstanceState == null) {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.main_container, MessageStatusFragment.newInstance())
                            .commit();
                }
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_SMS}, 0);
            }
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_container, MessageStatusFragment.newInstance())
                    .commit();
        }
        this.savedInstanceState = savedInstanceState;
        if (Build.VERSION.SDK_INT < 23){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_container, MessageStatusFragment.newInstance())
                    .commit();
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (savedInstanceState == null) {
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.main_container, MessageStatusFragment.newInstance())
                                .commit();
                    }
                } else {
                    Toast.makeText(this, "You must allow permission to READ SMS, exiting app...", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void changeFragment(Fragment fragment, String TAG) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.main_container, fragment, TAG)
                .commit();
    }

    @Override
    public void changeFragmentBackstack(Fragment fragment, String TAG) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.main_container, fragment)
                .addToBackStack(TAG)
                .commit();
    }

    @Override
    public void popAllBackStack() {
        getSupportFragmentManager().popBackStack("backstack", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_message_status) {
            changeFragment(MessageStatusFragment.newInstance(), "MessageStatus");
        } else if (id == R.id.nav_whitelist) {
            changeFragment(WhitelistFragment.newInstance(), "Whitelist");
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

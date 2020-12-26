package com.androgeekapps.covid_19status;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.androgeekapps.covid_19status.service.FirebaseDataService;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private AppBarConfiguration mAppBarConfiguration;
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    View v;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseDataService.initialise(this);
        v = findViewById(R.id.nav_host_fragment);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        checkVersion();
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_links, R.id.nav_faq)
                .setDrawerLayout(drawer)
                .build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_share) {
                    drawer.closeDrawer(GravityCompat.START);
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = "Download Covid-19 status app to get updated status about COVID-19 affected area. " + "http://play.google.com/store/apps/details?id=" + getPackageName();
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    return false;
                }
                if (item.getItemId() == R.id.nav_rate_us) {
                    boolean is_published = mFirebaseRemoteConfig.getBoolean("is_published");
                    if (is_published) {
                        Uri uri = Uri.parse("market://details?id=" + getPackageName());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try {
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                        }
                    } else {
                        Snackbar.make(v, "This app is waitlisted on playstore. I will may take 4-5 days to reflect on playstore", Snackbar.LENGTH_LONG).show();
                    }
                }
                NavigationUI.onNavDestinationSelected(item,navController);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...\
                FirebaseDataService.fetchStateData(true);
                Snackbar snackbar = Snackbar.make(v, "Data updated", Snackbar.LENGTH_LONG);
                snackbar.show();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void checkVersion() {
        final Context context = this;
        this.runOnUiThread(new Runnable() {
            public void run() {

                try {
                    String version = mFirebaseRemoteConfig.getString("app_version");
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    String appVersion = pInfo.versionName;
                    if (!version.equals(appVersion)) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(R.string.new_version_message)
                                .setTitle(R.string.alert);
                        builder.setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                startActivity(goToMarket);
                            }
                        });
                        builder.setNegativeButton(R.string.later, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        final AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    public void openLink(View view) {
        Intent browserIntent;
                switch (view.getId()) {
            case R.id.linkOne:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mohfw.gov.in/"));
                break;
            case R.id.linkTwo:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://coronavirus.thebaselab.com/"));
                break;
            case R.id.linkThree:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.who.int/emergencies/diseases/novel-coronavirus-2019"));
                break;
            case R.id.linkFour:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cdc.gov/coronavirus/2019-ncov/faq.html"));
                break;
            default:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/"));
                break;
        }
        startActivity(browserIntent);
    }
}

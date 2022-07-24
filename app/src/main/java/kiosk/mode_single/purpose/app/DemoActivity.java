package kiosk.mode_single.purpose.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

import kiosk.mode_single.purpose.app.ui.AppAdapter;
import kiosk.mode_single.purpose.app.ui.AppList;
import kiosk.mode_single.purpose.app.utils.BaseActivity;
import kiosk.mode_single.purpose.app.utils.MySharedPreferences;
import kiosk.mode_single.purpose.app.utils.SettingFragment;

/*
* This Kiosk Mode based from https://github.com/dinkar1708-zz/KioskModeAndroid
 */

public class DemoActivity extends BaseActivity {
    private static final String TAG = DemoActivity.class.getSimpleName();
    final private FragmentManager fragmentManager = getSupportFragmentManager();

    private List<AppList> installedApps;
    ListView userInstalledApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpKioskMode();
        userInstalledApps = findViewById(R.id.installed_app_list);
        getAppsList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Make the app always lock when it's open
        kioskMode.lockUnlock(this, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showSettingsDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!kioskMode.isLocked(this)) {
            super.onBackPressed();
        }
    }

    private void setUpKioskMode() {
        if (!MySharedPreferences.isAppLaunched(this)) {
            Log.d(TAG, "onCreate() locking the app first time");
            kioskMode.lockUnlock(this, true);
            MySharedPreferences.saveAppLaunched(this, true);
        } else {
            //check if app was locked
            if (MySharedPreferences.isAppInKioskMode(this)) {
                Log.d(TAG, "onCreate() locking the app");
                kioskMode.lockUnlock(this, true);
            }
        }
    }

    /**
     * show settings dialog
     */
    private void showSettingsDialog() {
        SettingFragment settingFragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putBoolean(SettingFragment.LOCKED_BUNDLE_KEY, kioskMode.isLocked(this));
        settingFragment.setArguments(args);
        settingFragment.show(fragmentManager, settingFragment.getClass().getSimpleName());
        settingFragment.setActionHandler(isLocked -> {
            int msg = isLocked ? R.string.setting_device_locked : R.string.setting_device_unlocked;
            kioskMode.lockUnlock(DemoActivity.this, isLocked);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            if(msg == R.string.setting_device_unlocked){
                finish();
            }
        });
    }

    /**
     * Show installed apps
     */
    private void getAppsList(){
        installedApps = getInstalledApps();
        AppAdapter installedAppAdapter = new AppAdapter(this, installedApps);
        userInstalledApps.setAdapter(installedAppAdapter);
        userInstalledApps.setOnItemClickListener((adapterView, view, i, l) -> {
            kioskMode.lockUnlock(this, true);
            finish(); // Close app before opening intent
            Intent intent = new Intent(getPackageManager().getLaunchIntentForPackage(installedApps.get(i).packages));
            startActivity(intent);
        });
    }

    private List<AppList> getInstalledApps() {
        PackageManager pm = getPackageManager();
        List<AppList> apps = new ArrayList<>();
        @SuppressLint("QueryPermissionsNeeded")
        List<PackageInfo> packs = pm.getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((!isSystemPackage(p))) {
                String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
                Drawable icon = p.applicationInfo.loadIcon(getPackageManager());
                String packages = p.applicationInfo.packageName;
                apps.add(new AppList(appName, icon, packages));
            }
        }
        return apps;
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }
}

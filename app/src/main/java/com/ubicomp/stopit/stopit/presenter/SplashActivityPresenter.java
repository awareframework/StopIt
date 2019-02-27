package com.ubicomp.stopit.stopit.presenter;

import android.Manifest;
import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.ui.PermissionsHandler;
import com.ubicomp.stopit.stopit.model.Provider;

import java.util.ArrayList;

public class SplashActivityPresenter extends AppCompatActivity {

    public static final String STUDY_URL = "https://api.awareframework.com/index.php/webservice/index/2263/EhxRJNmHC2kt";

    @Override
    protected void onResume() {
        super.onResume();

        // List of required permission
        ArrayList<String> REQUIRED_PERMISSIONS = new ArrayList<>();
        REQUIRED_PERMISSIONS.add(Manifest.permission.WRITE_SYNC_SETTINGS);
        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_SYNC_SETTINGS);
        REQUIRED_PERMISSIONS.add(Manifest.permission.INTERNET);
        REQUIRED_PERMISSIONS.add(Manifest.permission.GET_ACCOUNTS);

        // flag to check permissions
        boolean permissions_ok = true;
        for (String p : REQUIRED_PERMISSIONS) {
            if (PermissionChecker.checkSelfPermission(this, p) != PermissionChecker.PERMISSION_GRANTED) {
                permissions_ok = false;
                break;
            }
        }

        // 1st: Check for permissions
        if (permissions_ok) {
            Intent aware = new Intent(getApplicationContext(), Aware.class);
            startService(aware);

            if (Aware.isStudy(this)) {
                // Open MainActivity when all conditions are ok
                Intent main = new Intent(this, MainActivityPresenter.class);
                startActivity(main);
                finish();

            } else {
                Aware.joinStudy(this, STUDY_URL);
                IntentFilter joinFilter = new IntentFilter(Aware.ACTION_JOINED_STUDY);
                registerReceiver(joinObserver, joinFilter);
            }

        } else {

            Intent permissions = new Intent(this, PermissionsHandler.class);
            permissions.putExtra(PermissionsHandler.EXTRA_REQUIRED_PERMISSIONS, REQUIRED_PERMISSIONS);
            permissions.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(permissions);
        }
    }

    // Reciever waiting for study joined state
    private JoinObserver joinObserver = new JoinObserver();
    private class JoinObserver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(Aware.ACTION_JOINED_STUDY)) {

                Account aware_account = Aware.getAWAREAccount(getApplicationContext());
                String authority = Provider.getAuthority(getApplicationContext());
                long frequency = Long.parseLong(Aware.getSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_WEBSERVICE)) * 60;
                ContentResolver.setIsSyncable(aware_account, authority, 1);
                ContentResolver.setSyncAutomatically(aware_account, authority, true);
                SyncRequest request = new SyncRequest.Builder()
                        .syncPeriodic(frequency, frequency / 3)
                        .setSyncAdapter(aware_account, authority)
                        .setExtras(new Bundle()).build();
                ContentResolver.requestSync(request);

                // Open MainActivity when all conditions are ok
                Intent main = new Intent(getApplicationContext(), MainActivityPresenter.class);
                startActivity(main);
                unregisterReceiver(joinObserver);
                finish();
            }
        }
    }
}
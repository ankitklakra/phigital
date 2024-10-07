package com.phigital.ai;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.phigital.ai.Auth.RegisterActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class Check extends AppCompatActivity {
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private AppUpdateManager mAppUpdateManager;
    private static final int RC_APP_UPDATE = 100;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        // Initialize App Update Manager
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        checkForAppUpdates();

        // Firebase Initialization
        mAuth = FirebaseAuth.getInstance();
        runcheck();
    }

    @Override
    protected void onStart() {
        super.onStart();
        runcheck();
    }

    @Override
    protected void onResume() {
        super.onResume();
        runcheck();
    }

    // Check for app updates
    private void checkForAppUpdates() {
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    mAppUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, RC_APP_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void runcheck() {
        // Fetch config file from server
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(43200).build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                final String latestAppVersion = mFirebaseRemoteConfig.getString("latest_app_version");
                if (Integer.parseInt(latestAppVersion) > getVersionCode()) {
                    new AlertDialog.Builder(this)
                            .setTitle("Please Update the App")
                            .setMessage("A new version of this app is available. Please update it")
                            .setPositiveButton("OK", (dialog, which) -> openAppRating(Check.this))
                            .setCancelable(false)
                            .show();
                    Toast.makeText(Check.this, "Update available", Toast.LENGTH_SHORT).show();
                } else {
                    checkUser();
                }
            } else {
                Toast.makeText(Check.this, "Please check internet connection and try again", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            navigateToRegister();
        } else {
            // Check if user is banned
            Query userQuery = FirebaseDatabase.getInstance().getReference().child("Ban");
            userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                        navigateToRegister();
                        Toast.makeText(Check.this, "Account terminated due to guideline violations.", Toast.LENGTH_LONG).show();
                    } else {
                        navigateToMain();
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    // Handle error
                }
            });
        }
    }

    private void navigateToRegister() {
        Intent intent = new Intent(Check.this, RegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void navigateToMain() {
        Intent intent = new Intent(Check.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private int getVersionCode() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (Exception ignored) {
        }
        return Objects.requireNonNull(packageInfo).versionCode;
    }

    // Method to prompt user for app update via Google Play
    public static void openAppRating(Context context) {
        String appId = context.getPackageName();
        Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appId));
        boolean marketFound = false;

        @SuppressLint("QueryPermissionsNeeded") final List<ResolveInfo> otherApps = context.getPackageManager().queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp : otherApps) {
            if (otherApp.activityInfo.applicationInfo.packageName.equals("com.android.vending")) {
                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(otherAppActivity.applicationInfo.packageName, otherAppActivity.name);
                rateIntent.setComponent(componentName);
                context.startActivity(rateIntent);
                marketFound = true;
                break;
            }
        }

        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appId));
            context.startActivity(webIntent);
        }
    }
}

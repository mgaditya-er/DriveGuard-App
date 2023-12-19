package com.example.vehiclesafe;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vehiclesafe.adapter.CallLogAdapter;
import com.example.vehiclesafe.databinding.ActivityHomePageBinding;
import com.example.vehiclesafe.model.CallLogModel;

import java.util.List;

public class Activity_HomePage extends AppCompatActivity {

    ActivityHomePageBinding binding;

    RecyclerView recyclerView;

    private List<CallLogModel> callLogList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = findViewById(R.id.itemsRecycler);


        // Find the ImageButton by its ID
        ImageButton electricBikeButton = findViewById(R.id.electricBikeButton);
        ImageButton permissionBtn = findViewById(R.id.permission);

        permissionBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // Display a toast message when the ImageButton is clicked
                requestNotificationPolicyAccess();
                Toast.makeText(Activity_HomePage.this, "Permission Clicked", Toast.LENGTH_SHORT).show();
            }
        });
                // Set an OnClickListener for the ImageButton
        electricBikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display a toas524+65+/11528t message when the ImageButton is clicked
                enableDoNotDisturbMode();
                Toast.makeText(Activity_HomePage.this, "ImageButton Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        binding.bottomNavView.setBackground(null);

        binding.bottomNavView.setOnItemSelectedListener(item->{
            int id = item.getItemId();
            switch(id) {
                case R.id.navigation_home:
                    break;

                case R.id.navigation_logs:
                    Intent intent1 = new Intent(Activity_HomePage.this, Activity_CallLogs.class);

                    startActivity(intent1);
                    finish();
                    break;

                case R.id.navigation_emergency:
                    Intent intent2 = new Intent(Activity_HomePage.this, Activity_Contacts.class);

                    startActivity(intent2);
                    finish();
                    break;

                default:

            }
            return true;
        });
    }
    private void enableDoNotDisturbMode() {
        // Get the NotificationManager service
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Check if the user has granted your app the permission to change DND mode
        if (isNotificationPolicyAccessGranted()) {
            // Activate Do Not Disturb mode
            if (notificationManager != null) {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                Toast.makeText(this, "Do Not Disturb mode enabled.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Ask the user to grant the permission manually
            Toast.makeText(this, "Please grant notification access permission manually.", Toast.LENGTH_LONG).show();
            requestNotificationPolicyAccess();
        }
    }
    private boolean isNotificationPolicyAccessGranted() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            NotificationManager.Policy policy = notificationManager.getNotificationPolicy();

            return policy != null && (policy.priorityCategories & NotificationManager.Policy.PRIORITY_CATEGORY_ALARMS) != 0;
        }
        return false;
    }
    private void requestNotificationPolicyAccess() {
        try {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        } catch (Exception e) {
            // Handle the exception, such as showing an error message to the user
            Toast.makeText(this, "Unable to open notification policy settings.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermissions() {
        int readCallLogPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);
        return readCallLogPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_CALL_LOG}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permissions were granted
                accessCallLog();
            } else {
                // Permissions were denied
                // Handle the case where the user denied the permissions
            }
        }
    }

    private void accessCallLog() {
        callLogList = CallLogReader.readCallLog(this);

        CallLogAdapter adapter = new CallLogAdapter(callLogList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

}

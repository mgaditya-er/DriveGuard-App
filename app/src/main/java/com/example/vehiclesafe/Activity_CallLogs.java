package com.example.vehiclesafe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.vehiclesafe.databinding.ActivityLogsBinding;

public class Activity_CallLogs extends AppCompatActivity {

    ActivityLogsBinding binding;
    private static final int REQUEST_CODE_PERMISSION_READ_CALL_LOG = 123;
    private Button btnGetMissedCalls;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityLogsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toast.makeText(Activity_CallLogs.this, "Call History Page", Toast.LENGTH_SHORT).show();
        btnGetMissedCalls = findViewById(R.id.btnGetMissedCalls);
        listView = binding.listView;
//        btnGetMissedCalls.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        if (checkPermission()) {
            showMissedCalls();
        } else {
            requestPermission();
        }
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_CallLogs.this, Activity_HomePage.class);

                startActivity(intent);
                finish();
            }
        });

        binding.bottomNavView.setBackground(null);

        binding.bottomNavView.setOnItemSelectedListener(item->{
            int id = item.getItemId();
            switch(id) {
                case R.id.navigation_home:
                    Intent intent1 = new Intent(Activity_CallLogs.this, Activity_HomePage.class);

                    startActivity(intent1);
                    finish();
                    break;

                case R.id.navigation_logs:

                    break;

                case R.id.navigation_emergency:
                    Intent intent2 = new Intent(Activity_CallLogs.this, Activity_Contacts.class);

                    startActivity(intent2);
                    finish();
                    break;

                default:

            }
            return true;
        });
    }
    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, REQUEST_CODE_PERMISSION_READ_CALL_LOG);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION_READ_CALL_LOG) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showMissedCalls();
            } else {
                Toast.makeText(this, "Permission denied. Cannot retrieve missed calls.", Toast.LENGTH_SHORT).show();
            }
        }
    }
//    private void showMissedCalls() {
//        String[] projection = {CallLog.Calls._ID, CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.DATE};
//        String selection = CallLog.Calls.TYPE + " = ? AND " + CallLog.Calls.DATE + " > ?";
//        String[] selectionArgs = {String.valueOf(CallLog.Calls.MISSED_TYPE), String.valueOf(System.currentTimeMillis() - 72000000)}; // Past hour
//
//        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, selection, selectionArgs, CallLog.Calls.DATE + " DESC");
//
//        if (cursor != null) {
//            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
//                    this,
//                    android.R.layout.simple_list_item_2,
//                    cursor,
//                    new String[]{CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER},
//                    new int[]{android.R.id.text1, android.R.id.text2},
//                    0
//            );
//
//            listView.setAdapter(adapter);
//        }
//    }
private void showMissedCalls() {
    String[] projection = {CallLog.Calls._ID, CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.DATE};
    String selection = CallLog.Calls.TYPE + " = ? AND " + CallLog.Calls.DATE + " > ?";
    String[] selectionArgs = {String.valueOf(CallLog.Calls.MISSED_TYPE), String.valueOf(System.currentTimeMillis() - 72000000)}; // Past hour

    Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, selection, selectionArgs, CallLog.Calls.DATE + " DESC");

    if (cursor != null) {
        // Create a list of unique phone numbers with the count of missed calls for each number
        List<Map<String, String>> dataList = new ArrayList<>();
        Map<String, Integer> callCountMap = new HashMap<>();

        // Get column indices once to avoid repeated calls
        int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int nameIndex = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);

        // Iterate through the cursor to populate the dataList and callCountMap
        while (cursor.moveToNext()) {
            // Check if the columns are present in the cursor
            if (numberIndex != -1 && nameIndex != -1) {
                String number = cursor.getString(numberIndex);
                String name = cursor.getString(nameIndex);

                if (!callCountMap.containsKey(number)) {
                    // If the number is not in the map, add it with a count of 1
                    callCountMap.put(number, 1);
                } else {
                    // If the number is already in the map, increment the count
                    int count = callCountMap.get(number);
                    callCountMap.put(number, count + 1);
                }

                // Create a map for each row in the list
                Map<String, String> data = new HashMap<>();
                data.put("name", name);
                data.put("number", number + " (" + callCountMap.get(number) + ")");
                dataList.add(data);
            }
        }

        // Create a custom adapter
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                dataList,
                android.R.layout.simple_list_item_2,
                new String[]{"name", "number"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );

        // Set the adapter for the listView
        listView.setAdapter(adapter);

        // Close the cursor after use
        cursor.close();
    }
}

}

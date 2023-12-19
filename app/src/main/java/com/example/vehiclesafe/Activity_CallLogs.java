package com.example.vehiclesafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vehiclesafe.databinding.ActivityLogsBinding;

public class Activity_CallLogs extends AppCompatActivity {

    ActivityLogsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityLogsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toast.makeText(Activity_CallLogs.this, "Call History Page", Toast.LENGTH_SHORT).show();

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
}

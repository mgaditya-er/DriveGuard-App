package com.example.vehiclesafe;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vehiclesafe.databinding.ActivityContactsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Activity_Contacts extends AppCompatActivity {
    private FirebaseFirestore db;
    ContactAdapter adapter;
    private static final int PERMISSION_REQUEST_CODE = 1;
    ActivityContactsBinding binding;
    List<String> contactList = new ArrayList<>();
    FirebaseAuth auth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityContactsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toast.makeText(Activity_Contacts.this, "Emergency Contacts Page", Toast.LENGTH_SHORT).show();
        db = FirebaseFirestore.getInstance();
        String userId = auth.getCurrentUser().getUid();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Contacts.this, Activity_HomePage.class);

                startActivity(intent);
                finish();
            }
        });

        binding.bottomNavView.setBackground(null);

        binding.bottomNavView.setOnItemSelectedListener(item->{
            int id = item.getItemId();
            switch(id) {
                case R.id.navigation_home:
                    Intent intent1 = new Intent(Activity_Contacts.this, Activity_HomePage.class);

                    startActivity(intent1);
                    finish();
                    break;

                case R.id.navigation_logs:
                    Intent intent2 = new Intent(Activity_Contacts.this, Activity_CallLogs.class);

                    startActivity(intent2);
                    finish();
                    break;

                case R.id.navigation_emergency:

                    break;

                default:

            }
            return true;
        });

        RecyclerView recyclerView = findViewById(R.id.emergency_contact);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Create an adapter for the RecyclerView and set it
        adapter = new ContactAdapter(contactList);
        recyclerView.setAdapter(adapter);

        // Floating action button (FAB) to add a contact
        binding.addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddContactDialog();
            }
        });

        loadContactsFromFirestore(userId);

    }
    private void loadContactsFromFirestore(String userId) {
        db.collection("users").document(userId).collection("contacts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    contactList.clear(); // Clear existing data
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String name = document.getString("name");
                        String contact = document.getString("phoneNumber");
                        String contactInfo = name + " - " + contact;
                        contactList.add(contactInfo);
                    }
                    adapter.notifyDataSetChanged(); // Notify the adapter about the data change
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Activity_Contacts.this, "Error loading contacts", Toast.LENGTH_SHORT).show();
                });
    }

    private void showAddContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        EditText editTextName = dialogView.findViewById(R.id.editTextName);
        EditText editTextContact = dialogView.findViewById(R.id.editTextContact);
        Button buttonSaveContact = dialogView.findViewById(R.id.buttonSaveContact);

        buttonSaveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String contact = editTextContact.getText().toString().trim();


                if (!name.isEmpty() && !contact.isEmpty()) {
                    String contactInfo = name + " - " + contact;
                    contactList.add(contactInfo);
                    // Add logic to display or save the contactInfo as needed

                    Toast.makeText(Activity_Contacts.this, "Contact saved", Toast.LENGTH_SHORT).show();
                    saveContactToFirestore(name, contact);
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(Activity_Contacts.this, "Please enter name and contact number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.show();
    }
    private void saveContactToFirestore(String name, String contact) {
        // Create a Map to store the contact data
        Map<String, Object> contactData = new HashMap<>();
        contactData.put("name", name);
        contactData.put("contact", contact);
        User user = new User(name, contact);
        String userId = auth.getCurrentUser().getUid();

        // Add data to Firestore
        db.collection("users").document(userId).collection("contacts")
                .add(user)
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                    Toast.makeText(Activity_Contacts.this, "Contact saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(Activity_Contacts.this, "Error saving contact", Toast.LENGTH_SHORT).show();
                });
    }
    public static void checkIncomingNumber(Context context, String incomingNumber) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        AtomicInteger flag= new AtomicInteger(1);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(userId).collection("contacts")
                .whereEqualTo("phoneNumber", incomingNumber) // Use whereEqualTo for efficient query
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Priority number found
                        Toast.makeText(context, "Priority number calling: " + incomingNumber, Toast.LENGTH_LONG).show();
                        requestSmsPermission(context,incomingNumber,"Location");
                        flag.set(2);
                    } else {
                        // Handle non-priority calls or log silently without flooding the user with toasts
                        // Toast.makeText(context, "Low priority number: " + incomingNumber, Toast.LENGTH_LONG).show();
                        Log.d("CheckIncomingNumber", "Non-priority call detected: " + incomingNumber);
                        Toast.makeText(context, "Low Priority number calling: " + incomingNumber, Toast.LENGTH_LONG).show();
                        if (flag.get() == 1) {
                            Toast.makeText(context, "Low priority number calling: " + incomingNumber, Toast.LENGTH_LONG).show();
                            requestSmsPermission(context, incomingNumber, " ");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Log error or handle failure silently
                    Log.e("CheckIncomingNumber", "Error checking for priority number", e);
                });
        if(flag.equals(1))
        {Toast.makeText(context, "Low priority number calling: " + incomingNumber, Toast.LENGTH_LONG).show();

            requestSmsPermission(context,incomingNumber," ");

        }
    }

    private static void requestSmsPermission(Context context,String incomingNumber, String location) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                // Permission already granted, proceed to send SMS
                sendCustomizedSMS(context,incomingNumber, location);

            } else {
                // Request SMS permission
                ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);
            }
        } else {
            // Permission is automatically granted on SDKs lower than Marshmallow
            sendCustomizedSMS(context,incomingNumber, location);


        }
    }

    private static void sendCustomizedSMS(Context context,String incomingNumber, String loc) {
        SmsManager smsManager = SmsManager.getDefault();

        // You can customize the SMS message here
        String finalMessage = "Hello, This is " + loc + "! Rider is driving.";

        try {
            // Send the SMS
            smsManager.sendTextMessage(incomingNumber, null, finalMessage, null, null);

            // Optionally, you can handle the sent SMS here
            Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Handle exceptions, e.g., permission denied or SMS not sent
            Toast.makeText(context, "Failed to send SMS", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


}

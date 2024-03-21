package com.example.vehiclesafe;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vehiclesafe.databinding.ActivityContactsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Activity_Contacts extends AppCompatActivity {
    private FirebaseFirestore db;
    ContactAdapter adapter;
    private boolean isDoNotDisturbModeEnabled = false;
    private LocationManager locationManager;
    private final static int REQUEST_CODE = 100;

    private static String addrr = "";
    static FusedLocationProviderClient fusedLocationProviderClient;
    private static final int PERMISSION_REQUEST_CODE = 1;
    ActivityContactsBinding binding;
    List<String> contactList = new ArrayList<>();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    private static final int VIBRATION_DURATION = 10000; // Vibration duration in milliseconds
    private static final int CALL_THRESHOLD = 3; // Number of calls to trigger vibration

    ImageButton deletebtn,editbtn;
    private static Map<String, AtomicInteger> callCountMap = new HashMap<>();
    private static Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityContactsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toast.makeText(Activity_Contacts.this, "Emergency Contacts Page", Toast.LENGTH_SHORT).show();
        db = FirebaseFirestore.getInstance();
        String userId = auth.getCurrentUser().getUid();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        LocationHelper locationHelper = new LocationHelper(locationManager);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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
        adapter.setOnDeleteClickListener(new ContactAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position, String contactName) {
                // Show the name of the clicked contact
                Toast.makeText(Activity_Contacts.this, "Clicked contact: " + contactName, Toast.LENGTH_SHORT).show();
                String contactInfo = contactList.get(position);
                String[] parts = contactInfo.split(" - ");
                String phoneNumber = parts[1]; // Extract phone number from the contact info
                deleteContactFromFirestore(phoneNumber); // Delete contact from Firestore
                contactList.remove(position); // Remove contact from the list
                adapter.notifyItemRemoved(position); // Notify adapter about the data change
                // Now you can proceed with deleting the contact from Firestore or perform any other actions
            }
        });

        adapter.setOnEditClickListener(new ContactAdapter.OnEditClickListener() {
            @Override
            public void onEditClick(int position) {
                Toast.makeText(Activity_Contacts.this, "Edit is clicked"+position, Toast.LENGTH_SHORT).show();
                String contactInfo = contactList.get(position);
                String[] parts = contactInfo.split(" - ");
                String name = parts[0];
                String phoneNumber = parts[1];

                // Create an edit dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Contacts.this);
                View dialogView = LayoutInflater.from(Activity_Contacts.this).inflate(R.layout.dialog_layoutedit, null);
                builder.setView(dialogView);

                EditText editTextName = dialogView.findViewById(R.id.editTextName);
                EditText editTextContact = dialogView.findViewById(R.id.editTextContact);
                Button buttonSaveContact = dialogView.findViewById(R.id.buttonSaveContact);

                editTextName.setText(name);
                editTextContact.setText(phoneNumber);

                AlertDialog alertDialog = builder.create();

                buttonSaveContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String updatedName = editTextName.getText().toString().trim();
                        String updatedContact = editTextContact.getText().toString().trim();

                        // Update the contact in the list
                        String updatedContactInfo = updatedName + " - " + updatedContact;
                        contactList.set(position, updatedContactInfo);
                        adapter.notifyDataSetChanged(); // Notify adapter about the data change
// Update the contact in Firestore
                        // Get the user ID of the current user
                        String userId = auth.getCurrentUser().getUid();

// Query Firestore to find the document ID of the contact you want to update
                        db.collection("users").document(userId).collection("contacts")
                                .whereEqualTo("phoneNumber", phoneNumber)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                                        String documentId = document.getId(); // Get the document ID of the contact

                                        // Update the contact document with the new data
                                        db.collection("users").document(userId).collection("contacts")
                                                .document(documentId)
                                                .update("name", updatedName, "phoneNumber", updatedContact)
                                                .addOnSuccessListener(aVoid -> {
                                                    // Handle successful update
                                                    Toast.makeText(Activity_Contacts.this, "Contact updated successfully", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Handle update failure
                                                    Toast.makeText(Activity_Contacts.this, "Error updating contact", Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    // Handle query failure
                                    Toast.makeText(Activity_Contacts.this, "Error querying contact", Toast.LENGTH_SHORT).show();
                                });

                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });

    }



    private void deleteContactFromFirestore(String phoneNumber) {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId).collection("contacts")
                .whereEqualTo("phoneNumber", phoneNumber)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete() // Delete the contact document
                                .addOnSuccessListener(aVoid -> {
                                    // Handle successful deletion
                                    Toast.makeText(Activity_Contacts.this, "Contact deleted successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Handle deletion failure
                                    Toast.makeText(Activity_Contacts.this, "Error deleting contact", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Activity_Contacts.this, "Error deleting contact", Toast.LENGTH_SHORT).show();
                });
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
        LocationHelper locationHelper = new LocationHelper((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            AtomicInteger flag = new AtomicInteger(1);
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            AtomicInteger callCount = callCountMap.get(incomingNumber);
            if (callCount == null) {
                callCount = new AtomicInteger(0);
                callCountMap.put(incomingNumber, callCount);
            }
//        AtomicInteger finalCallCount = callCount;
        AtomicInteger finalCallCount = callCount;
        db.collection("users").document(userId).collection("contacts")
                    .whereEqualTo("phoneNumber", incomingNumber) // Use whereEqualTo for efficient query
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Priority number found
                            Toast.makeText(context, "Priority number calling: " + incomingNumber, Toast.LENGTH_LONG).show();
//                            String googleMapsLink = locationHelper.getCurrentLocationLink();
//                            String link = "https://www.google.com/maps?q=19.030633,73.016621";
                          String link = "https://www.google.com/maps?q=16.741931502412022,74.38326381111027";
                            requestSmsPermission(context, incomingNumber, "Link ->  "+link);
                            Toast.makeText(context, "" + "Link"+link, Toast.LENGTH_SHORT).show();
                            int newCount = finalCallCount.incrementAndGet();

                            // Check if the call count exceeds the threshold
                            if (newCount >= CALL_THRESHOLD) {
                                // Trigger vibration
                                vibrate(context);
                                Toast.makeText(context, "Vibrating due to repeated calls", Toast.LENGTH_SHORT).show();
                            }
                            flag.set(2);
                        } else {
                            // Handle non-priority calls or log silently without flooding the user with toasts
                            // Toast.makeText(context, "Low priority number: " + incomingNumber, Toast.LENGTH_LONG).show();
                            Log.d("CheckIncomingNumber", "Non-priority call detected: " + incomingNumber);


                        }
                    })
                    .addOnFailureListener(e -> {
                        // Log error or handle failure silently
                        Log.e("CheckIncomingNumber", "Error checking for priority number", e);
                    });
        int newCount = finalCallCount.incrementAndGet();

        // Check if the call count exceeds the threshold
        if (newCount >= CALL_THRESHOLD) {
            // Trigger vibration
            vibrate(context);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(VIBRATION_DURATION, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(VIBRATION_DURATION);
            }
            Toast.makeText(context, "Vibrating due to repeated calls", Toast.LENGTH_SHORT).show();
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);

        }
        if (flag.get() == 1) {
//            Toast.makeText(context, "Low priority number calling: " + incomingNumber, Toast.LENGTH_LONG).show();
            if (isDndModeEnabled(context)) {
                String link;
            if (incomingNumber != null && (incomingNumber.equals("+919322453226") || incomingNumber.equals("+918983795295")
                     || incomingNumber.equals("+919834610889") || incomingNumber.equals("+919373616244"))) {
                 link = "https://www.google.com/maps?q=16.741931502412022,74.38326381111027";

                requestSmsPermission(context, incomingNumber, " Live Location " + link);
            }
            else {
                 link = "";
            }

                requestSmsPermission(context, incomingNumber, " Status " + link);
            }
//            requestSmsPermission(context, incomingNumber, " Live Location "+link);
        }

    }

    private static boolean isDndModeEnabled(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        return notificationManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                notificationManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_NONE;
    }

    private static void vibrate(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Check if the user has granted your app the permission to change DND mode
        if (notificationManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (notificationManager.isNotificationPolicyAccessGranted()) {
                // deactivate Do Not Disturb mode
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                // Toast.makeText(context, "Do Not Disturb mode disabled.", Toast.LENGTH_SHORT).show();
            }
        }
        if (vibrator != null) {
            // Check if the device has a vibrator (required for API level 26 and above)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(VIBRATION_DURATION, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(VIBRATION_DURATION);
            }
        }
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
    }



    private static void  getLastLocation(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                try {
                                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    updateUI(addresses.get(0));
                                    addrr = addresses.get(0).getAddressLine(0);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        } else {
            Toast.makeText(context, "Give Location permission", Toast.LENGTH_SHORT).show(); // Ask for permissions if not granted
        }

    }

    private static void updateUI(Address address) {
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
        String finalMessage = "Hello, This is   " + loc + "  ! Rider is driving.";

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

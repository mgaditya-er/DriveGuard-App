package com.example.vehiclesafe;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<String> contacts;

    // Constructor to initialize the list of contacts
    public ContactAdapter(List<String> contacts) {
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        // Bind data to the views in each item
        holder.bindContact(contacts.get(position));
    }

    @Override
    public int getItemCount() {
        // Return the number of items in the list
        return contacts.size();
    }

    // ViewHolder class to hold the views for each item in the RecyclerView
    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        private TextView contactTextView;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views in each item
            contactTextView = itemView.findViewById(R.id.contactTextView);
        }

        // Method to bind contact data to the views
        public void bindContact(String contact) {
            contactTextView.setText(contact);
        }
    }
}

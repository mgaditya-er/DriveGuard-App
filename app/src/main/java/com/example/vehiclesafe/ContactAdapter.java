package com.example.vehiclesafe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<String> contacts;
    private OnDeleteClickListener onDeleteClickListener;

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
        holder.setDeleteClickListener(position);
    }

    @Override
    public int getItemCount() {
        // Return the number of items in the list
        return contacts.size();
    }

    // ViewHolder class to hold the views for each item in the RecyclerView
    public class ContactViewHolder extends RecyclerView.ViewHolder {

        private TextView contactTextView;
        private ImageView deleteButton;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views in each item
            contactTextView = itemView.findViewById(R.id.contactTextView);
            deleteButton = itemView.findViewById(R.id.delete_btn);
        }


        // Method to bind contact data to the views
        public void bindContact(String contact) {
            contactTextView.setText(contact);
        }



        // Set click listener for delete button
//        public void setDeleteClickListener(final int position) {
//            deleteButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (onDeleteClickListener != null) {
//                        onDeleteClickListener.onDeleteClick(position);
//                    }
//                }
//            });
//        }
        public void setDeleteClickListener(final int position) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDeleteClickListener != null) {
                        // Get the name of the clicked contact
                        String contactName = contacts.get(position);
                        // Pass the position and contact name to the onDeleteClick method
                        onDeleteClickListener.onDeleteClick(position, contactName);
                    }
                }
            });
        }

    }


    // Interface to communicate delete button clicks to the activity/fragment
    public interface OnDeleteClickListener {
        void onDeleteClick(int position, String contactName);

    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        onDeleteClickListener = listener;
    }

}

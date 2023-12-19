package com.example.vehiclesafe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vehiclesafe.R;
import com.example.vehiclesafe.model.CallLogModel;

import java.util.List;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder> {

    private List<CallLogModel> callLogItems;

    public CallLogAdapter(List<CallLogModel> callLogItems) {
        this.callLogItems = callLogItems;
    }

    @NonNull
    @Override
    public CallLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new CallLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallLogViewHolder holder, int position) {
        CallLogModel item = callLogItems.get(position);
        holder.callerName.setText(item.getContactName());
        holder.phoneNumber.setText(item.getPhNumber());
        holder.callType.setText(item.getCallType());
        holder.callDate.setText(String.valueOf(item.getCallDate()));
    }

    @Override
    public int getItemCount() {
        return callLogItems.size();
    }

    static class CallLogViewHolder extends RecyclerView.ViewHolder {
        TextView callerName;
        TextView phoneNumber;
        TextView callType;
        TextView callDate;

        CallLogViewHolder(View itemView) {
            super(itemView);
            callerName = itemView.findViewById(R.id.layout_call_log_contact_name);
            phoneNumber = itemView.findViewById(R.id.layout_call_log_ph_no);
            callType = itemView.findViewById(R.id.layout_call_log_type);
            callDate = itemView.findViewById(R.id.layout_call_log_date);
        }
    }

}
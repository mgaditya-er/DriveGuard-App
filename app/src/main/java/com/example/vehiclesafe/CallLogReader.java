package com.example.vehiclesafe;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

import com.example.vehiclesafe.model.CallLogModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CallLogReader {

    @SuppressLint("Range")
    public static List<CallLogModel> readCallLog(Context context) {
        List<CallLogModel> callLogList = new ArrayList<>();

        Uri callLogUri = CallLog.Calls.CONTENT_URI;

        String[] projection = {
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE
        };

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(
                callLogUri, projection, null, null, CallLog.Calls.DEFAULT_SORT_ORDER
        );
        if(cursor != null) {
            while(cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                String type = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
                String date = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));
                Date callDate = new Date(Long.valueOf(date));
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM--yy HH:mm");
                String dateString = formatter.format(callDate);

                if(type.equals(CallLog.Calls.MISSED_TYPE)) {
                    CallLogModel call = new CallLogModel(name, number, "Missed Call", date);
                    callLogList.add(call);
                }
            }
            cursor.close();
        }
        return callLogList;
    }
}

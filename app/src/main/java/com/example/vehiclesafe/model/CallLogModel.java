package com.example.vehiclesafe.model;

public class CallLogModel {
    private String phNumber, contactName, callType, callDate;

    public CallLogModel(String phNumber, String contactName, String callType, String callDate) {
        this.phNumber = phNumber;
        this.contactName = contactName;
        this.callType = callType;
        this.callDate = callDate;

    }

    public String getPhNumber() {
        return phNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public String getCallType() {
        return callType;
    }

    public String getCallDate() {
        return callDate;
    }


}
package id.t12ue.kalimutu.model;

import androidx.room.TypeConverters;

import java.util.Date;

import id.t12ue.kalimutu.helpers.DateConverter;

public class DetailTransaction {
    long transactionId;
    long userId;
    @TypeConverters(DateConverter.class)
    Date transactionTime;
    String dateBegin;
    String dateFinish;
    int quota;
    int payMethode;
    int status;
    String userName;
    String phone;
    String token;
    String urlPay;

    public DetailTransaction(long transactionId, long userId, Date transactionTime, String dateBegin, String dateFinish, int quota, int payMethode, int status, String userName, String phone, String token, String urlPay){
        this.transactionId=transactionId;
        this.userId=userId;
        this.transactionTime=transactionTime;
        this.dateBegin=dateBegin;
        this.dateFinish=dateFinish;
        this.quota=quota;
        this.payMethode=payMethode;
        this.status=status;
        this.userName=userName;
        this.phone=phone;
        this.token=token;
        this.urlPay=urlPay;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public long getUserId() {
        return userId;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }

    public String getDateBegin() {
        return dateBegin;
    }

    public String getDateFinish() {
        return dateFinish;
    }

    public int getPayMethode() {
        return payMethode;
    }

    public int getStatus() {
        return status;
    }

    public int getQuota() {
        return quota;
    }

    public String getUserName() {
        return userName;
    }

    public String getToken() {
        return token;
    }

    public String getPhone() {
        return phone;
    }

    public String getUrlPay() {
        return urlPay;
    }
}

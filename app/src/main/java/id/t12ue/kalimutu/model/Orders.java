package id.t12ue.kalimutu.model;

import androidx.room.TypeConverters;

import java.util.Date;

import id.t12ue.kalimutu.helpers.DateConverter;

public class Orders {
    long transactionId;
    @TypeConverters(DateConverter.class)
    Date transactionTime;
    int payMethode;
    int status;
    String userName;

    public Orders(long transactionId, Date transactionTime, int payMethode, int status, String userName){
        this.transactionId=transactionId;
        this.transactionTime=transactionTime;
        this.payMethode=payMethode;
        this.status=status;
        this.userName=userName;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }

    public int getPayMethode() {
        return payMethode;
    }

    public int getStatus() {
        return status;
    }

    public String getUserName() {
        return userName;
    }
}

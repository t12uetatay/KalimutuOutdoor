package id.t12ue.kalimutu.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.Date;

import id.t12ue.kalimutu.helpers.DateConverter;

@Entity(tableName = "transactions")
public class TransactionEntity implements Serializable {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "transactionId")
    long transactionId;
    @ColumnInfo(name = "userId")
    long userId;
    @TypeConverters(DateConverter.class)
    Date transactionTime;
    @ColumnInfo(name = "dateBegin")
    String dateBegin;
    @ColumnInfo(name = "dateFinish")
    String dateFinish;
    @ColumnInfo(name = "quota")
    int quota;
    @ColumnInfo(name = "payMethode")
    int payMethode;
    @ColumnInfo(name = "urlPay")
    String urlPay;
    @ColumnInfo(name = "status")
    int status;

    public TransactionEntity(long transactionId, long userId, Date transactionTime, String dateBegin, String dateFinish, int quota, int payMethode, String urlPay, int status){
        this.transactionId=transactionId;
        this.userId=userId;
        this.transactionTime=transactionTime;
        this.dateBegin=dateBegin;
        this.dateFinish=dateFinish;
        this.quota=quota;
        this.payMethode=payMethode;
        this.urlPay=urlPay;
        this.status=status;
    }

    public TransactionEntity(){}

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

    public int getQuota() {
        return quota;
    }

    public int getPayMethode() {
        return payMethode;
    }

    public String getUrlPay() {
        return urlPay;
    }

    public int getStatus() {
        return status;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setPayMethode(int payMethode) {
        this.payMethode = payMethode;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

    public void setTransactionTime(Date transactionTime) {
        this.transactionTime = transactionTime;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setUrlPay(String urlPay) {
        this.urlPay = urlPay;
    }

    public void setDateBegin(String dateBegin) {
        this.dateBegin = dateBegin;
    }

    public void setDateFinish(String dateFinish) {
        this.dateFinish = dateFinish;
    }
}

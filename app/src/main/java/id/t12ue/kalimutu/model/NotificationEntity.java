package id.t12ue.kalimutu.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

import id.t12ue.kalimutu.helpers.DateConverter;

@Entity(tableName = "notification")
public class NotificationEntity {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "notificationId")
    long notificationId;
    @ColumnInfo(name = "userId")
    long userId;
    @ColumnInfo(name = "transactionId")
    long transactionId;
    @TypeConverters(DateConverter.class)
    Date notificationTime;
    @ColumnInfo(name = "message")
    String message;
    @ColumnInfo(name = "notificationRead")
    boolean read;

    public NotificationEntity(long notificationId, long userId, long transactionId, Date notificationTime, String message, boolean read){
        this.notificationId=notificationId;
        this.userId=userId;
        this.transactionId=transactionId;
        this.notificationTime=notificationTime;
        this.message=message;
        this.read=read;
    }

    public NotificationEntity(){}

    public long getNotificationId() {
        return notificationId;
    }

    public long getUserId() {
        return userId;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public Date getNotificationTime() {
        return notificationTime;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return read;
    }

    public void setNotificationId(long notificationId) {
        this.notificationId = notificationId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setNotificationTime(Date notificationTime) {
        this.notificationTime = notificationTime;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}

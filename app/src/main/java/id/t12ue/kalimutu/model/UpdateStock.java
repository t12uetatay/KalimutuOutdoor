package id.t12ue.kalimutu.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

public class UpdateStock {
    long packageId;
    String packageName;
    long packagePrice;
    String description;
    long stock;
    int category;
    boolean arzip;

    public UpdateStock(long packageId, String packageName, long packagePrice, String description, long stock, int category, boolean arzip){
        this.packageId=packageId;
        this.packageName=packageName;
        this.packagePrice=packagePrice;
        this.description=description;
        this.stock=stock;
        this.category=category;
        this.arzip=arzip;
    }

    public long getPackageId() {
        return packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public long getPackagePrice() {
        return packagePrice;
    }

    public String getDescription() {
        return description;
    }

    public long getStock() {
        return stock;
    }

    public int getCategory() {
        return category;
    }

    public boolean isArzip() {
        return arzip;
    }

}

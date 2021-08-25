package id.t12ue.kalimutu.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "packages")
public class PackageEntity implements Serializable {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "packageId")
    long packageId;
    @ColumnInfo(name = "packageName")
    String packageName;
    @ColumnInfo(name = "packagePrice")
    long packagePrice;
    @ColumnInfo(name = "description")
    String description;
    @ColumnInfo(name = "stock")
    long stock;
    @ColumnInfo(name = "category")
    int category;
    @ColumnInfo(name = "arzip")
    boolean arzip;

    public PackageEntity(long packageId, String packageName, long packagePrice, String description, long stock, int category, boolean arzip){
        this.packageId=packageId;
        this.packageName=packageName;
        this.packagePrice=packagePrice;
        this.description=description;
        this.stock=stock;
        this.category=category;
        this.arzip=arzip;
    }

    public PackageEntity(){}

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

    public void setPackageId(long packageId) {
        this.packageId = packageId;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void setPackagePrice(long packagePrice) {
        this.packagePrice = packagePrice;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStock(long stock) {
        this.stock = stock;
    }

    public void setArzip(boolean arzip) {
        this.arzip = arzip;
    }
}

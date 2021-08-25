package id.t12ue.kalimutu.model;

import java.io.Serializable;

public class Packages implements Serializable {
    long packageId;
    String packageName;
    long packagePrice;
    String description;
    long stock;
    int category;
    boolean arzip;
    String imageUrl;

    public Packages(long packageId, String packageName, long packagePrice, String description, long stock, int category, boolean arzip, String imageUrl){
        this.packageId=packageId;
        this.packageName=packageName;
        this.packagePrice=packagePrice;
        this.description=description;
        this.stock=stock;
        this.category=category;
        this.arzip=arzip;
        this.imageUrl=imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }
}

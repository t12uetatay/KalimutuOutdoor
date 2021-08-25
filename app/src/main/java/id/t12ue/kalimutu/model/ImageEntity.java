package id.t12ue.kalimutu.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "image")
public class ImageEntity {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "imageId")
    long imageId;
    @ColumnInfo(name = "packageId")
    long packageId;
    @ColumnInfo(name = "imageUrl")
    String imageUrl;

    public ImageEntity(long imageId, long packageId, String imageUrl){
        this.imageId=imageId;
        this.packageId=packageId;
        this.imageUrl=imageUrl;
    }

    public ImageEntity(){}

    public long getImageId() {
        return imageId;
    }

    public long getPackageId() {
        return packageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public void setPackageId(long packageId) {
        this.packageId = packageId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

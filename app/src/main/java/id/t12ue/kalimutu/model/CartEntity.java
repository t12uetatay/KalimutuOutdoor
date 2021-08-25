package id.t12ue.kalimutu.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart")
public class CartEntity {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "cartId")
    long cartId;
    @ColumnInfo(name = "transactionId")
    long transactionId;
    @ColumnInfo(name = "userId")
    long userId;
    @ColumnInfo(name = "packageId")
    long packageId;
    @ColumnInfo(name = "quantity")
    int quantity;

    public CartEntity(long cartId, long transactionId, long userId, long packageId, int quantity){
        this.cartId=cartId;
        this.transactionId=transactionId;
        this.userId=userId;
        this.packageId=packageId;
        this.quantity=quantity;
    }

    public CartEntity(){}

    public long getCartId() {
        return cartId;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public long getUserId() {
        return userId;
    }

    public long getPackageId() {
        return packageId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setCartId(long cartId) {
        this.cartId = cartId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setPackageId(long packageId) {
        this.packageId = packageId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

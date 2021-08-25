package id.t12ue.kalimutu.model;

public class Cart {
    long cartId;
    long packageId;
    String packageName;
    long packagePrice;
    String description;
    long stock;
    String imageUrl;
    int quantity;

    public Cart(long cartId, long packageId, String packageName, long packagePrice, String description, long stock, String imageUrl, int quantity){
        this.cartId=cartId;
        this.packageId=packageId;
        this.packageName=packageName;
        this.packagePrice=packagePrice;
        this.description=description;
        this.stock=stock;
        this.imageUrl=imageUrl;
        this.quantity=quantity;
    }

    public long getCartId() {
        return cartId;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }
}

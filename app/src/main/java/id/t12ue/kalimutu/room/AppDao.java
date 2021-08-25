package id.t12ue.kalimutu.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import id.t12ue.kalimutu.model.BankEntity;
import id.t12ue.kalimutu.model.Cart;
import id.t12ue.kalimutu.model.CartEntity;
import id.t12ue.kalimutu.model.DetailTransaction;
import id.t12ue.kalimutu.model.ImageEntity;
import id.t12ue.kalimutu.model.NotificationEntity;
import id.t12ue.kalimutu.model.Orders;
import id.t12ue.kalimutu.model.PackageEntity;
import id.t12ue.kalimutu.model.Packages;
import id.t12ue.kalimutu.model.SendFor;
import id.t12ue.kalimutu.model.TransactionEntity;
import id.t12ue.kalimutu.model.TransactionRow;
import id.t12ue.kalimutu.model.UpdateStock;
import id.t12ue.kalimutu.model.UserEntity;


@Dao
public interface AppDao {

    /**
     * User
     * @param
     * @return
     */

    @Query("SELECT * FROM user WHERE userId=:id")
    UserEntity readUserById(long id);

    @Query("SELECT * FROM user WHERE role='A'")
    UserEntity readAdmin();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUser(UserEntity entity);

    /**
     * Category
     * @param
     * @return
     */

    @Query("SELECT * FROM bank WHERE arsip=0 ORDER BY bankName ")
    LiveData<List<BankEntity>> readAllBank();

    @Query("SELECT * FROM bank WHERE arsip=0 ORDER BY bankName ")
    List<BankEntity> readBank();

    @Query("SELECT * FROM bank WHERE bankId=:id")
    BankEntity readBankById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertBank(BankEntity entity);

    /**
     * Package
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertImage(ImageEntity entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertPackage(PackageEntity entity);

    @Query("SELECT p.packageId, p.packageName, p.packagePrice, p.description, p.stock, p.category, p.arzip, ifnull((SELECT imageUrl FROM image AS i WHERE i.packageId=p.packageId ORDER BY i.imageId DESC LIMIT 1), 0) AS imageUrl FROM packages AS p WHERE p.arzip=0 ORDER BY packageName ")
    LiveData<List<Packages>> readAllPackage();

    @Query("SELECT p.packageId, p.packageName, p.packagePrice, p.description, p.stock, p.category, p.arzip, ifnull((SELECT imageUrl FROM image AS i WHERE i.packageId=p.packageId ORDER BY i.imageId DESC LIMIT 1), 0) AS imageUrl FROM packages AS p WHERE category=:cat AND p.arzip=0 ")
    LiveData<List<Packages>> readPackageByCategory(int cat);


    @Query("SELECT * FROM packages WHERE packageId=:id")
    PackageEntity readPackageById(long id);

    @Query("SELECT * FROM image WHERE packageId=:k")
    List<ImageEntity>getImageEntity(long k);

    /**
     * Cart
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCart(CartEntity entity);

    @Query("SELECT * FROM cart WHERE userId=:u AND packageId=:p")
    CartEntity readCartUser(long u, long p);

    @Query("SELECT c.cartId, p.packageId, p.packageName, p.packagePrice, p.description, p.stock, ifnull((SELECT imageUrl FROM image AS i WHERE i.packageId=p.packageId LIMIT 1), 0) AS imageUrl, c.quantity " +
            "FROM cart AS c " +
            "INNER JOIN packages AS p ON p.packageId=c.packageId " +
            "WHERE c.userId=:id AND c.transactionId=-1 ")
    LiveData<List<Cart>> readShoppingCart(long id);

    @Query("SELECT * FROM cart WHERE userId=:id AND transactionId=-1")
    List<CartEntity>readCart(long id);

    @Query("DELETE FROM cart WHERE cartId=:id")
    void deleteCartById(long id);

    @Query("SELECT ifnull((SUM((packages.packagePrice*cart.quantity))), 0) AS total FROM cart INNER JOIN packages ON packages.packageId=cart.packageId WHERE cart.userId=:id AND packages.category=0 AND cart.transactionId=-1 ")
    LiveData<Long> sumCart0(long id);

    @Query("SELECT ifnull((SUM((packages.packagePrice*cart.quantity))), 0) AS total FROM cart INNER JOIN packages ON packages.packageId=cart.packageId WHERE cart.userId=:id AND packages.category=1 AND cart.transactionId=-1 ")
    LiveData<Long> sumCart1(long id);

    @Query("SELECT ifnull((COUNT(*)), 0) FROM cart WHERE userId=:id AND transactionId=-1 ")
    LiveData<Long> countCart(long id);

    @Query("SELECT transactionId FROM transactions WHERE transactionId LIKE:currentDate ORDER BY transactionId DESC LIMIT 1")
    LiveData<Long> readTransactionId(String currentDate);

    @Query("SELECT userId, token FROM user WHERE role='A'")
    SendFor getTokenAdmin();

    @Query("SELECT userId, token FROM user WHERE userId=:id")
    SendFor getTokenUser(long id);

    /**
     * Trs
     */
    @Query("SELECT t.transactionId, t.transactionTime, t.payMethode, t.status, u.userName " +
            "FROM transactions AS t " +
            "INNER JOIN user AS u ON u.userId=t.userId " +
            "WHERE t.userId=:id ORDER BY transactionTime DESC ")
    LiveData<List<Orders>> readMyOrders(long id);

    @Query("SELECT t.transactionId, t.transactionTime, t.payMethode, t.status, u.userName " +
            "FROM transactions AS t " +
            "INNER JOIN user AS u ON u.userId=t.userId " +
            "ORDER BY transactionTime DESC ")
    LiveData<List<Orders>> readAllOrders();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTransaction(TransactionEntity entity);

    @Query("SELECT c.cartId, p.packageId, p.packageName, p.packagePrice, p.description, p.stock, ifnull((SELECT imageUrl FROM image AS i WHERE i.packageId=p.packageId LIMIT 1), 0) AS imageUrl, c.quantity " +
            "FROM cart AS c " +
            "INNER JOIN packages AS p ON p.packageId=c.packageId " +
            "WHERE c.transactionId=:id")
    LiveData<List<Cart>> readShopping(long id);

    @Query("SELECT t.transactionId, t.userId, t.transactionTime, t.dateBegin, t.dateFinish, t.quota, t.payMethode, t.status, u.userName, u.phone, u.token, t.urlPay " +
            "FROM transactions AS t " +
            "INNER JOIN user AS u ON u.userId=t.userId " +
            "WHERE t.transactionId=:id")
    LiveData<DetailTransaction> readDetailTransaction(long id);

    @Query("SELECT * FROM transactions WHERE transactionId=:id")
    TransactionEntity readTransaction(long id);

    @Query("SELECT p.packageId, p.packageName, p.packagePrice, p.description, (p.stock-c.quantity) AS stock, p.category, p.arzip " +
            "FROM cart AS c " +
            "INNER JOIN packages AS p ON p.packageId=c.packageId WHERE c.transactionId=:id ")
    List<UpdateStock> updateStock(long id);

    @Query("SELECT p.packageId, p.packageName, p.packagePrice, p.description, (p.stock+c.quantity) AS stock, p.category, p.arzip " +
            "FROM cart AS c " +
            "INNER JOIN packages AS p ON p.packageId=c.packageId WHERE c.transactionId=:id ")
    List<UpdateStock> reStock(long id);


    @Query("SELECT ifnull((SUM((p.packagePrice*c.quantity*t.quota))), 0) AS total " +
            "FROM cart AS c " +
            "INNER JOIN packages AS p ON p.packageId=c.packageId " +
            "INNER JOIN transactions AS t ON t.transactionId=c.transactionId " +
            "WHERE t.transactionId=:id AND p.category=0 ")
    LiveData<Long> sumTotalSewa(long id);

    @Query("SELECT ifnull((SUM((p.packagePrice*c.quantity))), 0) AS total " +
            "FROM cart AS c " +
            "INNER JOIN packages AS p ON p.packageId=c.packageId " +
            "INNER JOIN transactions AS t ON t.transactionId=c.transactionId " +
            "WHERE t.transactionId=:id AND p.category=1 ")
    LiveData<Long> sumTotalTrip(long id);

    /**
     * Notif
     */
    @Query("SELECT * FROM notification WHERE userId=:id ORDER BY notificationTime DESC ")
    LiveData<List<NotificationEntity>> readNotification(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertNotification(NotificationEntity entity);


}

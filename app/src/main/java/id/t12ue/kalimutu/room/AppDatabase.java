package id.t12ue.kalimutu.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import id.t12ue.kalimutu.helpers.DateConverter;
import id.t12ue.kalimutu.model.BankEntity;
import id.t12ue.kalimutu.model.CartEntity;
import id.t12ue.kalimutu.model.ImageEntity;
import id.t12ue.kalimutu.model.NotificationEntity;
import id.t12ue.kalimutu.model.PackageEntity;
import id.t12ue.kalimutu.model.TransactionEntity;
import id.t12ue.kalimutu.model.UserEntity;


@Database(entities = {
        PackageEntity.class,
        BankEntity.class,
        ImageEntity.class,
        UserEntity.class,
        NotificationEntity.class,
        CartEntity.class,
        TransactionEntity.class
}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract AppDao appDao();
    private static AppDatabase INSTANCE;
    private static String DATABASE_NAME="mydata.db";

    public synchronized static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME)
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();

                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     *     @Override
     *     protected void onDestroy() {
     *         super.onDestroy();
     *         AppDatabase.destroyInstance();
     *     }
     *@Insert(onConflict = REPLACE)
     *         void insertAllUser(User... mUsersList);
     *     @Delete
     */
}
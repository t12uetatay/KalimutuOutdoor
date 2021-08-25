package id.t12ue.kalimutu.repo;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import id.t12ue.kalimutu.model.Cart;
import id.t12ue.kalimutu.model.CartEntity;
import id.t12ue.kalimutu.model.ImageEntity;
import id.t12ue.kalimutu.model.Params;
import id.t12ue.kalimutu.model.SendFor;
import id.t12ue.kalimutu.room.AppDao;
import id.t12ue.kalimutu.room.AppDatabase;

public class CartRepository {
    private AppDao aDao;

    public CartRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        aDao = db.appDao();
    }

    public LiveData<List<Cart>> readAll(long id) {
        LiveData<List<Cart>> liveData = aDao.readShoppingCart(id);
        return liveData;
    }

    public List<CartEntity> readCart(long id) throws ExecutionException, InterruptedException {
        return new readCartAsync(aDao).execute(id).get();
    }

    private static class readCartAsync extends AsyncTask<Long, Void, List<CartEntity>> {

        private AppDao dbOperation;

        readCartAsync(AppDao db) {
            dbOperation = db;
        }

        @Override
        protected List<CartEntity> doInBackground(Long... id) {
            return dbOperation.readCart(id[0]);
        }
    }

    public CartEntity readById(Params params) throws ExecutionException, InterruptedException {
        return new readByIdAsync(aDao).execute(params).get();
    }

    private static class readByIdAsync extends AsyncTask<Params, Void, CartEntity> {

        private AppDao mAppDao;

        readByIdAsync(AppDao appDao) {
            mAppDao = appDao;
        }

        @Override
        protected CartEntity doInBackground(Params... p) {
            return mAppDao.readCartUser(p[0].getP1(), p[0].getP2());
        }
    }

    public void insert(CartEntity entity) {
        new insertAsync(aDao).execute(entity);
    }

    private static class insertAsync extends AsyncTask<CartEntity, Void, Long> {

        private AppDao mAppDao;

        insertAsync(AppDao appDao) {
            mAppDao = appDao;
        }

        @Override
        protected Long doInBackground(CartEntity... entity) {
            long id = mAppDao.insertCart(entity[0]);
            return id;
        }
    }

    public void delete(long id) {
        new deleteAsync(aDao).execute(id);
    }

    private static class deleteAsync extends AsyncTask<Long, Void, Void> {

        private AppDao mAppDao;

        deleteAsync(AppDao appDao) {
            mAppDao = appDao;
        }

        @Override
        protected Void doInBackground(Long... id) {
            mAppDao.deleteCartById(id[0]);
            return null;
        }
    }

    public LiveData<Long> sumCart0(long id) {
        LiveData<Long> liveData = aDao.sumCart0(id);
        return liveData;
    }

    public LiveData<Long> sumCart1(long id) {
        LiveData<Long> liveData = aDao.sumCart1(id);
        return liveData;
    }

    public LiveData<Long> countCart(long id) {
        LiveData<Long> liveData = aDao.countCart(id);
        return liveData;
    }

    public LiveData<Long> readTransactionId(String current) {
        LiveData<Long> liveData = aDao.readTransactionId(current);
        return liveData;
    }

    public SendFor getTokenAdmin() throws ExecutionException, InterruptedException {
        return new getTokenAdminAsync(aDao).execute().get();
    }

    private static class getTokenAdminAsync extends AsyncTask<Long, Void, SendFor> {

        private AppDao mAppDao;

        getTokenAdminAsync(AppDao appDao) {
            mAppDao = appDao;
        }

        @Override
        protected SendFor doInBackground(Long... p) {
            return mAppDao.getTokenAdmin();
        }
    }


}

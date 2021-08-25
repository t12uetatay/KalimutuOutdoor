package id.t12ue.kalimutu.repo;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import id.t12ue.kalimutu.model.BankEntity;
import id.t12ue.kalimutu.model.Cart;
import id.t12ue.kalimutu.model.CartEntity;
import id.t12ue.kalimutu.model.DetailTransaction;
import id.t12ue.kalimutu.model.Orders;
import id.t12ue.kalimutu.model.Params;
import id.t12ue.kalimutu.model.SendFor;
import id.t12ue.kalimutu.model.TransactionEntity;
import id.t12ue.kalimutu.model.TransactionRow;
import id.t12ue.kalimutu.model.UpdateStock;
import id.t12ue.kalimutu.room.AppDao;
import id.t12ue.kalimutu.room.AppDatabase;

public class TransactionRepository {
    private AppDao aDao;

    public TransactionRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        aDao = db.appDao();
    }

    public LiveData<List<Orders>> readMyOrders(long id, String rl) {
        LiveData<List<Orders>> liveData;
        if (rl.equals("A")){
            liveData = aDao.readAllOrders();
        } else {
            liveData = aDao.readMyOrders(id);
        }
        return liveData;
    }


    public LiveData<List<Cart>> readShopping(long id) {
        LiveData<List<Cart>> liveData = aDao.readShopping(id);
        return liveData;
    }

    public LiveData<DetailTransaction> readDetailTransaction(long id) {
        LiveData<DetailTransaction> liveData = aDao.readDetailTransaction(id);
        return liveData;
    }

    public List<BankEntity> readBank() throws ExecutionException, InterruptedException {
        return new readBankAsync(aDao).execute().get();
    }

    private static class readBankAsync extends AsyncTask<Long, Void, List<BankEntity>> {

        private AppDao dbOperation;

        readBankAsync(AppDao db) {
            dbOperation = db;
        }

        @Override
        protected List<BankEntity> doInBackground(Long... id) {
            return dbOperation.readBank();
        }
    }

    public List<UpdateStock> updateStock(long id) throws ExecutionException, InterruptedException {
        return new updateStockAsync(aDao).execute(id).get();
    }

    private static class updateStockAsync extends AsyncTask<Long, Void, List<UpdateStock>> {

        private AppDao dbOperation;

        updateStockAsync(AppDao db) {
            dbOperation = db;
        }

        @Override
        protected List<UpdateStock> doInBackground(Long... id) {
            return dbOperation.updateStock(id[0]);
        }
    }

    public List<UpdateStock> reStock(long id) throws ExecutionException, InterruptedException {
        return new reStockAsync(aDao).execute(id).get();
    }

    private static class reStockAsync extends AsyncTask<Long, Void, List<UpdateStock>> {

        private AppDao dbOperation;

        reStockAsync(AppDao db) {
            dbOperation = db;
        }

        @Override
        protected List<UpdateStock> doInBackground(Long... id) {
            return dbOperation.reStock(id[0]);
        }
    }

    public TransactionEntity readById(long id) throws ExecutionException, InterruptedException {
        return new readByIdAsync(aDao).execute(id).get();
    }

    private static class readByIdAsync extends AsyncTask<Long, Void, TransactionEntity> {

        private AppDao mAppDao;

        readByIdAsync(AppDao appDao) {
            mAppDao = appDao;
        }

        @Override
        protected TransactionEntity doInBackground(Long... id) {
            return mAppDao.readTransaction(id[0]);
        }
    }

    public void insert(TransactionEntity entity) {
        new insertAsync(aDao).execute(entity);
    }

    private static class insertAsync extends AsyncTask<TransactionEntity, Void, Long> {

        private AppDao mAppDao;

        insertAsync(AppDao appDao) {
            mAppDao = appDao;
        }

        @Override
        protected Long doInBackground(TransactionEntity... entity) {
            long id = mAppDao.insertTransaction(entity[0]);
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

    public LiveData<Long> sumTotalSewa(long id) {
        LiveData<Long> liveData = aDao.sumTotalSewa(id);
        return liveData;
    }

    public LiveData<Long> sumTotalTrip(long id) {
        LiveData<Long> liveData = aDao.sumTotalTrip(id);
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

    public SendFor getTokenUser(long id) throws ExecutionException, InterruptedException {
        return new getTokenUserAsync(aDao).execute(id).get();
    }

    private static class getTokenUserAsync extends AsyncTask<Long, Void, SendFor> {

        private AppDao mAppDao;

        getTokenUserAsync(AppDao appDao) {
            mAppDao = appDao;
        }

        @Override
        protected SendFor doInBackground(Long... p) {
            return mAppDao.getTokenUser(p[0]);
        }
    }


}

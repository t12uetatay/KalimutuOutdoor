package id.t12ue.kalimutu.repo;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import id.t12ue.kalimutu.model.BankEntity;
import id.t12ue.kalimutu.room.AppDao;
import id.t12ue.kalimutu.room.AppDatabase;

public class BankRepository {
    private AppDao aDao;

    public BankRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        aDao = db.appDao();
    }

    public LiveData<List<BankEntity>> readAll() {
        LiveData<List<BankEntity>> liveData = aDao.readAllBank();
        return liveData;
    }

    public BankEntity readById(long id) throws ExecutionException, InterruptedException {
        return new readByIdAsync(aDao).execute(id).get();
    }

    private static class readByIdAsync extends AsyncTask<Long, Void, BankEntity> {

        private AppDao mAppDao;

        readByIdAsync(AppDao appDao) {
            mAppDao = appDao;
        }

        @Override
        protected BankEntity doInBackground(Long... id) {
            return mAppDao.readBankById(id[0]);
        }
    }

    public void insert(BankEntity entity) {
        new insertAsync(aDao).execute(entity);
    }

    private static class insertAsync extends AsyncTask<BankEntity, Void, Long> {

        private AppDao mAppDao;

        insertAsync(AppDao appDao) {
            mAppDao = appDao;
        }

        @Override
        protected Long doInBackground(BankEntity... entity) {
            long id = mAppDao.insertBank(entity[0]);
            return id;
        }
    }


}

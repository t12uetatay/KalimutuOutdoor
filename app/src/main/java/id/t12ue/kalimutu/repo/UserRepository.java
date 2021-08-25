package id.t12ue.kalimutu.repo;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import id.t12ue.kalimutu.model.UserEntity;
import id.t12ue.kalimutu.room.AppDao;
import id.t12ue.kalimutu.room.AppDatabase;

public class UserRepository {
    private AppDao aDao;

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        aDao = db.appDao();
    }

    public UserEntity readById(long id) throws ExecutionException, InterruptedException {
        return new readByIdAsync(aDao).execute(id).get();
    }

    private static class readByIdAsync extends AsyncTask<Long, Void, UserEntity> {

        private AppDao mAppDao;

        readByIdAsync(AppDao appDao) {
            mAppDao = appDao;
        }

        @Override
        protected UserEntity doInBackground(Long... id) {
            return mAppDao.readUserById(id[0]);
        }
    }

    public UserEntity readAdmin() throws ExecutionException, InterruptedException {
        return new readAdminAsync(aDao).execute().get();
    }

    private static class readAdminAsync extends AsyncTask<Long, Void, UserEntity> {

        private AppDao mAppDao;

        readAdminAsync(AppDao appDao) {
            mAppDao = appDao;
        }

        @Override
        protected UserEntity doInBackground(Long... id) {
            return mAppDao.readAdmin();
        }
    }

    public void insert(UserEntity entity) {
        new insertAsync(aDao).execute(entity);
    }

    private static class insertAsync extends AsyncTask<UserEntity, Void, Long> {

        private AppDao mAppDao;

        insertAsync(AppDao appDao) {
            mAppDao = appDao;
        }

        @Override
        protected Long doInBackground(UserEntity... entity) {
            long id = mAppDao.insertUser(entity[0]);
            return id;
        }
    }


}

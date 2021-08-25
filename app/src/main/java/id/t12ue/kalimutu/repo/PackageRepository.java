package id.t12ue.kalimutu.repo;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import id.t12ue.kalimutu.model.ImageEntity;
import id.t12ue.kalimutu.model.PackageEntity;
import id.t12ue.kalimutu.model.Packages;
import id.t12ue.kalimutu.room.AppDao;
import id.t12ue.kalimutu.room.AppDatabase;

public class PackageRepository {
    private AppDao aDao;

    public PackageRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        aDao = db.appDao();
    }

    public LiveData<List<Packages>> readAllPackage() {
        LiveData<List<Packages>> liveData = aDao.readAllPackage();
        return liveData;
    }

    public LiveData<List<Packages>> readPackageByCategory(int cat) {
        LiveData<List<Packages>> liveData = aDao.readPackageByCategory(cat);
        return liveData;
    }

    public List<ImageEntity> getImageEntity(long idm) throws ExecutionException, InterruptedException {
        return new getImageEntityAsync(aDao).execute(idm).get();
    }

    private static class getImageEntityAsync extends AsyncTask<Long, Void, List<ImageEntity>> {

        private AppDao dbOperation;

        getImageEntityAsync(AppDao db) {
            dbOperation = db;
        }

        @Override
        protected List<ImageEntity> doInBackground(Long... idm) {
            return dbOperation.getImageEntity(idm[0]);
        }
    }

    public PackageEntity readById(long id) throws ExecutionException, InterruptedException {
        return new readByIdAsync(aDao).execute(id).get();
    }

    private static class readByIdAsync extends AsyncTask<Long, Void, PackageEntity> {

        private AppDao mAppDao;

        readByIdAsync(AppDao appDao) {
            mAppDao = appDao;
        }

        @Override
        protected PackageEntity doInBackground(Long... id) {
            return mAppDao.readPackageById(id[0]);
        }
    }

    public void insertPackage(PackageEntity entity) {
        new insertPackageAsync(aDao).execute(entity);
    }

    private static class insertPackageAsync extends AsyncTask<PackageEntity, Void, Long> {

        private AppDao mAppDao;

        insertPackageAsync(AppDao appDao) {
            mAppDao = appDao;
        }

        @Override
        protected Long doInBackground(PackageEntity... entity) {
            long id = mAppDao.insertPackage(entity[0]);
            return id;
        }
    }

    public void insertImage(ImageEntity entity) {
        new insertImageAsync(aDao).execute(entity);
    }

    private static class insertImageAsync extends AsyncTask<ImageEntity, Void, Long> {

        private AppDao mAppDao;

        insertImageAsync(AppDao appDao) {
            mAppDao = appDao;
        }

        @Override
        protected Long doInBackground(ImageEntity... entity) {
            long id = mAppDao.insertImage(entity[0]);
            return id;
        }
    }


}

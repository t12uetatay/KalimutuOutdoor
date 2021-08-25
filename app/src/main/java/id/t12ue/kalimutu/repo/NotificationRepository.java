package id.t12ue.kalimutu.repo;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import id.t12ue.kalimutu.model.BankEntity;
import id.t12ue.kalimutu.model.NotificationEntity;
import id.t12ue.kalimutu.room.AppDao;
import id.t12ue.kalimutu.room.AppDatabase;

public class NotificationRepository {
    private AppDao aDao;

    public NotificationRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        aDao = db.appDao();
    }

    public LiveData<List<NotificationEntity>> readNotification(long id) {
        LiveData<List<NotificationEntity>> liveData = aDao.readNotification(id);
        return liveData;
    }

    public void insert(NotificationEntity entity) {
        new insertAsync(aDao).execute(entity);
    }

    private static class insertAsync extends AsyncTask<NotificationEntity, Void, Long> {

        private AppDao mAppDao;

        insertAsync(AppDao appDao) {
            mAppDao = appDao;
        }

        @Override
        protected Long doInBackground(NotificationEntity... entity) {
            long id = mAppDao.insertNotification(entity[0]);
            return id;
        }
    }


}

package id.t12ue.kalimutu.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import id.t12ue.kalimutu.model.BankEntity;
import id.t12ue.kalimutu.model.NotificationEntity;
import id.t12ue.kalimutu.repo.BankRepository;
import id.t12ue.kalimutu.repo.NotificationRepository;


public class NotificationViewModel extends AndroidViewModel {
    private NotificationRepository mRepository;

    public NotificationViewModel(@NonNull Application application) {
        super(application);
        mRepository = new NotificationRepository(application);
    }

    public LiveData<List<NotificationEntity>> readNotification(long id) {
        LiveData<List<NotificationEntity>> liveData = mRepository.readNotification(id);
        return liveData;
    }


    public void insert(NotificationEntity entity) {
        mRepository.insert(entity);
    }

}

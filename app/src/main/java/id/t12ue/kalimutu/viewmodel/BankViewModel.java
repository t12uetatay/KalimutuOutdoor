package id.t12ue.kalimutu.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import id.t12ue.kalimutu.model.BankEntity;
import id.t12ue.kalimutu.repo.BankRepository;


public class BankViewModel extends AndroidViewModel {
    private BankRepository mRepository;

    public BankViewModel(@NonNull Application application) {
        super(application);
        mRepository = new BankRepository(application);
    }

    public LiveData<List<BankEntity>> readAll() {
        LiveData<List<BankEntity>> liveData = mRepository.readAll();
        return liveData;
    }


    public BankEntity readById(long id) throws ExecutionException, InterruptedException {
        return mRepository.readById(id);
    }

    public void insert(BankEntity entity) {
        mRepository.insert(entity);
    }

}

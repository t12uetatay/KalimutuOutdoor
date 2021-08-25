package id.t12ue.kalimutu.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import id.t12ue.kalimutu.model.UserEntity;
import id.t12ue.kalimutu.repo.UserRepository;


public class UserViewModel extends AndroidViewModel {
    private UserRepository mRepository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        mRepository = new UserRepository(application);
    }


    public UserEntity readById(long id) throws ExecutionException, InterruptedException {
        return mRepository.readById(id);
    }

    public UserEntity readAdmin() throws ExecutionException, InterruptedException {
        return mRepository.readAdmin();
    }

    public void insert(UserEntity entity) {
        mRepository.insert(entity);
    }

}

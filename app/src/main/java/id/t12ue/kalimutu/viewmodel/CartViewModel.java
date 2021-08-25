package id.t12ue.kalimutu.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import id.t12ue.kalimutu.model.Cart;
import id.t12ue.kalimutu.model.CartEntity;
import id.t12ue.kalimutu.model.ImageEntity;
import id.t12ue.kalimutu.model.Params;
import id.t12ue.kalimutu.model.SendFor;
import id.t12ue.kalimutu.repo.CartRepository;


public class CartViewModel extends AndroidViewModel {
    private CartRepository mRepository;

    public CartViewModel(@NonNull Application application) {
        super(application);
        mRepository = new CartRepository(application);
    }

    public LiveData<List<Cart>> readAll(long id) {
        LiveData<List<Cart>> liveData = mRepository.readAll(id);
        return liveData;
    }

    public List<CartEntity> readCart(long id) throws ExecutionException, InterruptedException {
        return mRepository.readCart(id);
    }


    public CartEntity readById(Params params) throws ExecutionException, InterruptedException {
        return mRepository.readById(params);
    }

    public void insert(CartEntity entity) {
        mRepository.insert(entity);
    }

    public void delete(long id) {
        mRepository.delete(id);
    }

    public LiveData<Long> sumCart0(long id) {
        LiveData<Long> liveData = mRepository.sumCart0(id);
        return liveData;
    }

    public LiveData<Long> sumCart1(long id) {
        LiveData<Long> liveData = mRepository.sumCart1(id);
        return liveData;
    }

    public LiveData<Long> countCart(long id) {
        LiveData<Long> liveData = mRepository.countCart(id);
        return liveData;
    }

    public LiveData<Long> readTransactionId(String current) {
        LiveData<Long> liveData = mRepository.readTransactionId(current);
        return liveData;
    }

    public SendFor getTokenAdmin() throws ExecutionException, InterruptedException {
        return mRepository.getTokenAdmin();
    }

}

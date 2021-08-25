package id.t12ue.kalimutu.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
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
import id.t12ue.kalimutu.repo.CartRepository;
import id.t12ue.kalimutu.repo.TransactionRepository;


public class TransactionViewModel extends AndroidViewModel {
    private TransactionRepository mRepository;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        mRepository = new TransactionRepository(application);
    }

    public LiveData<List<Orders>> readMyOrders(long id, String rl) {
        LiveData<List<Orders>> liveData = mRepository.readMyOrders(id, rl);
        return liveData;
    }

    public LiveData<List<Cart>> readShopping(long id) {
        LiveData<List<Cart>> liveData = mRepository.readShopping(id);
        return liveData;
    }

    public LiveData<DetailTransaction> readDetailTransaction(long id) {
        LiveData<DetailTransaction> liveData = mRepository.readDetailTransaction(id);
        return liveData;
    }

    public List<BankEntity> readBank() throws ExecutionException, InterruptedException {
        return mRepository.readBank();
    }

    public List<UpdateStock> updateStock(long id) throws ExecutionException, InterruptedException {
        return mRepository.updateStock(id);
    }

    public List<UpdateStock> reStock(long id) throws ExecutionException, InterruptedException {
        return mRepository.reStock(id);
    }

    public TransactionEntity readById(long id) throws ExecutionException, InterruptedException {
        return mRepository.readById(id);
    }

    public void insert(TransactionEntity entity) {
        mRepository.insert(entity);
    }

    public void delete(long id) {
        mRepository.delete(id);
    }

    public LiveData<Long> sumTotalSewa(long id) {
        LiveData<Long> liveData = mRepository.sumTotalSewa(id);
        return liveData;
    }

    public LiveData<Long> sumTotalTrip(long id) {
        LiveData<Long> liveData = mRepository.sumTotalTrip(id);
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

    public SendFor getTokenUser(long id) throws ExecutionException, InterruptedException {
        return mRepository.getTokenUser(id);
    }

}

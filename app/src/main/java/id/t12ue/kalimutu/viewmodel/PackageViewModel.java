package id.t12ue.kalimutu.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import id.t12ue.kalimutu.model.ImageEntity;
import id.t12ue.kalimutu.model.PackageEntity;
import id.t12ue.kalimutu.model.Packages;
import id.t12ue.kalimutu.repo.PackageRepository;


public class PackageViewModel extends AndroidViewModel {
    private PackageRepository mRepository;

    public PackageViewModel(@NonNull Application application) {
        super(application);
        mRepository = new PackageRepository(application);
    }

    public LiveData<List<Packages>> readAllPackage() {
        LiveData<List<Packages>> liveData = mRepository.readAllPackage();
        return liveData;
    }

    public LiveData<List<Packages>> readPackageByCategory(int cat) {
        LiveData<List<Packages>> liveData = mRepository.readPackageByCategory(cat);
        return liveData;
    }

    public List<ImageEntity> getImageEntity(long idm) throws ExecutionException, InterruptedException {
        return mRepository.getImageEntity(idm);
    }


    public PackageEntity readById(long id) throws ExecutionException, InterruptedException {
        return mRepository.readById(id);
    }

    public void insertPackage(PackageEntity entity) {
        mRepository.insertPackage(entity);
    }

    public void insertImage(ImageEntity entity) {
        mRepository.insertImage(entity);
    }

}

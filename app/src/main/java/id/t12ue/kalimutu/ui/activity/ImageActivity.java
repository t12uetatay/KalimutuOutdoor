package id.t12ue.kalimutu.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import id.t12ue.kalimutu.R;
import id.t12ue.kalimutu.adapter.ImageAdapter;
import id.t12ue.kalimutu.helpers.Cons;
import id.t12ue.kalimutu.helpers.Space;
import id.t12ue.kalimutu.model.ImageEntity;
import id.t12ue.kalimutu.viewmodel.PackageViewModel;

public class ImageActivity extends AppCompatActivity implements ImageAdapter.AdapterListener {
    private ImageAdapter mAdapter;
    private RecyclerView recyclerView;
    private static final int REQUEST_CODE_CHOOSE = 23;
    private List<ImageEntity> list = new ArrayList<>();
    private FirebaseDatabase database;
    private DatabaseReference df;
    private StorageReference storage;
    private ProgressDialog progressDialog;
    private String dt="";
    private int cout=0;
    int a=1,  size=4, sz;
    private String edit;
    int max=0;
    private PackageViewModel viewModelPackage;
    private ImageEntity entity;
    private MaterialButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        init();
    }

    @SuppressLint("CheckResult")
    public void init() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Upload Gambar");
        toolbar.setNavigationIcon(R.drawable.ic_baseline_close_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mAdapter = new ImageAdapter(this, this);
        recyclerView = findViewById(R.id.recyclerView);
        btn = findViewById(R.id.btn);
        database = FirebaseDatabase.getInstance();
        viewModelPackage = ViewModelProviders.of(this).get(PackageViewModel.class);
        storage= FirebaseStorage.getInstance().getReference("assets");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,
                2,//span count no of items in single row
                GridLayoutManager.VERTICAL,//Orientation
                false);//reverse scrolling of recyclerview
        //set layout manager as gridLayoutManager
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.addItemDecoration(new Space(2, 3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploaded....");
        progressDialog.setCancelable(false);

        dt = getIntent().getStringExtra("data");
        edit = getIntent().getStringExtra("edit");
        if (edit.equals("Y")){
            try {
                List<ImageEntity> list = viewModelPackage.getImageEntity(Long.parseLong(dt));
                mAdapter.setDataList(list);
                sz=size-list.size();
                if (sz==0){
                    btn.setEnabled(false);
                } else {
                    btn.setEnabled(true);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            RxPermissions rxPermissions = new RxPermissions(ImageActivity.this);
            rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(aBoolean -> {
                        if (aBoolean) {
                            startAction(4);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.permission_request_denied, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }, Throwable::printStackTrace);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                RxPermissions rxPermissions = new RxPermissions(ImageActivity.this);
                rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                startAction(sz);
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.permission_request_denied, Toast.LENGTH_LONG)
                                        .show();
                            }
                        }, Throwable::printStackTrace);
            }
        });
    }

    private void startAction(int i){
        Matisse.from(ImageActivity.this)
                .choose(MimeType.ofImage(), false)
                .theme(R.style.Matisse_Dracula)
                .countable(true)
                .capture(true)
                .captureStrategy(
                        new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider", "test"))
                .maxSelectable(i)
                //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(
                        getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .setOnSelectedListener((uriList, pathList) -> {
                    //Log.e("onSelected", "onSelected: pathList=" + pathList);
                })
                .showSingleMediaType(true)
                .originalEnable(true)
                .maxOriginalSize(10)
                .autoHideToolbarOnSingleTap(true)
                .setOnCheckedListener(isChecked -> {
                    //Log.e("isChecked", "onCheck: isChecked=" + isChecked);
                })
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            for (int i=0; i<Matisse.obtainPathResult(data).size(); i++){
                Uri uri = Uri.fromFile(new File(Matisse.obtainPathResult(data).get(i)));
                //File file = new File(Matisse.obtainPathResult(data).get(i));
                cout=Matisse.obtainPathResult(data).size();
                Crop(uri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                upload(result.getUri(), Cons.getRandom(), cout);
                list.add(new ImageEntity(
                        0, Long.parseLong(dt), result.getUri().toString()
                ));
            }
        }
        mAdapter.setDataList(list);
    }

    private void Crop(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAspectRatio(3,4)
                .start(this);
    }


    private void upload(Uri uri, String filename, int idx) {
        if (uri!=null){
            progressDialog.setMessage("Uploaded ("+a+"/"+idx+")");
            progressDialog.show();
            final StorageReference storageReference = storage.child(filename);
            UploadTask uploadTask = storageReference.putFile(uri);
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull final Task<Uri> task) {
                    if (task.isSuccessful()) {
                        if (entity!=null){
                            deleteFile(entity.getImageUrl());
                            storeData(entity.getImageId(), task.getResult().toString());
                        } else {
                            incrementCounter(task.getResult().toString());
                        }
                        a++;
                        int b;
                        if (a==5){
                            b=4;
                        } else {
                            b=a;
                        }
                        progressDialog.setMessage("Uploaded ("+b+"/"+idx+")");
                        Toast.makeText(getApplicationContext(), "Upload Succsessful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    public void incrementCounter(String url) {
        //progressDialog.show();
        df = database.getReference("c"+ Cons.KEY_IMAGE);
        df.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(final MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error != null) {
                    Toast.makeText(getApplicationContext(), "Firebase counter increment failed.", Toast.LENGTH_SHORT).show();
                } else {
                    storeData((Long)currentData.getValue(), url);
                }
            }

        });
    }


    private void storeData(long id, String url){
        ImageEntity entity = new ImageEntity(
                id,
                Long.parseLong(dt),
                url
        );
        df = database.getReference(Cons.KEY_IMAGE);
        df.child(String.valueOf(id)).setValue(entity).addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void mVoid){
                //progressDialog.hide();
                if (a>cout){
                    progressDialog.hide();
                    onBackPressed();
                }
            }
        });
    }

    public boolean deleteFile(String mImageUrl){ /* Delete file from firebase storage */
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(mImageUrl);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("delete","true");
            }
        });
        return false;
    }


    @SuppressLint("CheckResult")
    @Override
    public void onClick(ImageEntity enty) {
        entity=enty;
        startAction(1);
    }

}
package id.t12ue.kalimutu.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import id.t12ue.kalimutu.R;
import id.t12ue.kalimutu.adapter.PaySliderAdapter;
import id.t12ue.kalimutu.helpers.Cons;
import id.t12ue.kalimutu.model.BankEntity;
import id.t12ue.kalimutu.model.NotificationEntity;
import id.t12ue.kalimutu.model.SendFor;
import id.t12ue.kalimutu.model.TransactionEntity;
import id.t12ue.kalimutu.model.User;
import id.t12ue.kalimutu.notification.APIService;
import id.t12ue.kalimutu.notification.Client;
import id.t12ue.kalimutu.notification.Data;
import id.t12ue.kalimutu.notification.MyResponse;
import id.t12ue.kalimutu.notification.NotificationSender;
import id.t12ue.kalimutu.notification.Token;
import id.t12ue.kalimutu.pref.UserPref;
import id.t12ue.kalimutu.viewmodel.TransactionViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayActivity extends AppCompatActivity implements PaySliderAdapter.AdapterListener{
    private TransactionEntity entity;
    private TransactionViewModel viewModel;
    private ViewPager2 viewPager;
    private PaySliderAdapter adapter;
    private LinearLayout dotsLayout;
    private MaterialButton button;
    private ImageView imageView;
    private FirebaseDatabase database;
    private DatabaseReference df;
    private StorageReference storage;
    private ProgressDialog progressDialog;
    private static final int REQUEST_CODE_CHOOSE = 23;
    private APIService apiService;
    private Date createdAt;
    private SendFor sendFor;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        init();
        entity = (TransactionEntity) getIntent().getSerializableExtra("data");

        if (!entity.getUrlPay().equals("-")){
            Glide.with(getApplicationContext())
                    .load(entity.getUrlPay())
                    .error(R.drawable.picture)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }
        retrive();
    }

    private void retrive(){
        try {
            List<BankEntity> list = viewModel.readBank();
            adapter=new PaySliderAdapter(getApplicationContext(), list, this);
            viewPager.setAdapter(adapter);
            setupIndicator();
            setupCurrentIndicator(0);
            sendFor=viewModel.getTokenAdmin();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void init(){
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Upload Pembayaran");
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        viewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.layoutDots);
        viewModel = ViewModelProviders.of(this).get(TransactionViewModel.class);
        button = findViewById(R.id.btn);
        imageView = findViewById(R.id.imageView);
        database = FirebaseDatabase.getInstance();
        storage= FirebaseStorage.getInstance().getReference("assets");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploaded....");
        progressDialog.setCancelable(false);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        createdAt = Calendar.getInstance().getTime();
        user= UserPref.getInstance(this).getUser();

        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                RxPermissions rxPermissions = new RxPermissions(PayActivity.this);
                rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                startAction();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.permission_request_denied, Toast.LENGTH_LONG)
                                        .show();
                            }
                        }, Throwable::printStackTrace);
            }
        });
    }

    private void setupIndicator() {
        ImageView[] indicator=new ImageView[adapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(4,0,4,0);
        for (int i=0; i<indicator.length; i++){
            indicator[i]=new ImageView(getApplicationContext());
            indicator[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.indicator_inactive));
            indicator[i].setLayoutParams(layoutParams);
            dotsLayout.addView(indicator[i]);
        }

    }
    private void setupCurrentIndicator(int index) {
        int itemcildcount=dotsLayout.getChildCount();
        for (int i=0; i<itemcildcount; i++){
            ImageView imageView=(ImageView)dotsLayout.getChildAt(i);
            if (i==index){
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.indicator_active));
            }else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.indicator_inactive));
            }
        }

    }

    private void startAction(){
        Matisse.from(PayActivity.this)
                .choose(MimeType.ofImage(), false)
                .theme(R.style.Matisse_Dracula)
                .countable(true)
                .capture(true)
                .captureStrategy(
                        new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider", "test"))
                .maxSelectable(1)
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
            Uri uri = Uri.fromFile(new File(Matisse.obtainPathResult(data).get(0)));
            Crop(uri);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                //imgUri = result.getUri();
                File file = new File(result.getUri().toString());
                upload(result.getUri(), entity.getTransactionId()+".jpg");
                //uploadImage(result.getUri(), file.getName());
                //incrementCounter(file.getName());

            }
        }
    }

    private void Crop(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAspectRatio(3,4)
                .start(this);
    }

    private void upload(Uri uri, String filename) {

        if (uri!=null){
            progressDialog.show();
            final StorageReference storageReference = storage.child(filename);
            UploadTask uploadTask = storageReference.putFile(uri);
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
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
                        storeTransaction(task.getResult().toString());
                        Glide.with(getApplicationContext())
                                .load(task.getResult().toString())
                                .error(R.drawable.picture)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imageView);
                        Toast.makeText(getApplicationContext(), "Upload Succsessful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void storeTransaction(String url){
        progressDialog.show();
        TransactionEntity trs = new TransactionEntity(
                entity.getTransactionId(),
                entity.getUserId(),
                entity.getTransactionTime(),
                entity.getDateBegin(),
                entity.getDateFinish(),
                entity.getQuota(),
                entity.getPayMethode(),
                url,
                1
        );
        df = database.getReference(Cons.KEY_TRANSACTION);
        df.child(String.valueOf(trs.getTransactionId())).setValue(trs).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                viewModel.insert(trs);
                progressDialog.hide();
                storeNotif(
                        sendFor.getUserId(),
                        entity.getTransactionId(),
                        user.getName()+" telah mengupload bukti pembayaran untuk kode pesanan KLM"+entity.getTransactionId()
                );
            }
        });

    }

    private void storeNotif(long userId, long orderId, String msg){
        progressDialog.show();
        Long tsLong = System.currentTimeMillis()/1000;
        NotificationEntity entity = new NotificationEntity(
                tsLong,
                userId,
                orderId,
                createdAt,
                msg,
                false
        );
        df = database.getReference(Cons.KEY_NOTIFICATION);
        df.child(String.valueOf(tsLong)).setValue(entity).addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void mVoid){
                progressDialog.hide();
            }
        });

        updateNotif(msg);
    }

    private void updateNotif(String m){
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Cons.KEY_TOKEN)
                .child(sendFor.getToken())
                .child("token")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String usertoken=dataSnapshot.getValue(String.class);
                sendNotifications(
                        usertoken,
                        "KLM"+entity.getTransactionId(),
                        m,
                        entity.getTransactionId()+""
                );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        UpdateToken();
    }


    private void UpdateToken(){
        //FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        Token token= new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference(Cons.KEY_TOKEN).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }

    public void sendNotifications(String usertoken, String title, String message, String id) {
        Data data = new Data(id, title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(getApplicationContext(), "Failed ", Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onActionCopy(BankEntity entity) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", entity.getRekeningNumber());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getApplicationContext(), "Copied to Clipboard",
                Toast.LENGTH_LONG).show();
    }
}
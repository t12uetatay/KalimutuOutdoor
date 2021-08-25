package id.t12ue.kalimutu.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import id.t12ue.kalimutu.R;
import id.t12ue.kalimutu.adapter.ShoppingAdapter;
import id.t12ue.kalimutu.helpers.Cons;
import id.t12ue.kalimutu.helpers.DateConverter;
import id.t12ue.kalimutu.helpers.DividerDecoration;
import id.t12ue.kalimutu.model.Cart;
import id.t12ue.kalimutu.model.CartEntity;
import id.t12ue.kalimutu.model.DetailTransaction;
import id.t12ue.kalimutu.model.NotificationEntity;
import id.t12ue.kalimutu.model.PackageEntity;
import id.t12ue.kalimutu.model.SendFor;
import id.t12ue.kalimutu.model.TransactionEntity;
import id.t12ue.kalimutu.model.UpdateStock;
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

public class TransactionActivity extends AppCompatActivity implements ShoppingAdapter.AdapterListener{
    private TextView idPesanan, namaPemesan, telp, jumlah,waktuPesanan;
    private TextView tanggalPesanan, tanggalSelesai, totalBayar, metodePembayaran, ket, textStatus;
    private RecyclerView recyclerView;
    private long id, totalSewa=0, totalStrip=0;
    private TransactionViewModel viewModel;
    private ShoppingAdapter mAdapter;
    private TypedArray typedArray;
    private TransactionEntity entity;
    private MaterialButton btnConfrm, btnCancel;
    private FirebaseDatabase database;
    private DatabaseReference df;
    private ProgressDialog progressDialog;
    private Date createdAt = Calendar.getInstance().getTime();
    private APIService apiService;
    private SendFor sendFor;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        init();
    }

    private void init(){
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Rincian Pesanan");
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        idPesanan = findViewById(R.id.idPesanan);
        namaPemesan = findViewById(R.id.namaPemesan);
        telp = findViewById(R.id.telp);
        jumlah = findViewById(R.id.jumlah);
        waktuPesanan = findViewById(R.id.waktuPesanan);
        tanggalPesanan = findViewById(R.id.tanggalPesanan);
        tanggalSelesai = findViewById(R.id.tanggalSelesai);
        totalBayar = findViewById(R.id.totalBayar);
        metodePembayaran = findViewById(R.id.metodePembayaran);
        ket = findViewById(R.id.ket);
        user = UserPref.getInstance(this).getUser();
        textStatus = findViewById(R.id.textStatus);
        recyclerView = findViewById(R.id.recyclerView);
        btnConfrm = findViewById(R.id.btnConfrm);
        btnCancel = findViewById(R.id.btnCancel);
        mAdapter = new ShoppingAdapter(this, this);
        viewModel = ViewModelProviders.of(this).get(TransactionViewModel.class);
        typedArray  = this.getResources().obtainTypedArray(R.array.pay_methode);
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        progressDialog.setMessage("Menyimpan....");
        progressDialog.setCancelable(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerDecoration(this, DividerItemDecoration.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);


        id = Long.parseLong(getIntent().getStringExtra("data"));
        retrive(id);

        LiveData<DetailTransaction> liveData = viewModel.readDetailTransaction(id);
        liveData.observe(this, new Observer<DetailTransaction>() {
            @Override
            public void onChanged(DetailTransaction detail) {
                textStatus.setText(Cons.status(detail.getStatus()));
                idPesanan.setText("KLM"+detail.getTransactionId());
                namaPemesan.setText(detail.getUserName());
                telp.setText(detail.getPhone());
                jumlah.setText(detail.getQuota()+" Hari");
                waktuPesanan.setText(DateConverter.getTanggal(detail.getTransactionTime()));
                tanggalPesanan.setText(Cons.dateFormat(detail.getDateBegin()));
                tanggalSelesai.setText(Cons.dateFormat(detail.getDateFinish()));
                metodePembayaran.setText(typedArray.getString(detail.getPayMethode()));
                if (detail.getPayMethode()==0 && detail.getStatus()==0){
                    btnConfrm.setEnabled(false);
                } else {
                    btnConfrm.setEnabled(true);
                }

                setComp(detail.getStatus());
                getTotal(detail.getTransactionId());
            }

        });

        try {
            entity = viewModel.readById(id);
            sendFor=viewModel.getTokenAdmin();
            if (entity.getPayMethode()==0){
                ket.setVisibility(View.GONE);
            } else {
                ket.setVisibility(View.VISIBLE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        metodePembayaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entity.getPayMethode()==0){
                    Intent intent = new Intent(TransactionActivity.this, PayActivity.class);
                    intent.putExtra("data", entity);
                    startActivity(intent);
                }
            }
        });

        btnConfrm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(TransactionActivity.this, R.style.AlertRoundShapeTheme)
                        .setTitle("Info")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Konfirmasi pengambilan pesanan?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                storeTransaction(user.getName()+" telah mengambil pesanan dengan kode pesanan KLM"+entity.getTransactionId(), 3);
                                updateStock(entity.getTransactionId());
                            }
                        })
                        .setNeutralButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(TransactionActivity.this, R.style.AlertRoundShapeTheme)
                        .setTitle("Info")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Anda ingin membatalkan pesanan ini?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                storeTransaction(user.getName()+" telah membatalkan pesanan kode pesanan KLM"+entity.getTransactionId(), -1);
                            }
                        })
                        .setNeutralButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });
    }

    private void retrive(long id){
        LiveData<List<Cart>> liveData = viewModel.readShopping(id);
        liveData.observe(this, new Observer<List<Cart>>() {
            @Override
            public void onChanged(List<Cart> entities) {
                mAdapter.setDataList(entities);
            }

        });
    }

    private void getTotal(long id){
        try {
            LiveData<Long> mSewa = viewModel.sumTotalSewa(id);
            mSewa.observe(this, new Observer<Long>() {
                @Override
                public void onChanged(Long sewa) {
                    totalSewa=sewa;
                    totalBayar.setText(Cons.currencyId(totalSewa+totalStrip));
                }

            });

            LiveData<Long> mTrip = viewModel.sumTotalTrip(id);
            mTrip.observe(this, new Observer<Long>() {
                @Override
                public void onChanged(Long trip) {
                    totalStrip=trip;
                    totalBayar.setText(Cons.currencyId(totalSewa+totalStrip));
                }

            });

        } catch (Exception e){}
    }

    private void updateStock(long id){
        try {
            df = database.getReference(Cons.KEY_PACKAGE);
            List<UpdateStock> list = viewModel.updateStock(id);
            for (int i=0; i<list.size(); i++){
                df.child(String.valueOf(list.get(i).getPackageId())).setValue(new PackageEntity(
                        list.get(i).getPackageId(),
                        list.get(i).getPackageName(),
                        list.get(i).getPackagePrice(),
                        list.get(i).getDescription(),
                        list.get(i).getStock(),
                        list.get(i).getCategory(),
                        list.get(i).isArzip()
                ));
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setComp(int sts){
        if (sts==-2){
            btnConfrm.setEnabled(false);
            btnCancel.setEnabled(true);
        }
        if (sts==-1){
            btnConfrm.setEnabled(false);
            btnCancel.setEnabled(false);
        }
        if (sts==0){
            btnConfrm.setEnabled(false);
            btnCancel.setEnabled(true);
        }

        if (sts==1){
            btnConfrm.setEnabled(false);
            btnCancel.setEnabled(true);
        }

        if (sts==2){
            btnConfrm.setEnabled(true);
            btnCancel.setEnabled(true);
        }
        if (sts==3){
            btnConfrm.setEnabled(false);
            btnCancel.setEnabled(false);
        }

        if (sts==4){
            btnConfrm.setEnabled(false);
            btnCancel.setEnabled(false);
        }
    }

    private void storeTransaction(String m, int sts){
        progressDialog.show();
        TransactionEntity entity1 = new TransactionEntity(
                entity.getTransactionId(),
                entity.getUserId(),
                entity.getTransactionTime(),
                entity.getDateBegin(),
                entity.getDateFinish(),
                entity.getQuota(),
                entity.getPayMethode(),
                entity.getUrlPay(),
                sts
        );
        df = database.getReference(Cons.KEY_TRANSACTION);
        df.child(String.valueOf(entity1.getTransactionId())).setValue(entity1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                viewModel.insert(entity1);
                storeNotif(sendFor.getUserId(), entity.getTransactionId(), m);
            }
        });

        FirebaseDatabase.getInstance().getReference().child(Cons.KEY_TOKEN).child(sendFor.getToken()).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String usertoken=dataSnapshot.getValue(String.class);
                sendNotifications(
                        usertoken, m, entity.getTransactionId()+""
                );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        UpdateToken();
    }

    private void storeNotif(long userId, long orderId, String msg){
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
    }


    private void UpdateToken(){
        //FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        Token token= new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference(Cons.KEY_TOKEN).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }

    public void sendNotifications(String usertoken, String message, String id) {
        Data data = new Data(id, "KLM"+id, message);
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
    public void onClick(Cart entity) {

    }
}
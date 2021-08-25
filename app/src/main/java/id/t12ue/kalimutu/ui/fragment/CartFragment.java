package id.t12ue.kalimutu.ui.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


import id.t12ue.kalimutu.R;
import id.t12ue.kalimutu.adapter.CartAdapter;
import id.t12ue.kalimutu.helpers.Cons;
import id.t12ue.kalimutu.helpers.DateConverter;
import id.t12ue.kalimutu.helpers.DividerDecoration;
import id.t12ue.kalimutu.helpers.NumPik;
import id.t12ue.kalimutu.model.Cart;
import id.t12ue.kalimutu.model.CartEntity;
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
import id.t12ue.kalimutu.ui.activity.TransactionActivity;
import id.t12ue.kalimutu.viewmodel.CartViewModel;
import id.t12ue.kalimutu.viewmodel.TransactionViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends DialogFragment implements CartAdapter.AdapterListener {
    private FirebaseDatabase database;
    private DatabaseReference df;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private CartAdapter mAdapter;
    private CartViewModel viewModel;
    private TransactionViewModel viewModelTrasaction;
    private User user;
    private TextView textTotal, tanggal_sewa, jumlah, edit_jumlah;
    private MaterialButton btn;
    private Calendar calendar;
    private long cat1=0, cat0=0;
    private AppCompatSpinner spinner;
    private String idTrns;
    private Date createdAt = Calendar.getInstance().getTime();
    private APIService apiService;
    private SendFor sendFor;
    int c=0;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        init(view);
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
        getDialog().getWindow().setLayout(width, height);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void init(View view){
        recyclerView = view.findViewById(R.id.recyclerView);
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Menyimpan....");
        progressDialog.setCancelable(false);
        mAdapter = new CartAdapter(getActivity(), this);
        viewModel = ViewModelProviders.of(this).get(CartViewModel.class);
        viewModelTrasaction = ViewModelProviders.of(this).get(TransactionViewModel.class);
        user = UserPref.getInstance(getActivity()).getUser();
        textTotal = view.findViewById(R.id.textTotal);
        btn = view.findViewById(R.id.btn);
        tanggal_sewa = view.findViewById(R.id.tanggal_sewa);
        jumlah = view.findViewById(R.id.jumlah);
        edit_jumlah = view.findViewById(R.id.edit_jumlah);
        spinner = view.findViewById(R.id.spinner);
        calendar = Calendar.getInstance();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        tanggal_sewa.setText(DateConverter.getDateFormat(createdAt));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerDecoration(getActivity(), DividerItemDecoration.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        loadCart();
        loadTransaction();
        retrive(Long.parseLong(user.getId()));

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeTransaction(getString(R.string.new_order)+" "+user.getName());
            }
        });

        tanggal_sewa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(tanggal_sewa);
                newFragment.show(getFragmentManager(), "date picker");
            }
        });

        edit_jumlah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog(cat0, jumlah);
            }
        });

        newId();

        try {
            sendFor=viewModel.getTokenAdmin();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void editDialog(long total, TextView textView){
        View v=LayoutInflater.from(getActivity()).inflate(R.layout.edit_qty, null);
        final NumPik num=(NumPik) v.findViewById(R.id.numr);
        AppCompatTextView hargaview= (AppCompatTextView) v.findViewById(R.id.hargaview);
        final AppCompatTextView totalview= (AppCompatTextView) v.findViewById(R.id.totalview);
        int hari = Integer.parseInt(textView.getText().toString());
        hargaview.setText(Cons.currencyId(total));
        totalview.setText(Cons.currencyId(total*hari));
        MaterialAlertDialogBuilder dlg = new MaterialAlertDialogBuilder(getActivity());
        dlg.setView(v);
        dlg.setTitle("Lama Penyewaan");
        dlg.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface p1, int p2) {
                textTotal.setText(Cons.currencyId(cat1+(total*num.getValue())));
            }
        });
        dlg.setNeutralButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dlg.show();

        num.setMinValue(1);
        num.setMaxValue(200);
        num.setValue(hari);
        num.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){
            @Override
            public void onValueChange(NumberPicker p1, int p2, int p3) {
                textView.setText(String.valueOf(num.getValue()));
                totalview.setText(Cons.currencyId(total*num.getValue()));
                textTotal.setText(Cons.currencyId(cat1+(total*num.getValue())));
            }
        });
    }

    private void retrive(long id){
        final LiveData<List<Cart>> liveData = viewModel.readAll(id);
        liveData.observe(getActivity(), new Observer<List<Cart>>() {
            @Override
            public void onChanged(List<Cart> entities) {
                mAdapter.setDataList(entities);
                sumTotal(entities.size());
                if (entities.size()==0){
                    btn.setEnabled(false);
                } else {
                    btn.setEnabled(true);
                }
            }

        });
    }

    private void sumTotal(int c){
        try {
            if (c>0){
                final LiveData<Long> total1 = viewModel.sumCart0(Long.parseLong(user.getId()));
                total1.observe(getActivity(), new Observer<Long>() {
                    @Override
                    public void onChanged(Long total) {
                        cat0=total;
                        textTotal.setText(Cons.currencyId(cat0+cat1));
                    }

                });
                final LiveData<Long> total2 = viewModel.sumCart1(Long.parseLong(user.getId()));
                total2.observe(getActivity(), new Observer<Long>() {
                    @Override
                    public void onChanged(Long total) {
                        cat1=total;
                        textTotal.setText(Cons.currencyId(cat0+cat1));
                    }

                });
            }
        } catch (Exception e){}
    }

    private void newId(){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String id = df.format(createdAt);
        String current="%"+id+"%";
        LiveData<Long> liveData = viewModel.readTransactionId(current);
        liveData.observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long ids) {
                if (ids==null){
                    idTrns = id+"001";
                } else {
                    idTrns = String.valueOf(ids+1);
                }

            }

        });

    }

    private void loadCart(){
        df = database.getReference(Cons.KEY_CART);
        df.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()){
                    CartEntity entity = mDataSnapshot.getValue(CartEntity.class);
                    CartEntity tmp = new CartEntity(
                            entity.getCartId(),
                            entity.getTransactionId(),
                            entity.getUserId(),
                            entity.getPackageId(),
                            entity.getQuantity()
                    );
                    viewModel.insert(tmp);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError){

            }

        });
    }

    private void loadTransaction(){
        df = database.getReference(Cons.KEY_TRANSACTION);
        df.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()){
                    TransactionEntity entity = mDataSnapshot.getValue(TransactionEntity.class);
                    TransactionEntity tmp = new TransactionEntity(
                            entity.getTransactionId(),
                            entity.getUserId(),
                            entity.getTransactionTime(),
                            entity.getDateBegin(),
                            entity.getDateFinish(),
                            entity.getQuota(),
                            entity.getPayMethode(),
                            entity.getUrlPay(),
                            entity.getStatus()
                    );
                    viewModelTrasaction.insert(tmp);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError){

            }

        });
    }

    /*private void storeData(long ID){
        progressDialog.show();
        df = database.getReference(Cons.KEY_CATEGORY);
        df.child(String.valueOf(ID)).setValue(new CategoryEntity(ID, nama)).addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void mVoid){
                progressDialog.hide();
                editText1.setText(null);
            }
        });
    }*/

    @Override
    public void plusOnClick(Cart entity, TextView textView) {
        int num = Integer.parseInt(textView.getText().toString().trim());
        if (entity.getStock()>num){
            num=num+1;
            storeData(entity, num, -1);
        } else {
            Toast.makeText(getActivity(), "Jumlah melebihi stok", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void minOnClick(Cart entity, TextView textView) {
        int num = Integer.parseInt(textView.getText().toString());
        if (num==1){
            removeData(entity.getCartId());
        } else {
            num=num-1;
            storeData(entity, num, -1);
        }
    }

    private void storeData(Cart cart, int qty, long id){
        progressDialog.show();
        CartEntity cartEntity = new CartEntity(
                cart.getCartId(),
                id,
                Long.parseLong(user.getId()),
                cart.getPackageId(),
                qty
        );
        df = database.getReference(Cons.KEY_CART);
        df.child(String.valueOf(cartEntity.getCartId())).setValue(cartEntity).addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void mVoid){
                progressDialog.hide();
                viewModel.insert(cartEntity);
            }
        });
    }

    private void removeData(long ID){
        progressDialog.show();
        df = database.getReference(Cons.KEY_CART);
        df.child(String.valueOf(ID)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void mVoid){
                progressDialog.hide();
                viewModel.delete(ID);
            }
        });
    }

    private void storeTransaction(String m){
        progressDialog.show();
        TransactionEntity entity = new TransactionEntity(
                Long.parseLong(idTrns),
                Long.parseLong(user.getId()),
                createdAt,
                tanggal_sewa.getText().toString(),
                "-",
                Integer.parseInt(jumlah.getText().toString()),
                spinner.getSelectedItemPosition(),
                "-",
                0
        );
        df = database.getReference(Cons.KEY_TRANSACTION);
        df.child(String.valueOf(entity.getTransactionId())).setValue(entity).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                try {
                    List<CartEntity> list = viewModel.readCart(Long.parseLong(user.getId()));
                    for (int i=0; i<list.size(); i++){
                        storeRinciaan(
                                entity.getTransactionId(),
                                list.get(i)
                        );
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                viewModelTrasaction.insert(entity);
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

    private void storeRinciaan(long id, CartEntity cart){
        CartEntity entity = new CartEntity(
                cart.getCartId(),
                id,
                -1,
                cart.getPackageId(),
                cart.getQuantity()
        );
        df = database.getReference(Cons.KEY_CART);
        df.child(String.valueOf(entity.getCartId())).setValue(entity).addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void mVoid){
                viewModel.insert(entity);
            }
        });
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
                int time = 2000;
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressDialog.hide();
                Intent intent = new Intent(getActivity(), TransactionActivity.class);
                intent.putExtra("data", String.valueOf(orderId));
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                dismiss();
                startActivity(intent);
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
                        Toast.makeText(getActivity(), "Failed ", Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }
}
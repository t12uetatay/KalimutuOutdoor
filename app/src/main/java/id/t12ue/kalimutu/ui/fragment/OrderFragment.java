package id.t12ue.kalimutu.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import id.t12ue.kalimutu.R;
import id.t12ue.kalimutu.adapter.OrderAdapter;
import id.t12ue.kalimutu.helpers.Cons;
import id.t12ue.kalimutu.helpers.Space;
import id.t12ue.kalimutu.model.Orders;
import id.t12ue.kalimutu.model.TransactionEntity;
import id.t12ue.kalimutu.model.User;
import id.t12ue.kalimutu.pref.UserPref;
import id.t12ue.kalimutu.ui.activity.ReciveActivity;
import id.t12ue.kalimutu.ui.activity.TransactionActivity;
import id.t12ue.kalimutu.viewmodel.TransactionViewModel;

public class OrderFragment extends Fragment implements OrderAdapter.AdapterListener{
    private FirebaseDatabase database;
    private DatabaseReference df;
    private RecyclerView recyclerView;
    private OrderAdapter mAdapter;
    private TransactionViewModel viewModel;
    private User user;

    public OrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        init(view);
        return view;
    }

    private void init(View view){
        recyclerView = view.findViewById(R.id.recyclerView);
        database = FirebaseDatabase.getInstance();
        mAdapter = new OrderAdapter(getActivity(), this);
        viewModel = ViewModelProviders.of(this).get(TransactionViewModel.class);
        user = UserPref.getInstance(getActivity()).getUser();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),
                1,//span count no of items in single row
                GridLayoutManager.VERTICAL,//Orientation
                false);//reverse scrolling of recyclerview
        //set layout manager as gridLayoutManager
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.addItemDecoration(new Space(5, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        loadTransaction();
        retrive(
                Long.parseLong(user.getId()), user.getRole()
        );
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
                    viewModel.insert(tmp);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError){

            }

        });
    }

    private void retrive(long id, String role){
        final LiveData<List<Orders>> allProduct = viewModel.readMyOrders(id, role);
        allProduct.observe(getActivity(), new Observer<List<Orders>>() {
            @Override
            public void onChanged(List<Orders> entities) {
                mAdapter.setDataList(entities, 1);
            }

        });
    }

    @Override
    public void onClick(Orders orders) {
        Intent intent;
        if (user.getRole().equals("A")){
            intent= new Intent(getActivity(), ReciveActivity.class);
        } else {
            intent= new Intent(getActivity(), TransactionActivity.class);
        }
        intent.putExtra("data", String.valueOf(orders.getTransactionId()));
        startActivity(intent);
    }
}
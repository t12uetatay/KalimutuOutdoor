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
import id.t12ue.kalimutu.adapter.NotificationAdapter;
import id.t12ue.kalimutu.helpers.Cons;
import id.t12ue.kalimutu.helpers.Space;
import id.t12ue.kalimutu.model.NotificationEntity;
import id.t12ue.kalimutu.model.User;
import id.t12ue.kalimutu.pref.UserPref;
import id.t12ue.kalimutu.ui.activity.ReciveActivity;
import id.t12ue.kalimutu.ui.activity.TransactionActivity;
import id.t12ue.kalimutu.viewmodel.NotificationViewModel;

public class NotificationFragment extends Fragment implements NotificationAdapter.AdapterListener{
    private FirebaseDatabase database;
    private DatabaseReference df;
    private RecyclerView recyclerView;
    private NotificationAdapter mAdapter;
    private NotificationViewModel viewModel;
    private User user;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        init(view);
        return view;
    }

    private void init(View view){
        recyclerView = view.findViewById(R.id.recyclerView);
        database = FirebaseDatabase.getInstance();
        mAdapter = new NotificationAdapter(getActivity(), this);
        viewModel = ViewModelProviders.of(this).get(NotificationViewModel.class);
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

        loadNotification();
        retrive(
                Long.parseLong(user.getId())
        );
    }

    private void loadNotification(){
        df = database.getReference(Cons.KEY_NOTIFICATION);
        df.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()){
                    NotificationEntity entity = mDataSnapshot.getValue(NotificationEntity.class);
                    NotificationEntity tmp = new NotificationEntity(
                            entity.getNotificationId(),
                            entity.getUserId(),
                            entity.getTransactionId(),
                            entity.getNotificationTime(),
                            entity.getMessage(),
                            entity.isRead()
                    );
                    viewModel.insert(tmp);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError){

            }

        });
    }

    private void retrive(long id){
        final LiveData<List<NotificationEntity>> allProduct = viewModel.readNotification(id);
        allProduct.observe(getActivity(), new Observer<List<NotificationEntity>>() {
            @Override
            public void onChanged(List<NotificationEntity> entities) {
                mAdapter.setDataList(entities, 1);
            }

        });
    }

    @Override
    public void onClick(NotificationEntity entity) {
        Intent intent;
        if (user.getRole().equals("A")){
            intent= new Intent(getActivity(), ReciveActivity.class);
        } else {
            intent= new Intent(getActivity(), TransactionActivity.class);
        }
        intent.putExtra("data", String.valueOf(entity.getTransactionId()));
        startActivity(intent);
    }
}
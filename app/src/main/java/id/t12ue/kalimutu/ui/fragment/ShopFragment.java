package id.t12ue.kalimutu.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.concurrent.ExecutionException;

import id.t12ue.kalimutu.R;
import id.t12ue.kalimutu.adapter.ProductAdapter;
import id.t12ue.kalimutu.adapter.TripAdapter;
import id.t12ue.kalimutu.helpers.Cons;
import id.t12ue.kalimutu.helpers.Space;
import id.t12ue.kalimutu.model.ImageEntity;
import id.t12ue.kalimutu.model.PackageEntity;
import id.t12ue.kalimutu.model.Packages;
import id.t12ue.kalimutu.model.User;
import id.t12ue.kalimutu.model.UserEntity;
import id.t12ue.kalimutu.pref.UserPref;
import id.t12ue.kalimutu.viewmodel.PackageViewModel;
import id.t12ue.kalimutu.viewmodel.UserViewModel;

public class ShopFragment extends Fragment implements ProductAdapter.AdapterListener, TripAdapter.AdapterListener{
    private FirebaseDatabase database;
    private DatabaseReference df;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView, recyclerView2;
    private ProductAdapter pAdapter;
    private TripAdapter tAdapter;
    private PackageViewModel viewModel;
    private UserViewModel viewModelUser;
    private User user;
    private int tipe=0;
    private LinearLayout layTrip, layProduct;
    private UserEntity admin;

    public ShopFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        init(view);
        return view;
    }

    private void init(View view){
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView2 = view.findViewById(R.id.recyclerView2);
        database = FirebaseDatabase.getInstance();
        layTrip = view.findViewById(R.id.layTrip);
        layProduct = view.findViewById(R.id.layProduct);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Menyimpan....");
        progressDialog.setCancelable(false);
        pAdapter = new ProductAdapter(getActivity(), this);
        tAdapter = new TripAdapter(getActivity(), this);
        viewModel = ViewModelProviders.of(this).get(PackageViewModel.class);
        viewModelUser = ViewModelProviders.of(this).get(UserViewModel.class);
        user = UserPref.getInstance(getActivity()).getUser();

        if (!UserPref.getInstance(getActivity()).isLoggedIn()||user.getRole().equals("U")){
            tipe=0;
        } else {
            tipe=1;
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),
                1,//span count no of items in single row
                GridLayoutManager.HORIZONTAL,//Orientation
                false);//reverse scrolling of recyclerview
        //set layout manager as gridLayoutManager
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.addItemDecoration(new Space(5, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(tAdapter);

        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(getActivity(),
                2,//span count no of items in single row
                GridLayoutManager.VERTICAL,//Orientation
                false);//reverse scrolling of recyclerview
        //set layout manager as gridLayoutManager
        recyclerView2.setLayoutManager(gridLayoutManager2);

        recyclerView2.addItemDecoration(new Space(5, 2));
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        recyclerView2.setAdapter(pAdapter);

        try {
            admin = viewModelUser.readAdmin();
            if (admin!=null){
                view.findViewById(R.id.fab).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.fab).setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        loadPackages();
        loadImage();
        retrive();

        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("smsto:"+admin.getPhone());
                Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                i.setPackage("com.whatsapp");
                startActivity(Intent.createChooser(i, "Chat admin"));
            }
        });
    }

    private void loadPackages(){
        df = database.getReference(Cons.KEY_PACKAGE);
        df.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()){
                    PackageEntity entity = mDataSnapshot.getValue(PackageEntity.class);
                    PackageEntity tmp = new PackageEntity(
                            entity.getPackageId(),
                            entity.getPackageName(),
                            entity.getPackagePrice(),
                            entity.getDescription(),
                            entity.getStock(),
                            entity.getCategory(),
                            entity.isArzip()
                    );
                    viewModel.insertPackage(tmp);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError){

            }

        });
    }

    private void loadImage(){
        df = database.getReference(Cons.KEY_IMAGE);
        df.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()){
                    ImageEntity entity = mDataSnapshot.getValue(ImageEntity.class);
                    ImageEntity tmp = new ImageEntity(
                            entity.getImageId(),
                            entity.getPackageId(),
                            entity.getImageUrl()
                    );
                    viewModel.insertImage(tmp);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError){

            }

        });
    }

    private void retrive(){
        final LiveData<List<Packages>> allProduct = viewModel.readPackageByCategory(0);
        allProduct.observe(getActivity(), new Observer<List<Packages>>() {
            @Override
            public void onChanged(List<Packages> entities) {
                pAdapter.setDataList(entities, tipe);
                if (entities.size()==0){
                    layProduct.setVisibility(View.GONE);
                } else {
                    layProduct.setVisibility(View.VISIBLE);
                }
            }

        });

        final LiveData<List<Packages>> allTrip = viewModel.readPackageByCategory(1);
        allTrip.observe(getActivity(), new Observer<List<Packages>>() {
            @Override
            public void onChanged(List<Packages> entities) {
                tAdapter.setDataList(entities, tipe);
                if (entities.size()==0){
                    layTrip.setVisibility(View.GONE);
                } else {
                    layTrip.setVisibility(View.VISIBLE);
                }
            }

        });
    }


    /*@Override
    public void onActionMoreClick(Product entity, ImageView imageView) {
        
    }

    @Override
    public void onClick(Product entity) {
        DetailFragment detailFragment = new DetailFragment(entity);
        detailFragment.show(getParentFragmentManager(), detailFragment.getTag());
    }

    @Override
    public void onActionMoreClick(Trip entity, ImageView imageView) {
        DetailTrip detailTrip = new DetailTrip(entity);
        detailTrip.show(getParentFragmentManager(), detailTrip.getTag());
    }*/


    @Override
    public void onClick(Packages entity) {
        DetailFragment detailFragment = new DetailFragment(entity);
        detailFragment.show(getParentFragmentManager(), detailFragment.getTag());
    }

    @Override
    public void onActionMoreClick(Packages entity, ImageView imageView) {
        DetailFragment detailFragment = new DetailFragment(entity);
        detailFragment.show(getParentFragmentManager(), detailFragment.getTag());
    }
}
package id.t12ue.kalimutu.ui.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import id.t12ue.kalimutu.R;
import id.t12ue.kalimutu.adapter.PackageAdapter;
import id.t12ue.kalimutu.helpers.Cons;
import id.t12ue.kalimutu.helpers.Space;
import id.t12ue.kalimutu.model.ImageEntity;
import id.t12ue.kalimutu.model.PackageEntity;
import id.t12ue.kalimutu.model.Packages;
import id.t12ue.kalimutu.ui.activity.ImageActivity;
import id.t12ue.kalimutu.ui.activity.PostingActivity;
import id.t12ue.kalimutu.viewmodel.PackageViewModel;

public class PackagesFragment extends Fragment implements PackageAdapter.AdapterListener{
    private FirebaseDatabase database;
    private DatabaseReference df;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private PackageAdapter pAdapter;
    private PackageViewModel viewModel;

    public PackagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_package, container, false);
        init(view);
        return view;
    }

    private void init(View view){
        recyclerView = view.findViewById(R.id.recyclerView);
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Menyimpan....");
        progressDialog.setCancelable(false);
        pAdapter = new PackageAdapter(getActivity(), this);
        viewModel = ViewModelProviders.of(this).get(PackageViewModel.class);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),
                2,//span count no of items in single row
                GridLayoutManager.VERTICAL,//Orientation
                false);//reverse scrolling of recyclerview
        //set layout manager as gridLayoutManager
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.addItemDecoration(new Space(5, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(pAdapter);

        loadPackages();
        loadImage();
        retrive();
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
        final LiveData<List<Packages>> allProduct = viewModel.readAllPackage();
        allProduct.observe(getActivity(), new Observer<List<Packages>>() {
            @Override
            public void onChanged(List<Packages> entities) {
                pAdapter.setDataList(entities, 1);
            }

        });
    }


    private static final String POPUP_CONSTANT = "mPopup";
    private static final String POPUP_FORCE_SHOW_ICON = "setForceShowIcon";

    public void popupShowMenu(final Packages entity, ImageView imageView){
        PopupMenu popup = new PopupMenu(getActivity(), imageView);
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(POPUP_CONSTANT)) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(POPUP_FORCE_SHOW_ICON, boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.edit) {
                    //PostingFragment fragment = new PostingFragment(entity);
                    //fragment.show(getParentFragmentManager(), fragment.getTag());
                    Intent intent = new Intent(getActivity(), PostingActivity.class);
                    intent.putExtra("data", entity);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.delete) {
                    new MaterialAlertDialogBuilder(getActivity())
                            .setTitle("Konfirmasi!")
                            .setMessage("Hapus "+entity.getPackageName()+"?")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    storeData(entity);
                                }
                            })
                            .setNeutralButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();
                    return true;
                } else {
                    return onMenuItemClick(item);
                }

            }
        });
        popup.show();

    }

    private void storeData(Packages entity){
        progressDialog.show();
        df = database.getReference(Cons.KEY_PACKAGE);
        df.child(String.valueOf(entity.getPackageId())).setValue(new PackageEntity(
                entity.getPackageId(),
                entity.getPackageName(),
                entity.getPackagePrice(),
                entity.getDescription(),
                entity.getStock(),
                entity.getCategory(),
                true
        )).addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void mVoid){
                progressDialog.hide();
            }
        });
    }


    @Override
    public void onActionMoreClick(Packages entity, ImageView imageView) {
        popupShowMenu(entity, imageView);
    }

    @Override
    public void onClick(Packages entity) {

    }
}
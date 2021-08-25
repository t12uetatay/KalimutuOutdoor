package id.t12ue.kalimutu.ui.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import id.t12ue.kalimutu.adapter.BankAdapter;
import id.t12ue.kalimutu.helpers.Cons;
import id.t12ue.kalimutu.helpers.DividerDecoration;
import id.t12ue.kalimutu.model.BankEntity;
import id.t12ue.kalimutu.viewmodel.BankViewModel;

public class BankFragment extends Fragment implements BankAdapter.AdapterListener {
    private FirebaseDatabase database;
    private DatabaseReference df;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private BankAdapter mAdapter;
    private BankViewModel viewModel;

    public BankFragment() {
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
        mAdapter = new BankAdapter(getActivity(), this);
        viewModel = ViewModelProviders.of(this).get(BankViewModel.class);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerDecoration(getActivity(), DividerItemDecoration.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        loadBank();
        retrive();
    }

    private void retrive(){
        final LiveData<List<BankEntity>> liveData = viewModel.readAll();
        liveData.observe(getActivity(), new Observer<List<BankEntity>>() {
            @Override
            public void onChanged(List<BankEntity> entities) {
                mAdapter.setDataList(entities);
            }

        });
    }

    private void loadBank(){
        df = database.getReference(Cons.KEY_BANK);
        df.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()){
                    BankEntity entity = mDataSnapshot.getValue(BankEntity.class);
                    BankEntity tmp = new BankEntity(
                            entity.getBankId(),
                            entity.getBankName(),
                            entity.getRekeningName(),
                            entity.getRekeningNumber(),
                            entity.isArsip()
                    );
                    viewModel.insert(tmp);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError){

            }

        });
    }

    @Override
    public void onActionMoreClick(BankEntity entity, ImageView imageView) {
        popupShowMenu(entity, imageView);
    }

    private static final String POPUP_CONSTANT = "mPopup";
    private static final String POPUP_FORCE_SHOW_ICON = "setForceShowIcon";

    public void popupShowMenu(final BankEntity entity, ImageView imageView){
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
                    EditorBank fragment = new EditorBank(entity);
                    fragment.show(getParentFragmentManager(), fragment.getTag());
                    return true;
                } else if (id == R.id.delete) {
                    new MaterialAlertDialogBuilder(getActivity())
                            .setTitle("Konfirmasi!")
                            .setMessage("Hapus "+entity.getBankName()+"?")
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

    private void storeData(BankEntity bankEntity){
        progressDialog.show();
        df = database.getReference(Cons.KEY_BANK);
        df.child(String.valueOf(bankEntity.getBankId())).setValue(new BankEntity(
                bankEntity.getBankId(),
                bankEntity.getBankName(),
                bankEntity.getRekeningName(),
                bankEntity.getRekeningNumber(),
                true
        )).addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void mVoid){
                progressDialog.hide();
            }
        });
    }
}
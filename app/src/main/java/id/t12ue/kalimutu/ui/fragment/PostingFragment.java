package id.t12ue.kalimutu.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import id.t12ue.kalimutu.R;
import id.t12ue.kalimutu.helpers.Cons;
import id.t12ue.kalimutu.model.PackageEntity;
import id.t12ue.kalimutu.model.Packages;
import id.t12ue.kalimutu.ui.activity.ImageActivity;

public class PostingFragment extends DialogFragment {
    private TextInputEditText editText1;
    private TextInputEditText editText2;
    private TextInputEditText editText3;
    private TextInputEditText editText4;
    private TextInputLayout textInputLayout1;
    private TextInputLayout textInputLayout2;
    private TextInputLayout textInputLayout3;
    private TextInputLayout textInputLayout4;
    private TextView title;
    private FirebaseDatabase database;
    private DatabaseReference df;
    private CoordinatorLayout rootLayout;
    private ProgressDialog progressDialog;
    private Packages packages;
    private AppCompatSpinner spinner;

    public PostingFragment(Packages entity) {
        // Required empty public constructor
        this.packages=entity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_posting, container, false);
        init(view);
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    if (packages!=null){
                        storeData(packages.getPackageId());
                    } else {
                        getID();
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        getDialog().getWindow().setGravity(Gravity.CENTER);
        getDialog().getWindow().setLayout(width, height);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (packages!=null){
            title.setText("Ubah Postingan");
            spinner.setSelection(packages.getCategory());
            editText1.setText(packages.getPackageName());
            editText2.setText(packages.getPackagePrice()+"");
            editText3.setText(packages.getStock()+"");
            editText4.setText(packages.getDescription());
        }

    }

    private void init(View view){
        editText1 = view.findViewById(R.id.editext1);
        editText2 = view.findViewById(R.id.editext2);
        editText3 = view.findViewById(R.id.editext3);
        editText4 = view.findViewById(R.id.editext4);
        title = view.findViewById(R.id.title);
        textInputLayout1 = view.findViewById(R.id.textInputLayout1);
        textInputLayout2 = view.findViewById(R.id.textInputLayout2);
        textInputLayout3 = view.findViewById(R.id.textInputLayout3);
        textInputLayout4 = view.findViewById(R.id.textInputLayout4);
        rootLayout = view.findViewById(R.id.rootLayout);
        spinner = view.findViewById(R.id.spinner);
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Menyimpan....");
        progressDialog.setCancelable(false);
    }

    public boolean validate() {
        boolean valid = false;
        String nama = editText1.getText().toString();
        String harga = editText2.getText().toString();
        String stok = editText3.getText().toString();
        String deskripsi = editText4.getText().toString();
        if (nama.isEmpty()) {
            valid = false;
            textInputLayout1.setError("Nama paket diperlukan!");
        } else {
            valid = true;
            textInputLayout1.setError(null);
        }

        if (harga.isEmpty()) {
            valid = false;
            textInputLayout2.setError("Harga paket diperlukan!");
        } else {
            valid = true;
            textInputLayout2.setError(null);
        }


        if (stok.isEmpty()) {
            valid = false;
            textInputLayout3.setError("Stok/Kuota diperlukan!");
        } else {
            valid = true;
            textInputLayout3.setError(null);
        }

        if (deskripsi.isEmpty()) {
            valid = false;
            textInputLayout4.setError("Deskripsi paket diperlukan!");
        } else {
            valid = true;
            textInputLayout4.setError(null);
        }

        return valid;
    }

    private void getID() {
        progressDialog.show();
        df = database.getReference("c"+ Cons.KEY_PACKAGE);
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
                    Toast.makeText(getActivity(), "Firebase counter increment failed.", Toast.LENGTH_SHORT).show();
                } else {
                    storeData((Long)currentData.getValue());
                }
            }

        });
    }

    private void storeData(long ID){
        progressDialog.show();
        df = database.getReference(Cons.KEY_PACKAGE);
        df.child(String.valueOf(ID)).setValue(new PackageEntity(
                ID,
                editText1.getText().toString(),
                Long.parseLong(editText2.getText().toString()),
                editText4.getText().toString(),
                Long.parseLong(editText3.getText().toString()),
                spinner.getSelectedItemPosition(),
                false

        )).addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void mVoid){
                progressDialog.hide();
                editText1.setText(null);
                editText2.setText(null);
                editText3.setText(null);
                editText4.setText(null);
                Intent intent = new Intent(getActivity(), ImageActivity.class);
                intent.putExtra("data", String.valueOf(ID));
                intent.putExtra("trip", false);
                startActivity(intent);
                dismiss();
            }
        });
    }

}
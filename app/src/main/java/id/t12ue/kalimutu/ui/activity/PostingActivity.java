package id.t12ue.kalimutu.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
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

public class PostingActivity extends AppCompatActivity {
    private TextInputEditText editText1;
    private TextInputEditText editText2;
    private TextInputEditText editText3;
    private TextInputEditText editText4;
    private TextInputLayout textInputLayout1;
    private TextInputLayout textInputLayout2;
    private TextInputLayout textInputLayout3;
    private TextInputLayout textInputLayout4;
    private FirebaseDatabase database;
    private DatabaseReference df;
    private CoordinatorLayout rootLayout;
    private ProgressDialog progressDialog;
    private Packages packages;
    private AppCompatSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);
        init();
    }

    private void init(){
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Buat Postingan");
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        editText1 = findViewById(R.id.editext1);
        editText2 = findViewById(R.id.editext2);
        editText3 = findViewById(R.id.editext3);
        editText4 = findViewById(R.id.editext4);
        textInputLayout1 = findViewById(R.id.textInputLayout1);
        textInputLayout2 = findViewById(R.id.textInputLayout2);
        textInputLayout3 = findViewById(R.id.textInputLayout3);
        textInputLayout4 = findViewById(R.id.textInputLayout4);
        rootLayout = findViewById(R.id.rootLayout);
        spinner = findViewById(R.id.spinner);
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Menyimpan....");
        progressDialog.setCancelable(false);

        packages = (Packages) getIntent().getSerializableExtra("data");
        if (packages!=null){
            getSupportActionBar().setTitle("Ubah Postingan");
            spinner.setSelection(packages.getCategory());
            editText1.setText(packages.getPackageName());
            editText2.setText(packages.getPackagePrice()+"");
            editText3.setText(packages.getStock()+"");
            editText4.setText(packages.getDescription());
        }

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(getApplicationContext(), "Firebase counter increment failed.", Toast.LENGTH_SHORT).show();
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
                if (packages!=null){
                    Intent intent = new Intent(PostingActivity.this, ImageActivity.class);
                    intent.putExtra("data", String.valueOf(ID));
                    intent.putExtra("edit", "Y");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(PostingActivity.this, ImageActivity.class);
                    intent.putExtra("data", String.valueOf(ID));
                    intent.putExtra("edit", "T");
                    startActivity(intent);
                }
            }
        });
    }
}
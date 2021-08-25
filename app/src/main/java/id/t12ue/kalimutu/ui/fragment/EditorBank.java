package id.t12ue.kalimutu.ui.fragment;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
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
import id.t12ue.kalimutu.model.BankEntity;

public class EditorBank extends DialogFragment {
    private TextInputEditText editText1;
    private TextInputEditText editText2;
    private TextInputEditText editText3;
    private TextInputLayout textInputLayout1;
    private TextInputLayout textInputLayout2;
    private TextInputLayout textInputLayout3;
    private FirebaseDatabase database;
    private DatabaseReference df;
    private CoordinatorLayout rootLayout;
    private ProgressDialog progressDialog;
    private BankEntity bankEntity;
    private TextView title;

    public EditorBank(BankEntity entity) {
        // Required empty public constructor
        this.bankEntity=entity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.editor_bank, container, false);
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
                    if (bankEntity!=null){
                        storeData(bankEntity.getBankId());
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

        if (bankEntity!=null){
            title.setText("Ubah Metode Pembayaran");
            editText1.setText(bankEntity.getBankName());
            editText2.setText(bankEntity.getRekeningName());
            editText3.setText(bankEntity.getRekeningNumber());
        }

    }

    private void init(View view){
        editText1 = view.findViewById(R.id.editext1);
        editText2 = view.findViewById(R.id.editext2);
        editText3 = view.findViewById(R.id.editext3);
        title = view.findViewById(R.id.title);
        textInputLayout1 = view.findViewById(R.id.textInputLayout1);
        textInputLayout2 = view.findViewById(R.id.textInputLayout2);
        textInputLayout3 = view.findViewById(R.id.textInputLayout3);
        rootLayout = view.findViewById(R.id.rootLayout);
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Menyimpan....");
        progressDialog.setCancelable(false);

    }

    public boolean validate() {
        boolean valid = false;
        String bank = editText1.getText().toString();
        String nama = editText2.getText().toString();
        String nomor = editText3.getText().toString();
        if (bank.isEmpty()) {
            valid = false;
            textInputLayout1.setError("Nama bank diperlukan!");
        } else {
            valid = true;
            textInputLayout1.setError(null);
        }

        if (nama.isEmpty()) {
            valid = false;
            textInputLayout2.setError("Nama rekening diperlukan!");
        } else {
            valid = true;
            textInputLayout2.setError(null);
        }

        if (nomor.isEmpty()) {
            valid = false;
            textInputLayout3.setError("Nomor rekening diperlukan!");
        } else {
            valid = true;
            textInputLayout3.setError(null);
        }

        return valid;
    }

    private void getID() {
        progressDialog.show();
        df = database.getReference("c"+ Cons.KEY_BANK);
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
        df = database.getReference(Cons.KEY_BANK);
        df.child(String.valueOf(ID)).setValue(new BankEntity(
                ID,
                editText1.getText().toString(),
                editText2.getText().toString(),
                editText3.getText().toString(),
                false

        )).addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void mVoid){
                progressDialog.hide();
                editText1.setText(null);
                editText2.setText(null);
                editText3.setText(null);
                Snackbar.make(rootLayout,"Sukses ditambahkan!", BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });
    }

}
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
import id.t12ue.kalimutu.model.UserEntity;

public class EditorAkun extends DialogFragment {
    private TextInputEditText editText1;
    private TextInputEditText editText2;
    private TextInputLayout textInputLayout1;
    private TextInputLayout textInputLayout2;
    private FirebaseDatabase database;
    private DatabaseReference df;
    private CoordinatorLayout rootLayout;
    private ProgressDialog progressDialog;
    private UserEntity userEntity;
    private TextView title;

    public EditorAkun(UserEntity entity) {
        // Required empty public constructor
        this.userEntity=entity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.editor_akun, container, false);
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
                    if (userEntity!=null){
                        storeData();
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

        if (userEntity!=null){
            editText1.setText(userEntity.getUserName());
            editText2.setText(userEntity.getPhone());
        }

    }

    private void init(View view){
        editText1 = view.findViewById(R.id.editext1);
        editText2 = view.findViewById(R.id.editext2);
        title = view.findViewById(R.id.title);
        textInputLayout1 = view.findViewById(R.id.textInputLayout1);
        textInputLayout2 = view.findViewById(R.id.textInputLayout2);
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
        if (bank.isEmpty()) {
            valid = false;
            textInputLayout1.setError("Nama Lengkap diperlukan!");
        } else {
            valid = true;
            textInputLayout1.setError(null);
        }

        if (nama.isEmpty()) {
            valid = false;
            textInputLayout2.setError("No.Hp diperlukan!");
        } else {
            valid = true;
            textInputLayout2.setError(null);
        }

        return valid;
    }

    private void storeData(){
        progressDialog.show();
        df = database.getReference(Cons.KEY_USER);
        df.child(String.valueOf(userEntity.getToken())).setValue(new UserEntity(
                userEntity.getUserId(),
                editText1.getText().toString(),
                userEntity.getEmail(),
                editText2.getText().toString(),
                userEntity.getToken(),
                userEntity.getRole()

        )).addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void mVoid){
                progressDialog.hide();
                editText1.setText(null);
                editText2.setText(null);
                dismiss();
            }
        });
    }

}
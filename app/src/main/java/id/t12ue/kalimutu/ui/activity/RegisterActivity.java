package id.t12ue.kalimutu.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import id.t12ue.kalimutu.R;
import id.t12ue.kalimutu.helpers.Cons;
import id.t12ue.kalimutu.model.User;
import id.t12ue.kalimutu.model.UserEntity;
import id.t12ue.kalimutu.pref.UserPref;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText editText1;
    private TextInputEditText editText2;
    private TextInputEditText editText3;
    private TextInputEditText editText4;
    private TextInputLayout textInputLayout1;
    private TextInputLayout textInputLayout2;
    private TextInputLayout textInputLayout3;
    private TextInputLayout textInputLayout4;
    private FirebaseDatabase database;
    private DatabaseReference df, dfc;
    private RelativeLayout rootLayout;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    getIdUser();
                }
            }
        });
    }

    private void init(){
        editText1 = findViewById(R.id.editext1);
        editText2 = findViewById(R.id.editext2);
        editText3 = findViewById(R.id.editext3);
        editText4 = findViewById(R.id.editext4);
        textInputLayout1 = findViewById(R.id.textInputLayout1);
        textInputLayout2 = findViewById(R.id.textInputLayout2);
        textInputLayout3 = findViewById(R.id.textInputLayout3);
        textInputLayout4 = findViewById(R.id.textInputLayout4);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        rootLayout = findViewById(R.id.rootLay);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registrasi....");
        progressDialog.setCancelable(false);

    }

    public boolean validate() {
        boolean valid = false;
        String nama = editText1.getText().toString();
        String email = editText2.getText().toString();
        String phone = editText3.getText().toString();
        String password = editText4.getText().toString();
        if (nama.isEmpty()) {
            valid = false;
            textInputLayout1.setError("Isi nama dengan benar!");
        } else {
            valid = true;
            textInputLayout1.setError(null);
        }

        if (email.isEmpty()) {
            valid = false;
            textInputLayout2.setError("Isi email dengan benar!");
        } else {
            valid = true;
            textInputLayout2.setError(null);
        }

        if (phone.isEmpty()) {
            valid = false;
            textInputLayout3.setError("Isi No. HP. dengan benar!");
        } else {
            valid = true;
            textInputLayout3.setError(null);
        }

        if (password.isEmpty()) {
            valid = false;
            textInputLayout4.setError("Isi password dengan benar!");
        } else {
            if (password.length()>=8) {
                valid = true;
                textInputLayout4.setError(null);
            } else {
                valid = false;
                textInputLayout4.setError("Password terlalu pendek!");
            }
        }

        return valid;
    }

    private void getIdUser() {
        progressDialog.show();
        df = database.getReference("c"+ Cons.KEY_USER);
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
                    userRegister((Long)currentData.getValue());
                }
            }

        });
    }

    private void userRegister(long ID){
        df = database.getReference(Cons.KEY_USER);
        String nama = editText1.getText().toString().trim();
        String email = editText2.getText().toString().trim();
        String phone = editText3.getText().toString().trim();
        String pass = editText4.getText().toString().trim();
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            progressDialog.hide();
                            Snackbar.make(rootLayout, "Registrasi : " + task.getException(), BaseTransientBottomBar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(rootLayout, "Registrasi : " + task.isSuccessful(), BaseTransientBottomBar.LENGTH_SHORT).show();
                            String token= FirebaseAuth.getInstance().getCurrentUser().getUid();
                            final UserEntity userEntity = new UserEntity(
                                    ID, nama, email, phone, token, "U"
                            );

                            df.child(token).setValue(userEntity).addOnSuccessListener(new OnSuccessListener<Void>(){
                                @Override
                                public void onSuccess(Void mVoid){
                                    progressDialog.hide();
                                    UserPref.getInstance(RegisterActivity.this).userLogin(new User(
                                            String.valueOf(userEntity.getUserId()), userEntity.getUserName(),  userEntity.getRole()
                                    ));
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            });


                        }
                    }
                });
    }

}
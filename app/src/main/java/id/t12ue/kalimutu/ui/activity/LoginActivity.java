package id.t12ue.kalimutu.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.database.ValueEventListener;

import id.t12ue.kalimutu.R;
import id.t12ue.kalimutu.helpers.Cons;
import id.t12ue.kalimutu.model.User;
import id.t12ue.kalimutu.model.UserEntity;
import id.t12ue.kalimutu.pref.UserPref;
import id.t12ue.kalimutu.ui.fragment.AccountFragment;
import id.t12ue.kalimutu.ui.fragment.ResetFragment;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText editText1;
    private TextInputEditText editText2;
    private TextInputLayout textInputLayout1;
    private TextInputLayout textInputLayout2;
    private FirebaseDatabase database;
    private DatabaseReference df;
    private RelativeLayout rootLayout;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (UserPref.getInstance(this).isLoggedIn() && auth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        init();
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    userLogin();
                }
            }
        });

        findViewById(R.id.lupa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetFragment fragment = new ResetFragment();
                fragment.show(getSupportFragmentManager(), fragment.getTag());
            }
        });
    }

    private void init(){
        editText1 = findViewById(R.id.editext1);
        editText2 = findViewById(R.id.editext2);
        textInputLayout1 = findViewById(R.id.textInputLayout1);
        textInputLayout2 = findViewById(R.id.textInputLayout2);
        auth = FirebaseAuth.getInstance();
        rootLayout = findViewById(R.id.rootLayout);
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memeriksa....");
        progressDialog.setCancelable(false);
    }

    public boolean validate() {
        boolean valid = false;
        String Email = editText1.getText().toString();
        String Password = editText2.getText().toString();
        if (Email.isEmpty()) {
            valid = false;
            textInputLayout1.setError("Email diperlukan!");
        } else {
            valid = true;
            textInputLayout1.setError(null);
        }

        if (Password.isEmpty()) {
            valid = false;
            textInputLayout2.setError("Password diperlukan!");
        } else {
            valid = true;
            textInputLayout2.setError(null);
        }

        return valid;
    }

    private void userLogin(){
        final String email = editText1.getText().toString().trim();
        final String pass = editText2.getText().toString().trim();
        progressDialog.show();
        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                        getDataUser(userId);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.hide();
                Snackbar.make(rootLayout, e.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });

    }

    private void getDataUser(final String token){
        df = database.getReference(Cons.KEY_USER);
        df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(token).exists()) {
                    progressDialog.hide();
                    UserEntity data = dataSnapshot.child(token).getValue(UserEntity.class);
                    User user = new User(
                            String.valueOf(data.getUserId()),
                            data.getUserName(),
                            data.getRole()
                    );
                    UserPref.getInstance(LoginActivity.this).userLogin(user);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void resetPasword(){

    }
}
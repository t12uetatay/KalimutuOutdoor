package id.t12ue.kalimutu.ui.fragment;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import id.t12ue.kalimutu.R;

public class ResetFragment extends DialogFragment {
    private CoordinatorLayout container;
    private FirebaseAuth auth;
    private TextInputLayout textInputLayout1;
    private TextInputEditText editText;

    public ResetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reset, container, false);
        container = view.findViewById(R.id.container);
        auth = FirebaseAuth.getInstance();
        textInputLayout1 = view.findViewById(R.id.textInputLayout1);
        editText = view.findViewById(R.id.editext1);

        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().isEmpty()){
                    textInputLayout1.setError("Isi email dengan benar!");
                } else {
                    textInputLayout1.setError(null);
                    resetPassword(editText.getText().toString());
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
        getDialog().getWindow().setLayout(width, height);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    private void resetPassword(String email){
        final ProgressDialog mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("Please Waiting...");
        mDialog.show();
        mDialog.setCancelable(false);
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mDialog.dismiss();
                        if (task.isSuccessful()) {
                            SnackMsg("Kami telah mengirimkan instruksi untuk mengatur ulang kata sandi Anda lewat email!");
                        } else {
                            SnackMsg("Gagal mengirim intruksi!");
                        }
                    }
                });
    }

    private void SnackMsg(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }
}
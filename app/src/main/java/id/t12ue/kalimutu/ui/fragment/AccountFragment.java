package id.t12ue.kalimutu.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.t12ue.kalimutu.R;
import id.t12ue.kalimutu.model.BankEntity;
import id.t12ue.kalimutu.model.User;
import id.t12ue.kalimutu.model.UserEntity;
import id.t12ue.kalimutu.pref.UserPref;
import id.t12ue.kalimutu.viewmodel.UserViewModel;

public class AccountFragment extends DialogFragment {
    private TextView textName, textEmail;
    private CircleImageView imageView;
    private User user;
    private UserViewModel viewModel;
    private UserEntity entity;
    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        init(view);

        view.findViewById(R.id.fab_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditorAkun fragment = new EditorAkun(entity);
                fragment.show(getParentFragmentManager(), fragment.getTag());
                dismiss();
            }
        });

        view.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserPref.getInstance(getActivity()).userLogout();
            }
        });

        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
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

    private void init(View view){
        imageView = view.findViewById(R.id.imageView);
        textName = view.findViewById(R.id.textName);
        textEmail = view.findViewById(R.id.textEmail);
        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        user = UserPref.getInstance(getActivity()).getUser();

        try {
            entity = viewModel.readById(Long.parseLong(user.getId()));
            if (entity!=null){
                textName.setText(entity.getUserName());
                textEmail.setText(entity.getEmail()+" | "+entity.getPhone());
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }


}
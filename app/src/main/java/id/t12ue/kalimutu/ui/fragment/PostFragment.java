package id.t12ue.kalimutu.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import id.t12ue.kalimutu.R;
import id.t12ue.kalimutu.model.BankEntity;
import id.t12ue.kalimutu.model.Packages;
import id.t12ue.kalimutu.ui.activity.ImageActivity;
import id.t12ue.kalimutu.ui.activity.PostingActivity;

public class PostFragment extends DialogFragment {

    public PostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        view.findViewById(R.id.add_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Packages entity = null;
                //PostingFragment fragment = new PostingFragment(entity);
                //fragment.show(getParentFragmentManager(), fragment.getTag());
                Intent intent = new Intent(getActivity(), PostingActivity.class);
                intent.putExtra("data", entity);
                dismiss();
                startActivity(intent);
            }
        });

        view.findViewById(R.id.pay_methode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BankEntity entity = null;
                EditorBank fragment = new EditorBank(entity);
                fragment.show(getParentFragmentManager(), fragment.getTag());
                dismiss();
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
}
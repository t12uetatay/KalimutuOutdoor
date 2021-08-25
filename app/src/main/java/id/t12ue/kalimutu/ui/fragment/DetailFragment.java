package id.t12ue.kalimutu.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import id.t12ue.kalimutu.R;
import id.t12ue.kalimutu.adapter.ImageSliderAdapter;
import id.t12ue.kalimutu.helpers.Cons;
import id.t12ue.kalimutu.model.CartEntity;
import id.t12ue.kalimutu.model.ImageEntity;
import id.t12ue.kalimutu.model.Packages;
import id.t12ue.kalimutu.model.Params;
import id.t12ue.kalimutu.model.User;
import id.t12ue.kalimutu.pref.UserPref;
import id.t12ue.kalimutu.ui.activity.LoginActivity;
import id.t12ue.kalimutu.viewmodel.CartViewModel;
import id.t12ue.kalimutu.viewmodel.PackageViewModel;

public class DetailFragment extends DialogFragment {
    private ViewPager2 viewPager;
    private ImageSliderAdapter adapter;
    private LinearLayout dotsLayout;
    private PackageViewModel viewModelPackage;
    private TextView textPrice, textName, textStock, textDesc, textViewQty;
    private ImageButton bMin, bPlus, bAdd;
    private Packages packages;
    private List<ImageEntity> list;
    private CartEntity cart=null;
    private User user;
    private CartViewModel viewModel;
    private ProgressDialog progressDialog;
    private FirebaseDatabase database;
    private DatabaseReference df;
    private LinearLayout rootLayout;

    public DetailFragment(Packages packages) {
        // Required empty public constructor
        this.packages=packages;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        init(view);
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserPref.getInstance(getActivity()).isLoggedIn()){
                    if (cart!=null){
                        storeData(cart.getCartId(), cart.getQuantity());
                    } else {
                        getID();
                    }
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    dismiss();
                }
            }
        });


        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        bMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = Integer.parseInt(textViewQty.getText().toString().trim());
                if (num>1){
                    num=num-1;
                    textViewQty.setText(String.valueOf(num));
                } else {
                    Toast.makeText(getActivity(), "Jumlah kurang dari pemesanan minimum", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = Integer.parseInt(textViewQty.getText().toString().trim());
                if (packages.getStock()>num){
                    num=num+1;
                    textViewQty.setText(String.valueOf(num));
                } else {
                    Toast.makeText(getActivity(), "Jumlah melebihi stok", Toast.LENGTH_SHORT).show();
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

    private void init(View view){
        viewPager = view.findViewById(R.id.viewPager);
        dotsLayout = view.findViewById(R.id.layoutDots);
        textPrice = view.findViewById(R.id.textPrice);
        textName = view.findViewById(R.id.textName);
        textStock = view.findViewById(R.id.textStock);
        textDesc = view.findViewById(R.id.textDesc);
        textViewQty = view.findViewById(R.id.textViewQty);
        bMin = view.findViewById(R.id.bMin);
        bPlus = view.findViewById(R.id.bPlus);
        bAdd = view.findViewById(R.id.btn);
        viewModelPackage = ViewModelProviders.of(this).get(PackageViewModel.class);
        viewModel = ViewModelProviders.of(this).get(CartViewModel.class);
        list = new ArrayList<>();
        user = UserPref.getInstance(getActivity()).getUser();
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Menyimpan....");
        progressDialog.setCancelable(false);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setupCurrentIndicator(position);
            }
        });

        try {
            list=viewModelPackage.getImageEntity(packages.getPackageId());
            adapter=new ImageSliderAdapter(getActivity(), list);
            viewPager.setAdapter(adapter);
            setupIndicator();
            setupCurrentIndicator(0);
            textName.setText(packages.getPackageName());
            textStock.setText("Stok : "+packages.getStock());
            textDesc.setText(packages.getDescription());
            textPrice.setText(Cons.currencyId(packages.getPackagePrice())+"/Hari");

            if (UserPref.getInstance(getActivity()).isLoggedIn()){
                cart = viewModel.readById(new Params(Long.parseLong(user.getId()), packages.getPackageId()));
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void setupIndicator() {
        ImageView[] indicator=new ImageView[adapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(4,0,4,0);
        for (int i=0; i<indicator.length; i++){
            indicator[i]=new ImageView(getActivity());
            indicator[i].setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.indicator_inactive));
            indicator[i].setLayoutParams(layoutParams);
            dotsLayout.addView(indicator[i]);
        }

    }
    private void setupCurrentIndicator(int index) {
        int itemcildcount=dotsLayout.getChildCount();
        for (int i=0; i<itemcildcount; i++){
            ImageView imageView=(ImageView)dotsLayout.getChildAt(i);
            if (i==index){
                imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.indicator_active));
            }else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.indicator_inactive));
            }
        }

    }

    private void getID() {
        progressDialog.show();
        df = database.getReference("c"+ Cons.KEY_CART);
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
                    storeData((Long)currentData.getValue(), 0);
                }
            }

        });
    }

    private void storeData(long ID, int qty){
        progressDialog.show();
        CartEntity cartEntity = new CartEntity(
                ID,
                -1,
                Long.parseLong(user.getId()),
                packages.getPackageId(),
                Integer.parseInt(textViewQty.getText().toString())+qty
        );
        df = database.getReference(Cons.KEY_CART);
        df.child(String.valueOf(ID)).setValue(cartEntity).addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void mVoid){
                progressDialog.hide();
                viewModel.insert(cartEntity);
                dismiss();
            }
        });
    }
}
package id.t12ue.kalimutu.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import id.t12ue.kalimutu.R;
import id.t12ue.kalimutu.helpers.Cons;
import id.t12ue.kalimutu.model.BankEntity;
import id.t12ue.kalimutu.model.CartEntity;
import id.t12ue.kalimutu.model.ImageEntity;
import id.t12ue.kalimutu.model.NotificationEntity;
import id.t12ue.kalimutu.model.PackageEntity;
import id.t12ue.kalimutu.model.TransactionEntity;
import id.t12ue.kalimutu.model.User;
import id.t12ue.kalimutu.model.UserEntity;
import id.t12ue.kalimutu.pref.UserPref;
import id.t12ue.kalimutu.ui.fragment.AccountFragment;
import id.t12ue.kalimutu.ui.fragment.CartFragment;
import id.t12ue.kalimutu.ui.fragment.HomeFragment;
import id.t12ue.kalimutu.ui.fragment.NotificationFragment;
import id.t12ue.kalimutu.ui.fragment.OrderFragment;
import id.t12ue.kalimutu.ui.fragment.PostFragment;
import id.t12ue.kalimutu.ui.fragment.ShopFragment;
import id.t12ue.kalimutu.viewmodel.BankViewModel;
import id.t12ue.kalimutu.viewmodel.CartViewModel;
import id.t12ue.kalimutu.viewmodel.NotificationViewModel;
import id.t12ue.kalimutu.viewmodel.PackageViewModel;
import id.t12ue.kalimutu.viewmodel.TransactionViewModel;
import id.t12ue.kalimutu.viewmodel.UserViewModel;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView navigationView;
    private View notificationsBudge, cartsBudge;
    private BottomNavigationItemView itemView;
    private FirebaseDatabase database;
    private DatabaseReference df;
    private User user;
    private UserViewModel viewModelUser;
    private PackageViewModel viewModelPackage;
    private BankViewModel viewModelBank;
    private TransactionViewModel viewModelTransaction;
    private NotificationViewModel viewModelNotification;
    private CartViewModel viewModelCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        database = FirebaseDatabase.getInstance();
        navigationView = findViewById(R.id.navigationView);
        user = UserPref.getInstance(this).getUser();
        if (!UserPref.getInstance(this).isLoggedIn() || user.getRole().equals("U")) {
            navigationView.inflateMenu(R.menu.navigation_user);
            loadFragment(new ShopFragment());
        } else if (user.getRole().equals("A")){
            navigationView.inflateMenu(R.menu.navigation_admin);
            loadFragment(new HomeFragment());
        }

        navigationView.setOnNavigationItemSelectedListener(this);
        viewModelUser = ViewModelProviders.of(this).get(UserViewModel.class);
        viewModelPackage = ViewModelProviders.of(this).get(PackageViewModel.class);
        viewModelCart = ViewModelProviders.of(this).get(CartViewModel.class);
        viewModelBank = ViewModelProviders.of(this).get(BankViewModel.class);
        viewModelNotification = ViewModelProviders.of(this).get(NotificationViewModel.class);
        viewModelTransaction = ViewModelProviders.of(this).get(TransactionViewModel.class);

        loadPackages();
        loadImage();
        loadUser();
        loadCart();
        loadBank();
        loadNotification();
        loadTransaction();

    }

    public void onResume(){
        super.onResume();
        if (UserPref.getInstance(this).isLoggedIn()) {
            if (user.getRole().equals("U")){
                final LiveData<Long> cc = viewModelCart.countCart(Long.parseLong(user.getId()));
                cc.observe(this, new Observer<Long>() {
                    @Override
                    public void onChanged(Long entities) {
                        cartBadge(entities);
                    }

                });
            }
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.action_home:
                fragment = new HomeFragment();
                break;

            case R.id.action_shop:
                fragment = new ShopFragment();
                break;

            case R.id.action_order:
                if (UserPref.getInstance(this).isLoggedIn()){
                    fragment = new OrderFragment();
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                break;

            case R.id.action_cart:
                if (UserPref.getInstance(this).isLoggedIn()){
                    CartFragment cartFragment = new CartFragment();
                    cartFragment.show(getSupportFragmentManager(), cartFragment.getTag());
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                break;

            case R.id.action_notifikasi:
                if (UserPref.getInstance(this).isLoggedIn()){
                    fragment = new NotificationFragment();
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                break;

            case R.id.action_user:
                if (UserPref.getInstance(this).isLoggedIn()){
                    AccountFragment accountFragment = new AccountFragment();
                    accountFragment.show(getSupportFragmentManager(), accountFragment.getTag());
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                break;
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }


    public void cartBadge(long c) {
        itemView = (BottomNavigationItemView)findViewById(R.id.action_cart);
        cartsBudge = LayoutInflater.from(this).inflate(R.layout.budge, navigationView, false);
        TextView text = cartsBudge.findViewById(R.id.notif_budge);
        text.setText(String.valueOf(c));
        itemView.addView(cartsBudge);
        if (c>0){
            cartsBudge.setVisibility(View.VISIBLE);
        } else {
            cartsBudge.setVisibility(View.GONE);
        }

    }



    private void loadPackages(){
        df = database.getReference(Cons.KEY_PACKAGE);
        df.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()){
                    PackageEntity entity = mDataSnapshot.getValue(PackageEntity.class);
                    PackageEntity tmp = new PackageEntity(
                            entity.getPackageId(),
                            entity.getPackageName(),
                            entity.getPackagePrice(),
                            entity.getDescription(),
                            entity.getStock(),
                            entity.getCategory(),
                            entity.isArzip()
                    );
                    viewModelPackage.insertPackage(tmp);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError){

            }

        });
    }

    private void loadImage(){
        df = database.getReference(Cons.KEY_IMAGE);
        df.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()){
                    ImageEntity entity = mDataSnapshot.getValue(ImageEntity.class);
                    ImageEntity tmp = new ImageEntity(
                            entity.getImageId(),
                            entity.getPackageId(),
                            entity.getImageUrl()
                    );
                    viewModelPackage.insertImage(tmp);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError){

            }

        });
    }

    private void loadCart(){
        df = database.getReference(Cons.KEY_CART);
        df.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()){
                    CartEntity entity = mDataSnapshot.getValue(CartEntity.class);
                    CartEntity tmp = new CartEntity(
                            entity.getCartId(),
                            entity.getTransactionId(),
                            entity.getUserId(),
                            entity.getPackageId(),
                            entity.getQuantity()
                    );
                    viewModelCart.insert(tmp);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError){

            }

        });
    }

    private void loadUser(){
        df = database.getReference(Cons.KEY_USER);
        df.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()){
                    UserEntity entity = mDataSnapshot.getValue(UserEntity.class);
                    UserEntity tmp = new UserEntity(
                            entity.getUserId(),
                            entity.getUserName(),
                            entity.getEmail(),
                            entity.getPhone(),
                            entity.getToken(),
                            entity.getRole()
                    );
                    viewModelUser.insert(tmp);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError){

            }

        });
    }

    private void loadTransaction(){
        df = database.getReference(Cons.KEY_TRANSACTION);
        df.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()){
                    TransactionEntity entity = mDataSnapshot.getValue(TransactionEntity.class);
                    TransactionEntity tmp = new TransactionEntity(
                            entity.getTransactionId(),
                            entity.getUserId(),
                            entity.getTransactionTime(),
                            entity.getDateBegin(),
                            entity.getDateFinish(),
                            entity.getQuota(),
                            entity.getPayMethode(),
                            entity.getUrlPay(),
                            entity.getStatus()
                    );
                    viewModelTransaction.insert(tmp);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError){

            }

        });
    }

    private void loadNotification(){
        df = database.getReference(Cons.KEY_NOTIFICATION);
        df.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()){
                    NotificationEntity entity = mDataSnapshot.getValue(NotificationEntity.class);
                    NotificationEntity tmp = new NotificationEntity(
                            entity.getNotificationId(),
                            entity.getUserId(),
                            entity.getTransactionId(),
                            entity.getNotificationTime(),
                            entity.getMessage(),
                            entity.isRead()
                    );
                    viewModelNotification.insert(tmp);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError){

            }

        });
    }

    private void loadBank(){
        df = database.getReference(Cons.KEY_BANK);
        df.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()){
                    BankEntity entity = mDataSnapshot.getValue(BankEntity.class);
                    BankEntity tmp = new BankEntity(
                            entity.getBankId(),
                            entity.getBankName(),
                            entity.getRekeningName(),
                            entity.getRekeningNumber(),
                            entity.isArsip()
                    );
                    viewModelBank.insert(tmp);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError){

            }

        });
    }
}
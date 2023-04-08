package com.aditya.jsrpunt.views;

import static android.content.ContentValues.TAG;
import static android.view.View.*;

import static java.nio.file.Paths.get;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aditya.jsrpunt.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    NavHostFragment navHostFragment;
    NavController navController;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    CircleImageView imgProfile;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    TextView txtUserInfo, userWallet;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        imgProfile = findViewById(R.id.img_profile);
        mAuth = FirebaseAuth.getInstance();
        headerView = navigationView.getHeaderView(0);
        txtUserInfo = headerView.findViewById(R.id.number);
        userWallet = (TextView)headerView.findViewById(R.id.wallet_balance_profile);
        db = FirebaseFirestore.getInstance();


        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.home, R.id.transaction, R.id.id,R.id.wallet,R.id.details,R.id.terms,R.id.logOut).setOpenableLayout(drawerLayout).build();


        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        navigationView.getMenu().findItem(R.id.logOut).setOnMenuItemClickListener(menuItem ->{
            drawerLayout.closeDrawer(GravityCompat.START);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

                CookieManager.getInstance().removeAllCookies(null);
                CookieManager.getInstance().flush();
            }
            else
            {
                CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(MainActivity.this);
                cookieSyncMngr.startSync();
                CookieManager cookieManager=CookieManager.getInstance();
                cookieManager.removeAllCookie();
                cookieManager.removeSessionCookie();
                cookieSyncMngr.stopSync();
                cookieSyncMngr.sync();
            }
            Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            navController.navigate(R.id.action_global_loginFragment);
            return true;
        });

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if(destination.getId() == R.id.splashFragment || destination.getId() == R.id.loginFragment || destination.getId() == R.id.registerFragment
                  || destination.getId() == R.id.OTPFragment || destination.getId() == R.id.enterNumberFragment)
                {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    toolbar.setVisibility(GONE);
                    bottomNavigationView.setVisibility(GONE);

                } else {
                    toolbar.setVisibility(VISIBLE);
                    bottomNavigationView.setVisibility(VISIBLE);
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

                if (mAuth.getCurrentUser() != null){
                    DocumentReference docRef1 = db.collection("users").document(mAuth.getCurrentUser().getUid());
                    docRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    if (document.getData().get("phone") != null){
                                            txtUserInfo.setText(document.getData().get("phone").toString());
                                            userWallet.setText("Wallet Balance:  "+"₹"+ document.getData().get("wallet"));
                                    }else{
                                        txtUserInfo.setText(document.getData().get("email").toString());
                                        userWallet.setText("Wallet Balance:  "+"₹"+ document.getData().get("wallet"));
                                    }

                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                }

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
//        if (mAuth.getCurrentUser() != null){
//            DocumentReference docRef1 = db.collection("users").document(mAuth.getCurrentUser().getUid());
//            docRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
//                @SuppressLint("SetTextI18n")
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        if (document.exists()) {
//                            if ((Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("phone"))).toString().isEmpty()){
//                                txtUserInfo.setText(Objects.requireNonNull(document.getData().get("email")).toString());
//                                userWallet.setText("Wallet Balance:  "+"₹"+ document.getData().get("wallet"));
//                            }else{
//                                txtUserInfo.setText(Objects.requireNonNull(document.getData().get("phone")).toString());
//                                userWallet.setText("Wallet Balance:  "+"₹"+ document.getData().get("wallet"));
//                            }
//
//                        } else {
//                            Log.d(TAG, "No such document");
//                        }
//                    } else {
//                        Log.d(TAG, "get failed with ", task.getException());
//                    }
//                }
//            });
//        }

    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
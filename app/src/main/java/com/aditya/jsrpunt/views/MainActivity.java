package com.aditya.jsrpunt.views;

import static android.view.View.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import com.aditya.jsrpunt.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    NavHostFragment navHostFragment;
    NavController navController;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.home, R.id.transaction, R.id.id).setOpenableLayout(drawerLayout).build();

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if(destination.getId() == R.id.splashFragment || destination.getId() == R.id.loginFragment || destination.getId() == R.id.registerFragment
                  || destination.getId() == R.id.OTPFragment)
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
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

//    public void showSystemUI(){
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            getWindow().setDecorFitsSystemWindows(true);
//            getWindow().getDecorView().getWindowInsetsController().show(
//                    WindowInsets.Type.statusBars()
//                            | WindowInsets.Type.navigationBars()
//            );
//        }
//        else{
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_VISIBLE
//            );
//
//        }
//
//    }

//    public void hideSystemUI(){
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            getWindow().setDecorFitsSystemWindows(false);
//            getWindow().getDecorView().getWindowInsetsController().hide(
//                    WindowInsets.Type.statusBars()
//                            | WindowInsets.Type.navigationBars()
//            );
//            getWindow().getDecorView().getWindowInsetsController()
//                    .setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
//        }
//        else{
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LOW_PROFILE
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//            );
//
//        }
//    }
}
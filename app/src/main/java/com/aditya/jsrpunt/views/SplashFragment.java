package com.aditya.jsrpunt.views;

import android.os.Bundle;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aditya.jsrpunt.R;
import com.google.android.material.navigation.NavigationView;


public class SplashFragment extends Fragment {

    View view;
    NavController navController;
    NavHostFragment navHostFragment;
    DrawerLayout drawerLayout;

    public SplashFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_splash, container, false);
        navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        drawerLayout = view.findViewById(R.id.drawer);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navController.navigate(R.id.action_splashFragment_to_loginFragment);
            }
        },3000);

        return view;
    }
}
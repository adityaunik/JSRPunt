package com.aditya.jsrpunt;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class LoginFragment extends Fragment {

    View view;
    NavHostFragment navHostFragment;
    NavController navController;
    Button btnLogin;
    TextView txtRegister;
    TextView txtOTP;

    public LoginFragment() {
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
        view = inflater.inflate(R.layout.fragment_login, container, false);
        navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        btnLogin = view.findViewById(R.id.btn_login);
        txtRegister = view.findViewById(R.id.txt_goto_register);
        txtOTP = view.findViewById(R.id.txt_goto_otp);


        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_loginFragment_to_registerFragment);
            }
        });

        txtOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_loginFragment_to_OTPFragment);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_loginFragment_to_home);
            }
        });

        return view;
    }
}
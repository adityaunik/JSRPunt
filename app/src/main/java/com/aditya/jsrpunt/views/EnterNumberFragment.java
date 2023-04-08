package com.aditya.jsrpunt.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavAction;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.aditya.jsrpunt.R;
import com.hbb20.CountryCodePicker;


public class EnterNumberFragment extends Fragment {

    View view;
    CountryCodePicker ccp;
    EditText edtPhoneOTP;
    AppCompatButton btnSendOTP;
    NavHostFragment navHostFragment;
    NavController navController;

    public EnterNumberFragment() {
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
        view = inflater.inflate(R.layout.fragment_enter_number, container, false);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ccp = view.findViewById(R.id.ccp_enter_number);
        edtPhoneOTP = view.findViewById(R.id.edt_phone_enter_number);
        ccp.registerCarrierNumberEditText(edtPhoneOTP);
        btnSendOTP = view.findViewById(R.id.btn_send_OTP);
        navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();

        btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtPhoneOTP.getText().toString().isEmpty()){
                    String phone = ccp.getFullNumberWithPlus().replace(" ","");
                    NavDirections action = EnterNumberFragmentDirections.actionEnterNumberFragmentToOTPFragment(phone);
                    navController.navigate(action);
                }else {
                    if (isAdded() && !isDetached()){

                    Toast.makeText(getActivity(), "Enter phone number", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
}
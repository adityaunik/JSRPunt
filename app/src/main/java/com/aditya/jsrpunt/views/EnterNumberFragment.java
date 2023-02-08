package com.aditya.jsrpunt.views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aditya.jsrpunt.R;
import com.hbb20.CountryCodePicker;


public class EnterNumberFragment extends Fragment {

    View view;
    CountryCodePicker ccp;

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
}
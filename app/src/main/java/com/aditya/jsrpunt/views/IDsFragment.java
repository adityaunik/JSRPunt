package com.aditya.jsrpunt.views;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.aditya.jsrpunt.R;
import com.aditya.jsrpunt.adapters.ViewPagerDomainAdapter;
import com.google.android.material.tabs.TabLayout;

public class IDsFragment extends Fragment {

    View view;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerDomainAdapter adapter;

    public IDsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_i_ds, container, false);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
        adapter= new ViewPagerDomainAdapter(getChildFragmentManager());

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
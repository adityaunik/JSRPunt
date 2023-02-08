package com.aditya.jsrpunt.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.aditya.jsrpunt.views.CreateIDFragment;
import com.aditya.jsrpunt.views.MyIDFragment;

public class ViewPagerDomainAdapter extends FragmentPagerAdapter {
    public ViewPagerDomainAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        if(position == 0){
            return new CreateIDFragment();
        }
        else {
            return new MyIDFragment();
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0){
            return "Create ID";
        }
        else{
            return "My ID";
        }
    }
}

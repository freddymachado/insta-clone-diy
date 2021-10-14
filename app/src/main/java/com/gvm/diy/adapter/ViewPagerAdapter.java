package com.gvm.diy.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.gvm.diy.fragments.SearchHashtagsFragment;
import com.gvm.diy.fragments.SearchUsersFragment;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private final ArrayList<String> fragmentTitle = new ArrayList<>();
    private final ArrayList<String> fragmentData = new ArrayList<>();
    String mMessage;
    int currentItem;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

    public void addFragment(Fragment fragment, String title, String data){
        fragmentArrayList.add(fragment);
        fragmentTitle.add(title);
        fragmentData.add(data);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        Log.e("fragment",object.getClass().toString());
        /*
        if(currentItem==0){
            SearchHashtagsFragment fragment = (SearchHashtagsFragment) object;
            if(fragment!=null){
                fragment.update(mMessage);
            }
        }else{
            SearchUsersFragment fragment = (SearchUsersFragment) object;
            if(fragment!=null){
                fragment.update(mMessage);
            }

        }*/
        return super.getItemPosition(object);
    }

    public String getData(int position){
        return fragmentData.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitle.get(position);
    }

    public void setData(String mMessage, int currentItem) {
        this.mMessage = mMessage;
        this.currentItem = currentItem;
    }
}

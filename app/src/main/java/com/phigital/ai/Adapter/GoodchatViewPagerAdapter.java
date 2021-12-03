package com.phigital.ai.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.phigital.ai.Chat.GroupFragment;
import com.phigital.ai.Chat.ChatFragment;
import com.phigital.ai.Chat.OnlineFragment;

public class GoodchatViewPagerAdapter extends FragmentPagerAdapter {

    public GoodchatViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment =null;
        switch (position){
            case 0:
                fragment = new ChatFragment();
                break;
            case 1:
                fragment = new OnlineFragment();
                break;
            case 2:
                fragment = new GroupFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Chats";
            case 1:
                return "Online";
            case 2:
              return "Groups";
        }
        return null;
    }
}

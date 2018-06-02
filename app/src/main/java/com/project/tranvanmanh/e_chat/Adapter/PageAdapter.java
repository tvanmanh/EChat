package com.project.tranvanmanh.e_chat.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.project.tranvanmanh.e_chat.Fragment.MyFriendsFragment;
import com.project.tranvanmanh.e_chat.Fragment.MyMesssagesFragment;
import com.project.tranvanmanh.e_chat.Fragment.MyRequestsFragment;

/**
 * Created by tranvanmanh on 4/26/2018.
 */

public class PageAdapter extends FragmentPagerAdapter {

    final int COUNT_PAGE = 3;
    private String tabTitles[] = new String[]{"My Friends", "My Messages", "My Requests"};
    private Context context;


    public PageAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MyFriendsFragment myFriendsFragment = new MyFriendsFragment();
                return myFriendsFragment;
            case 1:
                MyMesssagesFragment myMesssagesFragment = new MyMesssagesFragment();
                return myMesssagesFragment;
            case 2:
                MyRequestsFragment myRequestsFragment = new MyRequestsFragment();
                return myRequestsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return COUNT_PAGE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}

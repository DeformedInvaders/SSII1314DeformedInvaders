package com.example.animation;

import com.example.main.AttackFragment;
import com.example.main.DownFragment;
import com.example.main.JumpFragment;
import com.example.main.RunFragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {
 
	public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
        case 0:
            return new RunFragment();
        case 1:
            return new JumpFragment();
        case 2:
            return new DownFragment();
        case 3:
            return new AttackFragment();
        }
 
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 4;
    }
 
}

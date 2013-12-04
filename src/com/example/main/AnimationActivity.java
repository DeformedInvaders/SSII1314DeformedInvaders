package com.example.main;

import com.example.animation.TabsPagerAdapter;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;

@SuppressLint("NewApi")
public class AnimationActivity extends FragmentActivity implements
ActionBar.TabListener {
	 
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_layout);
 
        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
 
        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);        
 
        // Adding Tabs
        actionBar.addTab(actionBar.newTab().setText("Run")
                    .setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("Jump")
                .setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("Get Down")
                .setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("Attack")
                .setTabListener(this));

    	}

		@Override
		public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
			// TODO Auto-generated method stub
			
		}
	
		@Override
		public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
			// TODO Auto-generated method stub
			
		}
	
		@Override
		public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
			// TODO Auto-generated method stub
			
		}
}

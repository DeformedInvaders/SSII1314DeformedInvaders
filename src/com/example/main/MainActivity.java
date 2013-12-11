package com.example.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.design.DesignActivity;

public class MainActivity extends Activity
{	
	private Thread threadTimer;
	
	private static final int segundos = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		
		threadTimer = new Thread() {
			@Override
			public void run()
			{
				try
				{
	                synchronized(this)
	                {
	                    wait(segundos*1000);
	                }
	                
					Intent intent = new Intent(MainActivity.this, DesignActivity.class);
					startActivity(intent);
				}
				catch(InterruptedException ex)
				{ 
					
				}             
			}
		};
		
		threadTimer.start(); 
	}
}

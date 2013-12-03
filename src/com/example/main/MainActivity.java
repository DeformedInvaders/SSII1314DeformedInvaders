package com.example.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

public class MainActivity extends Activity
{	
	private Thread threadProgress, threadTimer;
	private boolean procesoActivo;
	private int procesoEstado;
	
	private static final int segundos = 1;
	
	ProgressBar progress;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		
		progress = (ProgressBar) findViewById(R.id.progressBar1);
		procesoEstado = 0;
		procesoActivo = true;
		
		threadProgress = new Thread() {
			@Override
			public void run()
			{
				try
				{
	                synchronized(this)
	                {
	                    wait(segundos*1000);
	                }
	                
	                procesoActivo = false;
	                
					Intent intent = new Intent(MainActivity.this, DesignActivity.class);
					startActivity(intent);
				}
				catch(InterruptedException ex)
				{ 
					
				}             
			}
		};
		
		threadProgress.start(); 
		
		threadTimer = new Thread() {
			@Override
            public void run()
            {
                while(procesoActivo)
                {
                	try
                	{
                		synchronized(this)
    	                {
                			wait(segundos*100);
    	                }
                		
                		procesoEstado = (procesoEstado + 10)%100;
                    	progress.setProgress(procesoEstado);
                	}
                	catch(InterruptedException ex)
                	{
                		
                	}
                }
            }
		};
		
		threadTimer.start();
	}
}

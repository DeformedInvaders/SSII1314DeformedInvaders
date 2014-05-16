package com.android.touch;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

public abstract class SensorDetector implements SensorEventListener
{
	private Display mDisplay;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;

    private float[] mLastAccelerometer, mLastMagnetometer;
    private boolean mLastAccelerometerSet, mLastMagnetometerSet;

    private float[] mRotationMatrix, mOrientation, mOriginOrientation;
    private boolean[] mChangeOrientation;
    private double mAngle, mOriginAngle;
    
    private boolean mSensorCalibrated;
    
    public SensorDetector(Context context)
    {        
    	WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    	mDisplay = mWindowManager.getDefaultDisplay();
    	
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        
        mLastAccelerometer = new float[3];
        mLastMagnetometer = new float[3];
        mLastAccelerometerSet = false;
        mLastMagnetometerSet = false;

        mRotationMatrix = new float[9];
        mOrientation = new float[3];
        mOriginOrientation = new float[3];
        mChangeOrientation = new boolean[3];
        
        mAngle = 0;
        mOriginAngle = 0;
        mSensorCalibrated = false;
    }
    
    public void onSensorCalibrated()
    {
    	if(!mSensorCalibrated)
    	{
    		System.arraycopy(mOrientation, 0, mOriginOrientation, 0, mOrientation.length);
    		mOriginAngle = mAngle;
    		mSensorCalibrated = true;
    	}
    }
    
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) { }

	@Override
	public void onSensorChanged(SensorEvent event)
	{
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
		{
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        }
		else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
		{
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        
		if (mLastAccelerometerSet && mLastMagnetometerSet)
		{
            SensorManager.getRotationMatrix(mRotationMatrix, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mRotationMatrix, mOrientation);
                        
            for (int i = 0; i < mOrientation.length; i++)
            {
            	if (Math.abs(mOrientation[i] - mOriginOrientation[i]) > 0.2)
            	{     
            		mChangeOrientation[i] = true;
            	}
            }
            
            if (mChangeOrientation[1])
            {
            	if (mDisplay.getRotation() == Surface.ROTATION_0)
            	{
            		if (mOrientation[0] > 0)
                	{
                		mAngle = 180 + Math.toDegrees(mOrientation[1]);
                	}
                	else
                	{
                		mAngle = -Math.toDegrees(mOrientation[1]);
                	}
            	}
            	else if (mDisplay.getRotation() == Surface.ROTATION_180)
            	{
            		if (mOrientation[0] > 0)
                	{
                		mAngle = Math.toDegrees(mOrientation[1]);
                	}
                	else
                	{
                		mAngle = 180 - Math.toDegrees(mOrientation[1]);
                	}
            	}
            	
            	if(mSensorCalibrated)
            	{
	            	if (mOriginAngle > mAngle)
	            	{
	            		onDecreaseXAngle(mAngle);
	            	}
	            	else 
	            	{
	            		onIncreaseXAngle(mAngle);
	            	}
            	}
            }
            else
            {
            	onStabilizeXAngle(mAngle);
            }
            
            for (int i = 0; i < mOrientation.length; i++)
            {
            	mChangeOrientation[i] = false;
            }
        }
	}
	
	public void onResume()
	{
		mLastAccelerometerSet = false;
		mLastMagnetometerSet = false;
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
	}
	 
	public void onPause()
	{
		mSensorManager.unregisterListener(this);
	}
	
	/* Métodos Abstractos */
	
	public abstract void onIncreaseXAngle(double angle);
	public abstract void onDecreaseXAngle(double angle);
	public abstract void onStabilizeXAngle(double angle);
}

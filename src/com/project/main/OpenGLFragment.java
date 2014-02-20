package com.project.main;

import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public abstract class OpenGLFragment <T extends OpenGLSurfaceView> extends Fragment
{
	protected abstract void reiniciarInterfaz();
	protected abstract void actualizarInterfaz();
	
	protected void setCanvasListener(final T canvas)
	{
		canvas.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event)
			{
				canvas.onTouch(view, event);
				
				reiniciarInterfaz();
				actualizarInterfaz();
				return true;
			}
		});
	}
}
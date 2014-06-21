package com.android.view;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public abstract class OpenGLFragment<T extends OpenGLSurfaceView> extends AlertFragment
{
	/* Métodos Abstractos */

	protected abstract void resetInterface();

	protected abstract void updateInterface();

	/* Métodos Protegidos */

	protected void setCanvasListener(final T canvas)
	{
		canvas.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View view, MotionEvent event)
			{
				boolean touch = canvas.onTouch(view, event);

				resetInterface();
				updateInterface();
				return touch;
			}
		});
	}
}
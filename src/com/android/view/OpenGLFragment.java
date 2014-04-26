package com.android.view;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public abstract class OpenGLFragment<T extends OpenGLSurfaceView> extends AlertFragment
{
	/* M�todos Abstractos */

	protected abstract void reiniciarInterfaz();

	protected abstract void actualizarInterfaz();

	/* M�todos Protegidos */

	protected void setCanvasListener(final T canvas)
	{
		canvas.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View view, MotionEvent event)
			{
				boolean touch = canvas.onTouch(view, event);

				reiniciarInterfaz();
				actualizarInterfaz();
				return touch;
			}
		});
	}
}
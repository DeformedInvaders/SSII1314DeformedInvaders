package com.android.multitouch;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.OnTouchListener;

import com.project.main.OpenGLSurfaceView;
import com.project.main.TTouchEstado;

public class MultitouchGLSurfaceView extends OpenGLSurfaceView implements OnTouchListener
{
	// Renderer
    private final MultitouchOpenGLRenderer renderer;

    public MultitouchGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs, TTouchEstado.MultiTouch);

        // Asignar Renderer al GLSurfaceView
        renderer = new MultitouchOpenGLRenderer(context, NUM_HANDLES);
        setRenderer(renderer);
        setOnTouchListener(this);
    }
}

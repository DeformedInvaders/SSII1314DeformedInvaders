package com.video.video;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.opengl.OpenGLRenderer;
import com.android.opengl.TTipoFondoRenderer;
import com.android.opengl.TTipoTexturasRenderer;
import com.game.data.Personaje;
import com.video.data.Video;

public class VideoOpenGLRenderer extends OpenGLRenderer
{
	private Personaje cientifico, guitarrista;
	private boolean dibujarCientifico, dibujarGuitarrista;
	
	public VideoOpenGLRenderer(Context context, Video video)
	{
		super(context, TTipoFondoRenderer.Intercambiable, TTipoTexturasRenderer.Video);
		
		seleccionarTexturaFondo(video.getIdTexturaFondos());
		
		cientifico = video.getCientifico();
		guitarrista = video.getGuitarrista();
		
		dibujarGuitarrista = false;
		dibujarCientifico = false;
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);

		cientifico.cargarTextura(gl, this, mContext);
		guitarrista.cargarTextura(gl, this, mContext);
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{
		super.onDrawFrame(gl);

		// Centrado de Marco
		centrarPersonajeEnMarcoInicio(gl);

		if (dibujarCientifico)
		{
			cientifico.dibujar(gl, this);
		}
		
		if (dibujarGuitarrista)
		{
			guitarrista.dibujar(gl, this);
		}
		
		// Centrado de Marco
		centrarPersonajeEnMarcoFinal(gl);
	}
	
	public boolean avanzarEscena(TEstadoVideo estado)
	{
		dibujarGuitarrista = estado == TEstadoVideo.Rock;
		dibujarCientifico = estado == TEstadoVideo.Brief;		
		
		animarFondo();
		return isFondoFinal();
	}
}

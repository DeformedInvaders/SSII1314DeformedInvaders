package com.video.video;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.opengl.OpenGLRenderer;
import com.android.opengl.TTipoFondoRenderer;
import com.android.opengl.TTipoTexturasRenderer;
import com.creation.data.TTipoMovimiento;
import com.game.data.Personaje;
import com.video.data.TTipoActores;
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
		
		guitarrista.seleccionarAnimacion(TTipoMovimiento.Attack);
		cientifico.seleccionarAnimacion(TTipoMovimiento.Jump);
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
	
	public void acercarEscena()
	{
		camaraZoom(0.9f);
	}
	
	public void recuperarEscena()
	{
		camaraRestore();
	}
	
	public void animarEscena()
	{
		if (cientifico.animar(false))
		{
			cientifico.seleccionarAnimacion(TTipoMovimiento.Jump);
		}
		
		if (guitarrista.animar(false))
		{
			guitarrista.seleccionarAnimacion(TTipoMovimiento.Attack);
		}
	}
	
	public boolean avanzarEscena()
	{
		cientifico.reposo();
		guitarrista.reposo();
		
		animarFondo();
		return isFondoFinal();
	}
	
	public void activarActor(TTipoActores actor, boolean activar)
	{
		if (actor == TTipoActores.Guitarrista)
		{
			dibujarGuitarrista = activar;
		}
		else
		{
			dibujarCientifico = activar;
		}
	}
	
	/* Métodos de Guardado de Información */

	public void saveData()
	{
		cientifico.descargarTextura(this);
		guitarrista.descargarTextura(this);
	}
}

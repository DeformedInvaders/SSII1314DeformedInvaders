package com.video.video;

import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;

import com.android.opengl.OpenGLRenderer;
import com.android.opengl.TTipoFondoRenderer;
import com.android.opengl.TTipoTexturasRenderer;
import com.creation.data.TTipoMovimiento;
import com.game.data.Personaje;
import com.main.model.GamePreferences;
import com.project.main.R;
import com.video.data.ObjetoInanimado;
import com.video.data.TTipoActores;
import com.video.data.Video;

public class VideoOpenGLRenderer extends OpenGLRenderer
{
	private TEstadoVideo estadoVideo;
	private int sonidoActivado;
	
	private boolean texturasCargadas;
	
	private Personaje cientifico, guitarrista;
	private List<ObjetoInanimado> listaObjetos;

	public VideoOpenGLRenderer(Context context, Video video)
	{
		super(context, TTipoFondoRenderer.Intercambiable, TTipoTexturasRenderer.Video);
		
		seleccionarTexturaFondo(video.getIdTexturaFondos());
		
		sonidoActivado = -1;
		estadoVideo = TEstadoVideo.Nada;
		
		cientifico = video.getPersonaje(TTipoActores.Cientifico);
		guitarrista = video.getPersonaje(TTipoActores.Guitarrista);
		
		guitarrista.seleccionarAnimacion(TTipoMovimiento.Attack);
		cientifico.seleccionarAnimacion(TTipoMovimiento.Jump);
		
		listaObjetos = video.getListaObjetos();
	
		texturasCargadas = false;
		
		final ProgressDialog alert = ProgressDialog.show(mContext, mContext.getString(R.string.text_processing_video_title), mContext.getString(R.string.text_processing_video_description), true);

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run()
			{
				while(!texturasCargadas);
				alert.dismiss();
			}
		});
		
		thread.start();
	
	}	
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);

		cientifico.cargarTextura(gl, this, mContext);
		guitarrista.cargarTextura(gl, this, mContext);
		
		Iterator<ObjetoInanimado> it = listaObjetos.iterator();
		while(it.hasNext())
		{
			it.next().cargarTextura(gl, this, mContext);
		}
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		super.onSurfaceChanged(gl, width, height);
		
		texturasCargadas = true;
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{
		super.onDrawFrame(gl);
		
		// Lista Objetos
		
		for (int i = 0; i < listaObjetos.size(); i++)
		{
			ObjetoInanimado objeto = listaObjetos.get(i);
			
			if (objeto.getEstadoActivo() == estadoVideo)
			{
				objeto.dibujar(gl, this);
			}
		}
		
		// Centrado de Marco
		centrarPersonajeEnMarcoInicio(gl);

		if (estadoVideo == TEstadoVideo.Brief)
		{
			cientifico.dibujar(gl, this);
		}
		else if (estadoVideo == TEstadoVideo.Rock)
		{
			guitarrista.dibujar(gl, this);
		}
		
		// Centrado de Marco
		centrarPersonajeEnMarcoFinal(gl);
		
		if (estadoVideo == TEstadoVideo.Nada)
		{
			dibujarMarcoCompleto(gl, Color.argb(175, 0, 0, 0), GamePreferences.DEEP_OUTSIDE_FRAMES);
		}
	}
	
	@Override
	protected boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		float worldX = convertPixelXToWorldXCoordinate(pixelX, screenWidth);
		float worldY = convertPixelYToWorldYCoordinate(pixelY, screenHeight);

		for (int i = 0; i < listaObjetos.size(); i++)
		{
			ObjetoInanimado objeto = listaObjetos.get(i);
			
			if (objeto.getEstadoActivo() == estadoVideo && objeto.contains(worldX, worldY))
			{
				sonidoActivado = objeto.getSonidoActivo();
				return true;
			}
		}
		
		return false;
	} 
	
	public void acercarEscena()
	{
		camaraZoom(0.999f);
	}
	
	public void recuperarEscena()
	{
		camaraRestore();
	}
	
	public void animarEscena()
	{
		if (cientifico.animar())
		{
			cientifico.seleccionarAnimacion(TTipoMovimiento.Jump);
		}
		
		if (guitarrista.animar())
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
	
	public void seleccionarEstado(TEstadoVideo estado)
	{
		estadoVideo = estado;
	}
	
	public void desactivarEstadoSonido()
	{
		sonidoActivado = -1;
	}
	
	public int getSonidoActivado()
	{
		return sonidoActivado;
	}
	
	/* Métodos de Guardado de Información */

	public void saveData()
	{
		cientifico.descargarTextura(this);
		guitarrista.descargarTextura(this);
		
		Iterator<ObjetoInanimado> it = listaObjetos.iterator();
		while(it.hasNext())
		{
			it.next().descargarTextura(this);
		}
	}
}
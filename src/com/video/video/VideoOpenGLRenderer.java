package com.video.video;

import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;

import com.android.opengl.OpenGLRenderer;
import com.android.opengl.TTypeBackgroundRenderer;
import com.android.opengl.TTypeTexturesRenderer;
import com.creation.data.TTypeMovement;
import com.game.data.Character;
import com.main.model.GamePreferences;
import com.project.main.R;
import com.video.data.InanimatedObject;
import com.video.data.TTypeActors;
import com.video.data.Video;

public class VideoOpenGLRenderer extends OpenGLRenderer
{
	private TStateVideo estadoVideo;
	private int sonidoActivado;
	
	private boolean texturasCargadas;
	
	private Character cientifico, guitarrista;
	private List<InanimatedObject> listaObjetos;

	public VideoOpenGLRenderer(Context context, Video video)
	{
		super(context, TTypeBackgroundRenderer.Swappable, TTypeTexturesRenderer.Video);
		
		seleccionarTexturaFondo(video.getIdTexturaFondos());
		
		sonidoActivado = -1;
		estadoVideo = TStateVideo.Nothing;
		
		cientifico = video.getPersonaje(TTypeActors.Scientific);
		guitarrista = video.getPersonaje(TTypeActors.Guitarist);
		
		guitarrista.seleccionarAnimacion(TTypeMovement.Attack);
		cientifico.seleccionarAnimacion(TTypeMovement.Jump);
		
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
		
		Iterator<InanimatedObject> it = listaObjetos.iterator();
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
			InanimatedObject objeto = listaObjetos.get(i);
			
			if (objeto.getEstadoActivo() == estadoVideo)
			{
				objeto.dibujar(gl, this);
			}
		}
		
		// Centrado de Marco
		centrarPersonajeEnMarcoInicio(gl);

		if (estadoVideo == TStateVideo.Brief)
		{
			cientifico.dibujar(gl, this);
		}
		else if (estadoVideo == TStateVideo.Rock)
		{
			guitarrista.dibujar(gl, this);
		}
		
		// Centrado de Marco
		centrarPersonajeEnMarcoFinal(gl);
		
		if (estadoVideo == TStateVideo.Nothing)
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
			InanimatedObject objeto = listaObjetos.get(i);
			
			if (objeto.getEstadoActivo() == estadoVideo && objeto.contains(worldX, worldY))
			{
				sonidoActivado = objeto.getSonidoActivo();
				return true;
			}
		}
		
		return false;
	} 
	
	public void acercarEscena(float factor)
	{
		camaraZoom(factor);
	}
	
	public void animarEscena()
	{
		if (cientifico.animar())
		{
			cientifico.seleccionarAnimacion(TTypeMovement.Jump);
		}
		
		if (guitarrista.animar())
		{
			guitarrista.seleccionarAnimacion(TTypeMovement.Attack);
		}
	}
	
	public boolean avanzarEscena()
	{
		cientifico.reposo();
		guitarrista.reposo();
		
		animarFondo();
		return isFondoFinal();
	}
	
	public void seleccionarEstado(TStateVideo estado)
	{
		estadoVideo = estado;
		camaraRestore();
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
		
		Iterator<InanimatedObject> it = listaObjetos.iterator();
		while(it.hasNext())
		{
			it.next().descargarTextura(this);
		}
	}
}
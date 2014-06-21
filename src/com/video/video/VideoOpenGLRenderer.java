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
		
		selectBackground(video.getListBackgrounds());
		
		sonidoActivado = -1;
		estadoVideo = TStateVideo.Nothing;
		
		cientifico = video.getActor(TTypeActors.Scientific);
		guitarrista = video.getActor(TTypeActors.Guitarist);
		
		guitarrista.selectMovement(TTypeMovement.Attack);
		cientifico.selectMovement(TTypeMovement.Jump);
		
		listaObjetos = video.getListObjects();
	
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

		cientifico.loadTexture(gl, this, mContext);
		guitarrista.loadTexture(gl, this, mContext);
		
		Iterator<InanimatedObject> it = listaObjetos.iterator();
		while(it.hasNext())
		{
			it.next().loadTexture(gl, this, mContext);
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
			
			if (objeto.getStateActive() == estadoVideo)
			{
				objeto.drawTexture(gl, this);
			}
		}
		
		// Centrado de Marco
		drawInsideFrameBegin(gl);

		if (estadoVideo == TStateVideo.Brief)
		{
			cientifico.drawTexture(gl, this);
		}
		else if (estadoVideo == TStateVideo.Rock)
		{
			guitarrista.drawTexture(gl, this);
		}
		
		// Centrado de Marco
		drawInsideFrameEnd(gl);
		
		if (estadoVideo == TStateVideo.Nothing)
		{
			drawFrameFill(gl, Color.argb(175, 0, 0, 0), GamePreferences.DEEP_OUTSIDE_FRAMES);
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
			
			if (objeto.getStateActive() == estadoVideo && objeto.contains(worldX, worldY))
			{
				sonidoActivado = objeto.getSoundActive();
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
		if (cientifico.animateTexture())
		{
			cientifico.selectMovement(TTypeMovement.Jump);
		}
		
		if (guitarrista.animateTexture())
		{
			guitarrista.selectMovement(TTypeMovement.Attack);
		}
	}
	
	public boolean avanzarEscena()
	{
		cientifico.stopAnimation();
		guitarrista.stopAnimation();
		
		moveBackground();
		return isBackgroundEnded();
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
		cientifico.deleteTexture(this);
		guitarrista.deleteTexture(this);
		
		Iterator<InanimatedObject> it = listaObjetos.iterator();
		while(it.hasNext())
		{
			it.next().deleteTexture(this);
		}
	}
}
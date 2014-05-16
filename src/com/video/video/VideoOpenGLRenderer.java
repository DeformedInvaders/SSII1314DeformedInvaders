package com.video.video;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;

import com.android.opengl.OpenGLRenderer;
import com.android.opengl.TTipoFondoRenderer;
import com.android.opengl.TTipoTexturasRenderer;
import com.creation.data.Handle;
import com.creation.data.TTipoMovimiento;
import com.game.data.Personaje;
import com.lib.math.Rectangle;
import com.main.model.GamePreferences;
import com.project.main.R;
import com.video.data.TTipoActores;
import com.video.data.Video;

public class VideoOpenGLRenderer extends OpenGLRenderer
{
	private TEstadoVideo estadoVideo;
	private TEstadoSonido estadoSonido;
	
	private boolean texturasCargadas;
	
	private Personaje cientifico, guitarrista;
	
	private Rectangle areaAgua, areaElectricidad, areaMonitores, areaMicrofono, areaAltavoz;
	private Handle handleAgua, handleElectricidad, handleMonitores1, handleMonitores2, handleMicrofono, handleAltavoz;
	
	public VideoOpenGLRenderer(Context context, Video video)
	{
		super(context, TTipoFondoRenderer.Intercambiable, TTipoTexturasRenderer.Video);
		
		seleccionarTexturaFondo(video.getIdTexturaFondos());
		
		estadoSonido = TEstadoSonido.Nada;
		estadoVideo = TEstadoVideo.Nada;
		
		cientifico = video.getPersonaje(TTipoActores.Cientifico);
		guitarrista = video.getPersonaje(TTipoActores.Guitarrista);
		
		guitarrista.seleccionarAnimacion(TTipoMovimiento.Attack);
		cientifico.seleccionarAnimacion(TTipoMovimiento.Jump);
	
		// FIXME Calcular distancias en función de la pantalla.
		areaAgua = new Rectangle(100, 20, 210, 560);
		areaElectricidad = new Rectangle(1010, 20, 170, 325);
		areaMonitores = new Rectangle(310, 210, 120, 80);
		
		areaMicrofono = new Rectangle(225, 20, 190, 290);
		areaAltavoz = new Rectangle(800, 50, 350, 420);
	
		handleAgua = new Handle(areaAgua.getX(), areaAgua.getY(), areaAgua.getWidth(), areaAgua.getHeight(), Color.RED);
		handleElectricidad = new Handle(areaElectricidad.getX(), areaElectricidad.getY(), areaElectricidad.getWidth(), areaElectricidad.getHeight(), Color.YELLOW);
		handleMonitores1 = new Handle(areaMonitores.getX(), areaMonitores.getY(), areaMonitores.getWidth(), areaMonitores.getHeight(), Color.GREEN);
		handleMonitores2 = new Handle(areaMonitores.getX() + 580, areaMonitores.getY(), areaMonitores.getWidth(), areaMonitores.getHeight(), Color.BLUE);
	
		handleMicrofono = new Handle(areaMicrofono.getX(), areaMicrofono.getY(), areaMicrofono.getWidth(), areaMicrofono.getHeight(), Color.RED);
		handleAltavoz = new Handle(areaAltavoz.getX(), areaAltavoz.getY(), areaAltavoz.getWidth(), areaAltavoz.getHeight(), Color.BLUE);
	
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
		
		if (GamePreferences.IS_DEBUG_ENABLED())
		{
			if (estadoVideo == TEstadoVideo.Brief)
			{
				handleAgua.dibujar(gl);
				handleElectricidad.dibujar(gl);
				handleMonitores1.dibujar(gl);
				handleMonitores2.dibujar(gl);
			}
			else if (estadoVideo == TEstadoVideo.Rock)
			{
				handleMicrofono.dibujar(gl);
				handleAltavoz.dibujar(gl);
			}
		}
		
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

		switch(estadoVideo)
		{
			case Door:
				return comprobarAreaDoor(worldX, worldY);
			case Rock:
				return comprobarAreaRock(worldX, worldY);
			case Noise:
				return comprobarAreaNoise(worldX, worldY);
			case Brief:
				return comprobarAreaBrief(worldX, worldY);
			default:
				return false;
		}
	}  
	
	private boolean comprobarAreaDoor(float worldX, float worldY)
	{
		estadoSonido = TEstadoSonido.Puerta;
		return true;
	}
	
	private boolean comprobarAreaRock(float worldX, float worldY)
	{
		if (areaMicrofono.contains(worldX, worldY))
		{
			estadoSonido = TEstadoSonido.Microfono;
			return true;
		}
		
		if (areaAltavoz.contains(worldX, worldY))
		{
			estadoSonido = TEstadoSonido.Altavoz;
			return true;
		}
		
		return false;
	}
	
	private boolean comprobarAreaNoise(float worldX, float worldY)
	{
		estadoSonido = TEstadoSonido.Trastos;
		return true;
	}
	
	private boolean comprobarAreaBrief(float worldX, float worldY)
	{
		if (areaAgua.contains(worldX, worldY))
		{
			estadoSonido = TEstadoSonido.Agua;
			return true;
		}
		
		if (areaElectricidad.contains(worldX, worldY))
		{
			estadoSonido = TEstadoSonido.Electricidad;
			return true;
		}
		
		if (areaMonitores.contains(worldX, worldY))
		{
			estadoSonido = TEstadoSonido.Monitores;
			return true;
		}
		
		areaMonitores.setX(areaMonitores.getX() + 580);
		if (areaMonitores.contains(worldX, worldY))
		{
			areaMonitores.setX(areaMonitores.getX() - 580);
			estadoSonido = TEstadoSonido.Monitores;
			return true;
		}
		
		areaMonitores.setX(areaMonitores.getX() - 580);
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
		estadoSonido = TEstadoSonido.Nada;
	}
	
	public TEstadoSonido isEstadoSonido()
	{
		return estadoSonido;
	}
	
	/* Métodos de Guardado de Información */

	public void saveData()
	{
		cientifico.descargarTextura(this);
		guitarrista.descargarTextura(this);
	}
}
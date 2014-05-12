package com.video.video;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

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
import com.video.data.TTipoActores;
import com.video.data.Video;

public class VideoOpenGLRenderer extends OpenGLRenderer
{
	private TEstadoSonido estado;
	
	private Personaje cientifico, guitarrista;
	private boolean dibujarCientifico, dibujarGuitarrista;
	
	private Rectangle areaAgua, areaElectricidad, areaMonitores;
	private Handle handleAgua, handleElectricidad, handleMonitores1, handleMonitores2;
	
	public VideoOpenGLRenderer(Context context, Video video)
	{
		super(context, TTipoFondoRenderer.Intercambiable, TTipoTexturasRenderer.Video);
		
		seleccionarTexturaFondo(video.getIdTexturaFondos());
		
		estado = TEstadoSonido.Nada;
		
		cientifico = video.getCientifico();
		guitarrista = video.getGuitarrista();
		
		dibujarGuitarrista = false;
		dibujarCientifico = false;
		
		guitarrista.seleccionarAnimacion(TTipoMovimiento.Attack);
		cientifico.seleccionarAnimacion(TTipoMovimiento.Jump);
	
		areaAgua = new Rectangle(100, 20, 210, 560);
		areaElectricidad = new Rectangle(1010, 20, 170, 325);
		areaMonitores = new Rectangle(310, 210, 120, 80);
	
		handleAgua = new Handle(areaAgua.getX(), areaAgua.getY(), areaAgua.getWidth(), areaAgua.getHeight(), Color.RED);
		handleElectricidad = new Handle(areaElectricidad.getX(), areaElectricidad.getY(), areaElectricidad.getWidth(), areaElectricidad.getHeight(), Color.YELLOW);
		handleMonitores1 = new Handle(areaMonitores.getX(), areaMonitores.getY(), areaMonitores.getWidth(), areaMonitores.getHeight(), Color.GREEN);
		handleMonitores2 = new Handle(areaMonitores.getX() + 580, areaMonitores.getY(), areaMonitores.getWidth(), areaMonitores.getHeight(), Color.BLUE);
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
		
		if (GamePreferences.IS_DEBUG_ENABLED())
		{			
			handleAgua.dibujar(gl);
			handleElectricidad.dibujar(gl);
			handleMonitores1.dibujar(gl);
			handleMonitores2.dibujar(gl);
		}
	}
	
	@Override
	protected boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		float worldX = convertPixelXToWorldXCoordinate(pixelX, screenWidth);
		float worldY = convertPixelYToWorldYCoordinate(pixelY, screenHeight);

		if (areaAgua.contains(worldX, worldY))
		{
			estado = TEstadoSonido.Agua;
			return true;
		}
		
		if (areaElectricidad.contains(worldX, worldY))
		{
			estado = TEstadoSonido.Electricidad;
			return true;
		}
		
		if (areaMonitores.contains(worldX, worldY))
		{
			estado = TEstadoSonido.Monitores;
			return true;
		}
		
		areaMonitores.setX(areaMonitores.getX() + 580);
		if (areaMonitores.contains(worldX, worldY))
		{
			areaMonitores.setX(areaMonitores.getX() - 580);
			estado = TEstadoSonido.Monitores;
			return true;
		}
		
		areaMonitores.setX(areaMonitores.getX() - 580);
		return false;
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
	
	public void desactivarEstadoSonido()
	{
		estado = TEstadoSonido.Nada;
	}
	
	public TEstadoSonido isEstadoSonido()
	{
		return estado;
	}
	
	/* Métodos de Guardado de Información */

	public void saveData()
	{
		cientifico.descargarTextura(this);
		guitarrista.descargarTextura(this);
	}
}
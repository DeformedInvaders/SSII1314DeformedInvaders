package com.character.display;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.android.opengl.OpenGLRenderer;
import com.android.opengl.TTypeBackgroundRenderer;
import com.android.opengl.TTypeTexturesRenderer;
import com.creation.data.BitmapImage;
import com.creation.data.TTypeMovement;
import com.game.data.Character;
import com.game.game.TStateGame;
import com.main.model.GamePreferences;

public class DisplayOpenGLRenderer extends OpenGLRenderer 
{
	private TStateDisplay estado;

	// Personaje
	private Character personaje;
	private boolean personajeCargado;

	// Captura
	private Bitmap captura;
	private TStateScreenshot estadoCaptura;

	/* Constructura */

	public DisplayOpenGLRenderer(Context context)
	{
		super(context, TTypeBackgroundRenderer.Blank, TTypeTexturesRenderer.Character);

		GamePreferences.SET_GAME_PARAMETERS(TStateGame.Nothing);
		
		personajeCargado = false;

		estado = TStateDisplay.Nothing;
		estadoCaptura = TStateScreenshot.Nothing;
	}

	public DisplayOpenGLRenderer(Context context, Character p)
	{
		super(context, TTypeBackgroundRenderer.Blank, TTypeTexturesRenderer.Character);
		
		GamePreferences.SET_GAME_PARAMETERS(TStateGame.Nothing);
		
		personajeCargado = true;
		personaje = p;

		estado = TStateDisplay.Nothing;
		estadoCaptura = TStateScreenshot.Nothing;
	}

	/* Métodos Renderer */

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);

		if (personajeCargado)
		{
			personaje.loadTexture(gl, this, mContext);
		}
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{
		super.onDrawFrame(gl);

		if (personajeCargado)
		{
			if (estadoCaptura == TStateScreenshot.Preparing)
			{
				// Marco Oscuro
				drawFrameInside(gl, Color.WHITE, GamePreferences.DEEP_INSIDE_FRAMES);
			}
			
			// Centrado de Marco
			drawInsideFrameBegin(gl);

			personaje.drawTexture(gl, this);

			// Centrado de Marco
			drawInsideFrameEnd(gl);

			if (estado == TStateDisplay.Nothing || estado == TStateDisplay.Screenshot)
			{
				if (estado == TStateDisplay.Screenshot)
				{
					if (estadoCaptura == TStateScreenshot.Capturing)
					{
						// Capturar Pantalla
						BitmapImage textura = getScreenshot(gl);
						captura = textura.getBitmap();

						// Desactivar Modo Captura
						estadoCaptura = TStateScreenshot.Finished;

						// Restaurar posición anterior de la Cámara
						camaraRestore();

						// Reiniciar Renderer
						super.onDrawFrame(gl);

						// Centrado de Marco
						drawInsideFrameBegin(gl);

						personaje.drawTexture(gl, this);

						// Centrado de Marco
						drawInsideFrameEnd(gl);
					}
				}
			}
		}
	}

	/* Métodos de Modificación de Estado */

	public void seleccionarRetoque(float height, float width)
	{
		estado = TStateDisplay.Screenshot;
		estadoCaptura = TStateScreenshot.Preparing;
	}

	public void seleccionarCaptura()
	{
		if (estado == TStateDisplay.Screenshot)
		{
			estadoCaptura = TStateScreenshot.Capturing;
		}
	}

	public void seleccionarTerminado()
	{
		if (estado == TStateDisplay.Screenshot)
		{
			estado = TStateDisplay.Nothing;
			estadoCaptura = TStateScreenshot.Nothing;
		}
	}

	public boolean reproducirAnimacion()
	{
		return personaje.animateTexture();
	}

	public void seleccionarReposo()
	{
		personaje.stopAnimation();
		
		estado = TStateDisplay.Nothing;
		estadoCaptura = TStateScreenshot.Nothing;
	}

	public void seleccionarAnimacion(TTypeMovement movimiento)
	{
		personaje.selectMovement(movimiento);
		
		estado = TStateDisplay.Animation;
		estadoCaptura = TStateScreenshot.Nothing;
	}

	/* Métodos de Obtención de Información */

	public boolean isEstadoReposo()
	{
		return estado == TStateDisplay.Nothing;
	}

	public boolean isEstadoRetoque()
	{
		return estado == TStateDisplay.Screenshot && estadoCaptura == TStateScreenshot.Preparing;
	}

	public boolean isEstadoCapturando()
	{
		return estado == TStateDisplay.Screenshot && estadoCaptura == TStateScreenshot.Capturing;
	}

	public boolean isEstadoTerminado()
	{
		return estado == TStateDisplay.Screenshot && estadoCaptura == TStateScreenshot.Finished;
	}

	public boolean isEstadoAnimacion()
	{
		return estado != TStateDisplay.Nothing && estado != TStateDisplay.Screenshot;
	}

	public Bitmap getCapturaPantalla()
	{
		if (estadoCaptura == TStateScreenshot.Capturing)
		{
			while (estadoCaptura != TStateScreenshot.Finished);

			return captura;
		}

		return null;
	}

	/* Métodos de Guardado de Información */

	public void saveData()
	{
		if (personajeCargado)
		{
			personaje.deleteTexture(this);
		}
	}
}

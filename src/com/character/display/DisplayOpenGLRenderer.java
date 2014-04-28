package com.character.display;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.android.view.OpenGLRenderer;
import com.creation.data.MapaBits;
import com.game.data.Personaje;
import com.main.model.GamePreferences;

public class DisplayOpenGLRenderer extends OpenGLRenderer 
{
	private TEstadoDisplay estado;

	// Personaje
	private Personaje personaje;
	private boolean personajeCargado;

	// Captura
	private Bitmap captura;
	private TEstadoCaptura estadoCaptura;

	/* Constructura */

	public DisplayOpenGLRenderer(Context context, int color)
	{
		super(context, color);

		personajeCargado = false;

		estado = TEstadoDisplay.Nada;
		estadoCaptura = TEstadoCaptura.Nada;
	}

	public DisplayOpenGLRenderer(Context context, int color, Personaje p)
	{
		super(context, color);

		personajeCargado = true;
		personaje = p;
		personaje.desactivarBurbuja();
		personaje.reiniciarVidas();

		estado = TEstadoDisplay.Nada;
		estadoCaptura = TEstadoCaptura.Nada;
	}

	/* Métodos Renderer */

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);

		if (personajeCargado)
		{
			personaje.cargarTextura(gl, this, mContext);
		}
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{
		super.onDrawFrame(gl);

		if (personajeCargado)
		{
			if (estadoCaptura == TEstadoCaptura.Retocando)
			{
				// Marco Oscuro
				dibujarMarcoInterior(gl, Color.WHITE, GamePreferences.DEEP_INSIDE_FRAMES);
			}
			
			// Centrado de Marco
			centrarPersonajeEnMarcoInicio(gl);

			personaje.dibujar(gl, this);

			// Centrado de Marco
			centrarPersonajeEnMarcoFinal(gl);

			if (estado == TEstadoDisplay.Nada || estado == TEstadoDisplay.Captura)
			{
				if (estado == TEstadoDisplay.Captura)
				{
					if (estadoCaptura == TEstadoCaptura.Capturando)
					{
						// Capturar Pantalla
						MapaBits textura = capturaPantalla(gl);
						captura = textura.getBitmap();

						// Desactivar Modo Captura
						estadoCaptura = TEstadoCaptura.Terminado;

						// Restaurar posición anterior de la Cámara
						camaraRestore();

						// Reiniciar Renderer
						super.onDrawFrame(gl);

						// Centrado de Marco
						centrarPersonajeEnMarcoInicio(gl);

						personaje.dibujar(gl, this);

						// Centrado de Marco
						centrarPersonajeEnMarcoFinal(gl);
					}
				}
			}
		}
	}

	/* Métodos de Modificación de Estado */

	public void seleccionarRetoque(float height, float width)
	{
		// Construir rectangulos
		estado = TEstadoDisplay.Captura;
		estadoCaptura = TEstadoCaptura.Retocando;
	}

	public void seleccionarCaptura()
	{
		if (estado == TEstadoDisplay.Captura)
		{
			estadoCaptura = TEstadoCaptura.Capturando;
		}
	}

	public void seleccionarTerminado()
	{
		if (estado == TEstadoDisplay.Captura)
		{
			estado = TEstadoDisplay.Nada;
			estadoCaptura = TEstadoCaptura.Nada;
		}
	}

	public boolean reproducirAnimacion()
	{
		return personaje.animar(false);
	}

	public void seleccionarReposo()
	{
		personaje.reposo();
	}

	public void seleccionarRun()
	{
		estado = TEstadoDisplay.Run;
		personaje.mover();
	}

	public void seleccionarJump()
	{
		estado = TEstadoDisplay.Jump;
		personaje.saltar();
	}

	public void seleccionarCrouch()
	{
		estado = TEstadoDisplay.Crouch;
		personaje.agachar();
	}

	public void seleccionarAttack()
	{
		estado = TEstadoDisplay.Attack;
		personaje.atacar();
	}

	/* Métodos de Obtención de Información */

	public boolean isEstadoReposo()
	{
		return estado == TEstadoDisplay.Nada;
	}

	public boolean isEstadoRetoque()
	{
		return estado == TEstadoDisplay.Captura && estadoCaptura == TEstadoCaptura.Retocando;
	}

	public boolean isEstadoCapturando()
	{
		return estado == TEstadoDisplay.Captura && estadoCaptura == TEstadoCaptura.Retocando;
	}

	public boolean isEstadoTerminado()
	{
		return estado == TEstadoDisplay.Captura && estadoCaptura == TEstadoCaptura.Terminado;
	}

	public boolean isEstadoAnimacion()
	{
		return estado != TEstadoDisplay.Nada && estado != TEstadoDisplay.Captura;
	}

	public Bitmap getCapturaPantalla()
	{
		if (estadoCaptura == TEstadoCaptura.Capturando)
		{
			while (estadoCaptura != TEstadoCaptura.Terminado);

			return captura;
		}

		return null;
	}

	/* Métodos de Guardado de Información */

	public void saveData()
	{
		if (personajeCargado)
		{
			personaje.descargarTextura(this);
		}
	}
}

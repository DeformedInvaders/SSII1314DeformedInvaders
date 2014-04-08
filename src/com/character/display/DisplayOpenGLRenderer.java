package com.character.display;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.view.OpenGLRenderer;
import com.creation.data.MapaBits;
import com.game.data.Personaje;

public class DisplayOpenGLRenderer extends OpenGLRenderer 
{
	private TDisplayEstado estado;

	// Personaje
	private Personaje personaje;
	private boolean personajeCargado;

	// Captura
	private Bitmap captura;
	private TCapturaEstado estadoCaptura;

	/* SECTION Constructura */

	public DisplayOpenGLRenderer(Context context)
	{
		super(context);

		personajeCargado = false;

		estado = TDisplayEstado.Nada;
		estadoCaptura = TCapturaEstado.Nada;
	}

	public DisplayOpenGLRenderer(Context context, Personaje p)
	{
		super(context);

		personajeCargado = true;
		personaje = p;

		estado = TDisplayEstado.Nada;
		estadoCaptura = TCapturaEstado.Nada;
	}

	/* SECTION Métodos Renderer */

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
			// Centrado de Marco
			centrarPersonajeEnMarcoInicio(gl);

			personaje.dibujar(gl, this);

			// Centrado de Marco
			centrarPersonajeEnMarcoFinal(gl);

			if (estado == TDisplayEstado.Nada || estado == TDisplayEstado.Captura)
			{
				if (estado == TDisplayEstado.Captura)
				{
					if (estadoCaptura == TCapturaEstado.Capturando)
					{
						// Capturar Pantalla
						MapaBits textura = capturaPantalla(gl);
						captura = textura.getBitmap();

						// Desactivar Modo Captura
						estadoCaptura = TCapturaEstado.Terminado;

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
					else if (estadoCaptura == TCapturaEstado.Retocando)
					{
						// Marco Oscuro
						dibujarMarcoLateral(gl);
						dibujarMarcoCentral(gl);
					}
				}
			}
		}
	}

	/* SECTION Métodos abstractos de OpenGLRenderer */

	@Override
	protected boolean reiniciar()
	{
		return false;
	}

	@Override
	protected boolean onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}

	@Override
	protected boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}

	@Override
	protected boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}

	@Override
	protected boolean onMultiTouchEvent()
	{
		return false;
	}

	/* SECTION Métodos de Modificación de Estado */

	public void seleccionarRetoque(float height, float width)
	{
		// Construir rectangulos
		estado = TDisplayEstado.Captura;
		estadoCaptura = TCapturaEstado.Retocando;
	}

	public void seleccionarCaptura()
	{
		if (estado == TDisplayEstado.Captura)
		{
			estadoCaptura = TCapturaEstado.Capturando;
		}
	}

	public void seleccionarTerminado()
	{
		if (estado == TDisplayEstado.Captura)
		{
			estado = TDisplayEstado.Nada;
			estadoCaptura = TCapturaEstado.Nada;
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
		estado = TDisplayEstado.Run;
		personaje.mover();
	}

	public void seleccionarJump()
	{
		estado = TDisplayEstado.Jump;
		personaje.saltar();
	}

	public void seleccionarCrouch()
	{
		estado = TDisplayEstado.Crouch;
		personaje.agachar();
	}

	public void seleccionarAttack()
	{
		estado = TDisplayEstado.Attack;
		personaje.atacar();
	}

	/* SECTION Métodos de Obtención de Información */

	public boolean isEstadoReposo()
	{
		return estado == TDisplayEstado.Nada;
	}

	public boolean isEstadoRetoque()
	{
		return estado == TDisplayEstado.Captura && estadoCaptura == TCapturaEstado.Retocando;
	}

	public boolean isEstadoCapturando()
	{
		return estado == TDisplayEstado.Captura && estadoCaptura == TCapturaEstado.Retocando;
	}

	public boolean isEstadoTerminado()
	{
		return estado == TDisplayEstado.Captura && estadoCaptura == TCapturaEstado.Terminado;
	}

	public boolean isEstadoAnimacion()
	{
		return estado != TDisplayEstado.Nada && estado != TDisplayEstado.Captura;
	}

	public Bitmap getCapturaPantalla()
	{
		if (estadoCaptura == TCapturaEstado.Capturando)
		{
			while (estadoCaptura != TCapturaEstado.Terminado);

			return captura;
		}

		return null;
	}

	/* SECTION Métodos de Guardado de Información */

	public void saveData()
	{
		if (personajeCargado)
		{
			personaje.descargarTextura(this);
		}
	}
}

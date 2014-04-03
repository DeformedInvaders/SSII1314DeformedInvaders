package com.game.game;

import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.view.OpenGLRenderer;
import com.game.data.Background;
import com.game.data.Entidad;
import com.game.data.InstanciaEntidad;
import com.game.data.InstanciaNivel;
import com.game.data.Personaje;
import com.project.main.GamePreferences;

public class GameOpenGLRenderer extends OpenGLRenderer
{
	// Background
	private Background background;

	// Protagonista
	private Personaje personaje;

	// Enemigos
	private List<Entidad> tipoEnemigos;
	private List<InstanciaEntidad> listaEnemigos;

	/* SECTION Constructura */

	public GameOpenGLRenderer(Context context, Personaje p, InstanciaNivel l)
	{
		super(context);

		personaje = p;
		background = l.getFondoNivel();

		tipoEnemigos = l.getTipoEnemigos();
		listaEnemigos = l.getListaEnemigos();
		
		personaje.reiniciarVidas();
		personaje.activarBurbuja();
	}

	/* SECTION Métodos Renderer */

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);

		// BackGround
		seleccionarTexturaFondo(background.getIdTexturaFondo1(), background.getIdTexturaFondo2(), background.getIdTexturaFondo3());

		// Protagonista
		personaje.cargarTextura(gl, this);

		// Lista Enemigos
		Iterator<Entidad> it = tipoEnemigos.iterator();
		while (it.hasNext())
		{
			it.next().cargarTextura(gl, this);
		}
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{
		super.onDrawFrame(gl);

		gl.glPushMatrix();

			gl.glTranslatef(GamePreferences.DISTANCE_RIGHT, GamePreferences.DISTANCE_BOTTOM, 0.0f);
	
			// Escala del Juego
			gl.glPushMatrix();
	
				gl.glScalef(0.5f, 0.5f, 0.0f);
		
				// Protagonista
				personaje.dibujar(gl, this);
				
				// Burbuja
	
			gl.glPopMatrix();
			
			// Vidas
	
			// Cola Enemigos
			Iterator<InstanciaEntidad> it = listaEnemigos.iterator();
			while (it.hasNext())
			{
				InstanciaEntidad instancia = it.next();
				Entidad entidad = tipoEnemigos.get(instancia.getIdEntidad());
				instancia.dibujar(gl, this, entidad);
			}

		gl.glPopMatrix();
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

	public boolean reproducirAnimacion()
	{
		// Background
		desplazarFondo();

		// Lista Enemigos
		Iterator<InstanciaEntidad> it = listaEnemigos.iterator();
		while (it.hasNext())
		{
			InstanciaEntidad instancia = it.next();
			Entidad entidad = tipoEnemigos.get(instancia.getIdEntidad());
			instancia.avanzar(this, entidad);
		}

		// Personaje
		personaje.animar();
		return personaje.avanzar(this);
	}

	public void pararAnimacion()
	{
		personaje.reposo();
	}

	public void seleccionarRun()
	{
		personaje.mover();
	}

	public void seleccionarJump()
	{
		personaje.saltar();
	}

	public void seleccionarCrouch()
	{
		personaje.agachar();
	}

	public void seleccionarAttack()
	{
		personaje.atacar();
	}

	/* SECTION Métodos de Obtención de Información */

	public TEstadoGame isJuegoFinalizado()
	{
		if (fondoFinalFijado)
		{
			return TEstadoGame.FinJuegoVictoria;
		}

		Iterator<InstanciaEntidad> it = listaEnemigos.iterator();
		while (it.hasNext())
		{
			InstanciaEntidad instancia = it.next();
			Entidad entidad = tipoEnemigos.get(instancia.getIdEntidad());

			if (!instancia.isDerrotado())
			{
				switch (personaje.colision(entidad, instancia))
				{
					case Nada:
					break;
					case EnemigoDerrotado:
						instancia.setDerrotado();
					break;
					case Colision:
						personaje.quitarVida();
						instancia.setDerrotado();
						
						if (!personaje.isAlive())
						{
							personaje.reiniciarVidas();
							personaje.desactivarBurbuja();
							
							return TEstadoGame.FinJuegoDerrota;
						}
					break;
				}
			}
		}

		return TEstadoGame.Nada;
	}

	/* SECTION Métodos de Guardado de Información */

	public void saveData()
	{
		// Personaje
		personaje.descargarTextura(this);

		// Lista Enemigos
		Iterator<Entidad> it = tipoEnemigos.iterator();
		while (it.hasNext())
		{
			it.next().descargarTextura(this);
		}
	}
}

package com.game.game;

import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;

import com.android.view.BackgroundDataSaved;
import com.android.view.OpenGLRenderer;
import com.creation.data.Handle;
import com.game.data.Background;
import com.game.data.Entidad;
import com.game.data.InstanciaEntidad;
import com.game.data.InstanciaNivel;
import com.game.data.Personaje;
import com.main.model.GamePreferences;
import com.project.main.R;

public class GameOpenGLRenderer extends OpenGLRenderer
{
	// Estado
	private TEstadoGame estado;
	
	// Background
	private Background background;

	// Protagonista
	private Personaje personaje;

	// Enemigos
	private List<Entidad> tipoEnemigos;
	private List<InstanciaEntidad> listaEnemigos;
	private int posEnemigoActual;
	private Handle handleEnemigoActual;
	
	// Puntuancion
	private int puntuacion;
	private boolean puntuacionModificada;
	
	// Texturas
	private boolean texturasCargadas;

	/* Constructura */

	public GameOpenGLRenderer(Context context, int color, Personaje p, InstanciaNivel l)
	{
		super(context, color);

		estado = TEstadoGame.FaseEnemies;
		
		personaje = p;
		background = l.getFondoNivel();

		tipoEnemigos = l.getTipoEnemigos();
		listaEnemigos = l.getListaEnemigos();
		posEnemigoActual = 0;
		handleEnemigoActual = new Handle(50, 20, Color.YELLOW);
		
		personaje.reiniciarVidas();
		personaje.activarBurbuja();
		
		puntuacion = 0;
		puntuacionModificada = false;
		
		texturasCargadas = false;
		
		final ProgressDialog alert = ProgressDialog.show(mContext, mContext.getString(R.string.text_processing_level_title), mContext.getString(R.string.text_processing_level_description), true);

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

	/* Métodos Renderer */

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);

		// BackGround
		seleccionarTexturaFondo(background.getIdTexturaFondos());

		// Protagonista
		personaje.cargarTextura(gl, this, mContext);

		// Lista Enemigos
		Iterator<Entidad> it = tipoEnemigos.iterator();
		while (it.hasNext())
		{
			it.next().cargarTextura(gl, this, mContext);
		}
		
		texturasCargadas = true;
		
		// TODO GAME: Añadir cargado de texturas nuevas.
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{
		super.onDrawFrame(gl);

		switch (estado)
		{
			case FaseEnemies:
				onDrawEnemiesPhase(gl);
			break;
			case FaseBoss:
				onDrawBossPhase(gl);
			break;
		}
	}
	
	private void onDrawEnemiesPhase(GL10 gl)
	{
		gl.glPushMatrix();

			gl.glTranslatef(GamePreferences.DISTANCE_GAME_RIGHT(), GamePreferences.DISTANCE_GAME_BOTTOM(), 0.0f);
					
			// Dibujar protagonista
			personaje.dibujar(gl, this);
	
			// Dibujar cola de enemigos
			boolean activo = true;
			int i = posEnemigoActual;
			while (activo && i < listaEnemigos.size())
			{
				InstanciaEntidad instancia = listaEnemigos.get(i);
				Entidad entidad = tipoEnemigos.get(instancia.getIdEntidad());
				instancia.dibujar(gl, this, entidad);
				
				activo = instancia.getPosicionX() < getScreenWidth();
				i++;
			}
			
			// Dibujar enemigo actual
			if (GamePreferences.IS_DEBUG_ENABLED())
			{
				if (posEnemigoActual < listaEnemigos.size())
				{
					InstanciaEntidad instancia = listaEnemigos.get(posEnemigoActual);
					Entidad entidad = tipoEnemigos.get(instancia.getIdEntidad());
					
					gl.glPushMatrix();
		
						gl.glTranslatef(instancia.getPosicionX() + entidad.getWidth() / 2.0f, instancia.getPosicionY() + entidad.getHeight() / 2.0f, 0.0f);
						handleEnemigoActual.dibujar(gl);
			
					gl.glPopMatrix();
				}
			}
	
		gl.glPopMatrix();
	}
	
	private void onDrawBossPhase(GL10 gl)
	{
		// TODO GAME: Dibujar elementos de la escena.
	}

	/* Métodos de Modificación de Estado */

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
	
	public boolean playAnimation()
	{
		switch (estado)
		{
			case FaseEnemies:
				return playAnimationEnemiesPhase();
			case FaseBoss:
				return playAnimationBossPhase();
		}
		
		return false;
	}
	
	private boolean playAnimationEnemiesPhase()
	{
		// Background
		desplazarTexturaFondo();
		
		// Avanzar cola de enemigos
		for (int i = posEnemigoActual; i < listaEnemigos.size(); i++)
		{
			listaEnemigos.get(i).avanzar();
		}
		
		if (posEnemigoActual < listaEnemigos.size())
		{
			InstanciaEntidad instancia = listaEnemigos.get(posEnemigoActual);
			Entidad entidad = tipoEnemigos.get(instancia.getIdEntidad());
			if(instancia.getPosicionX() < -entidad.getWidth())
			{
				switch(entidad.getTipo())
				{
					case Enemigo:
						puntuacion += GamePreferences.SCORE_ACTION_WRONG;
						puntuacionModificada = true;
					break;
					case Obstaculo:
						puntuacion += GamePreferences.SCORE_ACTION_RIGHT;
						puntuacionModificada = true;
					break;
					case Misil:
						puntuacion += GamePreferences.SCORE_ACTION_RIGHT;
						puntuacionModificada = true;
					default:
					break;
				}
				
				posEnemigoActual++;
			}
		}

		// Animar tipo de enemigos		
		for (int i = 0; i < GamePreferences.NUM_TYPE_OPPONENTS; i++)
		{
			tipoEnemigos.get(i).animar();
		}
		
		// Animar personaje
		return personaje.animar();
	}
	
	private boolean playAnimationBossPhase()
	{
		// TODO GAME: Reproducir animación nueva
		return false;
	}

	/* Métodos de Obtención de Información */
	
	public TEventoGame isGameEnded()
	{
		switch (estado)
		{
			case FaseEnemies:
				return isGameEndedEnemiesPhase();
			case FaseBoss:
				return isGameEndedBossPhase();
		}
		
		return TEventoGame.Nada;
	}

	private TEventoGame isGameEndedEnemiesPhase()
	{
		// Final del juego
		if (fondoFinalFijado)
		{
			puntuacion += GamePreferences.SCORE_LEVEL_COMPLETED;
			estado = TEstadoGame.FaseBoss;
			
			// TODO GAME: Cambiar fin de juego.
			personaje.reposo();
			return TEventoGame.FinJuegoVictoria;
		}
		
		// Colision con enemigo actual
		if (posEnemigoActual < listaEnemigos.size())
		{
			InstanciaEntidad instancia = listaEnemigos.get(posEnemigoActual);
			Entidad entidad = tipoEnemigos.get(instancia.getIdEntidad());
			entidad.moverArea(instancia.getPosicionX(), instancia.getPosicionY());
			
			TEstadoColision colision = personaje.colision(entidad);
			entidad.restaurarArea();
			
			switch (colision)
			{
				case EnemigoDerrotado:
					posEnemigoActual++;
					
					puntuacion += GamePreferences.SCORE_ACTION_RIGHT;
					return TEventoGame.CambioPuntuacion;
				case Colision:
					posEnemigoActual++;
					
					if (!GamePreferences.IS_DEBUG_ENABLED())
					{
						personaje.quitarVida();
					}
					
					puntuacion += GamePreferences.SCORE_LOSE_LIFE;
	
					if (!personaje.isAlive())
					{					
						personaje.reposo();
						return TEventoGame.FinJuegoDerrota;
					}
					
					return TEventoGame.VidaPerdida;
				default:
				break;
			}
		}
		
		// Cambio de puntuación de obstáculos y misiles
		if(puntuacionModificada)
		{
			puntuacionModificada = false;
			
			return TEventoGame.CambioPuntuacion;
		}

		return TEventoGame.Nada;
	}
	
	private TEventoGame isGameEndedBossPhase()
	{
		// TODO GAME: Comprobar final del juego.
		return TEventoGame.Nada;
	}
	
	public int getPuntuacion()
	{
		return puntuacion;
	}
	
	public int getVidas()
	{
		return personaje.getVidas();
	}

	/* Métodos de Guardado de Información */

	public BackgroundDataSaved saveData()
	{
		// Personaje
		personaje.descargarTextura(this);

		// Lista Enemigos
		Iterator<Entidad> it = tipoEnemigos.iterator();
		while (it.hasNext())
		{
			it.next().descargarTextura(this);
		}
		
		return backgroundSaveData();
	}
	
	public void restoreData(BackgroundDataSaved data)
	{
		backgroundRestoreData(data);
	}
}

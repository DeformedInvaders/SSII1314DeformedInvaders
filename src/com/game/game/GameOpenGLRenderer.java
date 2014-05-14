package com.game.game;

import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;

import com.android.opengl.BackgroundDataSaved;
import com.android.opengl.OpenGLRenderer;
import com.android.opengl.TTipoFondoRenderer;
import com.android.opengl.TTipoTexturasRenderer;
import com.creation.data.Handle;
import com.creation.data.TTipoMovimiento;
import com.game.data.Burbuja;
import com.game.data.Enemigo;
import com.game.data.Entidad;
import com.game.data.InstanciaEntidad;
import com.game.data.InstanciaNivel;
import com.game.data.Personaje;
import com.game.data.Plataforma;
import com.main.model.GamePreferences;
import com.project.main.R;

public class GameOpenGLRenderer extends OpenGLRenderer
{
	// Estado
	private TEstadoGame estado;

	// Protagonista
	private Personaje personaje;
	private Burbuja burbujaPersonaje;
	private Plataforma plataformaPersonaje;
	
	// Boss
	private Enemigo jefe;
	private Burbuja burbujaJefe;
	private Plataforma plataformaJefe;
	
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

	public GameOpenGLRenderer(Context context, Personaje p, InstanciaNivel l)
	{
		super(context, TTipoFondoRenderer.Desplazable, TTipoTexturasRenderer.Juego);
		seleccionarTexturaFondo(l.getFondoNivel().getIdTexturaFondos());

		estado = TEstadoGame.FaseEnemies;
		
		personaje = p;
		burbujaPersonaje = new Burbuja(personaje, GamePreferences.MAX_CHARACTER_LIVES);
		plataformaPersonaje = new Plataforma(personaje);
		
		personaje.activarEscalado();
		burbujaPersonaje.activarBurbuja();
		plataformaPersonaje.desactivarPlataforma();
		
		tipoEnemigos = l.getTipoEnemigos();
		listaEnemigos = l.getListaEnemigos();
		posEnemigoActual = 0;
		handleEnemigoActual = new Handle(50, 20, Color.YELLOW);
		
		jefe = (Enemigo) tipoEnemigos.get(tipoEnemigos.size() - 1);
		burbujaJefe = new Burbuja(jefe, GamePreferences.MAX_BOSS_LIVES);
		plataformaJefe = new Plataforma(jefe);
		
		burbujaJefe.desactivarBurbuja();
		plataformaJefe.desactivarPlataforma();
		
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

		// Protagonista
		personaje.cargarTextura(gl, this, mContext);
		burbujaPersonaje.cargarTextura(gl, this, mContext);
		plataformaPersonaje.cargarTextura(gl, this, mContext);

		// Lista Enemigos
		Iterator<Entidad> it = tipoEnemigos.iterator();
		while (it.hasNext())
		{
			it.next().cargarTextura(gl, this, mContext);
		}
		
		// TODO GAME: Añadir cargado de texturas nuevas.
		// jefe.cargarTextura(gl, this, mContext);
		burbujaJefe.cargarTextura(gl, this, mContext);
		plataformaJefe.cargarTextura(gl, this, mContext);
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
			burbujaPersonaje.dibujar(gl, this);
	
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
		
		// Dibujar protagonista
		gl.glPushMatrix();

			gl.glTranslatef(GamePreferences.DISTANCE_GAME_RIGHT(), GamePreferences.DISTANCE_GAME_BOTTOM(), 0.0f);

			plataformaPersonaje.dibujar(gl, this);
			personaje.dibujar(gl, this);
			burbujaPersonaje.dibujar(gl, this);
			
		gl.glPopMatrix();
		
		// Dibujar jefe
		gl.glPushMatrix();
			
			gl.glTranslatef(this.getScreenWidth() - GamePreferences.DISTANCE_GAME_RIGHT() - jefe.getWidth(), GamePreferences.DISTANCE_GAME_BOTTOM(), 0.0f);

			plataformaJefe.dibujar(gl, this);
			jefe.dibujar(gl, this);
			burbujaJefe.dibujar(gl, this);
			
		gl.glPopMatrix();
	}

	public boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		float worldY = convertPixelYToWorldYCoordinate(pixelY, screenHeight);

		if (personaje.getPosicionY() + 3.0f * personaje.getHeight() / 4.0f > worldY)
		{
			if(personaje.getPosicionY() - GamePreferences.DIST_MOVIMIENTO_CHARACTER() > 0)
			{
				personaje.bajar();
				return true;
			}
		}
		else if (personaje.getPosicionY() + personaje.getHeight() / 4.0f < worldY)
		{
			if(personaje.getPosicionY() + personaje.getHeight() + GamePreferences.DIST_MOVIMIENTO_CHARACTER() < getScreenHeight() - GamePreferences.DISTANCE_GAME_BOTTOM())
			{
				personaje.subir();
				return true;
			}
		}
		
		return false;
	}
	/* Métodos de Modificación de Estado */
	
	public void seleccionarReposo()
	{
		if (estado == TEstadoGame.FaseEnemies)
		{
			personaje.reposo();
		}
	}

	public void seleccionarAnimacion(TTipoMovimiento movimiento)
	{
		personaje.seleccionarAnimacion(movimiento);
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
		animarFondo();
		
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
		plataformaPersonaje.animar();
		plataformaJefe.animar();
		
		return personaje.animar();
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
		if (isFondoFinal())
		{
			puntuacion += GamePreferences.SCORE_LEVEL_COMPLETED;
			estado = TEstadoGame.FaseBoss;
			
			// TODO GAME: Cambiar fin de juego.
			personaje.reposo();
			plataformaPersonaje.activarPlataforma();
			
			burbujaJefe.activarBurbuja();
			plataformaJefe.activarPlataforma();
			
			return TEventoGame.FinFaseEnemigos;
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
						burbujaPersonaje.quitarVida();
					}
					
					puntuacion += GamePreferences.SCORE_LOSE_LIFE;
	
					if (!burbujaPersonaje.isAlive())
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
		if (puntuacionModificada)
		{
			puntuacionModificada = false;
			
			return TEventoGame.CambioPuntuacion;
		}

		return TEventoGame.Nada;
	}
	
	private TEventoGame isGameEndedBossPhase()
	{
		// TODO GAME: Comprobar final del juego.
		if (!burbujaJefe.isAlive())
		{
			return TEventoGame.FinFaseBoss;
		}
		
		return TEventoGame.Nada;
	}
	
	public TEstadoGame getEstado()
	{
		return estado;
	}
	
	public int getPuntuacion()
	{
		return puntuacion;
	}
	
	public int getVidasPersonaje()
	{
		return burbujaPersonaje.getVidas();
	}
	
	public int getVidasBoss() 
	{
		return burbujaJefe.getVidas();
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

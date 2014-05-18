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
import com.game.data.Disparo;
import com.game.data.Entidad;
import com.game.data.InstanciaEntidad;
import com.game.data.InstanciaNivel;
import com.game.data.Jefe;
import com.game.data.Personaje;
import com.game.data.Plataforma;
import com.main.model.GamePreferences;
import com.project.main.R;

public class GameOpenGLRenderer extends OpenGLRenderer
{
	// Estado
	private TEstadoJefe estadoJefe;
	private int numIteraciones;

	// Protagonista
	private Personaje protagonista;
	
	private InstanciaEntidad personaje;
	private Burbuja burbujaPersonaje;
	private Plataforma plataformaPersonaje;
	private Disparo disparoPersonaje;
	
	// Boss
	private Jefe lider;
	
	private InstanciaEntidad jefe;
	private Burbuja burbujaJefe;
	private Plataforma plataformaJefe;
	private Disparo disparoJefe;
	
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
	
	private TEstadoPersonaje estadoPersonaje;

	/* Constructura */

	public GameOpenGLRenderer(Context context, Personaje p, InstanciaNivel l)
	{
		super(context, TTipoFondoRenderer.Desplazable, TTipoTexturasRenderer.Juego);
		seleccionarTexturaFondo(l.getFondoNivel().getIdTexturaFondos());

		GamePreferences.setEstadoGame(TEstadoGame.FaseEnemies);
		
		protagonista = p;
		protagonista.activarEscalado();
		
		lider = l.getBoss();
		estadoJefe = TEstadoJefe.Nada;
		
		personaje = new InstanciaEntidad(protagonista.getId(), protagonista.getTipo());
		burbujaPersonaje = new Burbuja(personaje, GamePreferences.MAX_CHARACTER_LIVES);
		plataformaPersonaje = new Plataforma(personaje);
		disparoPersonaje = new Disparo(personaje);
		
		burbujaPersonaje.activarBurbuja();
		plataformaPersonaje.desactivarPlataforma();
		disparoPersonaje.desactivarDisparo();
		
		tipoEnemigos = l.getTipoEnemigos();
		listaEnemigos = l.getListaEnemigos();
		posEnemigoActual = 0;
		handleEnemigoActual = new Handle(50, 20, Color.YELLOW);
		
		jefe = new InstanciaEntidad(lider.getId(), lider.getTipo());
		burbujaJefe = new Burbuja(jefe, GamePreferences.MAX_BOSS_LIVES);
		plataformaJefe = new Plataforma(jefe);
		disparoJefe = new Disparo(jefe);
		
		burbujaJefe.desactivarBurbuja();
		plataformaJefe.desactivarPlataforma();
		disparoJefe.desactivarDisparo();
		
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
		protagonista.cargarTextura(gl, this, mContext);
		personaje.setDimensions(protagonista.getHeight(), protagonista.getWidth());
		
		// Jefe
		lider.cargarTextura(gl, this, mContext);
		jefe.setDimensions(lider.getHeight(), lider.getWidth());
		
		// Lista Enemigos
		Iterator<Entidad> it = tipoEnemigos.iterator();
		while (it.hasNext())
		{
			it.next().cargarTextura(gl, this, mContext);
		}
		
		// Actualización de Dimensiones
		Iterator<InstanciaEntidad> ite = listaEnemigos.iterator();
		while (ite.hasNext())
		{
			InstanciaEntidad instancia = ite.next();
			Entidad entidad = tipoEnemigos.get(instancia.getIdEntidad());
			instancia.setDimensions(entidad.getHeight(), entidad.getWidth());
		}
		
		// Otros Elementos
		burbujaPersonaje.cargarTextura(gl, this, mContext);
		plataformaPersonaje.cargarTextura(gl, this, mContext);
		disparoPersonaje.cargarTextura(gl, this, mContext);
		
		burbujaJefe.cargarTextura(gl, this, mContext);
		plataformaJefe.cargarTextura(gl, this, mContext);
		disparoJefe.cargarTextura(gl, this, mContext);
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

		switch (GamePreferences.GET_ESTADO_GAME())
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
			if (burbujaPersonaje.isAlive())
			{
				personaje.dibujar(gl, this, protagonista);
				burbujaPersonaje.dibujar(gl, this);
			}
			
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
		gl.glPushMatrix();

			gl.glTranslatef(GamePreferences.DISTANCE_GAME_RIGHT(), GamePreferences.DISTANCE_GAME_BOTTOM(), 0.0f);
			
			// Dibujar protagonista
			plataformaPersonaje.dibujar(gl, this);
			if (burbujaPersonaje.isAlive())
			{
				personaje.dibujar(gl, this, protagonista);
				burbujaPersonaje.dibujar(gl, this);
			}
			plataformaPersonaje.dibujar(gl, this);
		
			// Dibujar jefe
			plataformaJefe.dibujar(gl, this);
			if (burbujaJefe.isAlive())
			{
				jefe.dibujar(gl, this, lider);
				burbujaJefe.dibujar(gl, this);
			}
			plataformaJefe.dibujar(gl, this);
			
			// Dibujar disparos
			disparoPersonaje.dibujar(gl, this);
			disparoJefe.dibujar(gl, this);
			
		gl.glPopMatrix();
	}

	public boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		float worldY = convertPixelYToWorldYCoordinate(pixelY, screenHeight);
			
		if (personaje.getPosicionY() + 3.0f * personaje.getHeight() / 4.0f > worldY)
		{
			if (personaje.getPosicionY() - GamePreferences.DIST_MOVIMIENTO_CHARACTER() > 0)
			{
				personaje.bajar();
				return true;
			}
		}
		else if (personaje.getPosicionY() + personaje.getHeight() / 4.0f < worldY)
		{
			if (personaje.getPosicionY() + personaje.getHeight() + GamePreferences.DIST_MOVIMIENTO_CHARACTER() < getScreenHeight() - GamePreferences.DISTANCE_GAME_BOTTOM())
			{
				personaje.subir();
				return true;
			}
		}
		
		return false;
	}
	/* Métodos de Modificación de Estado */

	public void seleccionarAnimacion(TTipoMovimiento movimiento)
	{
		protagonista.seleccionarAnimacion(movimiento);
		
		if (movimiento == TTipoMovimiento.Attack)
		{
			if (!disparoPersonaje.isActivado())
			{
				disparoPersonaje.activarDisparo();
				plataformaPersonaje.activarDisparo();
			}
		}
		else if (movimiento == TTipoMovimiento.Jump)
		{
			personaje.saltar(protagonista.getIndiceAnimacion());
		}
	}
	
	public void seleccionarEstado(TEstadoPersonaje estado) 
	{
		estadoPersonaje = estado;
	}
	
	public boolean playAnimation()
	{
		switch (GamePreferences.GET_ESTADO_GAME())
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
		
		// Avanzar personaje
		personaje.avanzar();
		
		// Avanzar cola de enemigos
		for (int i = posEnemigoActual; i < listaEnemigos.size(); i++)
		{
			listaEnemigos.get(i).avanzar();
		}
		
		if (posEnemigoActual < listaEnemigos.size())
		{
			InstanciaEntidad instancia = listaEnemigos.get(posEnemigoActual);
			Entidad entidad = tipoEnemigos.get(instancia.getIdEntidad());
			if (instancia.getPosicionX() < -entidad.getWidth())
			{
				switch (entidad.getTipo())
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
		return protagonista.animar();
	}
	
	private boolean playAnimationBossPhase()
	{
		disparoPersonaje.mover();
		disparoJefe.mover();
		
		plataformaPersonaje.animar();
		plataformaJefe.animar();
		
		if(estadoPersonaje == TEstadoPersonaje.Bajar)
		{
			if (personaje.getPosicionY() - GamePreferences.DIST_MOVIMIENTO_CHARACTER() > 0)
			{
				personaje.bajar();
			}
		}
		else if (estadoPersonaje == TEstadoPersonaje.Subir)
		{
			if (personaje.getPosicionY() + personaje.getHeight() + GamePreferences.DIST_MOVIMIENTO_CHARACTER() < getScreenHeight() - GamePreferences.DISTANCE_GAME_BOTTOM())
			{
				personaje.subir();
			}
		}
		
		if(estadoJefe == TEstadoJefe.Nada)
		{
			if(numIteraciones == 0)
			{
				int tipoMovimiento = (int) Math.floor(Math.random() * TEstadoJefe.values().length);
				estadoJefe = TEstadoJefe.values()[tipoMovimiento];
				numIteraciones = (int) Math.floor(Math.random() * 9) + 1;
				
				if (estadoJefe == TEstadoJefe.Bajar && jefe.getPosicionY() - GamePreferences.DIST_MOVIMIENTO_CHARACTER() <= 0)
				{
					estadoJefe = TEstadoJefe.Subir;
				}	
				else if (estadoJefe == TEstadoJefe.Subir && jefe.getPosicionY() + jefe.getHeight() + GamePreferences.DIST_MOVIMIENTO_CHARACTER() >= getScreenHeight() - GamePreferences.DISTANCE_GAME_BOTTOM())
				{
					estadoJefe = TEstadoJefe.Bajar;
				}
				else if (estadoJefe == TEstadoJefe.Atacar)
				{
					numIteraciones = 10;
					
					if (disparoJefe.isActivado())
					{
						numIteraciones = 0;
						estadoJefe = TEstadoJefe.Nada;
					}
				}
				
				lider.animar();
				return protagonista.animar();
			}
		}
		else if (estadoJefe == TEstadoJefe.Subir)
		{	
			if (jefe.getPosicionY() + jefe.getHeight() + GamePreferences.DIST_MOVIMIENTO_CHARACTER() < getScreenHeight() - GamePreferences.DISTANCE_GAME_BOTTOM())
			{
				jefe.subir();
			}
			else
			{
				estadoJefe = TEstadoJefe.Nada;
			}
		}
		else if (estadoJefe == TEstadoJefe.Bajar)
		{
			if (jefe.getPosicionY() - GamePreferences.DIST_MOVIMIENTO_CHARACTER() > 0)
			{
				jefe.bajar();
			}
			else
			{
				estadoJefe = TEstadoJefe.Nada;
			}
		}
		else if (estadoJefe == TEstadoJefe.Atacar)
		{
			disparoJefe.activarDisparo();
			plataformaJefe.activarDisparo();
		}
	
		numIteraciones--;
		if (numIteraciones == 0)
		{
			estadoJefe = TEstadoJefe.Nada;
		}
			
		lider.animar();
		return protagonista.animar();
	}

	/* Métodos de Obtención de Información */
	
	public TEventoGame isGameEnded()
	{
		switch (GamePreferences.GET_ESTADO_GAME())
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
			GamePreferences.setEstadoGame(TEstadoGame.FaseBoss);
			
			protagonista.reposo();
			plataformaPersonaje.activarPlataforma();
			
			personaje.setDimensions(protagonista.getHeight(), protagonista.getWidth());
			jefe.setDimensions(lider.getHeight(), lider.getWidth());
		
			jefe.setPosicion(getScreenWidth() - 2*GamePreferences.DISTANCE_GAME_RIGHT() - jefe.getWidth(), 0.0f);
			
			burbujaJefe.activarBurbuja();
			plataformaJefe.activarPlataforma();
			
			return TEventoGame.FinFaseEnemigos;
		}
		
		// Colision con enemigo actual
		if (posEnemigoActual < listaEnemigos.size())
		{
			InstanciaEntidad instancia = listaEnemigos.get(posEnemigoActual);			
			TEstadoColision colision = personaje.colision(instancia, protagonista.getMovimientoActual());
			
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
					
					puntuacion += GamePreferences.SCORE_CHARACTER_LOSE_LIFE;
	
					if (!burbujaPersonaje.isAlive())
					{					
						protagonista.reposo();
						return TEventoGame.FinJuegoDerrota;
					}
					
					return TEventoGame.VidaPerdidaPersonaje;
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
		if (disparoPersonaje.getPosicionX() > getScreenWidth())
		{
			disparoPersonaje.desactivarDisparo();
		}
		
		if (disparoJefe.getPosicionX() < 0)
		{
			disparoJefe.desactivarDisparo();
		}
		
		if (personaje.colision(disparoJefe) == TEstadoColision.Colision)
		{
			if (!GamePreferences.IS_DEBUG_ENABLED())
			{
				burbujaPersonaje.quitarVida();
			}
			
			disparoJefe.desactivarDisparo();
			
			puntuacion += GamePreferences.SCORE_CHARACTER_LOSE_LIFE;
			
			if (!burbujaPersonaje.isAlive())
			{
				return TEventoGame.FinJuegoDerrota;
			}
			
			return TEventoGame.VidaPerdidaPersonaje;
		}
		
		if (jefe.colision(disparoPersonaje) == TEstadoColision.Colision)
		{
			burbujaJefe.quitarVida();
			disparoPersonaje.desactivarDisparo();
			
			puntuacion += GamePreferences.SCORE_BOSS_LOSE_LIFE;
			
			if (!burbujaJefe.isAlive())
			{
				return TEventoGame.FinFaseBoss;
			}
			
			return TEventoGame.VidaPerdidaBoss;
		}
		
		return TEventoGame.Nada;
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
		protagonista.descargarTextura(this);
		plataformaPersonaje.descargarTextura(this);
		burbujaPersonaje.descargarTextura(this);
		disparoPersonaje.descargarTextura(this);

		lider.descargarTextura(this);
		plataformaJefe.descargarTextura(this);
		burbujaJefe.descargarTextura(this);
		disparoJefe.descargarTextura(this);
		
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

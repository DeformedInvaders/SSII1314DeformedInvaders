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
import com.android.opengl.TTypeBackgroundRenderer;
import com.android.opengl.TTypeTexturesRenderer;
import com.creation.data.Handle;
import com.creation.data.TTypeMovement;
import com.game.data.Shield;
import com.game.data.Shot;
import com.game.data.Entity;
import com.game.data.InstanceEntity;
import com.game.data.InstanceLevel;
import com.game.data.Boss;
import com.game.data.Character;
import com.game.data.Platform;
import com.main.model.GamePreferences;
import com.project.main.R;

public class GameOpenGLRenderer extends OpenGLRenderer
{
	// Estado
	private TStateBoss estadoJefe;
	private int numIteraciones;

	// Protagonista
	private Character protagonista;
	
	private InstanceEntity personaje;
	private Shield burbujaPersonaje;
	private Platform plataformaPersonaje;
	private Shot disparoPersonaje;
	
	// Boss
	private Boss lider;
	
	private InstanceEntity jefe;
	private Shield burbujaJefe;
	private Platform plataformaJefe;
	private Shot disparoJefe;
	
	// Enemigos
	private List<Entity> tipoEnemigos;
	private List<InstanceEntity> listaEnemigos;
	private int posEnemigoActual;
	private Handle handleEnemigoActual;

	// Puntuancion
	private int puntuacion;
	private boolean puntuacionModificada;
	
	// Texturas
	private boolean texturasCargadas;
	
	private TStateCharacter estadoPersonaje;

	/* Constructura */

	public GameOpenGLRenderer(Context context, Character p, InstanceLevel l)
	{
		super(context, TTypeBackgroundRenderer.Movable, TTypeTexturesRenderer.Game);
		selectBackground(l.getBackground().getIdBackground());

		GamePreferences.SET_GAME_PARAMETERS(TStateGame.EnemiesPhase);
		
		protagonista = p;
		
		lider = l.getBoss();
		estadoJefe = TStateBoss.Nothing;
		
		personaje = new InstanceEntity(protagonista.getId(), protagonista.getType());
		burbujaPersonaje = new Shield(personaje, GamePreferences.MAX_CHARACTER_LIVES);
		plataformaPersonaje = new Platform(personaje);
		disparoPersonaje = new Shot(personaje);
		
		burbujaPersonaje.activate();
		plataformaPersonaje.deactivate();
		disparoPersonaje.deactivate();
		
		tipoEnemigos = l.getEnemyType();
		listaEnemigos = l.getEnemyList();
		posEnemigoActual = 0;
		handleEnemigoActual = new Handle(50, 20, Color.YELLOW);
		
		jefe = new InstanceEntity(lider.getId(), lider.getType());
		burbujaJefe = new Shield(jefe, GamePreferences.MAX_BOSS_LIVES);
		plataformaJefe = new Platform(jefe);
		disparoJefe = new Shot(jefe);
		
		burbujaJefe.deactivate();
		plataformaJefe.deactivate();
		disparoJefe.deactivate();
		
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
		protagonista.loadTexture(gl, this, mContext);
		personaje.setDimensions(protagonista.getHeight(), protagonista.getWidth());
		
		// Jefe
		lider.loadTexture(gl, this, mContext);
		jefe.setDimensions(lider.getHeight(), lider.getWidth());
		
		// Lista Enemigos
		Iterator<Entity> it = tipoEnemigos.iterator();
		while (it.hasNext())
		{
			it.next().loadTexture(gl, this, mContext);
		}
		
		// Actualización de Dimensiones
		Iterator<InstanceEntity> ite = listaEnemigos.iterator();
		while (ite.hasNext())
		{
			InstanceEntity instancia = ite.next();
			Entity entidad = tipoEnemigos.get(instancia.getIdEntity());
			instancia.setDimensions(entidad.getHeight(), entidad.getWidth());
		}
		
		// Otros Elementos
		burbujaPersonaje.loadTexture(gl, this, mContext);
		plataformaPersonaje.loadTexture(gl, this, mContext);
		disparoPersonaje.loadTexture(gl, this, mContext);
		
		burbujaJefe.loadTexture(gl, this, mContext);
		plataformaJefe.loadTexture(gl, this, mContext);
		disparoJefe.loadTexture(gl, this, mContext);
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
			case EnemiesPhase:
				onDrawEnemiesPhase(gl);
			break;
			case BossPhase:
				onDrawBossPhase(gl);
			break;
			default:
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
				personaje.drawTexture(gl, this, protagonista);
				burbujaPersonaje.drawTexture(gl, this);
			}
			
			// Dibujar cola de enemigos
			boolean activo = true;
			int i = posEnemigoActual;
			while (activo && i < listaEnemigos.size())
			{
				InstanceEntity instancia = listaEnemigos.get(i);
				Entity entidad = tipoEnemigos.get(instancia.getIdEntity());
				instancia.drawTexture(gl, this, entidad);
				
				activo = instancia.getCoordX() < getScreenWidth();
				i++;
			}
			
			// Dibujar enemigo actual
			if (GamePreferences.IS_DEBUG_ENABLED())
			{
				if (posEnemigoActual < listaEnemigos.size())
				{
					InstanceEntity instancia = listaEnemigos.get(posEnemigoActual);
					Entity entidad = tipoEnemigos.get(instancia.getIdEntity());
					
					gl.glPushMatrix();
		
						gl.glTranslatef(instancia.getCoordX() + entidad.getWidth() / 2.0f, instancia.getCoordY() + entidad.getHeight() / 2.0f, 0.0f);
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
			plataformaPersonaje.drawTexture(gl, this);
			if (burbujaPersonaje.isAlive())
			{
				personaje.drawTexture(gl, this, protagonista);
				burbujaPersonaje.drawTexture(gl, this);
			}
			plataformaPersonaje.drawTexture(gl, this);
		
			// Dibujar jefe
			plataformaJefe.drawTexture(gl, this);
			if (burbujaJefe.isAlive())
			{
				jefe.drawTexture(gl, this, lider);
				burbujaJefe.drawTexture(gl, this);
			}
			plataformaJefe.drawTexture(gl, this);
			
			// Dibujar disparos
			disparoPersonaje.drawTexture(gl, this);
			disparoJefe.drawTexture(gl, this);
			
		gl.glPopMatrix();
	}
	
	/* Métodos de Modificación de Estado */

	public boolean seleccionarAnimacion(TTypeMovement movimiento)
	{
		protagonista.selectMovement(movimiento);
		
		if (movimiento == TTypeMovement.Attack)
		{
			if (!disparoPersonaje.isActive())
			{
				disparoPersonaje.activate();
				plataformaPersonaje.shoot();
				return true;
			}
			
			return false;
		}
		else if (movimiento == TTypeMovement.Jump)
		{
			personaje.jump(protagonista.getAnimationLength());
			return true;
		}
		
		return true;
	}
	
	public void seleccionarEstado(TStateCharacter estado) 
	{
		estadoPersonaje = estado;
	}
	
	public void seleccionarPosicion(float pixelY, float screenWidth, float screenHeight)
	{
		float worldY = convertPixelYToWorldYCoordinate(pixelY, screenHeight);
			
		if (personaje.getCoordY() + 3.0f * personaje.getHeight() / 4.0f > worldY)
		{
			if (personaje.getCoordY() - GamePreferences.DIST_MOVIMIENTO_CHARACTER() > 0)
			{
				personaje.down();
			}
		}
		else if (personaje.getCoordY() + personaje.getHeight() / 4.0f < worldY)
		{
			if (personaje.getCoordY() + personaje.getHeight() + GamePreferences.DIST_MOVIMIENTO_CHARACTER() < getScreenHeight() - GamePreferences.DISTANCE_GAME_BOTTOM())
			{
				personaje.up();
			}
		}
	}
	
	public boolean playAnimation()
	{
		switch (GamePreferences.GET_ESTADO_GAME())
		{
			case EnemiesPhase:
				return playAnimationEnemiesPhase();
			case BossPhase:
				return playAnimationBossPhase();
			default:
			break;
		}
		
		return false;
	}
	
	private boolean playAnimationEnemiesPhase()
	{
		// Background
		moveBackground();
		
		// Avanzar personaje
		personaje.move();
		
		// Avanzar cola de enemigos
		for (int i = posEnemigoActual; i < listaEnemigos.size(); i++)
		{
			listaEnemigos.get(i).move();
		}
		
		if (posEnemigoActual < listaEnemigos.size())
		{
			InstanceEntity instancia = listaEnemigos.get(posEnemigoActual);
			Entity entidad = tipoEnemigos.get(instancia.getIdEntity());
			if (instancia.getCoordX() < -entidad.getWidth())
			{
				switch (entidad.getType())
				{
					case Enemy:
						puntuacion += GamePreferences.SCORE_ACTION_WRONG;
						puntuacionModificada = true;
					break;
					case Obstacle:
						puntuacion += GamePreferences.SCORE_ACTION_RIGHT;
						puntuacionModificada = true;
					break;
					case Missil:
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
			tipoEnemigos.get(i).animateTexture();
		}
		
		// Animar personaje
		return protagonista.animateTexture();
	}
	
	private boolean playAnimationBossPhase()
	{
		disparoPersonaje.move();
		disparoJefe.move();
		
		plataformaPersonaje.animateTexture();
		plataformaJefe.animateTexture();
		
		if(estadoPersonaje == TStateCharacter.Down)
		{
			if (personaje.getCoordY() - GamePreferences.DIST_MOVIMIENTO_CHARACTER() > 0)
			{
				personaje.down();
			}
		}
		else if (estadoPersonaje == TStateCharacter.Up)
		{
			if (personaje.getCoordY() + personaje.getHeight() + GamePreferences.DIST_MOVIMIENTO_CHARACTER() < getScreenHeight() - GamePreferences.DISTANCE_GAME_BOTTOM())
			{
				personaje.up();
			}
		}
		
		if(estadoJefe == TStateBoss.Nothing)
		{
			if(numIteraciones == 0)
			{
				int tipoMovimiento = (int) Math.floor(Math.random() * TStateBoss.values().length);
				estadoJefe = TStateBoss.values()[tipoMovimiento];
				numIteraciones = (int) Math.floor(Math.random() * 9) + 1;
				
				if (estadoJefe == TStateBoss.Down && jefe.getCoordY() - GamePreferences.DIST_MOVIMIENTO_CHARACTER() <= 0)
				{
					estadoJefe = TStateBoss.Up;
				}	
				else if (estadoJefe == TStateBoss.Up && jefe.getCoordY() + jefe.getHeight() + GamePreferences.DIST_MOVIMIENTO_CHARACTER() >= getScreenHeight() - GamePreferences.DISTANCE_GAME_BOTTOM())
				{
					estadoJefe = TStateBoss.Down;
				}
				else if (estadoJefe == TStateBoss.Attack)
				{
					numIteraciones = 10;
					
					if (disparoJefe.isActive())
					{
						numIteraciones = 0;
						estadoJefe = TStateBoss.Nothing;
					}
				}
				
				lider.animateTexture();
				return protagonista.animateTexture();
			}
		}
		else if (estadoJefe == TStateBoss.Up)
		{	
			if (jefe.getCoordY() + jefe.getHeight() + GamePreferences.DIST_MOVIMIENTO_CHARACTER() < getScreenHeight() - GamePreferences.DISTANCE_GAME_BOTTOM())
			{
				jefe.up();
			}
			else
			{
				estadoJefe = TStateBoss.Nothing;
			}
		}
		else if (estadoJefe == TStateBoss.Down)
		{
			if (jefe.getCoordY() - GamePreferences.DIST_MOVIMIENTO_CHARACTER() > 0)
			{
				jefe.down();
			}
			else
			{
				estadoJefe = TStateBoss.Nothing;
			}
		}
		else if (estadoJefe == TStateBoss.Attack)
		{
			disparoJefe.activate();
			plataformaJefe.shoot();
		}
	
		numIteraciones--;
		if (numIteraciones == 0)
		{
			estadoJefe = TStateBoss.Nothing;
		}
			
		lider.animateTexture();
		return protagonista.animateTexture();
	}

	/* Métodos de Obtención de Información */
	
	public TEventGame isGameEnded()
	{
		switch (GamePreferences.GET_ESTADO_GAME())
		{
			case EnemiesPhase:
				return isGameEndedEnemiesPhase();
			case BossPhase:
				return isGameEndedBossPhase();
			default:
			break;
		}
		
		return TEventGame.Nothing;
	}

	private TEventGame isGameEndedEnemiesPhase()
	{
		// Final del juego
		if (isBackgroundEnded())
		{
			puntuacion += GamePreferences.SCORE_LEVEL_COMPLETED;
			GamePreferences.SET_GAME_PARAMETERS(TStateGame.BossPhase);
			
			protagonista.stopAnimation();
			plataformaPersonaje.activate();
			
			personaje.setDimensions(protagonista.getHeight(), protagonista.getWidth());
			jefe.setDimensions(lider.getHeight(), lider.getWidth());
		
			jefe.setPosicion(getScreenWidth() - 2*GamePreferences.DISTANCE_GAME_RIGHT() - jefe.getWidth(), 0.0f);
			
			burbujaJefe.activate();
			plataformaJefe.activate();
			
			return TEventGame.EnemiesPhaseEnded;
		}
		
		// Colision con enemigo actual
		if (posEnemigoActual < listaEnemigos.size())
		{
			InstanceEntity instancia = listaEnemigos.get(posEnemigoActual);			
			TStateCollision colision = personaje.collision(instancia, protagonista.getMovementActual());
			
			switch (colision)
			{
				case EnemyDefeated:
					posEnemigoActual++;
					
					puntuacion += GamePreferences.SCORE_ACTION_RIGHT;
					return TEventGame.ScoreChanged;
				case EnemyCollision:
					posEnemigoActual++;
					
					if (!GamePreferences.IS_DEBUG_ENABLED())
					{
						burbujaPersonaje.loseLife();
					}
					
					puntuacion += GamePreferences.SCORE_CHARACTER_LOSE_LIFE;
	
					if (!burbujaPersonaje.isAlive())
					{					
						protagonista.stopAnimation();
						return TEventGame.GameOver;
					}
					
					return TEventGame.CharacterLifeLost;
				default:
				break;
			}
		}
		
		// Cambio de puntuación de obstáculos y misiles
		if (puntuacionModificada)
		{
			puntuacionModificada = false;
			
			return TEventGame.ScoreChanged;
		}

		return TEventGame.Nothing;
	}
	
	private TEventGame isGameEndedBossPhase()
	{
		if (disparoPersonaje.getCoordX() > getScreenWidth())
		{
			disparoPersonaje.deactivate();
		}
		
		if (disparoJefe.getCoordX() < 0)
		{
			disparoJefe.deactivate();
		}
		
		if (personaje.collision(disparoJefe) == TStateCollision.EnemyCollision)
		{
			if (!GamePreferences.IS_DEBUG_ENABLED())
			{
				burbujaPersonaje.loseLife();
			}
			
			disparoJefe.deactivate();
			
			puntuacion += GamePreferences.SCORE_CHARACTER_LOSE_LIFE;
			
			if (!burbujaPersonaje.isAlive())
			{
				return TEventGame.GameOver;
			}
			
			return TEventGame.CharacterLifeLost;
		}
		
		if (jefe.collision(disparoPersonaje) == TStateCollision.EnemyCollision)
		{
			burbujaJefe.loseLife();
			disparoPersonaje.deactivate();
			
			puntuacion += GamePreferences.SCORE_BOSS_LOSE_LIFE;
			
			if (!burbujaJefe.isAlive())
			{
				return TEventGame.BossPhaseEnded;
			}
			
			return TEventGame.BossLifeLost;
		}
		
		return TEventGame.Nothing;
	}
	
	public int getPuntuacion()
	{
		return puntuacion;
	}
	
	public int getVidasPersonaje()
	{
		return burbujaPersonaje.getLives();
	}
	
	public int getVidasBoss() 
	{
		return burbujaJefe.getLives();
	}

	/* Métodos de Guardado de Información */

	public BackgroundDataSaved saveData()
	{
		// Personaje
		protagonista.deleteTexture(this);
		plataformaPersonaje.deleteTexture(this);
		burbujaPersonaje.deleteTexture(this);
		disparoPersonaje.deleteTexture(this);

		lider.deleteTexture(this);
		plataformaJefe.deleteTexture(this);
		burbujaJefe.deleteTexture(this);
		disparoJefe.deleteTexture(this);
		
		// Lista Enemigos
		Iterator<Entity> it = tipoEnemigos.iterator();
		while (it.hasNext())
		{
			it.next().deleteTexture(this);
		}
		
		return backgroundSaveData();
	}
	
	public void restoreData(BackgroundDataSaved data)
	{
		backgroundRestoreData(data);
	}
}

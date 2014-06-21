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
	private TStateBoss mBossState;
	private int animationFrames;

	// Protagonista
	private Character character;
	
	private InstanceEntity characterInstance;
	private Shield characterShield;
	private Platform characterPlatform;
	private Shot characterShot;
	
	// Boss
	private Boss boss;
	
	private InstanceEntity bossInstance;
	private Shield bossShield;
	private Platform bossPlatform;
	private Shot bossShot;
	
	// Enemigos
	private List<Entity> enemiesTypes;
	private List<InstanceEntity> enemiesList;
	private int enemyActual;
	private Handle handleEnemy;

	// Puntuancion
	private int score;
	private boolean scoreModified;
	
	// Texturas
	private boolean textureLoaded;
	
	private TStateCharacter mStateCharacter;

	/* Constructura */

	public GameOpenGLRenderer(Context context, Character p, InstanceLevel l)
	{
		super(context, TTypeBackgroundRenderer.Movable, TTypeTexturesRenderer.Game);
		selectBackground(l.getBackground().getIdBackground());

		GamePreferences.SET_GAME_PARAMETERS(TStateGame.EnemiesPhase);
		
		character = p;
		
		boss = l.getBoss();
		mBossState = TStateBoss.Nothing;
		
		characterInstance = new InstanceEntity(character.getId(), character.getType());
		characterShield = new Shield(characterInstance, GamePreferences.MAX_CHARACTER_LIVES);
		characterPlatform = new Platform(characterInstance);
		characterShot = new Shot(characterInstance);
		
		characterShield.activate();
		characterPlatform.deactivate();
		characterShot.deactivate();
		
		enemiesTypes = l.getEnemyType();
		enemiesList = l.getEnemyList();
		enemyActual = 0;
		handleEnemy = new Handle(50, 20, Color.YELLOW);
		
		bossInstance = new InstanceEntity(boss.getId(), boss.getType());
		bossShield = new Shield(bossInstance, GamePreferences.MAX_BOSS_LIVES);
		bossPlatform = new Platform(bossInstance);
		bossShot = new Shot(bossInstance);
		
		bossShield.deactivate();
		bossPlatform.deactivate();
		bossShot.deactivate();
		
		score = 0;
		scoreModified = false;
		
		textureLoaded = false;
		
		final ProgressDialog alert = ProgressDialog.show(mContext, mContext.getString(R.string.text_processing_level_title), mContext.getString(R.string.text_processing_level_description), true);

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run()
			{
				while(!textureLoaded);
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
		character.loadTexture(gl, this, mContext);
		characterInstance.setDimensions(character.getHeight(), character.getWidth());
		
		// Jefe
		boss.loadTexture(gl, this, mContext);
		bossInstance.setDimensions(boss.getHeight(), boss.getWidth());
		
		// Lista Enemigos
		Iterator<Entity> it = enemiesTypes.iterator();
		while (it.hasNext())
		{
			it.next().loadTexture(gl, this, mContext);
		}
		
		// Actualización de Dimensiones
		Iterator<InstanceEntity> ite = enemiesList.iterator();
		while (ite.hasNext())
		{
			InstanceEntity instancia = ite.next();
			Entity entidad = enemiesTypes.get(instancia.getIdEntity());
			instancia.setDimensions(entidad.getHeight(), entidad.getWidth());
		}
		
		// Otros Elementos
		characterShield.loadTexture(gl, this, mContext);
		characterPlatform.loadTexture(gl, this, mContext);
		characterShot.loadTexture(gl, this, mContext);
		
		bossShield.loadTexture(gl, this, mContext);
		bossPlatform.loadTexture(gl, this, mContext);
		bossShot.loadTexture(gl, this, mContext);
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		super.onSurfaceChanged(gl, width, height);
		
		textureLoaded = true;
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
			if (characterShield.isAlive())
			{
				characterInstance.drawTexture(gl, this, character);
				characterShield.drawTexture(gl, this);
			}
			
			// Dibujar cola de enemigos
			boolean activo = true;
			int i = enemyActual;
			while (activo && i < enemiesList.size())
			{
				InstanceEntity instancia = enemiesList.get(i);
				Entity entidad = enemiesTypes.get(instancia.getIdEntity());
				instancia.drawTexture(gl, this, entidad);
				
				activo = instancia.getCoordX() < getScreenWidth();
				i++;
			}
			
			// Dibujar enemigo actual
			if (GamePreferences.IS_DEBUG_ENABLED())
			{
				if (enemyActual < enemiesList.size())
				{
					InstanceEntity instancia = enemiesList.get(enemyActual);
					Entity entidad = enemiesTypes.get(instancia.getIdEntity());
					
					gl.glPushMatrix();
		
						gl.glTranslatef(instancia.getCoordX() + entidad.getWidth() / 2.0f, instancia.getCoordY() + entidad.getHeight() / 2.0f, 0.0f);
						handleEnemy.dibujar(gl);
			
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
			characterPlatform.drawTexture(gl, this);
			if (characterShield.isAlive())
			{
				characterInstance.drawTexture(gl, this, character);
				characterShield.drawTexture(gl, this);
			}
			characterPlatform.drawTexture(gl, this);
		
			// Dibujar jefe
			bossPlatform.drawTexture(gl, this);
			if (bossShield.isAlive())
			{
				bossInstance.drawTexture(gl, this, boss);
				bossShield.drawTexture(gl, this);
			}
			bossPlatform.drawTexture(gl, this);
			
			// Dibujar disparos
			characterShot.drawTexture(gl, this);
			bossShot.drawTexture(gl, this);
			
		gl.glPopMatrix();
	}
	
	/* Métodos de Modificación de Estado */

	public boolean startAnimation(TTypeMovement movimiento)
	{
		character.selectMovement(movimiento);
		
		if (movimiento == TTypeMovement.Attack)
		{
			if (!characterShot.isActive())
			{
				characterShot.activate();
				characterPlatform.shoot();
				return true;
			}
			
			return false;
		}
		else if (movimiento == TTypeMovement.Jump)
		{
			characterInstance.jump(character.getAnimationLength());
			return true;
		}
		
		return true;
	}
	
	public void moveCharacter(TStateCharacter estado) 
	{
		mStateCharacter = estado;
	}
	
	public void moveCharacter(float pixelY, float screenWidth, float screenHeight)
	{
		float worldY = convertPixelYToWorldYCoordinate(pixelY, screenHeight);
			
		if (characterInstance.getCoordY() + 3.0f * characterInstance.getHeight() / 4.0f > worldY)
		{
			if (characterInstance.getCoordY() - GamePreferences.DIST_MOVIMIENTO_CHARACTER() > 0)
			{
				characterInstance.down();
			}
		}
		else if (characterInstance.getCoordY() + characterInstance.getHeight() / 4.0f < worldY)
		{
			if (characterInstance.getCoordY() + characterInstance.getHeight() + GamePreferences.DIST_MOVIMIENTO_CHARACTER() < getScreenHeight() - GamePreferences.DISTANCE_GAME_BOTTOM())
			{
				characterInstance.up();
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
		characterInstance.move();
		
		// Avanzar cola de enemigos
		for (int i = enemyActual; i < enemiesList.size(); i++)
		{
			enemiesList.get(i).move();
		}
		
		if (enemyActual < enemiesList.size())
		{
			InstanceEntity instancia = enemiesList.get(enemyActual);
			Entity entidad = enemiesTypes.get(instancia.getIdEntity());
			if (instancia.getCoordX() < -entidad.getWidth())
			{
				switch (entidad.getType())
				{
					case Enemy:
						score += GamePreferences.SCORE_ACTION_WRONG;
						scoreModified = true;
					break;
					case Obstacle:
						score += GamePreferences.SCORE_ACTION_RIGHT;
						scoreModified = true;
					break;
					case Missil:
						score += GamePreferences.SCORE_ACTION_RIGHT;
						scoreModified = true;
					default:
					break;
				}
				
				enemyActual++;
			}
		}

		// Animar tipo de enemigos		
		for (int i = 0; i < GamePreferences.NUM_TYPE_OPPONENTS; i++)
		{
			enemiesTypes.get(i).animateTexture();
		}
		
		// Animar personaje
		return character.animateTexture();
	}
	
	private boolean playAnimationBossPhase()
	{
		characterShot.move();
		bossShot.move();
		
		characterPlatform.animateTexture();
		bossPlatform.animateTexture();
		
		if(mStateCharacter == TStateCharacter.Down)
		{
			if (characterInstance.getCoordY() - GamePreferences.DIST_MOVIMIENTO_CHARACTER() > 0)
			{
				characterInstance.down();
			}
		}
		else if (mStateCharacter == TStateCharacter.Up)
		{
			if (characterInstance.getCoordY() + characterInstance.getHeight() + GamePreferences.DIST_MOVIMIENTO_CHARACTER() < getScreenHeight() - GamePreferences.DISTANCE_GAME_BOTTOM())
			{
				characterInstance.up();
			}
		}
		
		if(mBossState == TStateBoss.Nothing)
		{
			if(animationFrames == 0)
			{
				int tipoMovimiento = (int) Math.floor(Math.random() * TStateBoss.values().length);
				mBossState = TStateBoss.values()[tipoMovimiento];
				animationFrames = (int) Math.floor(Math.random() * 9) + 1;
				
				if (mBossState == TStateBoss.Down && bossInstance.getCoordY() - GamePreferences.DIST_MOVIMIENTO_CHARACTER() <= 0)
				{
					mBossState = TStateBoss.Up;
				}	
				else if (mBossState == TStateBoss.Up && bossInstance.getCoordY() + bossInstance.getHeight() + GamePreferences.DIST_MOVIMIENTO_CHARACTER() >= getScreenHeight() - GamePreferences.DISTANCE_GAME_BOTTOM())
				{
					mBossState = TStateBoss.Down;
				}
				else if (mBossState == TStateBoss.Attack)
				{
					animationFrames = 10;
					
					if (bossShot.isActive())
					{
						animationFrames = 0;
						mBossState = TStateBoss.Nothing;
					}
				}
				
				boss.animateTexture();
				return character.animateTexture();
			}
		}
		else if (mBossState == TStateBoss.Up)
		{	
			if (bossInstance.getCoordY() + bossInstance.getHeight() + GamePreferences.DIST_MOVIMIENTO_CHARACTER() < getScreenHeight() - GamePreferences.DISTANCE_GAME_BOTTOM())
			{
				bossInstance.up();
			}
			else
			{
				mBossState = TStateBoss.Nothing;
			}
		}
		else if (mBossState == TStateBoss.Down)
		{
			if (bossInstance.getCoordY() - GamePreferences.DIST_MOVIMIENTO_CHARACTER() > 0)
			{
				bossInstance.down();
			}
			else
			{
				mBossState = TStateBoss.Nothing;
			}
		}
		else if (mBossState == TStateBoss.Attack)
		{
			bossShot.activate();
			bossPlatform.shoot();
		}
	
		animationFrames--;
		if (animationFrames == 0)
		{
			mBossState = TStateBoss.Nothing;
		}
			
		boss.animateTexture();
		return character.animateTexture();
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
			score += GamePreferences.SCORE_LEVEL_COMPLETED;
			GamePreferences.SET_GAME_PARAMETERS(TStateGame.BossPhase);
			
			character.stopAnimation();
			characterPlatform.activate();
			
			characterInstance.setDimensions(character.getHeight(), character.getWidth());
			bossInstance.setDimensions(boss.getHeight(), boss.getWidth());
		
			bossInstance.setPosicion(getScreenWidth() - 2*GamePreferences.DISTANCE_GAME_RIGHT() - bossInstance.getWidth(), 0.0f);
			
			bossShield.activate();
			bossPlatform.activate();
			
			return TEventGame.EnemiesPhaseEnded;
		}
		
		// Colision con enemigo actual
		if (enemyActual < enemiesList.size())
		{
			InstanceEntity instancia = enemiesList.get(enemyActual);			
			TStateCollision colision = characterInstance.collision(instancia, character.getMovementActual());
			
			switch (colision)
			{
				case EnemyDefeated:
					enemyActual++;
					
					score += GamePreferences.SCORE_ACTION_RIGHT;
					return TEventGame.ScoreChanged;
				case EnemyCollision:
					enemyActual++;
					
					if (!GamePreferences.IS_DEBUG_ENABLED())
					{
						characterShield.loseLife();
					}
					
					score += GamePreferences.SCORE_CHARACTER_LOSE_LIFE;
	
					if (!characterShield.isAlive())
					{					
						character.stopAnimation();
						return TEventGame.GameOver;
					}
					
					return TEventGame.CharacterLifeLost;
				default:
				break;
			}
		}
		
		// Cambio de puntuación de obstáculos y misiles
		if (scoreModified)
		{
			scoreModified = false;
			
			return TEventGame.ScoreChanged;
		}

		return TEventGame.Nothing;
	}
	
	private TEventGame isGameEndedBossPhase()
	{
		if (characterShot.getCoordX() > getScreenWidth())
		{
			characterShot.deactivate();
		}
		
		if (bossShot.getCoordX() < 0)
		{
			bossShot.deactivate();
		}
		
		if (characterInstance.collision(bossShot) == TStateCollision.EnemyCollision)
		{
			if (!GamePreferences.IS_DEBUG_ENABLED())
			{
				characterShield.loseLife();
			}
			
			bossShot.deactivate();
			
			score += GamePreferences.SCORE_CHARACTER_LOSE_LIFE;
			
			if (!characterShield.isAlive())
			{
				return TEventGame.GameOver;
			}
			
			return TEventGame.CharacterLifeLost;
		}
		
		if (bossInstance.collision(characterShot) == TStateCollision.EnemyCollision)
		{
			bossShield.loseLife();
			characterShot.deactivate();
			
			score += GamePreferences.SCORE_BOSS_LOSE_LIFE;
			
			if (!bossShield.isAlive())
			{
				return TEventGame.BossPhaseEnded;
			}
			
			return TEventGame.BossLifeLost;
		}
		
		return TEventGame.Nothing;
	}
	
	public int getScore()
	{
		return score;
	}
	
	public int getCharacterLives()
	{
		return characterShield.getLives();
	}
	
	public int getBossLives() 
	{
		return bossShield.getLives();
	}

	/* Métodos de Guardado de Información */

	public BackgroundDataSaved saveData()
	{
		// Personaje
		character.deleteTexture(this);
		characterPlatform.deleteTexture(this);
		characterShield.deleteTexture(this);
		characterShot.deleteTexture(this);

		boss.deleteTexture(this);
		bossPlatform.deleteTexture(this);
		bossShield.deleteTexture(this);
		bossShot.deleteTexture(this);
		
		// Lista Enemigos
		Iterator<Entity> it = enemiesTypes.iterator();
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

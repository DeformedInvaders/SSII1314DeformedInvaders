package com.main.model;

import com.creation.data.TTypeMovement;
import com.creation.data.TTypeSticker;
import com.game.data.TTypeEntity;
import com.game.game.TStateGame;
import com.game.game.TTypeEndgame;
import com.game.select.TTypeLevel;

public class GamePreferences
{
	// OpenGL
	public static final int SIZE_LINE = 3;
	public static final int POINT_WIDTH = 7;
	
	// Multitouch
	public static final int NUM_HANDLES = 10;
	
	public static final float MAX_DISTANCE_PIXELS = 10.0f;
	public static final float MAX_DISTANCE_HANDLES = 30.0f;
	
	public static final long MAX_DURATION_TAP = 200;
	public static final float MAX_DISTANCE_DRAG = 80.0f;
	public static final float MAX_DRIFT_ROTATION = 10.0f;
	
	public static final float MAX_SCALE_FACTOR = 1.03f;
	public static final float MIN_SCALE_FACTOR = 0.97f;
	public static final float NULL_SCALE_FACTOR = 1.0f;
	
	// Animación
	private static final int TIME_INTERVAL_ANIMATION_SUPER_FAST = 10;
	private static final int TIME_INTERVAL_ANIMATION_FAST = 12;
	private static final int TIME_INTERVAL_ANIMATION_MEDIUM = 15;
	private static final int TIME_INTERVAL_ANIMATION_SLOW = 18;
	private static final int TIME_INTERVAL_ANIMATION_SUPER_SLOW = 20;
	
	private static final int TIME_INTERVAL_ANIMATION_VIDEO = 20;
	
	public static final int NUM_FRAMES_ANIMATION = 20;
	public static final int NUM_FRAMES_CYCLE = 5;
	public static final int NUM_FRAMES_DISPARO = 10;
	
	// Texturas
	public static final float DEEP_BACKGROUND = -0.99f;
	public static final float DEEP_INSIDE_FRAMES = -0.1f;
	public static final float DEEP_POLYLINES = 0.1f;
	public static final float DEEP_STICKERS = 0.2f;
	public static final float DEEP_HANDLE = 0.3f;
	public static final float DEEP_OUTSIDE_FRAMES = 0.4f;
	public static final float DEEP_OBJECTS = 0.99f;

	// Niveles
	private static final int MAX_ENEMIES = 3;//50;
	public static final int MAX_CHARACTERS = 10;
	public static final int MAX_CHARACTER_LIVES = 3;
	public static final int MAX_BOSS_LIVES = 3;
	
	public static final int NUM_TYPE_LEVELS = TTypeLevel.values().length;
	public static final int NUM_TYPE_MOVIMIENTOS = TTypeMovement.values().length;
	public static final int NUM_TYPE_STICKERS = TTypeSticker.values().length - 1;
	public static final int NUM_TYPE_ENDGAME = TTypeEndgame.values().length;
	
	public static final int NUM_TYPE_BACKGROUNDS_STATIC = 1;
	public static final int NUM_TYPE_BACKGROUNDS_LEVEL = 5;
	public static final int NUM_TYPE_BACKGROUNDS_VIDEO = 7;
	public static final int NUM_TYPE_CHARACTER_DESIGN = 1;
	public static final int NUM_TYPE_CHARACTER_VIDEO = 2;
	public static final int NUM_TYPE_CHARACTER_JUEGO = 1;
	public static final int NUM_TYPE_BUBBLES = 3;
	public static final int NUM_TYPE_PLATFORMS = 3;
	public static final int NUM_TYPE_WEAPONS = 4;
	public static final int NUM_TYPE_SHOTS = 1;
	public static final int NUM_TYPE_OBSTACLES = 2;
	public static final int NUM_TYPE_MISSILES = 1;
	public static final int NUM_TYPE_ENEMIES = 3;
	public static final int NUM_TYPE_BOSS = 1;
	public static final int NUM_TYPE_OPPONENTS = NUM_TYPE_OBSTACLES + NUM_TYPE_MISSILES + NUM_TYPE_ENEMIES;
	
	public static final int NUM_TYPE_TEXTURE_WEAPONS = 2;
	public static final int NUM_TYPE_TEXTURE_ANIMATED_OBJECTS = 4;
	
	public static final int NUM_TYPE_ANIMATED_OBJECTS = 5;
	public static final int NUM_TYPE_INANIMATED_OBJECTS = 2;
	
	private static final int NUM_TYPE_STICKERS_EYES = 12;
	private static final int NUM_TYPE_STICKERS_MOUTH = 12;
	private static final int NUM_TYPE_STICKERS_WEAPON = 18;
	private static final int NUM_TYPE_STICKERS_TRINKET = 18;
	private static final int NUM_TYPE_STICKERS_HELMET = 18;
	
	// Puntuaciones
	public static final int SCORE_LEVEL_COMPLETED = 100;
	public static final int SCORE_ACTION_RIGHT = 50;
	public static final int SCORE_ACTION_WRONG = 10;
	public static final int SCORE_CHARACTER_LOSE_LIFE = -100;
	public static final int SCORE_BOSS_LOSE_LIFE = 100;
	
	// Tamaño de Pantalla
	private static float WIDTH_SCREEN;
	private static float HEIGHT_SCREEN;
	
	// Opciones del Juego
	private static int CHARACTER_GAME;
	private static boolean TIPS_GAME, MUSIC_GAME, DEBUG_GAME, SENSOR_GAME;
	
	// Opciones del Dispositivo
	private static boolean ACCELEROMETER_AVAILABLE;
	
	// Estado del Juego
	private static TStateGame ESTADO_GAME;
	
	public static void SET_SCREEN_PARAMETERS(float width, float height)
	{
		WIDTH_SCREEN = width;
		HEIGHT_SCREEN = height;
	}
	
	public static void SET_ACCELEROMETER_PARAMETERS(boolean available)
	{
		ACCELEROMETER_AVAILABLE = available;
	}
	
	public static void SET_TIP_PARAMETERS(boolean active)
	{
		TIPS_GAME = active;
	}
	
	public static void SET_MUSIC_PARAMETERS(boolean active)
	{
		MUSIC_GAME = active;
	}
	
	public static void SET_DEBUG_PARAMETERS(boolean active)
	{
		DEBUG_GAME = active;
	}
	
	public static void SET_CHARACTER_PARAMETERS(int pos)
	{
		CHARACTER_GAME = pos;
	}
	
	public static void SET_GAME_PARAMETERS(TStateGame estado)
	{
		ESTADO_GAME = estado;
	}
	
	public static void SET_SENSOR_PARAMETERS(boolean active)
	{
		if (ACCELEROMETER_AVAILABLE)
		{
			SENSOR_GAME = active;
		}
		else
		{
			SENSOR_GAME = false;
		}
	}
	
	public static void SWITCH_MUSIC_GAME()
	{
		MUSIC_GAME = !MUSIC_GAME;
	}
	
	public static void SWITCH_TIPS_GAME()
	{
		TIPS_GAME = !TIPS_GAME;
	}
	
	public static void SWITCH_DEBUG_GAME()
	{
		DEBUG_GAME = !DEBUG_GAME;
	}
	
	public static void SWITCH_SENSOR_GAME()
	{
		if (ACCELEROMETER_AVAILABLE)
		{
			SENSOR_GAME = !SENSOR_GAME;
		}
		else
		{
			SENSOR_GAME = false;
		}
	}
	
	// Parametros Juego
	
	public static final boolean IS_TIPS_ENABLED()
	{
		return TIPS_GAME;
	}
	
	public static final boolean IS_MUSIC_ENABLED()
	{
		return MUSIC_GAME;
	}
	
	public static final boolean IS_DEBUG_ENABLED()
	{
		return DEBUG_GAME;
	}
	
	public static final int GET_CHARACTER_GAME()
	{
		return CHARACTER_GAME;
	}
	
	public static final TStateGame GET_ESTADO_GAME() 
	{
		return ESTADO_GAME;
	}
	
	public static final boolean IS_SENSOR_ENABLED()
	{
		return SENSOR_GAME;
	}
	
	public static final boolean IS_LONG_RATIO()
	{
		return WIDTH_SCREEN/HEIGHT_SCREEN > 1.5f;
	}
	
	// Distancias Escenario
	
	public static final float DISTANCE_CHARACTER_WIDTH()
	{
		return HEIGHT_SCREEN - HEIGHT_SCREEN * 0.30f;
	}
	
	public static final float DISTANCE_GAME_RIGHT()
	{
		return WIDTH_SCREEN / 25.0f;
	}
	
	public static final float DISTANCE_GAME_BOTTOM()
	{
		return HEIGHT_SCREEN / 16.0f;
	}
	
	public static final float DISTANCE_ENEMY_GROUND()
	{
		return 0.0f;
	}
	
	public static final float DISTANCE_ENEMY_AIR()
	{
		return HEIGHT_SCREEN / 4.0f;
	}
	
	public static final float DISTANCE_BETWEEN_ENEMIES()
	{
		return WIDTH_SCREEN / 1.8f;
	}
	
	public static final float POS_ENEMIES_INICIO()
	{
		return WIDTH_SCREEN;
	}
	
	public static final float POS_ENEMIES_FINAL()
	{
		return POS_ENEMIES_INICIO() + MAX_ENEMIES * DISTANCE_BETWEEN_ENEMIES() * 1.5f;
	}
	
	private static final float MAX_NUM_CICLOS()
	{
		return Math.round(POS_ENEMIES_FINAL() / DIST_MOVIMIENTO_ENEMIES());
	}
	
	public static final int NUM_ITERATION_BACKGROUND()
	{
		return Math.round(MAX_NUM_CICLOS() * DIST_MOVIMIENTO_BACKGROUND() / WIDTH_SCREEN) + 1;
	}
	
	public static final int TIME_INTERVAL_ANIMATION()
	{
		return TIME_INTERVAL_ANIMATION_SUPER_SLOW;
	}
	
	public static final int TIME_INTERVAL_ANIMATION_VIDEO()
	{
		return TIME_INTERVAL_ANIMATION_VIDEO;
	}
	
	public static final int TIME_INTERVAL_ANIMATION(int ciclos)
	{
		if (ESTADO_GAME == TStateGame.EnemiesPhase)
		{
			if(ciclos < MAX_NUM_CICLOS() / 6)
			{
				return TIME_INTERVAL_ANIMATION_SUPER_SLOW;
			}
			else if(ciclos < 2 * MAX_NUM_CICLOS() / 6)
			{
				return TIME_INTERVAL_ANIMATION_SLOW;
			}
			else if(ciclos < 3 * MAX_NUM_CICLOS() / 6)
			{
				return TIME_INTERVAL_ANIMATION_MEDIUM;
			}
			else if(ciclos < 4 * MAX_NUM_CICLOS() / 6)
			{
				return TIME_INTERVAL_ANIMATION_FAST;
			}
			else
			{
				return TIME_INTERVAL_ANIMATION_SUPER_FAST;
			}
		}
		else
		{
			switch (ciclos)
			{
				case 0:
					return TIME_INTERVAL_ANIMATION_SUPER_FAST;
				case 1:
					return TIME_INTERVAL_ANIMATION_FAST;
				case 2:
					return TIME_INTERVAL_ANIMATION_SLOW;
				default:
					return TIME_INTERVAL_ANIMATION_SUPER_SLOW;
			}
		}
	}
	
	public static final int PICTURE_ENEMY_WIDTH()
	{
		return Math.round(HEIGHT_SCREEN / 3.2f);
	}
	
	/* Tipos de Elementos */
	
	public static final int NUM_TYPE_STICKERS(TTypeSticker pegatina)
	{
		switch(pegatina)
		{
			case Trinket:
				return NUM_TYPE_STICKERS_TRINKET;
			case Helmet:
				return NUM_TYPE_STICKERS_HELMET;
			case Eyes:
				return NUM_TYPE_STICKERS_EYES;
			case Mouth:
				return NUM_TYPE_STICKERS_MOUTH;
			case Weapon:
				return NUM_TYPE_STICKERS_WEAPON;
			default:
				return -1;
		}
	}
	
	public static final int NUM_TYPE_ENEMIES(TTypeEntity entidad)
	{
		switch(entidad)
		{
			case Enemy:
				return NUM_TYPE_ENEMIES;
			case Obstacle:
				return NUM_TYPE_OBSTACLES;
			case Missil:
				return NUM_TYPE_MISSILES;
			default:
				return -1;
		}
	}	
	
	/* DISTANCIAS Y FACTORES */
	
	public static final float SCREEN_HEIGHT_SCALE_FACTOR()
	{
		final float GAME_HEIGHT_BASE = 752.0f;
		
		return HEIGHT_SCREEN / GAME_HEIGHT_BASE;
	}
	
	public static final float SCREEN_WIDTH_SCALE_FACTOR()
	{
		final float GAME_WIDTH_BASE = 1280.0f;
		
		return WIDTH_SCREEN / GAME_WIDTH_BASE;
	}
	
	private static final float GAME_SCALE_FACTOR_ENEMIES()
	{
		return 0.5f;
	}
	
	private static final float GAME_SCALE_FACTOR_BOSS()
	{
		return 0.5f;
	}
	
	public static final float GAME_SCALE_FACTOR(TTypeEntity entidad)
	{
		if (ESTADO_GAME == TStateGame.EnemiesPhase)
		{
			switch(entidad)
			{
				case Character:
					return GAME_SCALE_FACTOR_ENEMIES();
				case Missil:
					return SCREEN_HEIGHT_SCALE_FACTOR();
				case Obstacle:
					return SCREEN_HEIGHT_SCALE_FACTOR();
				case Enemy:
				case Boss:
					return SCREEN_HEIGHT_SCALE_FACTOR() * GAME_SCALE_FACTOR_ENEMIES();
				default:
					return 1.0f;
			}
		}
		else if (ESTADO_GAME == TStateGame.BossPhase)
		{
			switch(entidad)
			{
				case Character:
					return GAME_SCALE_FACTOR_BOSS() * GAME_SCALE_FACTOR_ENEMIES();
				case Enemy:
				case Boss:
					return SCREEN_HEIGHT_SCALE_FACTOR() * GAME_SCALE_FACTOR_BOSS() * GAME_SCALE_FACTOR_ENEMIES();
				case CharacterPlatform:
				case BossPlatform:
				case CharacterShield:
				case BossShield:
					return GAME_SCALE_FACTOR_BOSS();
				default:
					return 1.0f;
			}
		}
		else
		{
			return 1.0f;
		}
	}
	
	public static final float DIST_MOVIMIENTO_BACKGROUND()
	{
		final float BACKGROUND_DISTANCE_BASE = 4.0f;
		
		return BACKGROUND_DISTANCE_BASE;
	}
	
	public static final float DIST_MOVIMIENTO_ENEMIES()
	{
		final float ENEMIES_DISTANCE_BASE = 12.0f;
		
		return ENEMIES_DISTANCE_BASE * SCREEN_HEIGHT_SCALE_FACTOR();
	}
	
	public static final float DIST_MOVIMIENTO_CHARACTER()
	{
		final float CHARACTER_DISTANCE_BASE = 20.0f;
		
		return CHARACTER_DISTANCE_BASE * SCREEN_HEIGHT_SCALE_FACTOR();
	}
	
	public static final float DIST_MOVIMIENTO_PLATAFORMA()
	{
		final float PLATAFORMA_DISTANCE_BASE = 5.0f;
		
		return PLATAFORMA_DISTANCE_BASE * SCREEN_HEIGHT_SCALE_FACTOR();
	}
	
	public static final float DIST_MOVIMIENTO_SPACESHIP()
	{
		final float PLATAFORMA_DISTANCE_BASE = 5.0f;
		
		return PLATAFORMA_DISTANCE_BASE * SCREEN_HEIGHT_SCALE_FACTOR();
	}
	
	/* MARCOS */
	
	public static final float MARCO_ALTURA_LATERAL()
	{
		return MARCO_ALTURA_LATERAL(WIDTH_SCREEN, HEIGHT_SCREEN);
	}
	
	public static final float MARCO_ALTURA_LATERAL(float width, float height)
	{
		return 0.1f * height;
	}
	
	public static final float MARCO_ANCHURA_INTERIOR()
	{
		return MARCO_ANCHURA_INTERIOR(WIDTH_SCREEN, HEIGHT_SCREEN);
	}
	
	public static final float MARCO_ANCHURA_INTERIOR(float width, float height)
	{
		return height - 2 * MARCO_ALTURA_LATERAL(width, height);
	}
	
	public static final float MARCO_ANCHURA_LATERAL()
	{
		return MARCO_ANCHURA_LATERAL(WIDTH_SCREEN, HEIGHT_SCREEN);
	}
	
	public static final float MARCO_ANCHURA_LATERAL(float width, float height)
	{
		return (width - MARCO_ANCHURA_INTERIOR(width, height)) / 2.0f;
	}
}

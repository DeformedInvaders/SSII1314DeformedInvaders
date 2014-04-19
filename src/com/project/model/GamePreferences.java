package com.project.model;

import com.creation.data.TTipoMovimiento;
import com.creation.data.TTipoSticker;
import com.game.data.TTipoEntidad;
import com.game.game.TTipoEndgame;
import com.game.select.TTipoLevel;

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
	
	public static final int NUM_FRAMES_ANIMATION = 50;
	
	// Velocidades
	public static final float DIST_MOVIMIENTO_BACKGROUND = 4.0f;
	public static final float DIST_MOVIMIENTO_ENEMY = 10.0f;
	public static final float DIST_MOVIMIENTO_CHARACTER = 30.0f;
	
	// Texturas
	public static final float DEEP_INSIDE_FRAMES = -0.1f;
	public static final float DEEP_POLYLINES = 0.1f;
	public static final float DEEP_STICKERS = 0.2f;
	public static final float DEEP_HANDLE = 0.3f;
	public static final float DEEP_OUTSIDE_FRAMES = 0.4f;
	
	/* FIXME Menos de 1 segundo no se graba nada */
	public static final int TIME_DURATION_ANIMATION = 2000;

	// Niveles
	private static final int MAX_ENEMIES = 50;
	public static final int MAX_CHARACTERS = 10;
	public static final int MAX_LIVES = 3;
	
	public static final int NUM_TYPE_LEVELS = TTipoLevel.values().length;
	public static final int NUM_TYPE_MOVIMIENTOS = TTipoMovimiento.values().length;
	public static final int NUM_TYPE_STICKERS = TTipoSticker.values().length - 1;
	public static final int NUM_TYPE_ENDGAME = TTipoEndgame.values().length;
	
	public static final int NUM_TYPE_BACKGROUNDS = 5;
	public static final int NUM_TYPE_CHARACTER = 1;
	public static final int NUM_TYPE_BUBBLES = 3;
	public static final int NUM_TYPE_OBSTACLES = 2;
	public static final int NUM_TYPE_MISSILES = 1;
	public static final int NUM_TYPE_ENEMIES = 4;
	
	public static final int NUM_TYPE_OPPONENTS = NUM_TYPE_OBSTACLES + NUM_TYPE_MISSILES + NUM_TYPE_ENEMIES;
	public static final int NUM_TYPE_STICKERS_EYES = 8;
	public static final int NUM_TYPE_STICKERS_MOUTH = 7;
	public static final int NUM_TYPE_STICKERS_WEAPON = 16;
	public static final int NUM_TYPE_STICKERS_TRINKET = 16;
	public static final int NUM_TYPE_STICKERS_HELMET = 16;
	
	// Puntuaciones
	public static final int SCORE_LEVEL_COMPLETED = 100;
	public static final int SCORE_ACTION_RIGHT = 50;
	public static final int SCORE_ACTION_WRONG = 10;
	public static final int SCORE_LOSE_LIFE = -100;
	
	// Tamaño de Pantalla
	private static float WIDTH_SCREEN;
	private static float HEIGHT_SCREEN;
	
	// Opciones del Juego
	private static int CHARACTER_GAME;
	private static boolean TIPS_GAME;
	private static boolean MUSIC_GAME;
	
	public static void setScreenParameters(float width, float height)
	{
		WIDTH_SCREEN = width;
		HEIGHT_SCREEN = height;
	}
	
	public static void setTipParameters(boolean tips)
	{
		TIPS_GAME = tips;
	}
	
	public static void setMusicParameters(boolean music)
	{
		MUSIC_GAME = music;
	}
	
	public static void setCharacterParameters(int character)
	{
		CHARACTER_GAME = character;
	}
	
	public static void SWITCH_MUSIC_GAME()
	{
		MUSIC_GAME = !MUSIC_GAME;
	}
	
	public static void SWITCH_TIPS_GAME()
	{
		TIPS_GAME = !TIPS_GAME;
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
	
	public static final int GET_CHARACTER_GAME()
	{
		return CHARACTER_GAME;
	}
	
	// Distancias Escenario
	
	public static final float DISTANCE_CHARACTER_WIDTH()
	{
		return HEIGHT_SCREEN - HEIGHT_SCREEN * 0.30f;
	}
	
	public static final float DISTANCE_CHARACTER_RIGHT()
	{
		return WIDTH_SCREEN / 25.0f;
	}
	
	public static final float DISTANCE_CHARACTER_BOTTOM()
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
	
	public static final float DISTANCE_BETWEEN_ENEMY()
	{
		return WIDTH_SCREEN / 1.8f;
	}
	
	public static final float POS_ENEMIES_INICIO()
	{
		return WIDTH_SCREEN;
	}
	
	public static final float POS_ENEMIES_FINAL()
	{
		return POS_ENEMIES_INICIO() + MAX_ENEMIES * DISTANCE_BETWEEN_ENEMY();
	}
	
	private static final float MAX_NUM_CICLOS()
	{
		return Math.round(POS_ENEMIES_FINAL() / DIST_MOVIMIENTO_ENEMY);
	}
	
	public static final int NUM_ITERATION_BACKGROUND()
	{
		return Math.round(MAX_NUM_CICLOS() * DIST_MOVIMIENTO_BACKGROUND / WIDTH_SCREEN);
	}
	
	public static final int TIME_INTERVAL_ANIMATION()
	{
		return TIME_INTERVAL_ANIMATION_SUPER_SLOW;
	}
	
	public static final int TIME_INTERVAL_ANIMATION(int ciclos)
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
	
	public static final int PICTURE_ENEMY_WIDTH()
	{
		return Math.round(HEIGHT_SCREEN / 3.2f);
	}
	
	/* Tipos de Elementos */
	
	public static final int NUM_TYPE_STICKERS(TTipoSticker pegatina)
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
	
	public static final int NUM_TYPE_ENEMIES(TTipoEntidad entidad)
	{
		switch(entidad)
		{
			case Enemigo:
				return NUM_TYPE_ENEMIES;
			case Obstaculo:
				return NUM_TYPE_OBSTACLES;
			case Misil:
				return NUM_TYPE_MISSILES;
			default:
				return -1;
		}
	}	
}

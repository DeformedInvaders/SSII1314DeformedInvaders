package com.project.model;

public class GamePreferences
{
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
	
	// Texturas
	public static final float DEEP_INSIDE_FRAMES = -0.1f;
	public static final float DEEP_POLYLINES = 0.1f;
	public static final float DEEP_STICKERS = 0.2f;
	public static final float DEEP_OUTSIDE_FRAMES = 0.3f;
	
	/* FIXME Menos de 1 segundo no se graba nada */
	public static final int TIME_DURATION_ANIMATION = 2000;

	// Enemigos
	public static final int NUM_TEXTURE_BACKGROUND = 3;
	public static final int NUM_TEXTURE_CHARACTER = 1;
	public static final int NUM_TEXTURE_STICKER = 5;
	public static final int NUM_TEXTURE_BUBBLE = 3;
	public static final int NUM_TEXTURE_OBSTACLE = 2;
	public static final int NUM_TEXTURE_MISSILE = 1;
	public static final int NUM_TEXTURE_ENEMY = 4;

	public static final int NUM_TYPE_ENEMIES = NUM_TEXTURE_OBSTACLE + NUM_TEXTURE_MISSILE + NUM_TEXTURE_ENEMY;
	public static final int NUM_TYPE_STICKERS_EYES = 8;
	public static final int NUM_TYPE_STICKERS_MOUTH = 7;
	public static final int NUM_TYPE_STICKERS_WEAPON = 16;
	public static final int NUM_TYPE_STICKERS_TRINKET = 16;
	public static final int NUM_TYPE_STICKERS_HELMET = 16;
	
	// Busqueda de recursos
	public static final String RESOURCE_IMAGE_HEART = "imageViewGameHeart";
	
	public static final String RESOURCE_ID_STICKER_EYES = "sticker_eyes_";
	public static final String RESOURCE_ID_STICKER_MOUTH = "sticker_mouth_";
	public static final String RESOURCE_ID_STICKER_WEAPON = "sticker_weapon_";
	public static final String RESOURCE_ID_STICKER_TRINKET = "sticker_trinket_";
	public static final String RESOURCE_ID_STICKER_HELMET = "sticker_helmet_";
	
	// Video
	private static final String VIDEO_PATH = "android.resource://com.project.main/raw/";
	public static final String VIDEO_DESIGN_DRAW_PATH = VIDEO_PATH + "tips_design_draw";
	public static final String VIDEO_DESIGN_DRAG_PATH = VIDEO_PATH + "tips_design_drag";
	public static final String VIDEO_DESIGN_ROTATE_PATH = VIDEO_PATH + "tips_design_rotate";
	public static final String VIDEO_DESIGN_ZOOM_PATH = VIDEO_PATH + "tips_design_zoom";
	public static final String VIDEO_DESIGN_OUTSIDE_PATH = VIDEO_PATH + "tips_design_outside";
	public static final String VIDEO_DESIGN_NOREGULAR_PATH = VIDEO_PATH + "tips_design_noregular";
	public static final String VIDEO_PAINT_PENCIL_PATH = VIDEO_PATH + "tips_paint_pencil";
	public static final String VIDEO_PAINT_BUCKET_PATH = VIDEO_PATH + "tips_paint_bucket";
	public static final String VIDEO_PAINT_STICKER_PATH = VIDEO_PATH + "tips_paint_sticker";
	public static final String VIDEO_PAINT_ZOOM_PATH = VIDEO_PATH + "tips_paint_zoom";
	public static final String VIDEO_DEFORM_HANDLES_PATH = VIDEO_PATH + "tips_deform_handles";
	public static final String VIDEO_DEFORM_UNDEFINED_PATH = VIDEO_PATH + "tips_deform_undefined";
	
	public static final String VIDEO_GAME_LIVES_PATH = VIDEO_PATH + "tips_game_lives";
	public static final String VIDEO_GAME_CROUCH_PATH = VIDEO_PATH + "tips_game_crouch";
	public static final String VIDEO_GAME_JUMP_PATH = VIDEO_PATH + "tips_game_jump";
	public static final String VIDEO_GAME_ATTACK_PATH = VIDEO_PATH + "tips_game_attack";
	public static final String VIDEO_GAME_COMPLETE_PATH = VIDEO_PATH + "tips_game_complete";
	// Fuentes
	private static final String FONT_PATH = "fonts/";
	public static final String FONT_TYPEWRITER_PATH = FONT_PATH + "font_typewriter.ttf";
	public static final String FONT_LOGO_PATH = FONT_PATH + "font_logo.ttf";
	public static final String FONT_MOON_PATH = FONT_PATH + "font_moon.ttf";
	public static final String FONT_NEW_YORK_PATH = FONT_PATH + "font_new_york.ttf";
	public static final String FONT_ROME_PATH = FONT_PATH + "font_rome.ttf";
	public static final String FONT_EGYPT_PATH = FONT_PATH + "font_egypt.ttf";
	public static final String FONT_STONEHENGE_PATH = FONT_PATH + "font_stonehenge.ttf";
	
	// Puntuaciones
	public static final int SCORE_LEVEL_COMPLETED = 100;
	public static final int SCORE_ACTION_RIGHT = 50;
	public static final int SCORE_ACTION_WRONG = 10;
	public static final int SCORE_LOSE_LIFE = -100;

	// Velocidades
	public static final float DIST_MOVIMIENTO_BACKGROUND = 4.0f;
	public static final float DIST_MOVIMIENTO_ENEMY = 10.0f;
	public static final float DIST_MOVIMIENTO_CHARACTER = 30.0f;
	
	// Niveles
	private static final int MAX_ENEMIES = 50;
	public static final int MAX_CHARACTERS = 10;
	public static final int MAX_LIVES = 3;
	public static final int NUM_LEVELS = 5;
	public static final int NUM_MOVIMIENTOS = 4;
	
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
}

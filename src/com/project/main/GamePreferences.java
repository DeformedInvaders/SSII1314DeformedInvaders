package com.project.main;

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
	public static final int TIME_INTERVAL_ANIMATION = 15;
	public static final int TIME_INTERVAL_ANIMATION_FAST = 10;
	public static final int TIME_INTERVAL_ANIMATION_SLOW = 20;
	
	public static final int NUM_FRAMES_ANIMATION = 50;
	
	/* FIXME Cambiar al añadir tres velocidades */
	public static final int TIME_DURATION_ANIMATION = 2000;
	//public static final int TIME_DURACION_ANIMATION = TIME_INTERVAL_ANIMATION * NUM_FRAMES_ANIMATION;

	// Enemigos
	public static final int MAX_TEXTURE_BACKGROUND = 3;
	public static final int MAX_TEXTURE_CHARACTER = 1;
	public static final int MAX_TEXTURE_STICKER = 5;
	public static final int MAX_TEXTURE_BUBBLE = 3;
	public static final int MAX_TEXTURE_OBSTACLE = 1;
	public static final int MAX_TEXTURE_MISSILE = 1;
	public static final int MAX_TEXTURE_ENEMY = 4;

	public static final int NUM_TYPE_ENEMIES = 6;
	public static final int NUM_TYPE_STICKERS_EYES = 8;
	public static final int NUM_TYPE_STICKERS_MOUTH = 7;
	public static final int NUM_TYPE_STICKERS_WEAPON = 12;
	public static final int NUM_TYPE_STICKERS_TRINKET = 8;
	public static final int NUM_TYPE_STICKERS_HELMET = 12;
	
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
	
	// Fuentes
	private static final String FONT_PATH = "fonts/";
	public static final String FONT_TYPEWRITER_PATH = FONT_PATH + "font_typewriter.ttf";
	public static final String FONT_LOGO_PATH = FONT_PATH + "font_logo.ttf";
	public static final String FONT_MOON_PATH = FONT_PATH + "font_moon.ttf";
	public static final String FONT_NEW_YORK_PATH = FONT_PATH + "font_new_york.ttf";
	public static final String FONT_ROME_PATH = FONT_PATH + "font_rome.ttf";
	public static final String FONT_EGYPT_PATH = FONT_PATH + "font_egypt.ttf";
	public static final String FONT_STONEHENGE_PATH = FONT_PATH + "font_stonehenge.ttf";
	
	public static final int ID_TYPE_OBSTACLE = 0;
	public static final int ID_TYPE_ENEMY = 0;
	public static final int ID_TYPE_MISSILE = 0;
	
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
	public static final int MAX_LIVES = 3;
	public static final int NUM_LEVELS = 5;
	
	// Tamaño de Pantalla
	private static float WIDTH_SCREEN;
	private static float HEIGHT_SCREEN;
	
	// Opciones del Juego
	private static boolean TIPS_GAME;
	private static boolean MUSIC_GAME;
	
	public static final void setScreenParameters(float width, float height)
	{
		WIDTH_SCREEN = width;
		HEIGHT_SCREEN = height;
	}
	
	public static final void setTipParameters(boolean tips)
	{
		TIPS_GAME = tips;
	}
	
	public static final void setMusicParameters(boolean music)
	{
		MUSIC_GAME = music;
	}
	
	public static final void SWITCH_MUSIC_GAME()
	{
		MUSIC_GAME = !MUSIC_GAME;
	}
	
	public static final void SWITCH_TIPS_GAME()
	{
		TIPS_GAME = !TIPS_GAME;
	}
	
	// Parametros Juego
	
	public static final boolean TIPS_ENABLED()
	{
		return TIPS_GAME;
	}
	
	public static final boolean MUSIC_ENABLED()
	{
		return MUSIC_GAME;
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
	
	public static final int NUM_ITERATION_BACKGROUND()
	{
		float numMaxCiclos = Math.round(POS_ENEMIES_FINAL() / DIST_MOVIMIENTO_ENEMY);
		float distanciaMaxBackground = numMaxCiclos * DIST_MOVIMIENTO_BACKGROUND;
		
		return Math.round(distanciaMaxBackground / WIDTH_SCREEN);
	}
	
	public static final int PICTURE_ENEMY_WIDTH()
	{
		return Math.round(HEIGHT_SCREEN / 3.2f);
	}
}

package com.project.main;

public class GamePreferences
{
	// Multitouch
	public static final int NUM_HANDLES = 10;
	
	public static final float MAX_DISTANCE_PIXELS = 10.0f;
	public static final float MAX_DISTANCE_HANDLES = 30.0f;
	
	// Animación
	public static final int TIME_INTERVAL_ANIMATION = 15;
	public static final int NUM_FRAMES_ANIMATION = 50;

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
	public static final String RESOURCE_BUTTON_STICKER_EYES = "imageButtonStickerEyes";
	public static final String RESOURCE_BUTTON_STICKER_MOUTH = "imageButtonStickerMouth";
	public static final String RESOURCE_BUTTON_STICKER_WEAPON = "imageButtonStickerWeapon";
	public static final String RESOURCE_BUTTON_STICKER_TRINKET = "imageButtonStickerTrinket";
	public static final String RESOURCE_BUTTON_STICKER_HELMET = "imageButtonStickerHelmet";
	public static final String RESOURCE_IMAGE_HEART = "imageViewGameHeart";
	
	public static final String RESOURCE_ID_STICKER_EYES = "sticker_eyes_";
	public static final String RESOURCE_ID_STICKER_MOUTH = "sticker_mouth_";
	public static final String RESOURCE_ID_STICKER_WEAPON = "sticker_weapon_";
	public static final String RESOURCE_ID_STICKER_TRINKET = "sticker_trinket_";
	public static final String RESOURCE_ID_STICKER_HELMET = "sticker_helmet_";
	
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
	
	public static final void setParameters(float width, float height)
	{
		WIDTH_SCREEN = width;
		HEIGHT_SCREEN = height;
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

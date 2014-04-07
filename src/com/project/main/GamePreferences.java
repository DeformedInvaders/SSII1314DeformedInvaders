package com.project.main;

public class GamePreferences
{
	// Multitouch
	public static final int NUM_HANDLES = 10;

	// Animación
	public static final int TIME_INTERVAL_ANIMATION = 20;
	public static final int NUM_FRAMES_ANIMATION = 34;

	// Enemigos
	public static final int MAX_TEXTURE_BACKGROUND = 3;
	public static final int MAX_TEXTURE_CHARACTER = 1;
	public static final int MAX_TEXTURE_STICKER = 5;
	public static final int MAX_TEXTURE_BUBBLE = 3;
	public static final int MAX_TEXTURE_OBSTACLE = 1;
	public static final int MAX_TEXTURE_FISSURE = 1;
	public static final int MAX_TEXTURE_ENEMY = 4;

	public static final int NUM_TYPE_ENEMIGOS = 4;
	public static final int NUM_TYPE_STICKERS_EYES = 8;
	public static final int NUM_TYPE_STICKERS_MOUTH = 6;
	public static final int NUM_TYPE_STICKERS_WEAPON = 12;
	public static final int NUM_TYPE_STICKERS_TRINKET = 8;
	public static final int NUM_TYPE_STICKERS_HELMET = 12;
	
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
	
	public static final int TYPE_OBSTACLE = 0;
	public static final int TYPE_ENEMY = 0;
	public static final int TYPE_BOSS = TYPE_ENEMY + MAX_TEXTURE_ENEMY;
	
	// Puntuaciones
	public static final int SCORE_LEVEL_COMPLETED = 10;
	public static final int SCORE_ACTION_RIGHT = 5;
	public static final int SCORE_ACTION_WRONG = 1;
	public static final int SCORE_LOSE_LIFE = -10;

	/* FIXME Usar Width de pantalla */
	
	public static final float WIDTH_CHARACTER = 536.8f;

	// Velocidades
	public static final float DIST_MOVIMIENTO_BACKGROUND = 4.0f;
	public static final float DIST_MOVIMIENTO_ENEMY = 10.0f;
	public static final float DIST_MOVIMIENTO_CHARACTER = 30.0f;

	// Niveles
	public static final int NUM_LEVELS = 5;
	public static final int MAX_ENEMIES = 20;
	public static final int MAX_LIVES = 3;

	// Diastancias Escenario
	public static final float DISTANCE_RIGHT = 70.0f;
	public static final float DISTANCE_BOTTOM = 70.0f;

	public static final float DISTANCE_BETWEEN_ENEMY = 700.0f;
	public static final float POS_ENEMIES_INICIO = 1280.0f;
	public static final float POS_ENEMIES_FINAL = POS_ENEMIES_INICIO + MAX_ENEMIES * DISTANCE_BETWEEN_ENEMY;
	public static final float POS_BOSS = POS_ENEMIES_FINAL + DISTANCE_BETWEEN_ENEMY;

	public static final int NUM_ITERATION_BACKGROUND = 5;
}

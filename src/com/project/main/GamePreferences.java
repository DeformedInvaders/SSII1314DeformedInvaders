package com.project.main;

public class GamePreferences
{
	// Multitouch
	public static final int NUM_HANDLES = 10;
	
	// Animación
	public static final int TIME_INTERVAL_ANIMATION = 20;
	
	// Niveles
	public static final int NUM_LEVELS = 5;
	
	// Enemigos
	public static final int MAX_TEXTURE_BACKGROUND = 3;
	public static final int MAX_TEXTURE_STICKER = 4;
	public static final int MAX_TEXTURE_OBSTACLE = 1;
	public static final int MAX_TEXTURE_ENEMY = 4;
	
	public static final int TYPE_ENEMIGOS = 4;
	
	/* FIXME Usar Width de pantalla */
	
	// Escenario
	public static final int MAX_ENEMIES = 20;

	public static final float DISTANCE_BETWEEN_ENEMY = 700.0f;
	public static final float POS_ENEMIES_INICIO = 1280.0f;
	public static final float POS_ENEMIES_FINAL = POS_ENEMIES_INICIO + MAX_ENEMIES * DISTANCE_BETWEEN_ENEMY;
	public static final float POS_BOSS = POS_ENEMIES_FINAL + DISTANCE_BETWEEN_ENEMY;
	
	public static final int NUM_ITERATION_BACKGROUND = 5;
}

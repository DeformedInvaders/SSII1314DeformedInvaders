package com.game.select;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;

import com.game.data.Background;
import com.game.data.Enemigo;
import com.game.data.Entidad;
import com.game.data.InstanciaEntidad;
import com.game.data.InstanciaNivel;
import com.game.data.Misil;
import com.game.data.Nivel;
import com.game.data.Obstaculo;
import com.game.data.TTipoEntidad;
import com.game.game.TTipoEndgame;
import com.project.model.GamePreferences;
import com.project.model.GameResources;

public class LevelGenerator
{
	private Context mContext;

	private List<Nivel> listaNiveles;
	private List<String> listaNombres;
	private List<List<Entidad>> listaEnemigos;
	private List<Background> listaFondos;

	public LevelGenerator(Context context)
	{
		mContext = context;

		listaNiveles = new ArrayList<Nivel>();
		listaNombres = new ArrayList<String>();
		listaEnemigos = new ArrayList<List<Entidad>>();
		listaFondos = new ArrayList<Background>();

		TTipoLevel[] niveles = TTipoLevel.values();
		for (int i = 0; i < GamePreferences.NUM_TYPE_LEVELS; i++)
		{
			listaEnemigos.add(new ArrayList<Entidad>());

			crearNivel(niveles[i], listaNiveles, listaEnemigos.get(i), listaNombres, listaFondos);
		}
	}

	private int obtenerID(String id)
	{
		return mContext.getResources().getIdentifier(id, GameResources.RESOURCE_DRAWABLE, mContext.getPackageName());
	}
	
	private void crearNivel(TTipoLevel nivel, List<Nivel> listaNiveles, List<Entidad> listaEnemigos, List<String> listaNombres, List<Background> listaBackground)
	{
		Typeface textFont = Typeface.createFromAsset(mContext.getAssets(), GameResources.GET_FONT_PATH(nivel));
		int logroCompletado = obtenerID(GameResources.GET_ACHIEVEMENTS(nivel, TTipoEndgame.LevelCompleted));
		int logroPerfecto = obtenerID(GameResources.GET_ACHIEVEMENTS(nivel, TTipoEndgame.LevelPerfected));
		
		
		listaNiveles.add(new Nivel(nivel, nivel.getFondoDisplay(), logroCompletado, logroPerfecto, nivel.getTitle(), nivel.getDescription(), nivel.getColor(), textFont, nivel.getMusica()));
		listaNombres.add(mContext.getString(nivel.getTitle()));
		
		
		for (int i = 0; i < GamePreferences.NUM_TYPE_MISSILES; i++)
		{
			listaEnemigos.add(new Misil(obtenerID(GameResources.GET_ENEMIES(TTipoEntidad.Misil, nivel, i)), i));
		}
		
		for (int i = 0; i < GamePreferences.NUM_TYPE_OBSTACLES; i++)
		{
			listaEnemigos.add(new Obstaculo(obtenerID(GameResources.GET_ENEMIES(TTipoEntidad.Obstaculo, nivel, i)), i));
		}
		
		for (int i = 0; i < GamePreferences.NUM_TYPE_ENEMIES; i++)
		{
			listaEnemigos.add(new Enemigo(obtenerID(GameResources.GET_ENEMIES(TTipoEntidad.Enemigo, nivel, i)), i));
		}
		
		int[] fondos = new int[GamePreferences.NUM_TYPE_BACKGROUNDS];
		for (int i = 0; i < GamePreferences.NUM_TYPE_BACKGROUNDS; i++)
		{
			fondos[i] = obtenerID(GameResources.GET_BACKGROUND(nivel, i));
		}
		
		int[] polaroids = new int[GamePreferences.NUM_TYPE_ENDGAME];
		TTipoEndgame[] finJuegos = TTipoEndgame.values();
		for (int i = 0; i < GamePreferences.NUM_TYPE_ENDGAME; i++)
		{
			polaroids[i] = obtenerID(GameResources.GET_POLAROID(nivel, finJuegos[i]));
		}
		
		listaBackground.add(new Background(nivel.getFondoSol(), fondos, polaroids));
	}
	
	/*
	Typeface textFont = Typeface.createFromAsset(mContext.getAssets(), GamePreferences.FONT_MOON_PATH);
	
	listaNiveles.add(new Nivel(TTipoLevel.Moon, R.drawable.background_moon_1, R.drawable.achievement_moon_completed, R.drawable.achievement_moon_perfected, R.string.title_level_section_moon, R.string.text_level_description_moon, Color.WHITE, textFont, R.raw.music_moon));
	listaNombres.add(mContext.getString(R.string.title_level_section_moon));

	listaEnemigos.add(new Misil(R.drawable.missile_moon, 0));
	
	listaEnemigos.add(new Obstaculo(R.drawable.obstacle_moon_1, 0));
	listaEnemigos.add(new Obstaculo(R.drawable.obstacle_moon_2, 1));

	listaEnemigos.add(new Enemigo(R.drawable.enemy_moon_1, 0));
	listaEnemigos.add(new Enemigo(R.drawable.enemy_moon_1, 1));
	listaEnemigos.add(new Enemigo(R.drawable.enemy_moon_2, 2));
	listaEnemigos.add(new Enemigo(R.drawable.enemy_moon_2, 3));
	
	background.setBackground(R.drawable.background_moon_2, R.drawable.background_moon_3, R.drawable.background_moon_4, R.drawable.background_moon_5, R.drawable.polaroid_gameover_moon, R.drawable.polaroid_levelcompleted_moon, R.drawable.polaroid_levelperfected_moon);

	 */

	private List<InstanciaEntidad> getColaEnemigos(int indice)
	{
		List<InstanciaEntidad> listaEnemigos = new ArrayList<InstanciaEntidad>();

		float posXActual = GamePreferences.POS_ENEMIES_INICIO();
		while (posXActual < GamePreferences.POS_ENEMIES_FINAL())
		{
			int tipoEnemigo = (int) Math.floor(Math.random() * GamePreferences.NUM_TYPE_OPPONENTS);
			float posXEnemigo = posXActual + (float) (Math.random() * GamePreferences.DISTANCE_BETWEEN_ENEMY());
			float posYEnemigo = GamePreferences.DISTANCE_ENEMY_GROUND();
			
			if (tipoEnemigo == 0) posYEnemigo = GamePreferences.DISTANCE_ENEMY_AIR();
					
			listaEnemigos.add(new InstanciaEntidad(tipoEnemigo, posXEnemigo, posYEnemigo));
			posXActual = posXEnemigo + GamePreferences.DISTANCE_BETWEEN_ENEMY();
		}

		return listaEnemigos;
	}

	public InstanciaNivel getInstanciaLevel(TTipoLevel level)
	{
		int indice = level.ordinal();
		return new InstanciaNivel(level, listaNombres.get(indice), listaEnemigos.get(indice), getColaEnemigos(indice), listaFondos.get(indice));
	}
	
	public Nivel getLevel(TTipoLevel level)
	{
		int indice = level.ordinal();
		return listaNiveles.get(indice);
	}

	public List<Nivel> getListaNiveles()
	{
		return listaNiveles;
	}
}

package com.game.select;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

import com.game.data.Background;
import com.game.data.Enemigo;
import com.game.data.Entidad;
import com.game.data.InstanciaEntidad;
import com.game.data.InstanciaNivel;
import com.game.data.Misil;
import com.game.data.Nivel;
import com.game.data.Obstaculo;
import com.project.main.R;
import com.project.model.GamePreferences;

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

		for (int i = 0; i < GamePreferences.NUM_LEVELS; i++)
		{
			listaEnemigos.add(new ArrayList<Entidad>());
			listaFondos.add(new Background());

			crearNivel(i, listaNiveles, listaEnemigos.get(i), listaNombres, listaFondos.get(i));
		}
	}

	private void crearNivel(int nivel, List<Nivel> listaNiveles, List<Entidad> listaEnemigos, List<String> listaNombres, Background background)
	{
		switch (nivel)
		{
			case 0:
				crearNivelLuna(listaNiveles, listaEnemigos, listaNombres, background);
			break;
			case 1:
				crearNivelNewYork(listaNiveles, listaEnemigos, listaNombres, background);
			break;
			case 2:
				crearNivelRoma(listaNiveles, listaEnemigos, listaNombres, background);
			break;
			case 3:
				crearNivelEgipto(listaNiveles, listaEnemigos, listaNombres, background);
			break;
			case 4:
				crearNivelStonehenge(listaNiveles, listaEnemigos, listaNombres, background);
			break;
		}
	}

	private void crearNivelLuna(List<Nivel> listaNiveles, List<Entidad> listaEnemigos, List<String> listaNombres, Background background)
	{
		Typeface textFont = Typeface.createFromAsset(mContext.getAssets(), GamePreferences.FONT_MOON_PATH);
		
		listaNiveles.add(new Nivel(TTipoLevel.Moon, R.drawable.background_moon_1, R.drawable.achievement_moon_completed, R.drawable.achievement_moon_perfected, R.string.title_level_section_moon, R.string.text_level_section_moon, Color.WHITE, textFont, R.raw.music_moon));
		listaNombres.add(mContext.getString(R.string.title_level_section_moon));

		listaEnemigos.add(new Misil(R.drawable.missile_moon, GamePreferences.ID_TYPE_MISSILE));
		listaEnemigos.add(new Obstaculo(R.drawable.obstacle_moon_1, GamePreferences.ID_TYPE_OBSTACLE));
		listaEnemigos.add(new Obstaculo(R.drawable.obstacle_moon_2, GamePreferences.ID_TYPE_OBSTACLE + 1));

		listaEnemigos.add(new Enemigo(R.drawable.enemy_moon_1, GamePreferences.ID_TYPE_ENEMY));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_moon_1, GamePreferences.ID_TYPE_ENEMY + 1));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_moon_2, GamePreferences.ID_TYPE_ENEMY + 2));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_moon_2, GamePreferences.ID_TYPE_ENEMY + 3));

		background.setBackground(R.drawable.background_moon_2, R.drawable.background_moon_3, R.drawable.background_moon_4, R.drawable.background_moon_5, R.drawable.polaroid_gameover_moon, R.drawable.polaroid_levelcompleted_moon, R.drawable.polaroid_levelperfected_moon);
	}

	private void crearNivelNewYork(List<Nivel> listaNiveles, List<Entidad> listaEnemigos, List<String> listaNombres, Background background)
	{
		Typeface textFont = Typeface.createFromAsset(mContext.getAssets(), GamePreferences.FONT_NEW_YORK_PATH);
		
		listaNiveles.add(new Nivel(TTipoLevel.NewYork, R.drawable.background_newyork_1, R.drawable.achievement_newyork_completed, R.drawable.achievement_newyork_perfected, R.string.title_level_section_newyork, R.string.text_level_section_newyork, Color.BLACK, textFont, R.raw.music_newyork));
		listaNombres.add(mContext.getString(R.string.title_level_section_newyork));

		listaEnemigos.add(new Misil(R.drawable.missile_newyork, GamePreferences.ID_TYPE_MISSILE));
		listaEnemigos.add(new Obstaculo(R.drawable.obstacle_newyork_1, GamePreferences.ID_TYPE_OBSTACLE));
		listaEnemigos.add(new Obstaculo(R.drawable.obstacle_newyork_2, GamePreferences.ID_TYPE_OBSTACLE + 1));
		
		listaEnemigos.add(new Enemigo(R.drawable.enemy_newyork_1, GamePreferences.ID_TYPE_ENEMY));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_newyork_2, GamePreferences.ID_TYPE_ENEMY + 1));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_newyork_3, GamePreferences.ID_TYPE_ENEMY + 2));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_newyork_4, GamePreferences.ID_TYPE_ENEMY + 3));

		background.setBackground(R.drawable.background_newyork_2, R.drawable.background_newyork_3, R.drawable.background_newyork_4, R.drawable.background_newyork_5, R.drawable.polaroid_gameover_newyork, R.drawable.polaroid_levelcompleted_newyork, R.drawable.polaroid_levelperfected_newyork);
	}

	private void crearNivelRoma(List<Nivel> listaNiveles, List<Entidad> listaEnemigos, List<String> listaNombres, Background background)
	{
		Typeface textFont = Typeface.createFromAsset(mContext.getAssets(), GamePreferences.FONT_ROME_PATH);
		
		listaNiveles.add(new Nivel(TTipoLevel.Rome, R.drawable.background_rome_1, R.drawable.achievement_rome_completed, R.drawable.achievement_rome_perfected, R.string.title_level_section_rome, R.string.text_level_section_rome, Color.WHITE, textFont, R.raw.music_rome));
		listaNombres.add(mContext.getString(R.string.title_level_section_rome));

		listaEnemigos.add(new Misil(R.drawable.missile_rome, GamePreferences.ID_TYPE_MISSILE));
		listaEnemigos.add(new Obstaculo(R.drawable.obstacle_rome_1, GamePreferences.ID_TYPE_OBSTACLE));
		listaEnemigos.add(new Obstaculo(R.drawable.obstacle_rome_2, GamePreferences.ID_TYPE_OBSTACLE + 1));
		
		listaEnemigos.add(new Enemigo(R.drawable.enemy_rome_1, GamePreferences.ID_TYPE_ENEMY));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_rome_2, GamePreferences.ID_TYPE_ENEMY + 1));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_rome_3, GamePreferences.ID_TYPE_ENEMY + 2));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_rome_4, GamePreferences.ID_TYPE_ENEMY + 3));

		background.setBackground(R.drawable.background_rome_2, R.drawable.background_rome_3, R.drawable.background_rome_4, R.drawable.background_rome_5, R.drawable.polaroid_gameover_rome, R.drawable.polaroid_levelcompleted_rome, R.drawable.polaroid_levelperfected_rome);
	}

	private void crearNivelEgipto(List<Nivel> listaNiveles, List<Entidad> listaEnemigos, List<String> listaNombres, Background background)
	{
		Typeface textFont = Typeface.createFromAsset(mContext.getAssets(), GamePreferences.FONT_EGYPT_PATH);
		
		listaNiveles.add(new Nivel(TTipoLevel.Egypt, R.drawable.background_egypt_1, R.drawable.achievement_egypt_completed, R.drawable.achievement_egypt_perfected, R.string.title_level_section_egypt, R.string.text_level_section_egypt, Color.BLACK, textFont, R.raw.music_egypt));
		listaNombres.add(mContext.getString(R.string.title_level_section_egypt));
		
		listaEnemigos.add(new Misil(R.drawable.missile_egypt, GamePreferences.ID_TYPE_MISSILE));
		listaEnemigos.add(new Obstaculo(R.drawable.obstacle_egypt_1, GamePreferences.ID_TYPE_OBSTACLE));
		listaEnemigos.add(new Obstaculo(R.drawable.obstacle_egypt_2, GamePreferences.ID_TYPE_OBSTACLE + 1));

		listaEnemigos.add(new Enemigo(R.drawable.enemy_egypt_1, GamePreferences.ID_TYPE_ENEMY));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_egypt_2, GamePreferences.ID_TYPE_ENEMY + 1));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_egypt_3, GamePreferences.ID_TYPE_ENEMY + 2));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_egypt_4, GamePreferences.ID_TYPE_ENEMY + 3));
		
		background.setBackground(R.drawable.background_egypt_2, R.drawable.background_egypt_3, R.drawable.background_egypt_4, R.drawable.background_egypt_5, R.drawable.polaroid_gameover_egypt, R.drawable.polaroid_levelcompleted_egypt, R.drawable.polaroid_levelperfected_egypt);
	}

	private void crearNivelStonehenge(List<Nivel> listaNiveles, List<Entidad> listaEnemigos, List<String> listaNombres, Background background)
	{
		Typeface textFont = Typeface.createFromAsset(mContext.getAssets(), GamePreferences.FONT_STONEHENGE_PATH);
		
		listaNiveles.add(new Nivel(TTipoLevel.Stonehenge, R.drawable.background_stonehenge_1, R.drawable.achievement_stonehenge_completed, R.drawable.achievement_stonehenge_perfected, R.string.title_level_section_stonehenge, R.string.text_level_section_stonehenge, Color.BLACK, textFont, R.raw.music_stonhenge));
		listaNombres.add(mContext.getString(R.string.title_level_section_stonehenge));

		listaEnemigos.add(new Misil(R.drawable.missile_stonehenge, GamePreferences.ID_TYPE_MISSILE));
		listaEnemigos.add(new Obstaculo(R.drawable.obstacle_stonehenge_1, GamePreferences.ID_TYPE_OBSTACLE));
		listaEnemigos.add(new Obstaculo(R.drawable.obstacle_stonehenge_2, GamePreferences.ID_TYPE_OBSTACLE + 1));

		listaEnemigos.add(new Enemigo(R.drawable.enemy_stonehenge_1, GamePreferences.ID_TYPE_ENEMY));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_stonehenge_2, GamePreferences.ID_TYPE_ENEMY + 1));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_stonehenge_3, GamePreferences.ID_TYPE_ENEMY + 2));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_stonehenge_4, GamePreferences.ID_TYPE_ENEMY + 3));

		background.setBackground(R.drawable.background_stonehenge_2, R.drawable.background_stonehenge_3, R.drawable.background_stonehenge_4, R.drawable.background_stonehenge_5, R.drawable.polaroid_gameover_stonehenge, R.drawable.polaroid_levelcompleted_stonehenge, R.drawable.polaroid_levelperfected_stonehenge);
	}

	private List<Entidad> getListaEnemigos(int indice)
	{
		return listaEnemigos.get(indice);
	}

	private Background getFondo(int indice)
	{
		return listaFondos.get(indice);
	}

	private List<InstanciaEntidad> getColaEnemigos(int indice)
	{
		List<InstanciaEntidad> listaEnemigos = new ArrayList<InstanciaEntidad>();

		float posXActual = GamePreferences.POS_ENEMIES_INICIO();
		while (posXActual < GamePreferences.POS_ENEMIES_FINAL())
		{
			int tipoEnemigo = (int) Math.floor(Math.random() * GamePreferences.NUM_TYPE_ENEMIES);
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
		return new InstanciaNivel(level, listaNombres.get(indice), getListaEnemigos(indice), getColaEnemigos(indice), getFondo(indice));
	}
	
	public Nivel getLevel(TTipoLevel level)
	{
		return listaNiveles.get(level.ordinal());
	}

	public List<Nivel> getListaNiveles()
	{
		return listaNiveles;
	}
}

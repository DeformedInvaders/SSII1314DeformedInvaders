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
import com.game.data.Nivel;
import com.game.data.Obstaculo;
import com.project.main.GamePreferences;
import com.project.main.R;

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
		Typeface textFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/font_moon.ttf");
		
		listaNiveles.add(new Nivel(0, R.drawable.background_moon_1, R.string.title_level_section_moon, R.string.text_level_section_moon, Color.WHITE, textFont));
		listaNombres.add(mContext.getString(R.string.title_level_section_moon));

		listaEnemigos.add(new Obstaculo(R.drawable.obstacle_moon, GamePreferences.TYPE_OBSTACLE));

		listaEnemigos.add(new Enemigo(R.drawable.enemy_moon, GamePreferences.TYPE_ENEMY));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_moon, GamePreferences.TYPE_ENEMY + 1));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_moon, GamePreferences.TYPE_ENEMY + 2));
		listaEnemigos.add(new Enemigo(R.drawable.boss_moon, GamePreferences.TYPE_BOSS));

		background.setBackground(R.drawable.background_moon_2, R.drawable.background_moon_3, R.drawable.background_moon_4, R.drawable.background_moon_5, R.drawable.polaroid_gameover_moon, R.drawable.polaroid_levelcompleted_moon);
	}

	private void crearNivelNewYork(List<Nivel> listaNiveles, List<Entidad> listaEnemigos, List<String> listaNombres, Background background)
	{
		Typeface textFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/font_new_york.ttf");
		
		listaNiveles.add(new Nivel(1, R.drawable.background_newyork_1, R.string.title_level_section_newyork, R.string.text_level_section_newyork, Color.BLACK, textFont));
		listaNombres.add(mContext.getString(R.string.title_level_section_newyork));

		listaEnemigos.add(new Obstaculo(R.drawable.obstacle_newyork, GamePreferences.TYPE_OBSTACLE));

		listaEnemigos.add(new Enemigo(R.drawable.enemy_newyork1, GamePreferences.TYPE_ENEMY));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_newyork2, GamePreferences.TYPE_ENEMY + 1));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_newyork3, GamePreferences.TYPE_ENEMY + 2));
		listaEnemigos.add(new Enemigo(R.drawable.boss_newyork, GamePreferences.TYPE_BOSS));

		background.setBackground(R.drawable.background_newyork_2, R.drawable.background_newyork_3, R.drawable.background_newyork_4, R.drawable.background_newyork_5, R.drawable.polaroid_gameover_newyork, R.drawable.polaroid_levelcompleted_newyork);
	}

	private void crearNivelRoma(List<Nivel> listaNiveles, List<Entidad> listaEnemigos, List<String> listaNombres, Background background)
	{
		Typeface textFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/font_rome.ttf");
		
		listaNiveles.add(new Nivel(2, R.drawable.background_rome_1, R.string.title_level_section_rome, R.string.text_level_section_rome, Color.WHITE, textFont));
		listaNombres.add(mContext.getString(R.string.title_level_section_rome));

		listaEnemigos.add(new Obstaculo(R.drawable.obstacle_rome, GamePreferences.TYPE_OBSTACLE));

		listaEnemigos.add(new Enemigo(R.drawable.enemy_rome1, GamePreferences.TYPE_ENEMY));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_rome2, GamePreferences.TYPE_ENEMY + 1));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_rome3, GamePreferences.TYPE_ENEMY + 2));
		listaEnemigos.add(new Enemigo(R.drawable.boss_rome, GamePreferences.TYPE_BOSS));

		background.setBackground(R.drawable.background_rome_2, R.drawable.background_rome_3, R.drawable.background_rome_4, R.drawable.background_rome_5, R.drawable.polaroid_gameover_rome, R.drawable.polaroid_levelcompleted_rome);
	}

	private void crearNivelEgipto(List<Nivel> listaNiveles, List<Entidad> listaEnemigos, List<String> listaNombres, Background background)
	{
		Typeface textFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/font_egypt.ttf");
		
		listaNiveles.add(new Nivel(3, R.drawable.background_egypt_1, R.string.title_level_section_egypt, R.string.text_level_section_egypt, Color.BLACK, textFont));
		listaNombres.add(mContext.getString(R.string.title_level_section_egypt));

		listaEnemigos.add(new Obstaculo(R.drawable.obstacle_egypt, GamePreferences.TYPE_OBSTACLE));

		listaEnemigos.add(new Enemigo(R.drawable.enemy_egypt1, GamePreferences.TYPE_ENEMY));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_egypt2, GamePreferences.TYPE_ENEMY + 1));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_egypt3, GamePreferences.TYPE_ENEMY + 2));
		listaEnemigos.add(new Enemigo(R.drawable.boss_egypt, GamePreferences.TYPE_BOSS));

		background.setBackground(R.drawable.background_egypt_2, R.drawable.background_egypt_3, R.drawable.background_egypt_4, R.drawable.background_egypt_5, R.drawable.polaroid_gameover_egypt, R.drawable.polaroid_levelcompleted_egypt);
	}

	private void crearNivelStonehenge(List<Nivel> listaNiveles, List<Entidad> listaEnemigos, List<String> listaNombres, Background background)
	{
		Typeface textFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/font_stonehenge.ttf");
		
		listaNiveles.add(new Nivel(4, R.drawable.background_stonehenge_1, R.string.title_level_section_stonehenge, R.string.text_level_section_stonehenge, Color.BLACK, textFont));
		listaNombres.add(mContext.getString(R.string.title_level_section_stonehenge));

		listaEnemigos.add(new Obstaculo(R.drawable.obstacle_stonehenge, 0));

		listaEnemigos.add(new Enemigo(R.drawable.enemy_stonehenge1, GamePreferences.TYPE_ENEMY));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_stonehenge2, GamePreferences.TYPE_ENEMY + 1));
		listaEnemigos.add(new Enemigo(R.drawable.enemy_stonehenge3, GamePreferences.TYPE_ENEMY + 2));
		listaEnemigos.add(new Enemigo(R.drawable.boss_stonehenge, GamePreferences.TYPE_BOSS));

		background.setBackground(R.drawable.background_stonehenge_2, R.drawable.background_stonehenge_3, R.drawable.background_stonehenge_4, R.drawable.background_stonehenge_5, R.drawable.polaroid_gameover_stonehenge, R.drawable.polaroid_levelcompleted_stonehenge);
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
		List<InstanciaEntidad> colaEnemigos = new ArrayList<InstanciaEntidad>();

		float posActual = GamePreferences.POS_ENEMIES_INICIO;
		while (posActual < GamePreferences.POS_ENEMIES_FINAL)
		{
			int tipoEnemigo = (int) Math.floor(Math.random() * GamePreferences.NUM_TYPE_ENEMIGOS);
			float posEnemigo = posActual + (float) (Math.random() * GamePreferences.DISTANCE_BETWEEN_ENEMY);

			colaEnemigos.add(new InstanciaEntidad(tipoEnemigo, posEnemigo));
			posActual = posEnemigo + GamePreferences.DISTANCE_BETWEEN_ENEMY;
		}

		colaEnemigos.add(new InstanciaEntidad(GamePreferences.NUM_TYPE_ENEMIGOS, GamePreferences.POS_BOSS));

		return colaEnemigos;
	}

	public InstanciaNivel getLevel(int indice)
	{
		return new InstanciaNivel(indice, listaNombres.get(indice), getListaEnemigos(indice), getColaEnemigos(indice), getFondo(indice));
	}

	public List<Nivel> getListaNiveles()
	{
		return listaNiveles;
	}
}

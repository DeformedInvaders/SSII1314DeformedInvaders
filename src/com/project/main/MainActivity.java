package com.project.main;

import java.util.List;

import android.app.ActionBar;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.storage.OnLoadingListener;
import com.character.select.CharacterSelectionDataSaved;
import com.character.select.CharacterSelectionFragment;
import com.creation.deform.DeformationFragment;
import com.creation.design.DesignDataSaved;
import com.creation.design.DesignFragment;
import com.creation.paint.PaintDataSaved;
import com.creation.paint.PaintFragment;
import com.game.data.InstanciaNivel;
import com.game.data.Nivel;
import com.game.data.Personaje;
import com.game.game.GameFragment;
import com.game.select.LevelSelectionFragment;
import com.game.select.TTipoLevel;
import com.loading.load.LoadingFragment;
import com.project.controller.GameController;
import com.project.model.GameCore;
import com.project.model.GamePreferences;
import com.project.model.GameStatistics;

public class MainActivity extends FragmentActivity
{
	/* Controlador */
	private GameController controller;
	
	/* Modelo */
	private GameCore core;
	
	/* Elementos de la Interafaz */
	private ActionBar actionBar;
	private MenuItem botonTwitter, botonFacebook, botonMusica, botonConsejos;

	/* Métodos Activity */
	
	public interface ActivityListener
	{
		public void onActivityStarted();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_layout);

		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		// Parámetros globales del juego.
		
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
        core = new GameCore(this, metrics.widthPixels, metrics.heightPixels) {
			@Override
			public void onSocialConectionStatusChanged()
			{
				actualizarActionBar();	
			}
        };
        
        controller = new GameController(this, this, core);
        controller.onActivityStarted();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		core.continuarMusica();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		core.pausarMusica();
	}

	@Override
	public void onBackPressed()
	{
		limpiarActionBar();
		controller.desapilarEstado();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		
		botonConsejos = menu.getItem(0);
		botonMusica = menu.getItem(1);
		botonTwitter = menu.getItem(2);
		botonFacebook = menu.getItem(3);
		
		actualizarActionBar();

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menuIcon1:
				return onMenuTipsButtonClicked();
			case R.id.menuIcon2:
				return onMenuMusicButtonClicked();
			case R.id.menuIcon3:
				return onMenuTwitterButtonClicked();
			case R.id.menuIcon4:
				return onMenuFacebookButtonClicked();
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/* Métodos de Modificación de la ActionBar */

	public boolean onMenuTwitterButtonClicked()
	{
		return core.modificarConexionTwitter();
	}

	public boolean onMenuFacebookButtonClicked()
	{
		return core.modificarConexionFacebook();
	}
	
	public boolean onMenuMusicButtonClicked()
	{
		GamePreferences.SWITCH_MUSIC_GAME();
		core.actualizarPreferencias();
		
		controller.actualizarMusica();
		actualizarActionBar();
		return true;
	}
	
	public boolean onMenuTipsButtonClicked()
	{
		GamePreferences.SWITCH_TIPS_GAME();
		core.actualizarPreferencias();
		
		actualizarActionBar();
		return true;
	}

	public void actualizarActionBar()
	{
		if (core.isTwitterConectado())
		{
			botonTwitter.setIcon(R.drawable.icon_social_twitter_connected);
		}
		else
		{
			botonTwitter.setIcon(R.drawable.icon_social_twitter_disconnected);
		}

		if (core.isFacebookConectado())
		{
			botonFacebook.setIcon(R.drawable.icon_social_facebook_connected);
		}
		else
		{
			botonFacebook.setIcon(R.drawable.icon_social_facebook_disconnected);
		}
		
		if (GamePreferences.IS_MUSIC_ENABLED())
		{
			botonMusica.setIcon(R.drawable.icon_media_music_enabled);
		}
		else
		{
			botonMusica.setIcon(R.drawable.icon_media_music_disabled);
		}
		
		if (GamePreferences.IS_TIPS_ENABLED())
		{
			botonConsejos.setIcon(R.drawable.icon_tool_tips_enabled);
		}
		else
		{
			botonConsejos.setIcon(R.drawable.icon_tool_tips_disabled);
		}
	}

	private void limpiarActionBar()
	{
		actionBar.removeAllTabs();
	}
	
	private void cambiarTituloActionBar(int texto)
	{
		setTitle(texto);
	}

	/* Métodos de Modificación del FrameLayout */

	private void insertarFragmento(Fragment fragmento)
	{
		limpiarActionBar();

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.frameLayoutMain1, fragmento);
		transaction.commit();
		
		controller.actualizarMusica();
	}

	public void insertarLoadingFragmento(OnLoadingListener listener)
	{
		insertarFragmento(LoadingFragment.newInstance(listener));
		cambiarTituloActionBar(R.string.title_app);
	}

	public void insertarMainFragmento(Personaje personaje, int numeroPersonajes)
	{
		insertarFragmento(MainFragment.newInstance(controller, personaje, numeroPersonajes));
		cambiarTituloActionBar(R.string.title_app);
	}

	public void insertarDesignFragmento()
	{
		insertarFragmento(DesignFragment.newInstance(controller));
		cambiarTituloActionBar(R.string.title_design_phase);
	}
	
	public void insertarDesignFragmento(DesignDataSaved datosSalvados)
	{
		insertarFragmento(DesignFragment.newInstance(controller, datosSalvados));
		cambiarTituloActionBar(R.string.title_design_phase);
	}

	public void insertarPaintFragmento(Personaje nuevoPersonaje, GameStatistics[] estadisticasNiveles)
	{
		insertarFragmento(PaintFragment.newInstance(controller, nuevoPersonaje, estadisticasNiveles));
		cambiarTituloActionBar(R.string.title_paint_phase);
	}
	
	public void insertarPaintFragmento(Personaje nuevoPersonaje, GameStatistics[] estadisticasNiveles, PaintDataSaved datosSalvados)
	{
		insertarFragmento(PaintFragment.newInstance(controller, nuevoPersonaje, estadisticasNiveles, datosSalvados));
		cambiarTituloActionBar(R.string.title_paint_phase);
	}
	
	public void insertarPaintFragmento(Personaje personaje, int indice, GameStatistics[] estadisticasNiveles)
	{
		insertarFragmento(PaintFragment.newInstance(controller, personaje, indice, estadisticasNiveles));
		cambiarTituloActionBar(R.string.title_paint_phase);
	}

	public void insertarDeformationFragmento(Personaje nuevoPersonaje)
	{
		insertarFragmento(DeformationFragment.newInstance(controller, nuevoPersonaje));
		cambiarTituloActionBar(R.string.title_deformation_phase);
	}

	public void insertarCharacterSelectionFragmento(List<Personaje> listaPersonajes)
	{
		insertarFragmento(CharacterSelectionFragment.newInstance(controller, listaPersonajes));
		cambiarTituloActionBar(R.string.title_character_selection_phase);
	}
	
	public void insertarCharacterSelectionFragmento(List<Personaje> listaPersonajes, CharacterSelectionDataSaved datosSalvados)
	{
		insertarFragmento(CharacterSelectionFragment.newInstance(controller, listaPersonajes, datosSalvados));
		cambiarTituloActionBar(R.string.title_character_selection_phase);
	}

	public void insertarLevelSelectionFragmento(List<Nivel> listaNiveles, GameStatistics[] estadisticasNiveles)
	{
		insertarFragmento(LevelSelectionFragment.newInstance(controller, listaNiveles, estadisticasNiveles));
		cambiarTituloActionBar(R.string.title_level_selection_phase);
	}
	
	public void insertarLevelSelectionFragmento(List<Nivel> listaNiveles, GameStatistics[] estadisticasNiveles, TTipoLevel nivel)
	{
		insertarFragmento(LevelSelectionFragment.newInstance(controller, listaNiveles, estadisticasNiveles, nivel));
		cambiarTituloActionBar(R.string.title_level_selection_phase);
	}

	public void insertarGameFragmento(Personaje personajeSeleccionado, InstanciaNivel nivel)
	{
		insertarFragmento(GameFragment.newInstance(controller, personajeSeleccionado, nivel));
		cambiarTituloActionBar(R.string.title_game_phase);
	}
}

package com.main.view;

import java.util.List;
import java.util.Locale;

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
import com.main.controller.GameController;
import com.main.model.GameCore;
import com.main.model.GamePreferences;
import com.main.model.GameStatistics;
import com.project.main.R;

public class ViewActivity extends FragmentActivity
{
	/* Controlador */
	private GameController controller;
	
	/* Modelo */
	private GameCore core;
	
	/* Elementos de la Interafaz */
	private ActionBar actionBar;
	private MenuItem botonTwitter, botonFacebook, botonMusica, botonConsejos, botonDebug;
	
	/* Métodos Activity */
	
	public interface ActivityFragmentListener
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
		botonDebug = menu.getItem(4);
		
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
			case R.id.menuIcon5:
				return onMenuDebugButtonClicked();
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
	
	public boolean onMenuDebugButtonClicked()
	{
		GamePreferences.SWITCH_DEBUG_GAME();
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
		
		if (GamePreferences.IS_DEBUG_ENABLED())
		{
			botonDebug.setIcon(R.drawable.icon_tool_debug_enabled);
		}
		else
		{
			botonDebug.setIcon(R.drawable.icon_tool_debug_disabled);
		}
	}

	private void limpiarActionBar()
	{
		actionBar.removeAllTabs();
	}
	
	private void cambiarTituloActionBar(int texto)
	{
		setTitle(getString(texto).toUpperCase(Locale.getDefault()));
	}

	/* Métodos de Modificación del FrameLayout */

	private void insertarFragmento(Fragment fragmento)
	{
		limpiarActionBar();

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.frameLayoutMain1, fragmento);
		transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
		transaction.commit();
	}

	public void insertarMainFragmento(Personaje personaje, int numeroPersonajes, int title)
	{
		insertarFragmento(MainFragment.newInstance(controller, personaje, numeroPersonajes));
		cambiarTituloActionBar(title);
	}

	public void insertarDesignFragmento(int title)
	{
		insertarFragmento(DesignFragment.newInstance(controller));
		cambiarTituloActionBar(title);
	}
	
	public void insertarDesignFragmento(DesignDataSaved datosSalvados, int title)
	{
		insertarFragmento(DesignFragment.newInstance(controller, datosSalvados));
		cambiarTituloActionBar(title);
	}

	public void insertarPaintFragmento(Personaje nuevoPersonaje, GameStatistics[] estadisticasNiveles, int title)
	{
		insertarFragmento(PaintFragment.newInstance(controller, nuevoPersonaje, estadisticasNiveles));
		cambiarTituloActionBar(title);
	}
	
	public void insertarPaintFragmento(Personaje nuevoPersonaje, GameStatistics[] estadisticasNiveles, PaintDataSaved datosSalvados, int title)
	{
		insertarFragmento(PaintFragment.newInstance(controller, nuevoPersonaje, estadisticasNiveles, datosSalvados));
		cambiarTituloActionBar(title);
	}
	
	public void insertarPaintFragmento(Personaje personaje, int indice, GameStatistics[] estadisticasNiveles, int title)
	{
		insertarFragmento(PaintFragment.newInstance(controller, personaje, indice, estadisticasNiveles));
		cambiarTituloActionBar(title);
	}

	public void insertarDeformationFragmento(Personaje nuevoPersonaje, int title)
	{
		insertarFragmento(DeformationFragment.newInstance(controller, nuevoPersonaje));
		cambiarTituloActionBar(title);
	}

	public void insertarCharacterSelectionFragmento(List<Personaje> listaPersonajes, int title)
	{
		insertarFragmento(CharacterSelectionFragment.newInstance(controller, listaPersonajes));
		cambiarTituloActionBar(title);
	}
	
	public void insertarCharacterSelectionFragmento(List<Personaje> listaPersonajes, CharacterSelectionDataSaved datosSalvados, int title)
	{
		insertarFragmento(CharacterSelectionFragment.newInstance(controller, listaPersonajes, datosSalvados));
		cambiarTituloActionBar(title);
	}

	public void insertarLevelSelectionFragmento(List<Nivel> listaNiveles, GameStatistics[] estadisticasNiveles, int title)
	{
		insertarFragmento(LevelSelectionFragment.newInstance(controller, listaNiveles, estadisticasNiveles));
		cambiarTituloActionBar(title);
	}
	
	public void insertarLevelSelectionFragmento(List<Nivel> listaNiveles, GameStatistics[] estadisticasNiveles, TTipoLevel nivel, int title)
	{
		insertarFragmento(LevelSelectionFragment.newInstance(controller, listaNiveles, estadisticasNiveles, nivel));
		cambiarTituloActionBar(title);
	}

	public void insertarGameFragmento(Personaje personajeSeleccionado, InstanciaNivel nivel, int title)
	{
		insertarFragmento(GameFragment.newInstance(controller, personajeSeleccionado, nivel));
		cambiarTituloActionBar(title);
	}
}

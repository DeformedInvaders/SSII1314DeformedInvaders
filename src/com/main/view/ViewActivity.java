package com.main.view;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.sensor.OrientationDetector;
import com.android.storage.ExternalStorageManager;
import com.character.select.CharacterSelectionDataSaved;
import com.character.select.CharacterSelectionFragment;
import com.creation.deform.DeformationFragment;
import com.creation.design.DesignDataSaved;
import com.creation.design.DesignFragment;
import com.creation.paint.PaintDataSaved;
import com.creation.paint.PaintFragment;
import com.game.data.InstanceLevel;
import com.game.data.Level;
import com.game.data.Character;
import com.game.game.GameFragment;
import com.game.select.LevelSelectionFragment;
import com.game.select.TTypeLevel;
import com.main.controller.GameController;
import com.main.model.GameCore;
import com.main.model.GamePreferences;
import com.main.model.GameStatistics;
import com.project.main.R;
import com.video.data.Video;
import com.video.video.VideoFragment;

public class ViewActivity extends FragmentActivity
{
	/* Controlador */
	private GameController mController;
	
	/* Modelo */
	private GameCore mCore;
	
	/* Elementos de la Interafaz */
	private ActionBar mActionBar;
	private MenuItem botonTwitter, botonFacebook, buttonMusic, buttonTips, /*botonDebug,*/ botonSensor;
	
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
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		// ActionBar
		
		mActionBar = getActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Permisos Internet
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		// Parámetros globales de la pantalla.
		
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
        // Parámetros de los sensores.
        
        boolean sensorAvailable = OrientationDetector.isSensorAvailable(this);
        
        // Arquitectura
        
        mCore = new GameCore(this, metrics.widthPixels, metrics.heightPixels, sensorAvailable) {
			@Override
			public void onSocialConectionStatusChanged()
			{
				updateActionBar();	
			}
        };
        
        mController = new GameController(this, this, mCore);
        
        mController.onActivityStarted();
                
        ExternalStorageManager.writeLogcat("ACTIVITY", DateFormat.getDateTimeInstance().format(new Date()));
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		mController.resumeMusic();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		mController.pauseMusic();
	}

	@Override
	public void onBackPressed()
	{
		clearActionBar();
		mController.popState();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		
		buttonTips = menu.getItem(0);
		botonSensor = menu.getItem(1);
		buttonMusic = menu.getItem(2);
		botonTwitter = menu.getItem(3);
		botonFacebook = menu.getItem(4);
		//botonDebug = menu.getItem(5);
		
		updateActionBar();

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
				return onMenuSensorButtonClicked();
			case R.id.menuIcon3:
				return onMenuMusicButtonClicked();
			case R.id.menuIcon4:
				return onMenuTwitterButtonClicked();
			case R.id.menuIcon5:
				return onMenuFacebookButtonClicked();
			/*case R.id.menuIcon6:
				return onMenuDebugButtonClicked();*/
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/* Métodos de Modificación de la ActionBar */

	public boolean onMenuTwitterButtonClicked()
	{
		return mCore.modificarConexionTwitter();
	}

	public boolean onMenuFacebookButtonClicked()
	{
		return mCore.modificarConexionFacebook();
	}
	
	public boolean onMenuMusicButtonClicked()
	{
		GamePreferences.SWITCH_MUSIC_GAME();
		mCore.updatePreferences();
		
		mController.updateMusic();
		updateActionBar();
		return true;
	}
	
	public boolean onMenuTipsButtonClicked()
	{
		GamePreferences.SWITCH_TIPS_GAME();
		mCore.updatePreferences();
		
		updateActionBar();
		return true;
	}
	
	public boolean onMenuSensorButtonClicked()
	{
		GamePreferences.SWITCH_SENSOR_GAME();
		mCore.updatePreferences();
		
		updateActionBar();
		return true;
	}
	
	public boolean onMenuDebugButtonClicked()
	{
		GamePreferences.SWITCH_DEBUG_GAME();
		mCore.updatePreferences();
		
		updateActionBar();
		return true;
	}

	public void updateActionBar()
	{
		if (mCore.isTwitterConnected())
		{
			botonTwitter.setIcon(R.drawable.icon_social_twitter_connected);
		}
		else
		{
			botonTwitter.setIcon(R.drawable.icon_social_twitter_disconnected);
		}

		if (mCore.isFacebookConnected())
		{
			botonFacebook.setIcon(R.drawable.icon_social_facebook_connected);
		}
		else
		{
			botonFacebook.setIcon(R.drawable.icon_social_facebook_disconnected);
		}
		
		if (GamePreferences.IS_MUSIC_ENABLED())
		{
			buttonMusic.setIcon(R.drawable.icon_media_music_enabled);
		}
		else
		{
			buttonMusic.setIcon(R.drawable.icon_media_music_disabled);
		}
		
		if (GamePreferences.IS_TIPS_ENABLED())
		{
			buttonTips.setIcon(R.drawable.icon_tool_tips_enabled);
		}
		else
		{
			buttonTips.setIcon(R.drawable.icon_tool_tips_disabled);
		}
		
		/*if (GamePreferences.IS_DEBUG_ENABLED())
		{
			botonDebug.setIcon(R.drawable.icon_tool_debug_enabled);
		}
		else
		{
			botonDebug.setIcon(R.drawable.icon_tool_debug_disabled);
		}*/
		
		if (GamePreferences.IS_SENSOR_ENABLED())
		{
			botonSensor.setIcon(R.drawable.icon_tool_sensor_enabled);
		}
		else
		{
			botonSensor.setIcon(R.drawable.icon_tool_sensor_disabled);
		}
	}

	private void clearActionBar()
	{
		mActionBar.removeAllTabs();
	}
	
	private void updateTitleActionBar(int texto)
	{
		setTitle(getString(texto).toUpperCase(Locale.getDefault()));
	}

	/* Métodos de Modificación del FrameLayout */

	private void addFragment(Fragment fragmento)
	{
		clearActionBar();

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.frameLayoutMain1, fragmento);
		transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
		transaction.commit();
	}

	public void addMainFragment(Character personaje, int numeroPersonajes, int numeroFicheros, int title)
	{
		addFragment(MainFragment.newInstance(mController, personaje, numeroPersonajes, numeroFicheros));
		updateTitleActionBar(title);
	}

	public void addDesignFragment(int title)
	{
		addFragment(DesignFragment.newInstance(mController));
		updateTitleActionBar(title);
	}
	
	public void addDesignFragment(DesignDataSaved datosSalvados, int title)
	{
		addFragment(DesignFragment.newInstance(mController, datosSalvados));
		updateTitleActionBar(title);
	}

	public void addPaintFragment(Character nuevoPersonaje, GameStatistics[] estadisticasNiveles, int title)
	{
		addFragment(PaintFragment.newInstance(mController, nuevoPersonaje, estadisticasNiveles));
		updateTitleActionBar(title);
	}
	
	public void addPaintFragment(Character nuevoPersonaje, GameStatistics[] estadisticasNiveles, PaintDataSaved datosSalvados, int title)
	{
		addFragment(PaintFragment.newInstance(mController, nuevoPersonaje, estadisticasNiveles, datosSalvados));
		updateTitleActionBar(title);
	}
	
	public void addPaintFragment(Character personaje, int indice, GameStatistics[] estadisticasNiveles, int title)
	{
		addFragment(PaintFragment.newInstance(mController, personaje, indice, estadisticasNiveles));
		updateTitleActionBar(title);
	}

	public void addDeformationFragment(Character nuevoPersonaje, int title)
	{
		addFragment(DeformationFragment.newInstance(mController, nuevoPersonaje));
		updateTitleActionBar(title);
	}
	
	public void addDeformationFragment(Character nuevoPersonaje, int indice, int title)
	{
		addFragment(DeformationFragment.newInstance(mController, nuevoPersonaje, indice));
		updateTitleActionBar(title);
	}

	public void addCharacterSelectionFragment(List<Character> listaPersonajes, int title)
	{
		addFragment(CharacterSelectionFragment.newInstance(mController, listaPersonajes));
		updateTitleActionBar(title);
	}
	
	public void addCharacterSelectionFragment(List<Character> listaPersonajes, CharacterSelectionDataSaved datosSalvados, int title)
	{
		addFragment(CharacterSelectionFragment.newInstance(mController, listaPersonajes, datosSalvados));
		updateTitleActionBar(title);
	}

	public void addLevelSelectionFragment(List<Level> listaNiveles, GameStatistics[] estadisticasNiveles, int title)
	{
		addFragment(LevelSelectionFragment.newInstance(mController, listaNiveles, estadisticasNiveles));
		updateTitleActionBar(title);
	}
	
	public void addLevelSelectionFragment(List<Level> listaNiveles, GameStatistics[] estadisticasNiveles, TTypeLevel nivel, int title)
	{
		addFragment(LevelSelectionFragment.newInstance(mController, listaNiveles, estadisticasNiveles, nivel));
		updateTitleActionBar(title);
	}

	public void addGameFragment(Character personajeSeleccionado, InstanceLevel nivel, int title)
	{
		addFragment(GameFragment.newInstance(mController, personajeSeleccionado, nivel));
		updateTitleActionBar(title);
	}
	
	public void addVideoFragment(Video video, int title)
	{
		addFragment(VideoFragment.newInstance(mController, video));
		updateTitleActionBar(title);
	}
}

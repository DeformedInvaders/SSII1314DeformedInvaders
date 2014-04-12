package com.project.main;

import java.io.File;
import java.util.List;

import android.app.ActionBar;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.alert.ConfirmationAlert;
import com.android.alert.ImageAlert;
import com.android.alert.SummaryAlert;
import com.android.alert.TextInputAlert;
import com.android.audio.AudioPlayerManager;
import com.android.social.SocialConnector;
import com.android.storage.ExternalStorageManager;
import com.android.storage.InternalStorageManager;
import com.character.select.CharacterSelectionFragment;
import com.creation.data.Esqueleto;
import com.creation.data.Movimientos;
import com.creation.data.TTipoMovimiento;
import com.creation.data.Textura;
import com.creation.deform.DeformationFragment;
import com.creation.design.DesignFragment;
import com.creation.paint.PaintFragment;
import com.game.data.InstanciaNivel;
import com.game.data.Nivel;
import com.game.data.Personaje;
import com.game.game.GameFragment;
import com.game.select.LevelGenerator;
import com.game.select.LevelSelectionFragment;
import com.game.select.TTipoLevel;

public class MainActivity extends FragmentActivity implements LoadingFragment.LoadingFragmentListener, MainFragment.MainFragmentListener, DesignFragment.DesignFragmentListener, PaintFragment.PaintFragmentListener, DeformationFragment.AnimationFragmentListener, CharacterSelectionFragment.CharacterSelectionFragmentListener, LevelSelectionFragment.LevelSelectionFragmentListener, GameFragment.GameFragmentListener
{
	/* Estructura de Datos */
	private List<Personaje> listaPersonajes;
	private Personaje personajeActual;
	
	/* Musica */
	private AudioPlayerManager audioManager;
	private int musicaSeleccionada;

	/* Almacenamiento */
	private InternalStorageManager internalManager;
	private ExternalStorageManager externalManager;

	/* Conectores Sociales */
	private SocialConnector socialConnector;

	/* Elementos de la Interafaz */
	private ActionBar actionBar;
	private MenuItem botonTwitter, botonFacebook, botonMusica, botonConsejos;

	/* Estado */
	private TEstadoMain estado;

	/* Niveles */
	private LevelGenerator levelGenerator;
	private boolean[] estadoNiveles;
	private int[] puntuacionNiveles;

	/* Métodos Activity */

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_layout);

		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		internalManager = new InternalStorageManager(this);
		externalManager = new ExternalStorageManager();
		
		levelGenerator = new LevelGenerator(this);
		
		socialConnector = new SocialConnector(this) {
			@Override
			public void onConectionStatusChange()
			{
				actualizarActionBar();
			}
		};
		
		audioManager = new AudioPlayerManager(this) {
			@Override
			public void onPlayerCompletion() { }
		};

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		// Parámetros globales del juego.
		
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        GamePreferences.setScreenParameters(metrics.widthPixels, metrics.heightPixels);
        
        GamePreferences.setMusicParameters(false);
        GamePreferences.setTipParameters(false);
        
		changeFragment(LoadingFragment.newInstance(internalManager));
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		audioManager.resumePlaying();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		audioManager.pausePlaying();
	}

	@Override
	public void onBackPressed()
	{
		if (estado != TEstadoMain.Main && estado != TEstadoMain.Game)
		{
			limpiarActionBar();

			super.onBackPressed();
			actualizarEstado();
		}
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

	/* Métodos de Modificación del FrameLayout */

	private void changeFragment(Fragment fragmento)
	{
		boolean clearBackStack = false;
		boolean addToBackStack = true;

		if (estado == TEstadoMain.CharacterSelection || estado == TEstadoMain.LevelSelection || estado == TEstadoMain.Game)
		{
			addToBackStack = false;
		}

		actualizarEstado(fragmento);
		limpiarActionBar();

		if (estado == TEstadoMain.Main)
		{
			clearBackStack = true;
		}

		FragmentManager manager = getSupportFragmentManager();

		// Limpiar la BackStack
		if (clearBackStack)
		{
			clearBackStack(manager);
		}

		// Reemplazar el Fragmento
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.frameLayoutMain1, fragmento, estado.toString());

		// Añadir a la BackStack
		if (addToBackStack)
		{
			transaction.addToBackStack(estado.toString());
		}

		transaction.commit();
		
		actualizarMusica();
	}

	private void clearBackStack(FragmentManager manager)
	{
		manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	/* Métodos Loading Fragment */

	@Override
	public void onLoadingListCharacters(List<Personaje> lista, boolean[] niveles, int[] puntuacion)
	{
		listaPersonajes = lista;
		estadoNiveles = niveles;
		puntuacionNiveles = puntuacion;
		
		changeFragment(MainFragment.newInstance(listaPersonajes, internalManager));
	}

	/* Métodos Main Fragment */

	@Override
	public void onMainCreateButtonClicked()
	{
		personajeActual = new Personaje();

		changeFragment(DesignFragment.newInstance());
	}

	@Override
	public void onMainSelectButtonClicked()
	{
		changeFragment(CharacterSelectionFragment.newInstance(listaPersonajes, internalManager, externalManager, socialConnector));
	}

	@Override
	public void onMainPlayButtonClicked()
	{
		changeFragment(LevelSelectionFragment.newInstance(levelGenerator.getListaNiveles(), estadoNiveles));
	}

	/* Métodos Design Fragment */

	@Override
	public void onDesignReadyButtonClicked(Esqueleto esqueleto)
	{
		if (esqueleto == null)
		{
			Toast.makeText(getApplication(), R.string.error_design, Toast.LENGTH_SHORT).show();
		}
		else
		{
			personajeActual.setEsqueleto(esqueleto);
			changeFragment(PaintFragment.newInstance(personajeActual.getEsqueleto()));
		}
	}

	/* Métodos Paint Fragment */

	@Override
	public void onPaintReadyButtonClicked(Textura textura)
	{
		if (textura == null)
		{
			Toast.makeText(getApplication(), R.string.error_paint, Toast.LENGTH_SHORT).show();
		}
		else
		{
			personajeActual.setTextura(textura);
			changeFragment(DeformationFragment.newInstance(personajeActual.getEsqueleto(), personajeActual.getTextura(), internalManager));
		}
	}
	
	@Override
	public void onRepaintReadyButtonClicked(int indice, Textura textura)
	{
		if (textura == null)
		{
			Toast.makeText(getApplication(), R.string.error_paint, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Personaje personaje = listaPersonajes.get(indice);
			personaje.setTextura(textura);
			
			internalManager.actualizarPersonaje(personaje);
			changeFragment(CharacterSelectionFragment.newInstance(listaPersonajes, internalManager, externalManager, socialConnector));
		}
	}

	/* Métodos Animation Fragment */

	@Override
	public void onAnimationReadyButtonClicked(Movimientos movimientos)
	{
		if (movimientos == null)
		{
			Toast.makeText(getApplication(), R.string.error_animation, Toast.LENGTH_SHORT).show();
		}
		else
		{
			personajeActual.setMovimientos(movimientos);

			TextInputAlert alert = new TextInputAlert(this, getString(R.string.text_save_character_title), getString(R.string.text_save_character_description), getString(R.string.text_button_yes), getString(R.string.text_button_no)) {
				@Override
				public void onPossitiveButtonClick(String text)
				{
					personajeActual.setNombre(text);
					
					if (internalManager.guardarPersonaje(personajeActual))
					{							
						internalManager.guardarAudio(text, TTipoMovimiento.Run);
						internalManager.guardarAudio(text, TTipoMovimiento.Jump);
						internalManager.guardarAudio(text, TTipoMovimiento.Crouch);
						internalManager.guardarAudio(text, TTipoMovimiento.Attack);

						listaPersonajes.add(personajeActual);
						personajeActual = null;

						Toast.makeText(getApplication(), R.string.text_save_character_confirmation, Toast.LENGTH_SHORT).show();

						changeFragment(MainFragment.newInstance(listaPersonajes, internalManager));
					}
				}

				@Override
				public void onNegativeButtonClick(String text)
				{
					changeFragment(MainFragment.newInstance(listaPersonajes, internalManager));
				}

			};

			alert.show();
		}
	}

	/* Métodos Character Selection Fragment */

	@Override
	public void onCharacterSelectionSelectClicked(int indice)
	{
		GamePreferences.setCharacterParameters(indice);

		internalManager.guardarPreferencias();
		Toast.makeText(getApplication(), R.string.text_select_character_confirmation, Toast.LENGTH_SHORT).show();

		changeFragment(MainFragment.newInstance(listaPersonajes, internalManager));
	}

	@Override
	public void onCharacterSelectionDeleteButtonClicked(final int indice)
	{
		ConfirmationAlert alert = new ConfirmationAlert(this, getString(R.string.text_delete_character_title), getString(R.string.text_delete_character_description), getString(R.string.text_button_ok), getString(R.string.text_button_no)) {
			@Override
			public void onPossitiveButtonClick()
			{
				if (internalManager.eliminarPersonaje(listaPersonajes.get(indice)))
				{
					listaPersonajes.remove(indice);

					if (GamePreferences.GET_CHARACTER_GAME() == indice)
					{
						GamePreferences.setCharacterParameters(-1);
						internalManager.guardarPreferencias();
					}

					Toast.makeText(getApplication(), R.string.text_delete_character_confirmation, Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(getApplication(), R.string.error_delete_character, Toast.LENGTH_SHORT).show();
				}

				changeFragment(MainFragment.newInstance(listaPersonajes, internalManager));
			}

			@Override
			public void onNegativeButtonClick() { }
		};

		alert.show();
	}
	
	@Override
	public void onCharacterSelectionRenameButtonClicked(final int indice)
	{
		TextInputAlert alert = new TextInputAlert(this, getString(R.string.text_rename_character_title), getString(R.string.text_rename_character_description), getString(R.string.text_button_rename), getString(R.string.text_button_cancel)) {
			@Override
			public void onPossitiveButtonClick(String text)
			{
				Personaje personaje = listaPersonajes.get(indice);
				
				if (internalManager.renombrarPersonaje(personaje, text))
				{
					Toast.makeText(getApplication(), R.string.text_rename_character_confirmation, Toast.LENGTH_SHORT).show();

					changeFragment(CharacterSelectionFragment.newInstance(listaPersonajes, internalManager, externalManager, socialConnector));
				}
			}

			@Override
			public void onNegativeButtonClick(String text) { }

		};

		alert.show();
	}
	
	@Override
	public void onCharacterSelectionRepaintButtonClicked(int indice)
	{
		changeFragment(PaintFragment.newInstance(listaPersonajes.get(indice), indice));
	}
	
	@Override
	public void onCharacterSelectionExportButtonClicked(int indice)
	{
		if (internalManager.exportarPersonaje(listaPersonajes.get(indice)))
		{
			Toast.makeText(getApplication(), R.string.text_export_character_confirmation, Toast.LENGTH_SHORT).show();
		}
	}

	/* Métodos Level Selection Fragment */

	public void onLevelSelectionSelectClicked(TTipoLevel level)
	{	
		Nivel nivelSeleccionado = levelGenerator.getLevel(level);
		InstanciaNivel intanciaNivelSeleccionado = levelGenerator.getInstanciaLevel(level);
		musicaSeleccionada = nivelSeleccionado.getMusicaNivel();
		
		changeFragment(GameFragment.newInstance(listaPersonajes.get(GamePreferences.GET_CHARACTER_GAME()), internalManager, intanciaNivelSeleccionado));
	
		// Resumen del nivel
		SummaryAlert alert = new SummaryAlert(this, getString(R.string.text_summary), getString(R.string.text_button_ready), intanciaNivelSeleccionado.getTipoEnemigos());
		alert.show();	
	}

	/* Métodos Game Fragment */

	@Override
	public void onGameFinished(final TTipoLevel level, final int score, final int idImagen, final String nameLevel)
	{
		// Sonido Victoria
		audioManager.startPlaying(R.raw.effect_level_complete, false);
		
		// Desbloquear Siguiente nivel
		int nextLevel = (level.ordinal() + 1) % estadoNiveles.length;
		estadoNiveles[nextLevel] = true;
		
		// Actualizar Puntuacion máxima
		if (score > puntuacionNiveles[level.ordinal()])
		{
			puntuacionNiveles[level.ordinal()] = score;
		}
		
		internalManager.guardarNiveles(estadoNiveles);
		internalManager.guardarPuntuacion(puntuacionNiveles);

		// Publicar Nivel Completado
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), idImagen);
		if (externalManager.guardarImagenTemp(bitmap))
		{
			String text = getString(R.string.text_social_level_completed_initial) + " " + nameLevel + " " + getString(R.string.text_social_level_completed_middle) + " " + score + " " + getString(R.string.text_social_level_completed_final);

			File foto = externalManager.cargarImagenTemp();
			socialConnector.publicar(text, foto);
			
			externalManager.eliminarImagenTemp();
		}

		// Seleccionar Siguiente Nivel
		ImageAlert alert = new ImageAlert(this, getString(R.string.text_game_finish) + " " + score, getString(R.string.text_button_replay), getString(R.string.text_button_levels), idImagen) {
			@Override
			public void onPossitiveButtonClick()
			{
				changeFragment(GameFragment.newInstance(listaPersonajes.get(GamePreferences.GET_CHARACTER_GAME()), internalManager, levelGenerator.getInstanciaLevel(level)));
			}

			@Override
			public void onNegativeButtonClick()
			{
				changeFragment(LevelSelectionFragment.newInstance(levelGenerator.getListaNiveles(), estadoNiveles));
			}
		};

		alert.show();
	}

	@Override
	public void onGameFailed(final TTipoLevel level, final int idImagen)
	{
		// Sonido Derrota
		audioManager.startPlaying(R.raw.effect_game_over, false);
		
		ImageAlert alert = new ImageAlert(this, getString(R.string.text_game_fail), getString(R.string.text_button_replay), getString(R.string.text_button_levels), idImagen) {
			@Override
			public void onPossitiveButtonClick()
			{
				changeFragment(GameFragment.newInstance(listaPersonajes.get(GamePreferences.GET_CHARACTER_GAME()), internalManager, levelGenerator.getInstanciaLevel(level)));
			}

			@Override
			public void onNegativeButtonClick()
			{
				changeFragment(LevelSelectionFragment.newInstance(levelGenerator.getListaNiveles(), estadoNiveles));
			}
		};

		alert.show();
	}

	/* Métodos de Modificación de la ActionBar */

	public boolean onMenuTwitterButtonClicked()
	{
		if (socialConnector.isTwitterConnected())
		{
			socialConnector.desconectarTwitter();
		}
		else
		{
			socialConnector.conectarTwitter();
		}
		
		return true;
	}

	public boolean onMenuFacebookButtonClicked()
	{
		if (socialConnector.isFacebookConnected())
		{
			socialConnector.desconectarFacebook();
		}
		else
		{
			socialConnector.conectarFacebook();
		}
		
		return true;
	}
	
	public boolean onMenuMusicButtonClicked()
	{
		GamePreferences.SWITCH_MUSIC_GAME();
		internalManager.guardarPreferencias();
		
		actualizarMusica();
		actualizarActionBar();
		return true;
	}
	
	public boolean onMenuTipsButtonClicked()
	{
		GamePreferences.SWITCH_TIPS_GAME();
		internalManager.guardarPreferencias();
		
		actualizarActionBar();
		return true;
	}

	public void actualizarActionBar()
	{
		if (socialConnector.isTwitterConnected())
		{
			botonTwitter.setIcon(R.drawable.icon_social_twitter_connected);
		}
		else
		{
			botonTwitter.setIcon(R.drawable.icon_social_twitter_disconnected);
		}

		if (socialConnector.isFacebookConnected())
		{
			botonFacebook.setIcon(R.drawable.icon_social_facebook_connected);
		}
		else
		{
			botonFacebook.setIcon(R.drawable.icon_social_facebook_disconnected);
		}
		
		if (GamePreferences.MUSIC_ENABLED())
		{
			botonMusica.setIcon(R.drawable.icon_media_music_enabled);
		}
		else
		{
			botonMusica.setIcon(R.drawable.icon_media_music_disabled);
		}
		
		if (GamePreferences.TIPS_ENABLED())
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

	/* Métodos de Modificación del Estado */

	private void actualizarMusica()
	{
		if(GamePreferences.MUSIC_ENABLED())
		{
			if(estado == TEstadoMain.Game)
			{
				audioManager.startPlaying(musicaSeleccionada, true);
			}
			else
			{
				if(audioManager.isStoped())
				{
					audioManager.startPlaying(R.raw.music_main, true);
				}
			}
		}
		else
		{
			audioManager.stopPlaying();
		}
	}
	
	
	private void actualizarEstado(Fragment fragmento)
	{
		if (fragmento != null)
		{
			if (fragmento instanceof LoadingFragment)
			{
				estado = TEstadoMain.Loading;
				setTitle(R.string.title_app);
			}
			else if (fragmento instanceof MainFragment)
			{
				estado = TEstadoMain.Main;
				setTitle(R.string.title_app);
			}
			else if (fragmento instanceof DesignFragment)
			{
				estado = TEstadoMain.Design;
				setTitle(R.string.title_design_phase);
			}
			else if (fragmento instanceof PaintFragment)
			{
				estado = TEstadoMain.Paint;
				setTitle(R.string.title_paint_phase);
			}
			else if (fragmento instanceof DeformationFragment)
			{
				estado = TEstadoMain.Animation;
				setTitle(R.string.title_animation_phase);
			}
			else if (fragmento instanceof CharacterSelectionFragment)
			{
				estado = TEstadoMain.CharacterSelection;
				setTitle(R.string.title_character_selection_phase);
			}
			else if (fragmento instanceof LevelSelectionFragment)
			{
				estado = TEstadoMain.LevelSelection;
				setTitle(R.string.title_level_selection_phase);
			}
			else if (fragmento instanceof GameFragment)
			{
				estado = TEstadoMain.Game;
				setTitle(R.string.title_game_phase);
			}
		}
	}

	private void actualizarEstado()
	{
		String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
		actualizarEstado(getSupportFragmentManager().findFragmentByTag(tag));
	}
}

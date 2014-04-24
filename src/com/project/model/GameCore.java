package com.project.model;

import java.io.File;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.android.alert.ChooseAlert;
import com.android.audio.AudioPlayerManager;
import com.android.audio.AudioRecorderManager;
import com.android.social.SocialConnector;
import com.android.storage.ExternalStorageManager;
import com.android.storage.InternalStorageManager;
import com.android.storage.OnLoadingListener;
import com.creation.data.Esqueleto;
import com.creation.data.Movimientos;
import com.creation.data.TTipoMovimiento;
import com.creation.data.Textura;
import com.game.data.InstanciaNivel;
import com.game.data.Nivel;
import com.game.data.Personaje;
import com.game.select.LevelGenerator;
import com.game.select.TTipoLevel;
import com.project.main.R;

public abstract class GameCore
{
	/* Contexto */
	private Context mContext;
	
	/* Estructura de Datos */
	private List<Personaje> listaPersonajes;
	private Personaje nuevoPersonaje;
	
	/* Niveles */
	private LevelGenerator levelGenerator;
	private GameStatistics[] estadisticasNiveles;
	
	/* Musica */
	private AudioPlayerManager audioPlayerManager, soundPlayerManager;
	private AudioRecorderManager audioRecorderManager;
	private int musicaSeleccionada;

	/* Almacenamiento */
	private InternalStorageManager internalManager;
	private ExternalStorageManager externalManager;

	/* Conector Social */
	private SocialConnector socialConnector;
	
	/* Constructora */
	
	public GameCore(Context context, int widthScreen, int heightScreen)
	{
		mContext = context;
		
		nuevoPersonaje = null;
		levelGenerator = new LevelGenerator(mContext);
		
		internalManager = new InternalStorageManager(mContext);
		externalManager = new ExternalStorageManager(mContext);
		
		socialConnector = new SocialConnector(mContext) {
			@Override
			public void onConectionStatusChange()
			{
				onSocialConectionStatusChanged();
			}
		};
		
		audioPlayerManager = new AudioPlayerManager(mContext) {
			@Override
			public void onPlayerCompletion() { }
		};
		
		soundPlayerManager = new AudioPlayerManager(mContext) {
			@Override
			public void onPlayerCompletion() { }
		};
		
		audioRecorderManager = new AudioRecorderManager();
		
		GamePreferences.setScreenParameters(widthScreen, heightScreen);
		
        GamePreferences.setMusicParameters(false);
        GamePreferences.setTipParameters(false);
	}
	
	public boolean cargarDatos()
	{
		levelGenerator.cargarEnemigos();
		
		internalManager.cargarPreferencias();
		estadisticasNiveles = internalManager.cargarEstadisticas();	
		listaPersonajes = internalManager.cargarListaPersonajes();
		
		return internalManager.setLoadingFinished();
	}
	
	private void sendToastMessage(int message)
	{
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}
	
	/* Métodos abstractos */
	
	public abstract void onSocialConectionStatusChanged();
	
	/* Métodos de obtención de datos */
	
	public List<Personaje> getListaPersonajes()
	{
		return listaPersonajes;
	}
	
	public GameStatistics[] getEstadisticasNiveles()
	{
		return estadisticasNiveles;
	}

	public List<Nivel> getListaNiveles()
	{
		return levelGenerator.getListaNiveles();
	}

	public InstanciaNivel getNivel(TTipoLevel nivel)
	{
		musicaSeleccionada = levelGenerator.getLevel(nivel).getMusicaNivel();
		
		return levelGenerator.getInstanciaLevel(nivel);
	}
	
	public boolean isNivelPerfecto(TTipoLevel nivel)
	{
		return estadisticasNiveles[nivel.ordinal()].isPerfected();
	}
	
	public Personaje getNuevoPersonaje()
	{
		return nuevoPersonaje;
	}
	
	public Personaje getPersonaje(int indice)
	{
		if (indice >= 0 && indice < listaPersonajes.size())
		{
			return listaPersonajes.get(indice);
		}
		
		return null;
	}
	
	public Personaje getPersonajeSeleccionado()
	{
		if (GamePreferences.GET_CHARACTER_GAME() != -1)
		{
			return listaPersonajes.get(GamePreferences.GET_CHARACTER_GAME());
		}
		
		return null;
	}
	
	public int getNumeroPersonajes()
	{
		return listaPersonajes.size();
	}
	
	public OnLoadingListener getLoadingListener()
	{
		return internalManager.getLoadingListener();
	}
	
	/* Métodos de modificación del Personaje Actual */
	
	public boolean crearNuevoPersonaje()
	{
		if (nuevoPersonaje == null)
		{
			nuevoPersonaje = new Personaje();
			return true;
		}
		
		return false;
	}
	
	public boolean actualizarNuevoPersonaje(Esqueleto esqueleto)
	{
		if (esqueleto != null)
		{
			if (nuevoPersonaje != null)
			{
				nuevoPersonaje.setEsqueleto(esqueleto);
				return true;
			}
		}
		else
		{
			sendToastMessage(R.string.error_design);
		}
		
		return false;
	}
	
	public boolean actualizarNuevoPersonaje(Textura textura)
	{
		if (textura != null)
		{
			if (nuevoPersonaje != null)
			{
				nuevoPersonaje.setTextura(textura);
				return true;
			}
		}
		else
		{
			sendToastMessage(R.string.error_paint);
		}
		
		return false;
	}
		
	public boolean actualizarNuevoPersonaje(Movimientos movimientos)
	{
		if (movimientos != null)
		{
			if (nuevoPersonaje != null)
			{
				nuevoPersonaje.setMovimientos(movimientos);
				return true;
			}
		}
		else
		{
			sendToastMessage(R.string.error_animation);
		}
		
		return false;		
	}
	
	public boolean actualizarNuevoPersonaje(String nombre)
	{
		if (nuevoPersonaje != null)
		{
			nuevoPersonaje.setNombre(nombre);
			
			if (internalManager.guardarPersonaje(nuevoPersonaje))
			{
				TTipoMovimiento[] movimientos = TTipoMovimiento.values();
				for (int i = 0; i < movimientos.length; i++)
				{
					internalManager.guardarAudio(nombre, movimientos[i]);
				}
				
				listaPersonajes.add(nuevoPersonaje);
				nuevoPersonaje = null;

				sendToastMessage(R.string.text_save_character_confirmation);
				return true;
			}
		}
		
		return false;
	}
	
	public boolean descartarNuevoPersonaje()
	{
		if (nuevoPersonaje != null)
		{
			nuevoPersonaje = null;
			return true;
		}
		
		return false;
	}

	/* Métodos de modificación de la Lista de Personajes */
	
	public void importarPersonaje()
	{
		String[] listaFicheros = externalManager.listaFicheros();
		if (listaFicheros != null)
		{
			ChooseAlert alert = new ChooseAlert(mContext, R.string.text_import_character_title, R.string.text_button_import, R.string.text_button_cancel, listaFicheros) {
				@Override
				public void onSelectedPossitiveButtonClick(String selected)
				{
					Personaje personaje = externalManager.importarPersonaje(selected);
					if (personaje != null)
					{
						if (internalManager.guardarPersonaje(personaje))
						{
							listaPersonajes.add(personaje);
							sendToastMessage(R.string.text_import_character_confirmation);
						}
						else
						{
							sendToastMessage(R.string.error_storage_used_name);
						}
					}
					else
					{
						sendToastMessage(R.string.error_import_character);
					}
				}
	
				@Override
				public void onNoSelectedPossitiveButtonClick() { }
	
				@Override
				public void onNegativeButtonClick() { }			
			};
			
			alert.show();
		}
	}
	
	public boolean repintarPersonaje(int indice, Textura textura)
	{
		if (textura != null)
		{
			if (indice >= 0 && indice < listaPersonajes.size())
			{
				Personaje personaje = listaPersonajes.get(indice);
				personaje.setTextura(textura);
				internalManager.actualizarPersonaje(personaje);
				return true;
			}
		}
		else
		{
			sendToastMessage(R.string.error_paint);
		}		
		
		return false;
	}
	
	public boolean seleccionarPersonaje(int indice)
	{
		if (indice >= 0 && indice < listaPersonajes.size())
		{
			GamePreferences.setCharacterParameters(indice);
			if (internalManager.guardarPreferencias())
			{
				sendToastMessage(R.string.text_select_character_confirmation);
				return true;
			}
		}
		
		return false;
	}
	
	public boolean eliminarPersonaje(int indice)
	{
		if (indice >= 0 && indice < listaPersonajes.size())
		{
			if (internalManager.eliminarPersonaje(listaPersonajes.get(indice)))
			{
				listaPersonajes.remove(indice);
	
				if (GamePreferences.GET_CHARACTER_GAME() == indice)
				{
					GamePreferences.setCharacterParameters(-1);
					internalManager.guardarPreferencias();
				}
	
				sendToastMessage(R.string.text_delete_character_confirmation);
				return true;
			}
		}

		sendToastMessage(R.string.error_delete_character);
		return false;
	}
	
	public boolean renombrarPersonaje(int indice, String nombre)
	{
		if (indice >= 0 && indice < listaPersonajes.size())
		{
			if (internalManager.renombrarPersonaje(listaPersonajes.get(indice), nombre))
			{
				sendToastMessage(R.string.text_rename_character_confirmation);
				return true;
			}
		}
		
		return false;
	}
	
	public void exportarPersonaje(final int indice)
	{		
		final ProgressDialog alert = ProgressDialog.show(mContext, mContext.getString(R.string.text_export_character_title), mContext.getString(R.string.text_export_character_description), true);

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run()
			{
				if (indice >= 0 && indice < listaPersonajes.size())
				{
					if (externalManager.exportarPersonaje(listaPersonajes.get(indice)))
					{
						//sendToastMessage(R.string.text_export_character_confirmation);
					}
				}
				
				alert.dismiss();
			}
		});
		
		thread.start();
	}

	/* Métodos de modificación de la Estadisticas del Juego */

	public boolean actualizarEstadisticas(TTipoLevel nivel, int score, int imagen, String nombre, boolean perfecto)
	{
		int posNivel = nivel.ordinal();
		
		// Sonido Victoria
		audioPlayerManager.startPlaying(R.raw.effect_level_complete, false);
		
		// Aumentar número de Victorias
		estadisticasNiveles[posNivel].increaseVictories();	
		
		// Actualizar logos
		estadisticasNiveles[posNivel].setCompleted();
		
		if(perfecto)
		{
			estadisticasNiveles[posNivel].setPerfected();
		}
		
		// Desbloquear Siguiente nivel
		int nextLevel = (posNivel + 1) % estadisticasNiveles.length;
		estadisticasNiveles[nextLevel].setUnlocked();
		
		// Actualizar Puntuacion máxima
		estadisticasNiveles[posNivel].setMaxScore(score);
		
		// Publiacación de Nivel Completo
		if (externalManager.guardarImagenTemp(imagen))
		{
			String text = mContext.getString(R.string.text_social_level_completed_initial) + " " + nombre + " " + mContext.getString(R.string.text_social_level_completed_middle) + " " + score + " " + mContext.getString(R.string.text_social_level_completed_final);
			File foto = externalManager.cargarImagenTemp();
			
			socialConnector.publicar(text, foto);
			externalManager.eliminarImagenTemp();
		}
		
		return internalManager.guardarEstadisticas(estadisticasNiveles);
	}

	public boolean actualizarEstadisticas(TTipoLevel nivel)
	{
		// Sonido Derrota
		audioPlayerManager.startPlaying(R.raw.effect_game_over, false);
		
		// Aumentar número de Derrotas
		estadisticasNiveles[nivel.ordinal()].increaseNumDeaths();
		
		return internalManager.guardarEstadisticas(estadisticasNiveles);
	}
	
	public boolean actualizarPreferencias()
	{
		return internalManager.guardarPreferencias();
	}
	
	/* Métodos de modificación del SocialConnector */
	
	public boolean modificarConexionTwitter()
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
	
	public boolean modificarConexionFacebook()
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
	
	public boolean isTwitterConectado()
	{
		return socialConnector.isTwitterConnected();
	}
	
	public boolean isFacebookConectado()
	{
		return socialConnector.isFacebookConnected();
	}

	/* Métodos de modificación del AudioManager */
	
	public boolean reproducirSonidoTemp(TTipoMovimiento tipo)
	{
		if (internalManager.comprobarAudioTemp(tipo))
		{
			return soundPlayerManager.startPlaying(internalManager.cargarAudioTemp(tipo));
		}
		
		return false;
	}
	
	public boolean reproducirSonido(TTipoMovimiento tipo)
	{
		if (internalManager.comprobarAudio(getPersonajeSeleccionado().getNombre(), tipo))
		{
			return soundPlayerManager.startPlaying(internalManager.cargarAudio(getPersonajeSeleccionado().getNombre(), tipo));
		}
		
		return false;
	}
	
	public boolean reproducirSonido(TTipoMovimiento tipo, int indice)
	{
		if (indice >= 0 && indice < listaPersonajes.size())
		{
			if (internalManager.comprobarAudio(listaPersonajes.get(indice).getNombre(), tipo))
			{
				return soundPlayerManager.startPlaying(internalManager.cargarAudio(listaPersonajes.get(indice).getNombre(), tipo));
			}
		}
		return false;
	}
	
	public boolean reproducirMusica(boolean loop)
	{
		return audioPlayerManager.startPlaying(musicaSeleccionada, loop);
	}
	
	public boolean reproducirMusica(int musica, boolean loop)
	{
		return audioPlayerManager.startPlaying(musica, loop);
	}
	
	public boolean pararMusica()
	{
		return audioPlayerManager.stopPlaying();
	}
	
	public boolean pausarMusica()
	{
		return audioPlayerManager.pausePlaying();
	}
	
	public boolean continuarMusica()
	{
		return audioPlayerManager.resumePlaying();
	}
	
	/* Métodos de modificación del SocialConnector */

	public boolean publicarPost(String text, Bitmap bitmap)
	{
		if (externalManager.guardarImagenTemp(bitmap))
		{
			socialConnector.publicar(text, externalManager.cargarImagenTemp());
			externalManager.eliminarImagenTemp();
			return true;
		}
		
		sendToastMessage(R.string.error_picture_character);
		return false;
	}
	
	/* Métodos de modificación del AudioRecorder */

	public void startRecording(TTipoMovimiento movimiento)
	{
		audioRecorderManager.startRecording(internalManager.cargarAudioTemp(movimiento));
	}

	public void stopRecording()
	{
		audioRecorderManager.stopRecording();
	}

	public void discardRecording(TTipoMovimiento movimiento)
	{
		if (audioRecorderManager.stopRecording())
		{
			internalManager.eliminarAudioTemp(movimiento);
		}
	}	
}

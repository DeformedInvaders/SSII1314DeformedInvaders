package com.main.model;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.android.audio.AudioPlayerManager;
import com.android.audio.AudioVolumeManager;
import com.android.social.SocialConnector;
import com.android.storage.AssetsStorageManager;
import com.android.storage.ExternalStorageManager;
import com.android.storage.InternalStorageManager;
import com.creation.data.Movements;
import com.creation.data.Skeleton;
import com.creation.data.Texture;
import com.game.data.Character;
import com.game.data.InstanceLevel;
import com.game.data.Level;
import com.game.game.TTypeEndgame;
import com.game.select.LevelGenerator;
import com.game.select.TTypeLevel;
import com.project.main.R;
import com.video.data.Video;
import com.video.video.VideoGenerator;

public abstract class GameCore
{
	/* Contexto */
	private Context mContext;
	
	/* Estructura de Datos */
	private List<Character> listaPersonajes;
	private Character nuevoPersonaje;
	
	/* Video */
	private VideoGenerator videoGenerator;
	
	/* Niveles */
	private LevelGenerator levelGenerator;
	private GameStatistics[] estadisticasNiveles;
	
	/* Musica */
	private AudioPlayerManager audioPlayerManager, soundPlayerManager, voicePlayerManager;
	private AudioVolumeManager audioVolumeManager;
	private int musicaSeleccionada;

	/* Almacenamiento */
	private InternalStorageManager internalManager;
	private ExternalStorageManager externalManager;
	private AssetsStorageManager assetsManager;

	/* Conector Social */
	private SocialConnector socialConnector;
	
	/* Constructora */
	
	public GameCore(Context context, int widthScreen, int heightScreen, boolean sensor)
	{
		GamePreferences.SET_SCREEN_PARAMETERS(widthScreen, heightScreen);
		GamePreferences.SET_ACCELEROMETER_PARAMETERS(sensor);
        GamePreferences.SET_MUSIC_PARAMETERS(false);
        GamePreferences.SET_TIP_PARAMETERS(false);
		
		mContext = context;
		
		internalManager = new InternalStorageManager(mContext);
		externalManager = new ExternalStorageManager(mContext);
		assetsManager = new AssetsStorageManager(mContext);
		
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
		
		voicePlayerManager = new AudioPlayerManager(mContext) {
			@Override
			public void onPlayerCompletion() { }
		};
		
		audioVolumeManager = new AudioVolumeManager(mContext);
		
		videoGenerator = new VideoGenerator(mContext, assetsManager);
		
		nuevoPersonaje = null;
		levelGenerator = new LevelGenerator(mContext, assetsManager);
	}
	
	public boolean cargarDatos()
	{
		videoGenerator.cargarVideo();
		
		levelGenerator.cargarEnemigos();
		internalManager.loadPreferences();
		
		estadisticasNiveles = internalManager.loadStatistics();			
		listaPersonajes = internalManager.loadCharacterList();
		
		return true;
	}
	
	private void sendToastMessage(int message)
	{
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}
	
	/* Métodos abstractos */
	
	public abstract void onSocialConectionStatusChanged();
	
	/* Métodos de obtención de datos */
	
	public List<Character> getListaPersonajes()
	{
		return listaPersonajes;
	}
	
	public GameStatistics[] getEstadisticasNiveles()
	{
		return estadisticasNiveles;
	}

	public List<Level> getListaNiveles()
	{
		return levelGenerator.getListaNiveles();
	}
	
	public String[] getListaFicheros()
	{
		return externalManager.getFileList(GameResources.CHARACTER_EXTENSION);
	}
	
	public int getNumeroFicheros()
	{
		String[] ficheros = getListaFicheros();
		if (ficheros == null)
		{
			return 0;
		}
		return ficheros.length;
	}

	public InstanceLevel getNivel(TTypeLevel nivel)
	{
		musicaSeleccionada = levelGenerator.getLevel(nivel).getLevelMusic();
		
		return levelGenerator.getInstanciaLevel(nivel);
	}
	
	public Video getVideo()
	{
		return videoGenerator.getVideo();
	}
	
	public boolean isNivelPerfecto(TTypeLevel nivel)
	{
		return estadisticasNiveles[nivel.ordinal()].isPerfected();
	}
	
	public Character getNuevoPersonaje()
	{
		return nuevoPersonaje;
	}
	
	public Character getPersonaje(int indice)
	{
		if (indice >= 0 && indice < listaPersonajes.size())
		{
			return listaPersonajes.get(indice);
		}
		
		return null;
	}
	
	public Character getPersonajeSeleccionado()
	{
		if (GamePreferences.GET_CHARACTER_GAME() != -1)
		{
			return getPersonaje(GamePreferences.GET_CHARACTER_GAME());
		}
		
		return null;
	}
	
	public int getNumeroPersonajes()
	{
		return listaPersonajes.size();
	}
	
	/* Métodos de modificación del Personaje Actual */
	
	public boolean crearNuevoPersonaje()
	{
		nuevoPersonaje = new Character();	
		return true;
	}
	
	public boolean actualizarNuevoPersonaje(Skeleton esqueleto)
	{
		if (esqueleto != null)
		{
			nuevoPersonaje.setSkeleton(esqueleto);
			return true;
		}
		else
		{
			sendToastMessage(R.string.error_design);
		}
		
		return false;
	}
	
	public boolean actualizarNuevoPersonaje(Texture textura)
	{
		if (textura != null)
		{
			if (nuevoPersonaje != null)
			{
				nuevoPersonaje.setTexture(textura);
				return true;
			}
		}
		else
		{
			sendToastMessage(R.string.error_paint);
		}
		
		return false;
	}
		
	public boolean actualizarNuevoPersonaje(Movements movimientos)
	{
		if (movimientos != null)
		{
			if (nuevoPersonaje != null)
			{
				nuevoPersonaje.setMovements(movimientos);
				return true;
			}
		}
		else
		{
			sendToastMessage(R.string.error_animation);
		}
		
		return false;		
	}
	
	public boolean actualizarNuevoPersonaje(String name)
	{
		if (nuevoPersonaje != null)
		{
			nuevoPersonaje.setName(name);
			
			if (internalManager.saveCharacter(nuevoPersonaje))
			{				
				listaPersonajes.add(nuevoPersonaje);
				nuevoPersonaje = null;

				sendToastMessage(R.string.text_save_character_confirmation);
				return true;
			}
			else
			{
				sendToastMessage(R.string.error_save_character);
			}
		}
		
		return false;
	}
	
	/*public boolean descartarNuevoPersonaje()
	{
		if (nuevoPersonaje != null)
		{
			nuevoPersonaje = new Personaje();			
			return true;
		}
		
		return false;
	}*/

	/* Métodos de modificación de la Lista de Personajes */
	
	public boolean importarPersonaje(String nombre)
	{
		Character personaje = externalManager.importCharacter(nombre);
		if (personaje != null)
		{
			if (internalManager.saveCharacter(personaje))
			{
				listaPersonajes.add(personaje);
				sendToastMessage(R.string.text_import_character_confirmation);
				return true;
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
		
		return false;
	}
	
	public boolean repintarPersonaje(int indice, Texture textura)
	{
		if (textura != null)
		{
			if (indice >= 0 && indice < listaPersonajes.size())
			{
				Character personaje = listaPersonajes.get(indice);
				personaje.setTexture(textura);
				internalManager.updateCharacter(personaje);
				return true;
			}
		}
		else
		{
			sendToastMessage(R.string.error_paint);
		}		
		
		return false;
	}
	
	public boolean redeformarPersonaje(int indice, Movements movimientos)
	{
		if (movimientos != null)
		{
			if (indice >= 0 && indice < listaPersonajes.size())
			{
				Character personaje = listaPersonajes.get(indice);
				personaje.setMovements(movimientos);
				internalManager.updateCharacter(personaje);
				return true;
			}
		}
		else
		{
			sendToastMessage(R.string.error_deform);
		}		
		
		return false;
	}
	
	public boolean seleccionarPersonaje(int indice)
	{
		if (indice >= 0 && indice < listaPersonajes.size())
		{
			GamePreferences.SET_CHARACTER_PARAMETERS(indice);
			if (internalManager.savePreferences())
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
			if (internalManager.deleteCharacter(listaPersonajes.get(indice)))
			{
				listaPersonajes.remove(indice);
				
				int seleccionado = GamePreferences.GET_CHARACTER_GAME();
	
				if (seleccionado != -1)
				{
					if (indice < seleccionado)
					{
						GamePreferences.SET_CHARACTER_PARAMETERS(seleccionado - 1);
						internalManager.savePreferences();
					}
					else if (indice == seleccionado)
					{
						GamePreferences.SET_CHARACTER_PARAMETERS(-1);
						internalManager.savePreferences();
					}
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
			if (internalManager.renameCharacter(listaPersonajes.get(indice), nombre))
			{
				sendToastMessage(R.string.text_rename_character_confirmation);
				return true;
			}
		}
		
		return false;
	}
	
	public boolean exportarPersonaje(final int indice)
	{	
		if (indice >= 0 && indice < listaPersonajes.size())
		{
			if (GamePreferences.IS_DEBUG_ENABLED())
			{
				return externalManager.exportEnemy(listaPersonajes.get(indice));
			}
			else
			{
				return externalManager.exportCharacter(listaPersonajes.get(indice));
			}
		}
		
		return false;
	}

	/* Métodos de modificación de la Estadisticas del Juego */

	public boolean actualizarEstadisticas(InstanceLevel nivel, int score, TTypeEndgame endgame)
	{
		int posNivel = nivel.getLevelType().ordinal();
		
		// Sonido Victoria
		audioPlayerManager.startPlaying(R.raw.effect_game_completed, false, false);
		
		// Aumentar número de Victorias
		estadisticasNiveles[posNivel].increaseVictories();	
		
		// Actualizar logos		
		if (endgame == TTypeEndgame.LevelMastered)
		{
			estadisticasNiveles[posNivel].setCompleted();
			estadisticasNiveles[posNivel].setPerfected();
			estadisticasNiveles[posNivel].setMastered();
		}
		else if (endgame == TTypeEndgame.LevelPerfected)
		{
			estadisticasNiveles[posNivel].setCompleted();
			estadisticasNiveles[posNivel].setPerfected();
		}
		else if (endgame == TTypeEndgame.LevelCompleted)
		{
			estadisticasNiveles[posNivel].setCompleted();
		}
		
		// Desbloquear Siguiente nivel
		int nextLevel = (posNivel + 1) % estadisticasNiveles.length;
		estadisticasNiveles[nextLevel].setUnlocked();
		
		// Actualizar Puntuacion máxima
		estadisticasNiveles[posNivel].setMaxScore(score);
		
		// Publiacación de Nivel Completo
		if (externalManager.saveTempImage(nivel.getBackground().getIdPolaroid(endgame)))
		{
			String text = mContext.getString(R.string.text_social_level_completed_initial) + " " + nivel.getLevelName() + " " + mContext.getString(R.string.text_social_level_completed_middle) + " " + score + " " + mContext.getString(R.string.text_social_level_completed_final);
			File foto = externalManager.loadTempImage();
			
			socialConnector.sendPost(text, foto);
			externalManager.deleteTempImage();
		}
		
		return internalManager.saveStatistics(estadisticasNiveles);
	}

	public boolean actualizarEstadisticas(InstanceLevel nivel, TTypeEndgame endgame)
	{
		// Sonido Derrota
		audioPlayerManager.startPlaying(R.raw.effect_game_over, false, false);
		
		// Aumentar número de Derrotas
		estadisticasNiveles[nivel.getLevelType().ordinal()].increaseNumDeaths();
		
		return internalManager.saveStatistics(estadisticasNiveles);
	}
	
	public boolean actualizarPreferencias()
	{
		return internalManager.savePreferences();
	}
	
	/* Métodos de modificación del SocialConnector */
	
	public boolean modificarConexionTwitter()
	{
		if (socialConnector.isTwitterConnected())
		{
			socialConnector.disconnectTwitter();
		}
		else
		{
			socialConnector.connectTwitter();
		}	
		
		return true;
	}
	
	public boolean modificarConexionFacebook()
	{
		if (socialConnector.isFacebookConnected())
		{
			socialConnector.disconnectTwitter();
		}
		else
		{
			socialConnector.connectTwitter();
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
	
	public void actualizarVolumen()
	{
		if (GamePreferences.IS_MUSIC_ENABLED())
		{
			audioVolumeManager.unmuteVolume();
		}
		else
		{
			audioVolumeManager.muteVolume();
		}
	}
	
	public void reproducirSonido(final int sonido, final boolean block)
	{
		 Thread thread = new Thread(new Runnable() {
			 @Override
			 public void run()
			 {
			 	soundPlayerManager.startPlaying(sonido, false, block);
			 }
	    });
		 
		thread.start();
	}
	
	public void reproducirVoz(final int voz, final boolean block)
	{
		Thread thread = new Thread(new Runnable() {
			 @Override
			 public void run()
			 {
			 	voicePlayerManager.startPlaying(voz, false, false);
			 }
	    });
		 
		thread.start();
	}
	
	public void reproducirMusica(final boolean loop)
	{
		Thread thread = new Thread(new Runnable() {
			 @Override
			 public void run()
			 {
				 audioPlayerManager.startPlaying(musicaSeleccionada, loop, false);
			 }
	    });
		 
		thread.start();
	}
	
	public void reproducirMusica(final int musica, final boolean loop)
	{
		Thread thread = new Thread(new Runnable() {
			 @Override
			 public void run()
			 {
				 if (musicaSeleccionada != musica)
				 {
					 musicaSeleccionada = musica;
					 audioPlayerManager.startPlaying(musicaSeleccionada, loop, false);
				 }
			 }
	    });
		 
		thread.start();
	}
	
	public boolean pausarMusica()
	{
		voicePlayerManager.pausePlaying();
		soundPlayerManager.pausePlaying();
		return audioPlayerManager.pausePlaying();
	}
	
	public boolean continuarMusica()
	{
		voicePlayerManager.resumePlaying();
		soundPlayerManager.resumePlaying();
		return audioPlayerManager.resumePlaying();
	}
	
	/* Métodos de modificación del SocialConnector */

	public boolean publicarPost(String text, Bitmap bitmap)
	{
		if (externalManager.saveTempImage(bitmap))
		{
			socialConnector.sendPost(text, externalManager.loadTempImage());
			externalManager.deleteTempImage();
			return true;
		}
		
		sendToastMessage(R.string.error_picture_character);
		return false;
	}
}

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
	private List<Character> characterList;
	private Character newCharacter;
	
	/* Video */
	private VideoGenerator videoGenerator;
	
	/* Niveles */
	private LevelGenerator levelGenerator;
	private GameStatistics[] statistics;
	
	/* Musica */
	private AudioPlayerManager audioPlayerManager, soundPlayerManager, voicePlayerManager;
	private AudioVolumeManager audioVolumeManager;
	private int musicSelected;

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
		
		newCharacter = null;
		levelGenerator = new LevelGenerator(mContext, assetsManager);
	}
	
	public boolean loadingData()
	{
		videoGenerator.cargarVideo();
		
		levelGenerator.loadLevels();
		internalManager.loadPreferences();
		
		statistics = internalManager.loadStatistics();			
		characterList = internalManager.loadCharacterList();
		
		return true;
	}
	
	private void sendToastMessage(int message)
	{
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}
	
	/* Métodos abstractos */
	
	public abstract void onSocialConectionStatusChanged();
	
	/* Métodos de obtención de datos */
	
	public List<Character> getCharacterList()
	{
		return characterList;
	}
	
	public GameStatistics[] getStatistics()
	{
		return statistics;
	}

	public List<Level> getLevelList()
	{
		return levelGenerator.getLevelList();
	}
	
	public String[] getFileList()
	{
		return externalManager.getFileList(GameResources.CHARACTER_EXTENSION);
	}
	
	public int getNumFiles()
	{
		String[] ficheros = getFileList();
		if (ficheros == null)
		{
			return 0;
		}
		return ficheros.length;
	}

	public InstanceLevel getLevel(TTypeLevel nivel)
	{
		musicSelected = levelGenerator.getLevel(nivel).getLevelMusic();
		
		return levelGenerator.getLevelInstance(nivel);
	}
	
	public Video getVideo()
	{
		return videoGenerator.getVideo();
	}
	
	public boolean isLevelPerfected(TTypeLevel nivel)
	{
		return statistics[nivel.ordinal()].isPerfected();
	}
	
	public Character getNewCharacter()
	{
		return newCharacter;
	}
	
	public Character getCharacter(int indice)
	{
		if (indice >= 0 && indice < characterList.size())
		{
			return characterList.get(indice);
		}
		
		return null;
	}
	
	public Character getCharacterSelected()
	{
		if (GamePreferences.GET_CHARACTER_GAME() != -1)
		{
			return getCharacter(GamePreferences.GET_CHARACTER_GAME());
		}
		
		return null;
	}
	
	public int getNumCharacters()
	{
		return characterList.size();
	}
	
	/* Métodos de modificación del Personaje Actual */
	
	public boolean createNewCharacter()
	{
		newCharacter = new Character();	
		return true;
	}
	
	public boolean updateNewCharacter(Skeleton esqueleto)
	{
		if (esqueleto != null)
		{
			newCharacter.setSkeleton(esqueleto);
			return true;
		}
		else
		{
			sendToastMessage(R.string.error_design);
		}
		
		return false;
	}
	
	public boolean updateNewCharacter(Texture textura)
	{
		if (textura != null)
		{
			if (newCharacter != null)
			{
				newCharacter.setTexture(textura);
				return true;
			}
		}
		else
		{
			sendToastMessage(R.string.error_paint);
		}
		
		return false;
	}
		
	public boolean updateNewCharacter(Movements movimientos)
	{
		if (movimientos != null)
		{
			if (newCharacter != null)
			{
				newCharacter.setMovements(movimientos);
				return true;
			}
		}
		else
		{
			sendToastMessage(R.string.error_animation);
		}
		
		return false;		
	}
	
	public boolean updateNewCharacter(String name)
	{
		if (newCharacter != null)
		{
			newCharacter.setName(name);
			
			if (internalManager.saveCharacter(newCharacter))
			{				
				characterList.add(newCharacter);
				newCharacter = null;

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
	
	public boolean importCharacter(String nombre)
	{
		Character personaje = externalManager.importCharacter(nombre);
		if (personaje != null)
		{
			if (internalManager.saveCharacter(personaje))
			{
				characterList.add(personaje);
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
	
	public boolean repaintCharacter(int indice, Texture textura)
	{
		if (textura != null)
		{
			if (indice >= 0 && indice < characterList.size())
			{
				Character personaje = characterList.get(indice);
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
	
	public boolean redeformCharacter(int indice, Movements movimientos)
	{
		if (movimientos != null)
		{
			if (indice >= 0 && indice < characterList.size())
			{
				Character personaje = characterList.get(indice);
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
	
	public boolean selectCharacter(int indice)
	{
		if (indice >= 0 && indice < characterList.size())
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
	
	public boolean deleteCharacter(int indice)
	{
		if (indice >= 0 && indice < characterList.size())
		{
			if (internalManager.deleteCharacter(characterList.get(indice)))
			{
				characterList.remove(indice);
				
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
	
	public boolean renameCharacter(int indice, String nombre)
	{
		if (indice >= 0 && indice < characterList.size())
		{
			if (internalManager.renameCharacter(characterList.get(indice), nombre))
			{
				sendToastMessage(R.string.text_rename_character_confirmation);
				return true;
			}
		}
		
		return false;
	}
	
	public boolean exportCharacter(final int indice)
	{	
		if (indice >= 0 && indice < characterList.size())
		{
			if (GamePreferences.IS_DEBUG_ENABLED())
			{
				return externalManager.exportEnemy(characterList.get(indice));
			}
			else
			{
				return externalManager.exportCharacter(characterList.get(indice));
			}
		}
		
		return false;
	}

	/* Métodos de modificación de la Estadisticas del Juego */

	public boolean updateStatistics(InstanceLevel nivel, int score, TTypeEndgame endgame)
	{
		int posNivel = nivel.getLevelType().ordinal();
		
		// Sonido Victoria
		audioPlayerManager.startPlaying(R.raw.effect_game_completed, false, false);
		
		// Aumentar número de Victorias
		statistics[posNivel].increaseVictories();	
		
		// Actualizar logos		
		if (endgame == TTypeEndgame.LevelMastered)
		{
			statistics[posNivel].setCompleted();
			statistics[posNivel].setPerfected();
			statistics[posNivel].setMastered();
		}
		else if (endgame == TTypeEndgame.LevelPerfected)
		{
			statistics[posNivel].setCompleted();
			statistics[posNivel].setPerfected();
		}
		else if (endgame == TTypeEndgame.LevelCompleted)
		{
			statistics[posNivel].setCompleted();
		}
		
		// Desbloquear Siguiente nivel
		int nextLevel = (posNivel + 1) % statistics.length;
		statistics[nextLevel].setUnlocked();
		
		// Actualizar Puntuacion máxima
		statistics[posNivel].setMaxScore(score);
		
		// Publiacación de Nivel Completo
		if (externalManager.saveTempImage(nivel.getBackground().getIdPolaroid(endgame)))
		{
			String text = mContext.getString(R.string.text_social_level_completed_initial) + " " + nivel.getLevelName() + " " + mContext.getString(R.string.text_social_level_completed_middle) + " " + score + " " + mContext.getString(R.string.text_social_level_completed_final);
			File foto = externalManager.loadTempImage();
			
			socialConnector.sendPost(text, foto);
			externalManager.deleteTempImage();
		}
		
		return internalManager.saveStatistics(statistics);
	}

	public boolean updateStatistics(InstanceLevel nivel, TTypeEndgame endgame)
	{
		// Sonido Derrota
		audioPlayerManager.startPlaying(R.raw.effect_game_over, false, false);
		
		// Aumentar número de Derrotas
		statistics[nivel.getLevelType().ordinal()].increaseNumDeaths();
		
		return internalManager.saveStatistics(statistics);
	}
	
	public boolean updatePreferences()
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
			socialConnector.disconnectFacebook();
		}
		else
		{
			socialConnector.connectFacebook();
		}	
		
		return true;
	}
	
	public boolean isTwitterConnected()
	{
		return socialConnector.isTwitterConnected();
	}
	
	public boolean isFacebookConnected()
	{
		return socialConnector.isFacebookConnected();
	}

	/* Métodos de modificación del AudioManager */
	
	public void updateVolume()
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
	
	public void playSound(final int sonido, final boolean block)
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
	
	public void playVoice(final int voz, final boolean block)
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
	
	public void playMusic(final boolean loop)
	{
		Thread thread = new Thread(new Runnable() {
			 @Override
			 public void run()
			 {
				 audioPlayerManager.startPlaying(musicSelected, loop, false);
			 }
	    });
		 
		thread.start();
	}
	
	public void playMusic(final int musica, final boolean loop)
	{
		Thread thread = new Thread(new Runnable() {
			 @Override
			 public void run()
			 {
				 if (musicSelected != musica)
				 {
					 musicSelected = musica;
					 audioPlayerManager.startPlaying(musicSelected, loop, false);
				 }
			 }
	    });
		 
		thread.start();
	}
	
	public boolean pauseMusic()
	{
		voicePlayerManager.pausePlaying();
		soundPlayerManager.pausePlaying();
		return audioPlayerManager.pausePlaying();
	}
	
	public boolean resumeMusic()
	{
		voicePlayerManager.resumePlaying();
		soundPlayerManager.resumePlaying();
		return audioPlayerManager.resumePlaying();
	}
	
	/* Métodos de modificación del SocialConnector */

	public boolean sendPost(String text, Bitmap bitmap)
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

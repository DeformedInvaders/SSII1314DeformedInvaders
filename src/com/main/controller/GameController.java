package com.main.controller;

import java.util.List;
import java.util.Stack;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.android.alert.ChooseAlert;
import com.android.alert.ConfirmationAlert;
import com.android.alert.ImageAlert;
import com.android.alert.SummaryAlert;
import com.android.alert.TextInputAlert;
import com.character.select.CharacterSelectionDataSaved;
import com.character.select.CharacterSelectionFragment;
import com.creation.data.Skeleton;
import com.creation.data.Movements;
import com.creation.data.Texture;
import com.creation.deform.DeformationFragment;
import com.creation.design.DesignDataSaved;
import com.creation.design.DesignFragment;
import com.creation.paint.PaintDataSaved;
import com.creation.paint.PaintFragment;
import com.game.data.InstanceLevel;
import com.game.data.Level;
import com.game.data.Character;
import com.game.game.GameFragment;
import com.game.game.TTypeEndgame;
import com.game.select.LevelSelectionFragment;
import com.game.select.TTypeLevel;
import com.main.model.GameCore;
import com.main.model.GameStatistics;
import com.main.view.MainFragment;
import com.main.view.ViewActivity;
import com.project.main.R;
import com.video.data.Video;
import com.video.video.VideoFragment;

public class GameController implements ViewActivity.ActivityFragmentListener, MainFragment.MainFragmentListener, DesignFragment.DesignFragmentListener, PaintFragment.PaintFragmentListener, DeformationFragment.AnimationFragmentListener, CharacterSelectionFragment.CharacterSelectionFragmentListener, LevelSelectionFragment.LevelSelectionFragmentListener, GameFragment.GameFragmentListener, VideoFragment.VideoFragmentListener
{	
	private Context mContext;
	private GameCore mCore;
	private ViewActivity mView;
	
	private Stack<SavedState> mBackStack;
	private TStateController mState;
	
	public GameController(Context context, ViewActivity activity, GameCore gameCore)
	{
		mContext = context;
		mCore = gameCore;
		mView = activity;
		mBackStack = new Stack<SavedState>();
	}
	
	private void sendToastMessage(int message)
	{
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}

	/* Callbacks de la Vista */
	
	@Override
	public void onActivityStarted()
	{
		final ProgressDialog alert = ProgressDialog.show(mContext, mContext.getString(R.string.text_loading_character_title), mContext.getString(R.string.text_loading_character_description), true);

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run()
			{
				if (mCore.loadingData())
				{					
					mView.runOnUiThread(new Runnable() {
				        @Override
				        public void run()
				        {
				        	mView.updateActionBar();
				        	changeStateMain(mCore.getCharacterSelected(), mCore.getNumCharacters(), mCore.getNumFiles());
				        }
				    });
				}	
				
				alert.dismiss();
			}
		});
		
		thread.start();
	}
	
	// Métodos Main Fragment

	@Override
	public void onMainCreateCharacter()
	{
		if (mCore.createNewCharacter())
		{
			changeStateDesign();
		}
	}
	
	@Override
	public void onMainImportCharacter()
	{
		String[] listaFicheros = mCore.getFileList();
		if (listaFicheros != null)
		{
			ChooseAlert alert = new ChooseAlert(mContext, R.string.text_import_character_title, R.string.text_button_import, R.string.text_button_cancel, listaFicheros) {
				@Override
				public void onSelectedPossitiveButtonClick(String selected)
				{
					if (mCore.importCharacter(selected))
					{
						changeStateMain(mCore.getCharacterSelected(), mCore.getNumCharacters(), mCore.getNumFiles());
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

	@Override
	public void onMainSelectCharacter()
	{
		changeStateCharacterSelection(mCore.getCharacterList());
	}

	@Override
	public void onMainPlayGame()
	{
		changeStateLevelSelection(mCore.getLevelList(), mCore.getStatistics());
	}
	
	@Override
	public void onMainPlayVideo()
	{
		changeStateVideo(mCore.getVideo());
	}
	
	@Override
	public void onMainPlaySoundEffect(int sound)
	{
		mCore.playSound(sound, false);
	}

	// Métodos Design Fragment

	@Override
	public void onDesignReady(Skeleton esqueleto, DesignDataSaved datosSalvados)
	{
		if (mCore.updateNewCharacter(esqueleto))
		{
			mBackStack.push(new SavedState(mState, datosSalvados));
			changeStatePaint(mCore.getNewCharacter(), mCore.getStatistics());
		}
	}

	// Métodos Paint Fragment

	@Override
	public void onPaintReady(final Texture textura, final PaintDataSaved datosSalvados)
	{
		if (mCore.updateNewCharacter(textura))
		{
			mBackStack.push(new SavedState(mState, datosSalvados));
			changeStateDeformation(mCore.getNewCharacter());
		}
	}
	
	@Override
	public void onRepaintReady(final Texture textura, final int indice)
	{
    	if (mCore.repaintCharacter(indice, textura))
		{
			changeStateCharacterSelection(mCore.getCharacterList(), new CharacterSelectionDataSaved(indice));
		}
	}

	// Métodos Animation Fragment

	@Override
	public void onDeformationReady(final Movements movimientos)
	{
		if (mCore.updateNewCharacter(movimientos))
		{
			TextInputAlert alert = new TextInputAlert(mContext, R.string.text_save_character_title, R.string.text_save_character_description, R.string.text_button_yes, R.string.text_button_no, true) {
				@Override
				public void onPossitiveButtonClick(String text)
				{
					if (mCore.updateNewCharacter(text))
					{
						changeStateMain(mCore.getCharacterSelected(), mCore.getNumCharacters(), mCore.getNumFiles());
					}
				}

				@Override
				public void onNegativeButtonClick(String text)
				{
					changeStateMain(mCore.getCharacterSelected(), mCore.getNumCharacters(), mCore.getNumFiles());
				}

			};

			alert.show();
		}
	}
	
	@Override
	public void onRedeformationReady(final Movements movimientos, final int indice)
	{
    	if (mCore.redeformCharacter(indice, movimientos))
		{
			changeStateCharacterSelection(mCore.getCharacterList(), new CharacterSelectionDataSaved(indice));
		}
	}
	
	@Override
	public void onDeformationPlaySoundEffect(int sound)
	{
		mCore.playSound(sound, false);
	}

	// Métodos Character Selection Fragment

	@Override
	public void onCharacterSelectionSelectCharacter(final int indice)
	{
		if (mCore.selectCharacter(indice))
		{
			changeStateMain(mCore.getCharacterSelected(), mCore.getNumCharacters(), mCore.getNumFiles());
		}
	}

	@Override
	public void onCharacterSelectionDeleteCharacter(final int indice)
	{
		ConfirmationAlert alert = new ConfirmationAlert(mContext, R.string.text_delete_character_title, R.string.text_delete_character_description, R.string.text_button_ok, R.string.text_button_no) {
			@Override
			public void onPossitiveButtonClick()
			{
				if(mCore.deleteCharacter(indice))
				{
					if (mCore.getNumCharacters() == 0)
					{
						changeStateMain(mCore.getCharacterSelected(), mCore.getNumCharacters(), mCore.getNumFiles());
					}
					else
					{
						changeStateCharacterSelection(mCore.getCharacterList());
					}
				}
			}

			@Override
			public void onNegativeButtonClick() { }
		};

		alert.show();
	}
	
	@Override
	public void onCharacterSelectionRenameCharacter(final int indice)
	{
		Character personaje = mCore.getCharacter(indice);
		if (personaje != null)
		{
			TextInputAlert alert = new TextInputAlert(mContext, R.string.text_rename_character_title, R.string.text_rename_character_description, personaje.getName(), R.string.text_button_rename, R.string.text_button_cancel, true) {
				@Override
				public void onPossitiveButtonClick(String text)
				{
					if (mCore.renameCharacter(indice, text))
					{
						changeStateCharacterSelection(mCore.getCharacterList(), new CharacterSelectionDataSaved(indice));
					}
				}
	
				@Override
				public void onNegativeButtonClick(String text) { }
			};
	
			alert.show();
		}
	}
	
	@Override
	public void onCharacterSelectionRepaintCharacter(final int indice, final CharacterSelectionDataSaved datosSalvados)
	{
		mBackStack.push(new SavedState(mState, datosSalvados));
		changeStateRepaint(mCore.getCharacter(indice), indice, mCore.getStatistics());
	}
	
	@Override
	public void onCharacterSelectionRedeformCharacter(final int indice,final CharacterSelectionDataSaved datosSalvados)
	{
		mBackStack.push(new SavedState(mState, datosSalvados));
		changeStateRedeformation(mCore.getCharacter(indice), indice);
	}
	
	@Override
	public void onCharacterSelectionExportCharacter(final int indice)
	{
		final ProgressDialog alert = ProgressDialog.show(mContext, mContext.getString(R.string.text_export_character_title), mContext.getString(R.string.text_export_character_description), true);

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run()
			{
				if (mCore.exportCharacter(indice))
				{
					mView.runOnUiThread(new Runnable() {
				        @Override
				        public void run()
				        {
				        	sendToastMessage(R.string.text_export_character_confirmation);				        	
				        }
				    });
				}
				alert.dismiss();
			}
		});
		
		thread.start();
	}
	
	@Override
	public void onCharacterSelectionPostPublish(final String mensaje, final Bitmap bitmap)
	{
		mCore.sendPost(mensaje, bitmap);		
	}
	
	@Override
	public void onCharacterSelectionPlaySoundEffect(int sound)
	{
		mCore.playSound(sound, false);
	}

	// Métodos Level Selection Fragment

	public void onLevelSelectionSelectLevel(final TTypeLevel level)
	{	
		SummaryAlert alert = new SummaryAlert(mContext, R.string.text_summary, R.string.text_button_ready, mCore.getLevel(level)) {
			@Override
			public void onPossitiveButtonClick()
			{
				changeStateGame(mCore.getCharacterSelected(), mCore.getLevel(level));
			}
		};
		
		alert.show();	
	}

	// Métodos Game Fragment

	@Override
	public void onGameFinished(final InstanceLevel level, final int score, TTypeEndgame endgame)
	{
		if(!mCore.isLevelPerfected(level.getLevelType()))
		{
			sendToastMessage(R.string.text_game_newstikers);
		}
		
		if (mCore.updateStatistics(level, score, endgame))
		{
			// Seleccionar Siguiente Nivel
			ImageAlert alert = new ImageAlert(mContext, mContext.getString(R.string.text_game_finish) + " " + score, R.string.text_button_replay, R.string.text_button_levels, level.getBackground().getIdPolaroid(endgame)) {
				@Override
				public void onPossitiveButtonClick()
				{
					changeStateGame(mCore.getCharacterSelected(), mCore.getLevel(level.getLevelType()));
				}
	
				@Override
				public void onNegativeButtonClick()
				{
					changeStateLevelSelection(mCore.getLevelList(), mCore.getStatistics(), level.getLevelType());
				}
			};
	
			alert.show();
		}
	}

	@Override
	public void onGameFailed(final InstanceLevel level, TTypeEndgame endgame)
	{
		if(mCore.updateStatistics(level, endgame))
		{
			ImageAlert alert = new ImageAlert(mContext, R.string.text_game_fail, R.string.text_button_replay, R.string.text_button_levels, level.getBackground().getIdPolaroid(endgame)) {
				@Override
				public void onPossitiveButtonClick()
				{
					changeStateGame(mCore.getCharacterSelected(), mCore.getLevel(level.getLevelType()));
				}
	
				@Override
				public void onNegativeButtonClick()
				{
					changeStateLevelSelection(mCore.getLevelList(), mCore.getStatistics(), level.getLevelType());
				}
			};
	
			alert.show();
		}
	}
	
	@Override
	public void onGamePlaySoundEffect(int sound, boolean blockable)
	{
		mCore.playSound(sound, blockable);
	}
	
	// Métodos VideoFragment
	
	@Override
	public void onVideoFinished()
	{
		changeStateMain(mCore.getCharacterSelected(), mCore.getNumCharacters(), mCore.getNumFiles());
	}

	@Override
	public void onVideoPlayMusic(int music)
	{
		mCore.playMusic(music, true);
	}

	@Override
	public void onVideoPlaySoundEffect(int sound, boolean blockable)
	{
		mCore.playSound(sound, blockable);
	}
	
	@Override
	public void onVideoPlayVoice(int voice)
	{
		mCore.playVoice(voice, false);
	}
	
	@Override
	public void onVideoResumeMusic()
	{
		mCore.resumeMusic();
	}

	/* Métodos de Modificación de la Vista */

	private void changeStateMain(Character personaje, int numeroPersonajes, int numeroFicheros)
	{
		mState = TStateController.Main;
		mView.addMainFragment(personaje, numeroPersonajes, numeroFicheros, mState.getTitle());
		
		updateMusic();
		
		mBackStack.clear();
		mBackStack.push(new SavedState(mState));
	}

	private void changeStateDesign()
	{
		mState = TStateController.Design;
		mView.addDesignFragment(mState.getTitle());
		
		updateMusic();
	}
	
	private void changeStateDesign(DesignDataSaved datosSalvados)
	{
		mState = TStateController.Design;
		mView.addDesignFragment(datosSalvados, mState.getTitle());
	}

	private void changeStatePaint(Character nuevoPersonaje, GameStatistics[] estadisticasNiveles)
	{		
		mState = TStateController.Paint;
		mView.addPaintFragment(nuevoPersonaje, estadisticasNiveles, mState.getTitle());
	}
	
	private void changeStatePaint(Character nuevoPersonaje, GameStatistics[] estadisticasNiveles, PaintDataSaved datosSalvados)
	{		
		mState = TStateController.Paint;
		mView.addPaintFragment(nuevoPersonaje, estadisticasNiveles, datosSalvados, mState.getTitle());
	}
	
	private void changeStateRepaint(Character personaje, int indice, GameStatistics[] estadisticasNiveles)
	{		
		mState = TStateController.Repaint;
		mView.addPaintFragment(personaje, indice, estadisticasNiveles, mState.getTitle());
		
		updateMusic();
	}

	private void changeStateDeformation(Character nuevoPersonaje)
	{		
		mState = TStateController.Deformation;
		mView.addDeformationFragment(nuevoPersonaje, mState.getTitle());
	}
	
	private void changeStateRedeformation(Character personaje, int indice)
	{		
		mState = TStateController.Redeformation;
		mView.addDeformationFragment(personaje, indice, mState.getTitle());
		
		updateMusic();
	}

	private void changeStateCharacterSelection(List<Character> listaPersonajes)
	{
		mState = TStateController.CharacterSelection;
		mView.addCharacterSelectionFragment(listaPersonajes, mState.getTitle());
	}
	
	private void changeStateCharacterSelection(List<Character> listaPersonajes, CharacterSelectionDataSaved datosSalvados)
	{
		mState = TStateController.CharacterSelection;
		mView.addCharacterSelectionFragment(listaPersonajes, datosSalvados, mState.getTitle());
		
		updateMusic();
	}

	private void changeStateLevelSelection(List<Level> listaNiveles, GameStatistics[] estadisticasNiveles)
	{
		mState = TStateController.LevelSelection;
		mView.addLevelSelectionFragment(listaNiveles, estadisticasNiveles, mState.getTitle());
	}
	
	private void changeStateLevelSelection(List<Level> listaNiveles, GameStatistics[] estadisticasNiveles, TTypeLevel nivel)
	{
		mState = TStateController.LevelSelection;
		mView.addLevelSelectionFragment(listaNiveles, estadisticasNiveles, nivel, mState.getTitle());
	}

	private void changeStateGame(Character personajeSeleccionado, InstanceLevel nivel)
	{		
		mState = TStateController.Game;
		mView.addGameFragment(personajeSeleccionado, nivel, nivel.getLevelType().getDescription());
		
		updateMusic();
	}
	
	private void changeStateVideo(Video video)
	{
		mState = TStateController.Video;
		mView.addVideoFragment(video, mState.getTitle());
		mCore.pauseMusic();
		updateMusic();
	}
	
	public boolean isPopState()
	{
		return mState != TStateController.Main && mState != TStateController.Game && mState != TStateController.Video;
	}

	public void popState()
	{
		if (!mBackStack.isEmpty() && isPopState())
		{
			SavedState cima = mBackStack.pop();

			switch(cima.getStateSaved())
			{
				case Main:
					changeStateMain(mCore.getCharacterSelected(), mCore.getNumCharacters(), mCore.getNumFiles());
				break;
				case Design:
					changeStateDesign((DesignDataSaved) cima.getDataSaved());
				break;
				case Paint:
					changeStatePaint(mCore.getNewCharacter(), mCore.getStatistics(), (PaintDataSaved) cima.getDataSaved());
				break;
				case CharacterSelection:
					changeStateCharacterSelection(mCore.getCharacterList(), (CharacterSelectionDataSaved) cima.getDataSaved());
				break;
				default:
				break;
			}
		}
	}

	public void updateMusic()
	{
		mCore.updateVolume();
		
		if (mState == TStateController.Game)
		{
			mCore.playMusic(true);
		}
		else if (mState == TStateController.Design || mState == TStateController.Paint || mState == TStateController.Deformation || mState == TStateController.Repaint)
		{
			mCore.playMusic(R.raw.music_creation, true);
		}
		else if (mState != TStateController.Video)
		{
			mCore.playMusic(R.raw.music_main, true);
		}
	}
	
	public void pauseMusic()
	{
		mCore.pauseMusic();
	}
	
	public void resumeMusic()
	{
		if (mState != TStateController.Video)
		{
			mCore.resumeMusic();
		}
	}
}

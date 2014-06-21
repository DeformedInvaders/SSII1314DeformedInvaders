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
	private GameCore core;
	private ViewActivity view;
	
	private Stack<SavedState> pila;
	private TStateController estado;
	
	public GameController(Context context, ViewActivity activity, GameCore gameCore)
	{
		mContext = context;
		core = gameCore;
		view = activity;
		pila = new Stack<SavedState>();
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
				if (core.cargarDatos())
				{					
					view.runOnUiThread(new Runnable() {
				        @Override
				        public void run()
				        {
				        	view.actualizarActionBar();
				        	cambiarEstadoMain(core.getPersonajeSeleccionado(), core.getNumeroPersonajes(), core.getNumeroFicheros());
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
		if (core.crearNuevoPersonaje())
		{
			cambiarEstadoDesign();
		}
	}
	
	@Override
	public void onMainImportCharacter()
	{
		String[] listaFicheros = core.getListaFicheros();
		if (listaFicheros != null)
		{
			ChooseAlert alert = new ChooseAlert(mContext, R.string.text_import_character_title, R.string.text_button_import, R.string.text_button_cancel, listaFicheros) {
				@Override
				public void onSelectedPossitiveButtonClick(String selected)
				{
					if (core.importarPersonaje(selected))
					{
						cambiarEstadoMain(core.getPersonajeSeleccionado(), core.getNumeroPersonajes(), core.getNumeroFicheros());
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
		cambiarEstadoCharacterSelection(core.getListaPersonajes());
	}

	@Override
	public void onMainPlayGame()
	{
		cambiarEstadoLevelSelection(core.getListaNiveles(), core.getEstadisticasNiveles());
	}
	
	@Override
	public void onMainPlayVideo()
	{
		cambiarEstadoVideo(core.getVideo());
	}
	
	@Override
	public void onMainPlaySoundEffect(int sound)
	{
		core.reproducirSonido(sound, false);
	}

	// Métodos Design Fragment

	@Override
	public void onDesignReady(Skeleton esqueleto, DesignDataSaved datosSalvados)
	{
		if (core.actualizarNuevoPersonaje(esqueleto))
		{
			pila.push(new SavedState(estado, datosSalvados));
			cambiarEstadoPaint(core.getNuevoPersonaje(), core.getEstadisticasNiveles());
		}
	}

	// Métodos Paint Fragment

	@Override
	public void onPaintReady(final Texture textura, final PaintDataSaved datosSalvados)
	{
		if (core.actualizarNuevoPersonaje(textura))
		{
			pila.push(new SavedState(estado, datosSalvados));
			cambiarEstadoDeformation(core.getNuevoPersonaje());
		}
	}
	
	@Override
	public void onRepaintReady(final Texture textura, final int indice)
	{
    	if (core.repintarPersonaje(indice, textura))
		{
			cambiarEstadoCharacterSelection(core.getListaPersonajes(), new CharacterSelectionDataSaved(indice));
		}
	}

	// Métodos Animation Fragment

	@Override
	public void onDeformationReady(final Movements movimientos)
	{
		if (core.actualizarNuevoPersonaje(movimientos))
		{
			TextInputAlert alert = new TextInputAlert(mContext, R.string.text_save_character_title, R.string.text_save_character_description, R.string.text_button_yes, R.string.text_button_no, true) {
				@Override
				public void onPossitiveButtonClick(String text)
				{
					if (core.actualizarNuevoPersonaje(text))
					{
						cambiarEstadoMain(core.getPersonajeSeleccionado(), core.getNumeroPersonajes(), core.getNumeroFicheros());
					}
				}

				@Override
				public void onNegativeButtonClick(String text)
				{
					cambiarEstadoMain(core.getPersonajeSeleccionado(), core.getNumeroPersonajes(), core.getNumeroFicheros());
				}

			};

			alert.show();
		}
	}
	
	@Override
	public void onRedeformationReady(final Movements movimientos, final int indice)
	{
    	if (core.redeformarPersonaje(indice, movimientos))
		{
			cambiarEstadoCharacterSelection(core.getListaPersonajes(), new CharacterSelectionDataSaved(indice));
		}
	}
	
	@Override
	public void onDeformationPlaySoundEffect(int sound)
	{
		core.reproducirSonido(sound, false);
	}

	// Métodos Character Selection Fragment

	@Override
	public void onCharacterSelectionSelectCharacter(final int indice)
	{
		if (core.seleccionarPersonaje(indice))
		{
			cambiarEstadoMain(core.getPersonajeSeleccionado(), core.getNumeroPersonajes(), core.getNumeroFicheros());
		}
	}

	@Override
	public void onCharacterSelectionDeleteCharacter(final int indice)
	{
		ConfirmationAlert alert = new ConfirmationAlert(mContext, R.string.text_delete_character_title, R.string.text_delete_character_description, R.string.text_button_ok, R.string.text_button_no) {
			@Override
			public void onPossitiveButtonClick()
			{
				if(core.eliminarPersonaje(indice))
				{
					if (core.getNumeroPersonajes() == 0)
					{
						cambiarEstadoMain(core.getPersonajeSeleccionado(), core.getNumeroPersonajes(), core.getNumeroFicheros());
					}
					else
					{
						cambiarEstadoCharacterSelection(core.getListaPersonajes());
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
		Character personaje = core.getPersonaje(indice);
		if (personaje != null)
		{
			TextInputAlert alert = new TextInputAlert(mContext, R.string.text_rename_character_title, R.string.text_rename_character_description, personaje.getNombre(), R.string.text_button_rename, R.string.text_button_cancel, true) {
				@Override
				public void onPossitiveButtonClick(String text)
				{
					if (core.renombrarPersonaje(indice, text))
					{
						cambiarEstadoCharacterSelection(core.getListaPersonajes(), new CharacterSelectionDataSaved(indice));
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
		pila.push(new SavedState(estado, datosSalvados));
		cambiarEstadoRepaint(core.getPersonaje(indice), indice, core.getEstadisticasNiveles());
	}
	
	@Override
	public void onCharacterSelectionRedeformCharacter(final int indice,final CharacterSelectionDataSaved datosSalvados)
	{
		pila.push(new SavedState(estado, datosSalvados));
		cambiarEstadoRedeformation(core.getPersonaje(indice), indice);
	}
	
	@Override
	public void onCharacterSelectionExportCharacter(final int indice)
	{
		final ProgressDialog alert = ProgressDialog.show(mContext, mContext.getString(R.string.text_export_character_title), mContext.getString(R.string.text_export_character_description), true);

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run()
			{
				if (core.exportarPersonaje(indice))
				{
					view.runOnUiThread(new Runnable() {
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
		core.publicarPost(mensaje, bitmap);		
	}
	
	@Override
	public void onCharacterSelectionPlaySoundEffect(int sound)
	{
		core.reproducirSonido(sound, false);
	}

	// Métodos Level Selection Fragment

	public void onLevelSelectionSelectLevel(final TTypeLevel level)
	{	
		SummaryAlert alert = new SummaryAlert(mContext, R.string.text_summary, R.string.text_button_ready, core.getNivel(level)) {
			@Override
			public void onPossitiveButtonClick()
			{
				cambiarEstadoGame(core.getPersonajeSeleccionado(), core.getNivel(level));
			}
		};
		
		alert.show();	
	}

	// Métodos Game Fragment

	@Override
	public void onGameFinished(final InstanceLevel level, final int score, TTypeEndgame endgame)
	{
		if(!core.isNivelPerfecto(level.getTipoNivel()))
		{
			sendToastMessage(R.string.text_game_newstikers);
		}
		
		if (core.actualizarEstadisticas(level, score, endgame))
		{
			// Seleccionar Siguiente Nivel
			ImageAlert alert = new ImageAlert(mContext, mContext.getString(R.string.text_game_finish) + " " + score, R.string.text_button_replay, R.string.text_button_levels, level.getFondoNivel().getIdPolaroid(endgame)) {
				@Override
				public void onPossitiveButtonClick()
				{
					cambiarEstadoGame(core.getPersonajeSeleccionado(), core.getNivel(level.getTipoNivel()));
				}
	
				@Override
				public void onNegativeButtonClick()
				{
					cambiarEstadoLevelSelection(core.getListaNiveles(), core.getEstadisticasNiveles(), level.getTipoNivel());
				}
			};
	
			alert.show();
		}
	}

	@Override
	public void onGameFailed(final InstanceLevel level, TTypeEndgame endgame)
	{
		if(core.actualizarEstadisticas(level, endgame))
		{
			ImageAlert alert = new ImageAlert(mContext, R.string.text_game_fail, R.string.text_button_replay, R.string.text_button_levels, level.getFondoNivel().getIdPolaroid(endgame)) {
				@Override
				public void onPossitiveButtonClick()
				{
					cambiarEstadoGame(core.getPersonajeSeleccionado(), core.getNivel(level.getTipoNivel()));
				}
	
				@Override
				public void onNegativeButtonClick()
				{
					cambiarEstadoLevelSelection(core.getListaNiveles(), core.getEstadisticasNiveles(), level.getTipoNivel());
				}
			};
	
			alert.show();
		}
	}
	
	@Override
	public void onGamePlaySoundEffect(int sound, boolean blockable)
	{
		core.reproducirSonido(sound, blockable);
	}
	
	// Métodos VideoFragment
	
	@Override
	public void onVideoFinished()
	{
		cambiarEstadoMain(core.getPersonajeSeleccionado(), core.getNumeroPersonajes(), core.getNumeroFicheros());
	}

	@Override
	public void onVideoPlayMusic(int music)
	{
		core.reproducirMusica(music, true);
	}

	@Override
	public void onVideoPlaySoundEffect(int sound, boolean blockable)
	{
		core.reproducirSonido(sound, blockable);
	}
	
	@Override
	public void onVideoPlayVoice(int voice)
	{
		core.reproducirVoz(voice, false);
	}
	
	@Override
	public void onVideoResumeMusic()
	{
		core.continuarMusica();
	}

	/* Métodos de Modificación de la Vista */

	private void cambiarEstadoMain(Character personaje, int numeroPersonajes, int numeroFicheros)
	{
		estado = TStateController.Main;
		view.insertarMainFragmento(personaje, numeroPersonajes, numeroFicheros, estado.getTitle());
		
		actualizarMusica();
		
		pila.clear();
		pila.push(new SavedState(estado));
	}

	private void cambiarEstadoDesign()
	{
		estado = TStateController.Design;
		view.insertarDesignFragmento(estado.getTitle());
		
		actualizarMusica();
	}
	
	private void cambiarEstadoDesign(DesignDataSaved datosSalvados)
	{
		estado = TStateController.Design;
		view.insertarDesignFragmento(datosSalvados, estado.getTitle());
	}

	private void cambiarEstadoPaint(Character nuevoPersonaje, GameStatistics[] estadisticasNiveles)
	{		
		estado = TStateController.Paint;
		view.insertarPaintFragmento(nuevoPersonaje, estadisticasNiveles, estado.getTitle());
	}
	
	private void cambiarEstadoPaint(Character nuevoPersonaje, GameStatistics[] estadisticasNiveles, PaintDataSaved datosSalvados)
	{		
		estado = TStateController.Paint;
		view.insertarPaintFragmento(nuevoPersonaje, estadisticasNiveles, datosSalvados, estado.getTitle());
	}
	
	private void cambiarEstadoRepaint(Character personaje, int indice, GameStatistics[] estadisticasNiveles)
	{		
		estado = TStateController.Repaint;
		view.insertarPaintFragmento(personaje, indice, estadisticasNiveles, estado.getTitle());
		
		actualizarMusica();
	}

	private void cambiarEstadoDeformation(Character nuevoPersonaje)
	{		
		estado = TStateController.Deformation;
		view.insertarDeformationFragmento(nuevoPersonaje, estado.getTitle());
	}
	
	private void cambiarEstadoRedeformation(Character personaje, int indice)
	{		
		estado = TStateController.Redeformation;
		view.insertarDeformationFragmento(personaje, indice, estado.getTitle());
		
		actualizarMusica();
	}

	private void cambiarEstadoCharacterSelection(List<Character> listaPersonajes)
	{
		estado = TStateController.CharacterSelection;
		view.insertarCharacterSelectionFragmento(listaPersonajes, estado.getTitle());
	}
	
	private void cambiarEstadoCharacterSelection(List<Character> listaPersonajes, CharacterSelectionDataSaved datosSalvados)
	{
		estado = TStateController.CharacterSelection;
		view.insertarCharacterSelectionFragmento(listaPersonajes, datosSalvados, estado.getTitle());
		
		actualizarMusica();
	}

	private void cambiarEstadoLevelSelection(List<Level> listaNiveles, GameStatistics[] estadisticasNiveles)
	{
		estado = TStateController.LevelSelection;
		view.insertarLevelSelectionFragmento(listaNiveles, estadisticasNiveles, estado.getTitle());
	}
	
	private void cambiarEstadoLevelSelection(List<Level> listaNiveles, GameStatistics[] estadisticasNiveles, TTypeLevel nivel)
	{
		estado = TStateController.LevelSelection;
		view.insertarLevelSelectionFragmento(listaNiveles, estadisticasNiveles, nivel, estado.getTitle());
	}

	private void cambiarEstadoGame(Character personajeSeleccionado, InstanceLevel nivel)
	{		
		estado = TStateController.Game;
		view.insertarGameFragmento(personajeSeleccionado, nivel, nivel.getTipoNivel().getDescription());
		
		actualizarMusica();
	}
	
	private void cambiarEstadoVideo(Video video)
	{
		estado = TStateController.Video;
		view.insertarVideoFragmento(video, estado.getTitle());
		core.pausarMusica();
		actualizarMusica();
	}
	
	public boolean isEstadoDesapilador()
	{
		return estado != TStateController.Main && estado != TStateController.Game && estado != TStateController.Video;
	}

	public void desapilarEstado()
	{
		if (!pila.isEmpty() && isEstadoDesapilador())
		{
			SavedState cima = pila.pop();

			switch(cima.getEstadoSalvado())
			{
				case Main:
					cambiarEstadoMain(core.getPersonajeSeleccionado(), core.getNumeroPersonajes(), core.getNumeroFicheros());
				break;
				case Design:
					cambiarEstadoDesign((DesignDataSaved) cima.getDatosSalvados());
				break;
				case Paint:
					cambiarEstadoPaint(core.getNuevoPersonaje(), core.getEstadisticasNiveles(), (PaintDataSaved) cima.getDatosSalvados());
				break;
				case CharacterSelection:
					cambiarEstadoCharacterSelection(core.getListaPersonajes(), (CharacterSelectionDataSaved) cima.getDatosSalvados());
				break;
				default:
				break;
			}
		}
	}

	public void actualizarMusica()
	{
		core.actualizarVolumen();
		
		if (estado == TStateController.Game)
		{
			core.reproducirMusica(true);
		}
		else if (estado == TStateController.Design || estado == TStateController.Paint || estado == TStateController.Deformation || estado == TStateController.Repaint)
		{
			core.reproducirMusica(R.raw.music_creation, true);
		}
		else if (estado != TStateController.Video)
		{
			core.reproducirMusica(R.raw.music_main, true);
		}
	}
	
	public void pausarMusica()
	{
		core.pausarMusica();
	}
	
	public void continuarMusica()
	{
		if (estado != TStateController.Video)
		{
			core.continuarMusica();
		}
	}
}

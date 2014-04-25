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
import com.creation.data.Esqueleto;
import com.creation.data.Movimientos;
import com.creation.data.TTipoMovimiento;
import com.creation.data.Textura;
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
import com.main.model.GameCore;
import com.main.model.GamePreferences;
import com.main.model.GameStatistics;
import com.main.view.MainFragment;
import com.main.view.ViewActivity;
import com.project.main.R;

public class GameController implements ViewActivity.ActivityFragmentListener, MainFragment.MainFragmentListener, DesignFragment.DesignFragmentListener, PaintFragment.PaintFragmentListener, DeformationFragment.AnimationFragmentListener, CharacterSelectionFragment.CharacterSelectionFragmentListener, LevelSelectionFragment.LevelSelectionFragmentListener, GameFragment.GameFragmentListener
{	
	private Context mContext;
	private GameCore core;
	private ViewActivity view;
	
	private Stack<Estado> pila;
	private TEstadoController estado;
	
	public GameController(Context context, ViewActivity activity, GameCore gameCore)
	{
		mContext = context;
		core = gameCore;
		view = activity;
		pila = new Stack<Estado>();
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
				        	cambiarEstadoMain(core.getPersonajeSeleccionado(), core.getNumeroPersonajes());
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
						cambiarEstadoMain(core.getPersonajeSeleccionado(), core.getNumeroPersonajes());
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
	public void onMainPlaySound(final TTipoMovimiento tipo)
	{
		core.reproducirSonido(tipo);
	}

	// Métodos Design Fragment

	@Override
	public void onDesignReady(final Esqueleto esqueleto, final DesignDataSaved datosSalvados)
	{
		if (core.actualizarNuevoPersonaje(esqueleto))
		{
			pila.push(new Estado(estado, datosSalvados));
			cambiarEstadoPaint(core.getNuevoPersonaje(), core.getEstadisticasNiveles());
		}
	}

	// Métodos Paint Fragment

	@Override
	public void onPaintReady(final Textura textura, final PaintDataSaved datosSalvados)
	{
		if (core.actualizarNuevoPersonaje(textura))
		{
			pila.push(new Estado(estado, datosSalvados));
			cambiarEstadoDeformation(core.getNuevoPersonaje());
		}
	}
	
	@Override
	public void onRepaintReady(final Textura textura, final int indice)
	{
    	if (core.repintarPersonaje(indice, textura))
		{
			cambiarEstadoCharacterSelection(core.getListaPersonajes(), new CharacterSelectionDataSaved(indice));
		}
	}

	// Métodos Animation Fragment

	@Override
	public void onAnimationReady(final Movimientos movimientos)
	{
		if (core.actualizarNuevoPersonaje(movimientos))
		{
			TextInputAlert alert = new TextInputAlert(mContext, R.string.text_save_character_title, R.string.text_save_character_description, R.string.text_button_yes, R.string.text_button_no, true) {
				@Override
				public void onPossitiveButtonClick(String text)
				{
					if (core.actualizarNuevoPersonaje(text))
					{
						cambiarEstadoMain(core.getPersonajeSeleccionado(), core.getNumeroPersonajes());
					}
				}

				@Override
				public void onNegativeButtonClick(String text)
				{
					if (core.descartarNuevoPersonaje())
					{
						cambiarEstadoMain(core.getPersonajeSeleccionado(), core.getNumeroPersonajes());
					}
				}

			};

			alert.show();
		}
	}
	
	@Override
	public void onAnimationStartRecording(final TTipoMovimiento movimiento)
	{
		core.startRecording(movimiento);
	}

	@Override
	public void onAnimationStopRecording()
	{
		core.stopRecording();
	}

	@Override
	public void onAnimationDiscardRecording(final TTipoMovimiento movimiento)
	{
		core.discardRecording(movimiento);
	}
	
	@Override
	public void onAnimationPlaySound(final TTipoMovimiento movimiento)
	{
		core.reproducirSonidoTemp(movimiento);
	}

	// Métodos Character Selection Fragment

	@Override
	public void onCharacterSelectionSelectCharacter(final int indice)
	{
		if (core.seleccionarPersonaje(indice))
		{
			cambiarEstadoMain(core.getPersonajeSeleccionado(), core.getNumeroPersonajes());
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
						cambiarEstadoMain(core.getPersonajeSeleccionado(), core.getNumeroPersonajes());
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
		Personaje personaje = core.getPersonaje(indice);
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
		pila.push(new Estado(estado, datosSalvados));
		cambiarEstadoRepaint(core.getPersonaje(indice), indice, core.getEstadisticasNiveles());
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
	public void onCharacterSelectionPlaySound(final TTipoMovimiento tipo, final int indice)
	{
		core.reproducirSonido(tipo, indice);
	}

	// Métodos Level Selection Fragment

	public void onLevelSelectionSelectLevel(final TTipoLevel level)
	{	
		SummaryAlert alert = new SummaryAlert(mContext, R.string.text_summary, R.string.text_button_ready, core.getNivel(level).getTipoEnemigos()) {
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
	public void onGameFinished(final TTipoLevel level, final int score, final int idImagen, final String nameLevel, final boolean perfecto)
	{
		if(!core.isNivelPerfecto(level))
		{
			sendToastMessage(R.string.text_game_newstikers);
		}
		
		if (core.actualizarEstadisticas(level, score, idImagen, nameLevel, perfecto))
		{
			// Seleccionar Siguiente Nivel
			ImageAlert alert = new ImageAlert(mContext, mContext.getString(R.string.text_game_finish) + " " + score, R.string.text_button_replay, R.string.text_button_levels, idImagen) {
				@Override
				public void onPossitiveButtonClick()
				{
					cambiarEstadoGame(core.getPersonajeSeleccionado(), core.getNivel(level));
				}
	
				@Override
				public void onNegativeButtonClick()
				{
					cambiarEstadoLevelSelection(core.getListaNiveles(), core.getEstadisticasNiveles(), level);
				}
			};
	
			alert.show();
		}
	}

	@Override
	public void onGameFailed(final TTipoLevel level, final int idImagen)
	{
		if(core.actualizarEstadisticas(level))
		{
			ImageAlert alert = new ImageAlert(mContext, R.string.text_game_fail, R.string.text_button_replay, R.string.text_button_levels, idImagen) {
				@Override
				public void onPossitiveButtonClick()
				{
					cambiarEstadoGame(core.getPersonajeSeleccionado(), core.getNivel(level));
				}
	
				@Override
				public void onNegativeButtonClick()
				{
					cambiarEstadoLevelSelection(core.getListaNiveles(), core.getEstadisticasNiveles(), level);
				}
			};
	
			alert.show();
		}
	}
	
	@Override
	public void onGamePlaySound(TTipoMovimiento tipo)
	{
		core.reproducirSonido(tipo);
	}


	/* Métodos de Modificación de la Vista */

	private void cambiarEstadoMain(Personaje personaje, int numeroPersonajes)
	{
		estado = TEstadoController.Main;
		view.insertarMainFragmento(personaje, numeroPersonajes, estado.getTitle());
		
		actualizarMusica();
		
		pila.clear();
		pila.push(new Estado(estado));
	}

	private void cambiarEstadoDesign()
	{
		estado = TEstadoController.Design;
		view.insertarDesignFragmento(estado.getTitle());
		
		actualizarMusica();
	}
	
	private void cambiarEstadoDesign(DesignDataSaved datosSalvados)
	{
		estado = TEstadoController.Design;
		view.insertarDesignFragmento(datosSalvados, estado.getTitle());
	}

	private void cambiarEstadoPaint(Personaje nuevoPersonaje, GameStatistics[] estadisticasNiveles)
	{		
		estado = TEstadoController.Paint;
		view.insertarPaintFragmento(nuevoPersonaje, estadisticasNiveles, estado.getTitle());
	}
	
	private void cambiarEstadoPaint(Personaje nuevoPersonaje, GameStatistics[] estadisticasNiveles, PaintDataSaved datosSalvados)
	{		
		estado = TEstadoController.Paint;
		view.insertarPaintFragmento(nuevoPersonaje, estadisticasNiveles, datosSalvados, estado.getTitle());
	}
	
	private void cambiarEstadoRepaint(Personaje personaje, int indice, GameStatistics[] estadisticasNiveles)
	{		
		estado = TEstadoController.Repaint;
		view.insertarPaintFragmento(personaje, indice, estadisticasNiveles, estado.getTitle());
		
		actualizarMusica();
	}

	private void cambiarEstadoDeformation(Personaje nuevoPersonaje)
	{		
		estado = TEstadoController.Deformation;
		view.insertarDeformationFragmento(nuevoPersonaje, estado.getTitle());
	}

	private void cambiarEstadoCharacterSelection(List<Personaje> listaPersonajes)
	{
		estado = TEstadoController.CharacterSelection;
		view.insertarCharacterSelectionFragmento(listaPersonajes, estado.getTitle());
	}
	
	private void cambiarEstadoCharacterSelection(List<Personaje> listaPersonajes, CharacterSelectionDataSaved datosSalvados)
	{
		estado = TEstadoController.CharacterSelection;
		view.insertarCharacterSelectionFragmento(listaPersonajes, datosSalvados, estado.getTitle());
		
		actualizarMusica();
	}

	private void cambiarEstadoLevelSelection(List<Nivel> listaNiveles, GameStatistics[] estadisticasNiveles)
	{
		estado = TEstadoController.LevelSelection;
		view.insertarLevelSelectionFragmento(listaNiveles, estadisticasNiveles, estado.getTitle());
	}
	
	private void cambiarEstadoLevelSelection(List<Nivel> listaNiveles, GameStatistics[] estadisticasNiveles, TTipoLevel nivel)
	{
		estado = TEstadoController.LevelSelection;
		view.insertarLevelSelectionFragmento(listaNiveles, estadisticasNiveles, nivel, estado.getTitle());
	}

	private void cambiarEstadoGame(Personaje personajeSeleccionado, InstanciaNivel nivel)
	{		
		estado = TEstadoController.Game;
		view.insertarGameFragmento(personajeSeleccionado, nivel, nivel.getTipoNivel().getDescription());
		
		actualizarMusica();
	}
	
	public boolean isEstadoDesapilador()
	{
		return estado != TEstadoController.Main && estado != TEstadoController.Game;
	}

	public void desapilarEstado()
	{
		if (!pila.isEmpty() && isEstadoDesapilador())
		{
			Estado cima = pila.pop();

			switch(cima.getEstadoSalvado())
			{
				case Main:
					cambiarEstadoMain(core.getPersonajeSeleccionado(), core.getNumeroPersonajes());
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
		if (GamePreferences.IS_MUSIC_ENABLED())
		{
			if (estado == TEstadoController.Game)
			{
				core.reproducirMusica(true);
			}
			else if (estado == TEstadoController.Design || estado == TEstadoController.Paint || estado == TEstadoController.Deformation || estado == TEstadoController.Repaint)
			{
				core.reproducirMusica(R.raw.music_creation, true);
			}
			else
			{
				core.reproducirMusica(R.raw.music_main, true);
			}
		}
		else
		{
			core.pararMusica();
		}
	}
}

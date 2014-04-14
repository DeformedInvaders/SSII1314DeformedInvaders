package com.project.controller;

import java.util.List;
import java.util.Stack;

import android.content.Context;
import android.widget.Toast;

import com.android.alert.ConfirmationAlert;
import com.android.alert.ImageAlert;
import com.android.alert.SummaryAlert;
import com.android.alert.TextInputAlert;
import com.android.social.SocialConnector;
import com.android.storage.ExternalStorageManager;
import com.android.storage.InternalStorageManager;
import com.character.select.CharacterSelectionFragment;
import com.creation.data.Esqueleto;
import com.creation.data.Movimientos;
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
import com.loading.load.LoadingFragment;
import com.project.main.MainActivity;
import com.project.main.MainFragment;
import com.project.main.R;
import com.project.model.GameCore;
import com.project.model.GamePreferences;
import com.project.model.GameStatistics;

public class GameController implements MainActivity.ActivityListener, LoadingFragment.LoadingFragmentListener, MainFragment.MainFragmentListener, DesignFragment.DesignFragmentListener, PaintFragment.PaintFragmentListener, DeformationFragment.AnimationFragmentListener, CharacterSelectionFragment.CharacterSelectionFragmentListener, LevelSelectionFragment.LevelSelectionFragmentListener, GameFragment.GameFragmentListener
{	
	private Context mContext;
	private GameCore core;
	private MainActivity view;
	
	private Stack<Estado> pila;
	private TEstadoController estado;
	
	public GameController(MainActivity activity, GameCore gameCore)
	{
		mContext = activity;
		core = gameCore;
		view = activity;
		pila = new Stack<Estado>();
	}

	/* Callbacks de la Vista */
	
	@Override
	public void onActivityStarted()
	{
		cambiarEstadoLoading(core.getInternalManager());
	}
	
	// Métodos Loading Fragment
	
	@Override
	public void onLoadingDataFinished(List<Personaje> personajes, GameStatistics[] estadisticas)
	{
		core.cargarDatos(personajes, estadisticas);
		
		cambiarEstadoMain(core.getPersonajeSeleccionado(), core.getNumeroPersonajes(), core.getInternalManager());
	}

	// Métodos Main Fragment

	@Override
	public void onMainCreateCharacter()
	{
		core.crearNuevoPersonaje();
		
		cambiarEstadoDesign();
	}

	@Override
	public void onMainSelectCharacter()
	{
		cambiarEstadoCharacterSelection(core.getListaPersonajes(), core.getInternalManager(), core.getExternalManager(), core.getSocialConnector());
	}

	@Override
	public void onMainPlayGame()
	{
		cambiarEstadoLevelSelection(core.getListaNiveles(), core.getEstadisticasNiveles());
	}

	// Métodos Design Fragment

	@Override
	public void onDesignReady(final Esqueleto esqueleto, final DesignDataSaved datosSalvados)
	{
		if (core.actualizarNuevoPersonaje(esqueleto))
		{
			cambiarEstadoPaint(core.getNuevoPersonaje(), core.getEstadisticasNiveles(), datosSalvados);
		}
	}

	// Métodos Paint Fragment

	@Override
	public void onPaintReady(final Textura textura, final PaintDataSaved datosSalvados)
	{
		if (core.actualizarNuevoPersonaje(textura))
		{
			cambiarEstadoDeformation(core.getNuevoPersonaje(), core.getInternalManager(), datosSalvados);
		}
	}
	
	@Override
	public void onRepaintReady(final int indice, final Textura textura)
	{
		if (core.repintarPersonaje(indice, textura))
		{
			cambiarEstadoCharacterSelection(core.getListaPersonajes(), core.getInternalManager(), core.getExternalManager(), core.getSocialConnector());
		}
	}

	// Métodos Animation Fragment

	@Override
	public void onAnimationReady(final Movimientos movimientos)
	{
		if (core.actualizarNuevoPersonaje(movimientos))
		{
			TextInputAlert alert = new TextInputAlert(mContext, R.string.text_save_character_title, R.string.text_save_character_description, R.string.text_button_yes, R.string.text_button_no) {
				@Override
				public void onPossitiveButtonClick(String text)
				{
					if (core.actualizarNuevoPersonaje(text))
					{
						cambiarEstadoMain(core.getPersonajeSeleccionado(), core.getNumeroPersonajes(), core.getInternalManager());
					}
				}

				@Override
				public void onNegativeButtonClick(String text)
				{
					if (core.descartarNuevoPersonaje())
					{
						cambiarEstadoMain(core.getPersonajeSeleccionado(), core.getNumeroPersonajes(), core.getInternalManager());
					}
				}

			};

			alert.show();
		}
	}

	// Métodos Character Selection Fragment

	@Override
	public void onCharacterSelectionSelectCharacter(final int indice)
	{
		if (core.seleccionarPersonaje(indice))
		{
			cambiarEstadoMain(core.getPersonajeSeleccionado(), core.getNumeroPersonajes(), core.getInternalManager());
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
					cambiarEstadoMain(core.getPersonajeSeleccionado(), core.getNumeroPersonajes(), core.getInternalManager());
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
		TextInputAlert alert = new TextInputAlert(mContext, R.string.text_rename_character_title, R.string.text_rename_character_description, R.string.text_button_rename, R.string.text_button_cancel) {
			@Override
			public void onPossitiveButtonClick(String text)
			{
				if (core.renombrarPersonaje(indice, text))
				{
					cambiarEstadoCharacterSelection(core.getListaPersonajes(), core.getInternalManager(), core.getExternalManager(), core.getSocialConnector());
				}
			}

			@Override
			public void onNegativeButtonClick(String text) { }
		};

		alert.show();
	}
	
	@Override
	public void onCharacterSelectionRepaintCharacter(final int indice)
	{
		cambiarEstadoRepaint(core.getPersonaje(indice), indice, core.getEstadisticasNiveles());
	}
	
	@Override
	public void onCharacterSelectionExportCharacter(final int indice)
	{
		core.exportarPersonaje(indice);
	}

	// Métodos Level Selection Fragment

	public void onLevelSelectionSelectLevel(final TTipoLevel level)
	{	
		SummaryAlert alert = new SummaryAlert(mContext, R.string.text_summary, R.string.text_button_ready, core.getNivel(level).getTipoEnemigos()) {
			@Override
			public void onPossitiveButtonClick()
			{
				cambiarEstadoGame(core.getPersonajeSeleccionado(), core.getInternalManager(), core.getNivel(level));
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
			Toast.makeText(mContext, R.string.text_game_newstikers, Toast.LENGTH_SHORT).show();
		}
		
		if (core.actualizarEstadisticas(level, score, idImagen, nameLevel, perfecto))
		{
			// Seleccionar Siguiente Nivel
			ImageAlert alert = new ImageAlert(mContext, mContext.getString(R.string.text_game_finish) + " " + score, R.string.text_button_replay, R.string.text_button_levels, idImagen) {
				@Override
				public void onPossitiveButtonClick()
				{
					cambiarEstadoGame(core.getPersonajeSeleccionado(), core.getInternalManager(), core.getNivel(level));
				}
	
				@Override
				public void onNegativeButtonClick()
				{
					cambiarEstadoLevelSelection(core.getListaNiveles(), core.getEstadisticasNiveles());
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
					cambiarEstadoGame(core.getPersonajeSeleccionado(), core.getInternalManager(), core.getNivel(level));
				}
	
				@Override
				public void onNegativeButtonClick()
				{
					cambiarEstadoLevelSelection(core.getListaNiveles(), core.getEstadisticasNiveles());
				}
			};
	
			alert.show();
		}
	}


	/* Métodos de Modificación de la Vista */
	
	// FIXME Revisar paso de parámetros
	private void cambiarEstadoLoading(InternalStorageManager internalManager)
	{
		estado = TEstadoController.Loading;
		view.insertarLoadingFragmento(internalManager);
	}

	private void cambiarEstadoMain(Personaje personaje, int numeroPersonajes, InternalStorageManager internalManager)
	{
		estado = TEstadoController.Main;
		view.insertarMainFragmento(personaje, numeroPersonajes, internalManager);
	
		pila.clear();
		pila.push(new Estado(estado));
	}

	private void cambiarEstadoDesign()
	{
		estado = TEstadoController.Design;
		view.insertarDesignFragmento();
	}

	private void cambiarEstadoPaint(Personaje nuevoPersonaje, GameStatistics[] estadisticasNiveles, DesignDataSaved datosSalvados)
	{
		pila.push(new Estado(estado, datosSalvados));
		
		estado = TEstadoController.Paint;
		view.insertarPaintFragmento(nuevoPersonaje, estadisticasNiveles);
	}
	
	private void cambiarEstadoRepaint(Personaje personaje, int indice, GameStatistics[] estadisticasNiveles)
	{
		pila.push(new Estado(estado));
		
		estado = TEstadoController.Repaint;
		view.insertarPaintFragmento(personaje, indice, estadisticasNiveles);
	}

	private void cambiarEstadoDeformation(Personaje nuevoPersonaje, InternalStorageManager internalManager, PaintDataSaved datosSalvados)
	{
		pila.push(new Estado(estado, datosSalvados));
		
		estado = TEstadoController.Deformation;
		view.insertarDeformationFragmento(nuevoPersonaje, internalManager);
	}

	private void cambiarEstadoCharacterSelection(List<Personaje> listaPersonajes, InternalStorageManager internalManager,	ExternalStorageManager externalManager, SocialConnector socialConnector)
	{
		estado = TEstadoController.CharacterSelection;
		view.insertarCharacterSelectionFragmento(listaPersonajes, internalManager, externalManager, socialConnector);
	}

	private void cambiarEstadoLevelSelection(List<Nivel> listaNiveles, GameStatistics[] estadisticasNiveles)
	{
		estado = TEstadoController.LevelSelection;
		view.insertarLevelSelectionFragmento(listaNiveles, estadisticasNiveles);
	}

	private void cambiarEstadoGame(Personaje personajeSeleccionado, InternalStorageManager internalManager, InstanciaNivel nivel)
	{
		pila.push(new Estado(estado));
		
		estado = TEstadoController.Game;
		view.insertarGameFragmento(personajeSeleccionado, internalManager, nivel);
	}

	public void desapilarEstado()
	{
		if (!pila.isEmpty() && estado != TEstadoController.Game)
		{
			Estado cima = pila.peek();
			
			estado = cima.getEstadoSalvado();
			
			switch(estado)
			{
				case Main:
					view.insertarMainFragmento(core.getPersonajeSeleccionado(), core.getNumeroPersonajes(), core.getInternalManager());
				break;
				case Design:
					view.insertarDesignFragmento((DesignDataSaved) cima.getDatosSalvados());
					pila.pop();
				break;
				case Paint:
					view.insertarPaintFragmento(core.getNuevoPersonaje(), core.getEstadisticasNiveles(), (PaintDataSaved) cima.getDatosSalvados());
					pila.pop();
				break;
				case CharacterSelection:
					view.insertarCharacterSelectionFragmento(core.getListaPersonajes(), core.getInternalManager(), core.getExternalManager(), core.getSocialConnector());
					pila.pop();
				break;
				case LevelSelection:
					view.insertarLevelSelectionFragmento(core.getListaNiveles(), core.getEstadisticasNiveles());
					pila.pop();
				break;
				default:
				break;
			}
		}
	}
	
	public void actualizarMusica()
	{
		if(GamePreferences.IS_MUSIC_ENABLED())
		{
			if(estado == TEstadoController.Game)
			{
				core.reproducirMusica(true);
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

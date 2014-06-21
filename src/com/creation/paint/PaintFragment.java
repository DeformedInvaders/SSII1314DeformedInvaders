package com.creation.paint;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.android.dialog.ColorDialog;
import com.android.dialog.SizeDialog;
import com.android.dialog.StickerDialog;
import com.android.view.IconImageButton;
import com.android.view.OpenGLFragment;
import com.creation.data.TTypeSticker;
import com.creation.data.Texture;
import com.game.data.Character;
import com.main.model.GameResources;
import com.main.model.GameStatistics;
import com.project.main.R;

public class PaintFragment extends OpenGLFragment
{
	private PaintFragmentListener mCallback;

	private ColorDialog colorDialog;
	private SizeDialog sizeDialog;
	private StickerDialog stickerDialog;

	private PaintOpenGLSurfaceView canvas;
	private IconImageButton botonPincel, botonCubo, botonMano, botonNext, botonPrev, botonDelete, botonListo, botonColor, botonSize, botonPegatina;

	private Character personaje;
	private int personajeIndice;
	
	private GameStatistics[] estadoNiveles;
	
	private PaintDataSaved dataSaved;

	/* Constructora */

	public static final PaintFragment newInstance(PaintFragmentListener c, Character p, GameStatistics[] e)
	{
		PaintFragment fragment = new PaintFragment();
		fragment.setParameters(c, p, -1, e, null);
		return fragment;
	}
	
	public static final PaintFragment newInstance(PaintFragmentListener c, Character p, int n, GameStatistics[] e)
	{
		PaintFragment fragment = new PaintFragment();
		fragment.setParameters(c, p, n, e, null);
		return fragment;
	}
	
	public static final PaintFragment newInstance(PaintFragmentListener c, Character p, GameStatistics[] e, PaintDataSaved s)
	{
		PaintFragment fragment = new PaintFragment();
		fragment.setParameters(c, p, -1, e, s);
		return fragment;
	}
	
	private void setParameters(PaintFragmentListener c, Character p, int n, GameStatistics[] e, PaintDataSaved s)
	{
		mCallback = c;
		personaje = p;
		personajeIndice = n;
		estadoNiveles = e;
		dataSaved = s;
	}

	public interface PaintFragmentListener
	{
		public void onPaintReady(final Texture textura, final PaintDataSaved datosSalvados);
		public void onRepaintReady(final Texture textura, final int indice);
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_creation_paint_layout, container, false);

		// Instanciar Elementos de la GUI
		canvas = (PaintOpenGLSurfaceView) rootView.findViewById(R.id.paintGLSurfaceViewPaint1);
		canvas.setParameters(personaje);
		
		botonPincel = (IconImageButton) rootView.findViewById(R.id.imageButtonPaint1);
		botonCubo = (IconImageButton) rootView.findViewById(R.id.imageButtonPaint2);
		botonColor = (IconImageButton) rootView.findViewById(R.id.imageButtonPaint3);
		botonSize = (IconImageButton) rootView.findViewById(R.id.imageButtonPaint4);
		botonPegatina = (IconImageButton) rootView.findViewById(R.id.imageButtonPaint5);
		botonMano = (IconImageButton) rootView.findViewById(R.id.imageButtonPaint6);
		botonPrev = (IconImageButton) rootView.findViewById(R.id.imageButtonPaint7);
		botonNext = (IconImageButton) rootView.findViewById(R.id.imageButtonPaint8);
		botonDelete = (IconImageButton) rootView.findViewById(R.id.imageButtonPaint9);
		botonListo = (IconImageButton) rootView.findViewById(R.id.imageButtonPaint10);

		botonPincel.setOnClickListener(new OnPincelClickListener());
		botonCubo.setOnClickListener(new OnCuboClickListener());
		botonColor.setOnClickListener(new OnColorClickListener());
		botonSize.setOnClickListener(new OnSizeClickListener());
		botonPegatina.setOnClickListener(new OnPegatinaClickListener());
		botonMano.setOnClickListener(new OnManoClickListener());
		botonNext.setOnClickListener(new OnNextClickListener());
		botonPrev.setOnClickListener(new OnPrevClickListener());
		botonDelete.setOnClickListener(new OnDeleteClickListener());
		botonListo.setOnClickListener(new OnReadyClickListener());

		setCanvasListener(canvas);
		
		if (dataSaved != null)
		{
			canvas.restoreData(dataSaved);
		}

		resetInterface();
		updateInterface();
		
		List<Integer> listaMensajes = new ArrayList<Integer>();
		listaMensajes.add(R.string.text_tip_paint_pencil_description);
		listaMensajes.add(R.string.text_tip_paint_bucket_description);
		listaMensajes.add(R.string.text_tip_paint_sticker_description);
		listaMensajes.add(R.string.text_tip_paint_editsticker_description);
		listaMensajes.add(R.string.text_tip_paint_zoom_description);
		
		List<String> listaVideos = new ArrayList<String>();
		listaVideos.add(GameResources.VIDEO_PAINT_PENCIL_PATH);
		listaVideos.add(GameResources.VIDEO_PAINT_BUCKET_PATH);
		listaVideos.add(GameResources.VIDEO_PAINT_STICKER_PATH);
		listaVideos.add(GameResources.VIDEO_PAINT_EDITSTICKER_PATH);
		listaVideos.add(GameResources.VIDEO_PAINT_ZOOM_PATH);
		
		sendAlertMessage(R.string.text_tip_paint_tools_title, listaMensajes, listaVideos);	
		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		canvas = null;
		colorDialog = null;
		sizeDialog = null;
		stickerDialog = null;
		botonPincel = null;
		botonCubo = null;
		botonMano = null;
		botonNext = null;
		botonPrev = null;
		botonDelete = null;
		botonListo = null;
		botonColor = null;
		botonSize = null;
		botonPegatina = null;
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		
		mCallback = null;
		personaje = null;
		estadoNiveles = null;
		dataSaved = null;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		
		if (dataSaved != null)
		{
			canvas.restoreData(dataSaved);

			resetInterface();
			updateInterface();
		}
		
		canvas.onResume();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		canvas.onPause();

		dataSaved = canvas.saveData();
	}

	/* Métodos Abstráctos OpenGLFragment */

	@Override
	protected void updateInterface()
	{
		if (!canvas.isBufferSiguienteVacio())
		{
			botonNext.setVisibility(View.VISIBLE);
		}

		if (!canvas.isBufferAnteriorVacio())
		{
			botonPrev.setVisibility(View.VISIBLE);
			botonDelete.setVisibility(View.VISIBLE);
		}
		
		botonPincel.setActivo(canvas.isEstadoPincel());
		botonCubo.setActivo(canvas.isEstadoCubo());
		botonMano.setActivo(canvas.isEstadoMover());
		botonPegatina.setActivo(canvas.isEstadoPegatinas());
	}

	@Override
	protected void resetInterface()
	{
		botonNext.setVisibility(View.INVISIBLE);
		botonPrev.setVisibility(View.INVISIBLE);
		botonDelete.setVisibility(View.INVISIBLE);
	}

	/* Métodos Listener onClick */

	private class OnPincelClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarPincel();

			resetInterface();
			updateInterface();
		}
	}

	private class OnCuboClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarCubo();

			resetInterface();
			updateInterface();
		}
	}

	private class OnColorClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if (colorDialog == null)
			{
				colorDialog = new ColorDialog(getActivity()) {
					@Override
					public void onColorSelected(int color)
					{
						canvas.seleccionarColor(color);
					}
				};
			}
			colorDialog.show(v);
		}
	}

	private class OnSizeClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if (sizeDialog == null)
			{
				sizeDialog = new SizeDialog(getActivity()) {
					@Override
					public void onSizeSelected(TTypeSize size)
					{
						canvas.seleccionarSize(size);
					}
				};
			}
			sizeDialog.show(v);
		}
	}

	private class OnPegatinaClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if (stickerDialog == null)
			{
				stickerDialog = new StickerDialog(getActivity(), estadoNiveles) {
					@Override
					public void onAddSticker(int tag, TTypeSticker tipo)
					{
						canvas.anyadirPegatina(tag, tipo);

						resetInterface();
						updateInterface();
					}
					
					@Override
					public void onDeleteSticker(TTypeSticker tipo)
					{
						canvas.eliminarPegatina(tipo);
						
						resetInterface();
						updateInterface();
					}
					
					@Override
					public void onEditSticker(TTypeSticker tipo)
					{
						canvas.editarPegatina(tipo);
						
						resetInterface();
						updateInterface();
					}
				};
			}
			
			stickerDialog.show(v);
			canvas.seleccionarNada();
			
			resetInterface();
			updateInterface();
		}
	}

	private class OnManoClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarMano();

			resetInterface();
			updateInterface();
		}
	}

	private class OnPrevClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.anteriorAccion();

			resetInterface();
			updateInterface();
		}
	}

	private class OnNextClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.siguienteAccion();

			resetInterface();
			updateInterface();
		}
	}

	private class OnDeleteClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.reiniciar();

			resetInterface();
			updateInterface();
		}
	}

	private class OnReadyClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			resetInterface();
			updateInterface();
			
			if(personajeIndice == -1)
			{
				mCallback.onPaintReady(canvas.getTextura(), canvas.saveData());
			}
			else
			{
				mCallback.onRepaintReady(canvas.getTextura(), personajeIndice);
			}	
		}
	}
}

package com.creation.paint;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.dialog.ColorDialog;
import com.android.dialog.SizeDialog;
import com.android.dialog.StickerDialog;
import com.android.view.OpenGLFragment;
import com.creation.data.TTipoSticker;
import com.creation.data.Textura;
import com.game.data.Personaje;
import com.project.main.R;
import com.project.model.GameResources;
import com.project.model.GameStatistics;

public class PaintFragment extends OpenGLFragment
{
	private PaintFragmentListener mCallback;

	private ColorDialog colorDialog;
	private SizeDialog sizeDialog;
	private StickerDialog stickerDialog;

	private PaintGLSurfaceView canvas;
	private ImageButton botonPincel, botonCubo, botonMano, botonNext, botonPrev, botonDelete, botonListo, botonColor, botonSize, botonPegatina;

	private Personaje personaje;
	private int personajeIndice;
	
	private GameStatistics[] estadoNiveles;
	
	private PaintDataSaved dataSaved;

	/* Constructora */

	public static final PaintFragment newInstance(PaintFragmentListener c, Personaje p, GameStatistics[] e)
	{
		PaintFragment fragment = new PaintFragment();
		fragment.setParameters(c, p, -1, e, null);
		return fragment;
	}
	
	public static final PaintFragment newInstance(PaintFragmentListener c, Personaje p, int n, GameStatistics[] e)
	{
		PaintFragment fragment = new PaintFragment();
		fragment.setParameters(c, p, n, e, null);
		return fragment;
	}
	
	public static final PaintFragment newInstance(PaintFragmentListener c, Personaje p, GameStatistics[] e, PaintDataSaved s)
	{
		PaintFragment fragment = new PaintFragment();
		fragment.setParameters(c, p, -1, e, s);
		return fragment;
	}
	
	private void setParameters(PaintFragmentListener c, Personaje p, int n, GameStatistics[] e, PaintDataSaved s)
	{
		mCallback = c;
		personaje = p;
		personajeIndice = n;
		estadoNiveles = e;
		dataSaved = s;
	}

	public interface PaintFragmentListener
	{
		public void onPaintReady(final Textura textura, final PaintDataSaved datosSalvados);
		public void onRepaintReady(final Textura textura, final int indice);
	}

	/* M�todos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_creation_paint_layout, container, false);

		// Instanciar Elementos de la GUI
		canvas = (PaintGLSurfaceView) rootView.findViewById(R.id.paintGLSurfaceViewPaint1);
		canvas.setParameters(personaje);
		
		botonPincel = (ImageButton) rootView.findViewById(R.id.imageButtonPaint1);
		botonCubo = (ImageButton) rootView.findViewById(R.id.imageButtonPaint2);
		botonColor = (ImageButton) rootView.findViewById(R.id.imageButtonPaint3);
		botonSize = (ImageButton) rootView.findViewById(R.id.imageButtonPaint4);
		botonPegatina = (ImageButton) rootView.findViewById(R.id.imageButtonPaint5);
		botonMano = (ImageButton) rootView.findViewById(R.id.imageButtonPaint6);
		botonPrev = (ImageButton) rootView.findViewById(R.id.imageButtonPaint7);
		botonNext = (ImageButton) rootView.findViewById(R.id.imageButtonPaint8);
		botonDelete = (ImageButton) rootView.findViewById(R.id.imageButtonPaint9);
		botonListo = (ImageButton) rootView.findViewById(R.id.imageButtonPaint10);

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

		reiniciarInterfaz();
		actualizarInterfaz();
		
		List<Integer> listaMensajes = new ArrayList<Integer>();
		listaMensajes.add(R.string.text_tip_paint_pencil_description);
		listaMensajes.add(R.string.text_tip_paint_bucket_description);
		listaMensajes.add(R.string.text_tip_paint_sticker_description);
		listaMensajes.add(R.string.text_tip_paint_zoom_description);
		
		List<String> listaVideos = new ArrayList<String>();
		listaVideos.add(GameResources.VIDEO_PAINT_PENCIL_PATH);
		listaVideos.add(GameResources.VIDEO_PAINT_BUCKET_PATH);
		listaVideos.add(GameResources.VIDEO_PAINT_STICKER_PATH);
		listaVideos.add(GameResources.VIDEO_PAINT_ZOOM_PATH);
		
		sendAlertMessage(R.string.text_tip_design_touch_title, listaMensajes, listaVideos);	
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

			reiniciarInterfaz();
			actualizarInterfaz();
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

	/* M�todos Abstr�ctos OpenGLFragment */

	@Override
	protected void actualizarInterfaz()
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

		if (canvas.isEstadoPincel())
		{
			botonPincel.setBackgroundResource(R.drawable.icon_tool_pencil_selected);
		}
		else if (canvas.isEstadoCubo())
		{
			botonCubo.setBackgroundResource(R.drawable.icon_tool_bucket_selected);
		}
		else if (canvas.isEstadoMover())
		{
			botonMano.setBackgroundResource(R.drawable.icon_tool_zoom_selected);
		}
		else if (canvas.isEstadoPegatinas())
		{
			botonPegatina.setBackgroundResource(R.drawable.icon_tool_sticker_selected);
		}
	}

	@Override
	protected void reiniciarInterfaz()
	{
		botonNext.setVisibility(View.INVISIBLE);
		botonPrev.setVisibility(View.INVISIBLE);
		botonDelete.setVisibility(View.INVISIBLE);

		botonPincel.setBackgroundResource(R.drawable.icon_tool_pencil);
		botonCubo.setBackgroundResource(R.drawable.icon_tool_bucket);
		botonMano.setBackgroundResource(R.drawable.icon_tool_zoom);
		botonPegatina.setBackgroundResource(R.drawable.icon_tool_sticker);
	}

	/* M�todos Listener onClick */

	private class OnPincelClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarPincel();

			reiniciarInterfaz();
			actualizarInterfaz();
		}
	}

	private class OnCuboClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarCubo();

			reiniciarInterfaz();
			actualizarInterfaz();
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
					public void onSizeSelected(TTipoSize size)
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
					public void onStickerSelected(int tag, TTipoSticker tipo)
					{
						canvas.seleccionarPegatina(tag, tipo);

						reiniciarInterfaz();
						actualizarInterfaz();
					}
					
					@Override
					public void onStickerDeleted(TTipoSticker tipo)
					{
						canvas.eliminarPegatina(tipo);
						
						reiniciarInterfaz();
						actualizarInterfaz();
					}
				};
			}
			stickerDialog.show(v);
		}
	}

	private class OnManoClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarMano();

			reiniciarInterfaz();
			actualizarInterfaz();
		}
	}

	private class OnPrevClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.anteriorAccion();

			reiniciarInterfaz();
			actualizarInterfaz();
		}
	}

	private class OnNextClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.siguienteAccion();

			reiniciarInterfaz();
			actualizarInterfaz();
		}
	}

	private class OnDeleteClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.reiniciar();

			reiniciarInterfaz();
			actualizarInterfaz();
		}
	}

	private class OnReadyClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			reiniciarInterfaz();
			actualizarInterfaz();

			if(personajeIndice != -1)
			{
				mCallback.onRepaintReady(canvas.getTextura(), personajeIndice);
			}
			else
			{
				mCallback.onPaintReady(canvas.getTextura(), canvas.saveData());
			}
		}
	}
}

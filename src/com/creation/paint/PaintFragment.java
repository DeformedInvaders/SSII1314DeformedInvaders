package com.creation.paint;

import android.app.Activity;
import android.content.Context;
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
import com.creation.data.Esqueleto;
import com.creation.data.Textura;
import com.game.data.TTipoSticker;
import com.project.main.R;

public class PaintFragment extends OpenGLFragment
{
	private PaintFragmentListener mCallback;
	private Context mContext;

	private ColorDialog colorDialog;
	private SizeDialog sizeDialog;
	private StickerDialog stickerDialog;

	private PaintGLSurfaceView canvas;
	private ImageButton botonPincel, botonCubo, botonMano, botonNext, botonPrev, botonDelete, botonListo, botonColor, botonSize, botonPegatina;

	private Esqueleto esqueleto;

	private PaintDataSaved dataSaved;

	/* SECTION Constructora */

	public static final PaintFragment newInstance(Esqueleto e)
	{
		PaintFragment fragment = new PaintFragment();
		fragment.setParameters(e);
		return fragment;
	}

	private void setParameters(Esqueleto e)
	{
		esqueleto = e;
	}

	public interface PaintFragmentListener
	{
		public void onPaintReadyButtonClicked(Textura t);
	}

	/* SECTION Métodos Fragment */

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mCallback = (PaintFragmentListener) activity;
		mContext = activity.getApplicationContext();
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		mCallback = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_creation_paint_layout, container, false);

		// Instanciar Elementos de la GUI
		canvas = (PaintGLSurfaceView) rootView.findViewById(R.id.paintGLSurfaceViewPaint1);
		canvas.setParameters(esqueleto);

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

		reiniciarInterfaz();
		actualizarInterfaz();
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
	public void onResume()
	{
		super.onResume();
		canvas.onResume();

		if (dataSaved != null)
		{
			canvas.restoreData(dataSaved);

			reiniciarInterfaz();
			actualizarInterfaz();
		}
	}

	@Override
	public void onPause()
	{
		super.onPause();
		canvas.onPause();

		dataSaved = canvas.saveData();
	}

	/* SECTION Métodos Abstráctos OpenGLFragment */

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

	/* SECTION Métodos Listener onClick */

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
				colorDialog = new ColorDialog(mContext) {
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
				sizeDialog = new SizeDialog(mContext) {
					@Override
					public void onSizeSelected(int size)
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
				stickerDialog = new StickerDialog(mContext) {
					@Override
					public void onStickerSelected(int tag, TTipoSticker tipo)
					{
						canvas.seleccionarPegatina(tag, tipo);

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

			mCallback.onPaintReadyButtonClicked(canvas.getTextura());
		}
	}
}

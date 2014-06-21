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

	private PaintOpenGLSurfaceView mCanvas;
	private IconImageButton buttonPencil, buttonBucket, buttonHand, buttonNext, buttonPrev, buttonDelete, buttonReady, buttonColor, buttonSize, buttonSticker;

	private Character mCharacter;
	private int mCharacterIndex;
	
	private GameStatistics[] mStatistics;
	
	private PaintDataSaved dataSaved;

	/* Constructora */

	public static final PaintFragment newInstance(PaintFragmentListener callback, Character character, GameStatistics[] statistics)
	{
		PaintFragment fragment = new PaintFragment();
		fragment.setParameters(callback, character, -1, statistics, null);
		return fragment;
	}
	
	public static final PaintFragment newInstance(PaintFragmentListener callback, Character character, int index, GameStatistics[] statistics)
	{
		PaintFragment fragment = new PaintFragment();
		fragment.setParameters(callback, character, index, statistics, null);
		return fragment;
	}
	
	public static final PaintFragment newInstance(PaintFragmentListener callback, Character character, GameStatistics[] statistics, PaintDataSaved data)
	{
		PaintFragment fragment = new PaintFragment();
		fragment.setParameters(callback, character, -1, statistics, data);
		return fragment;
	}
	
	private void setParameters(PaintFragmentListener callback, Character character, int index, GameStatistics[] statistics, PaintDataSaved data)
	{
		mCallback = callback;
		mCharacter = character;
		mCharacterIndex = index;
		mStatistics = statistics;
		dataSaved = data;
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
		mCanvas = (PaintOpenGLSurfaceView) rootView.findViewById(R.id.paintGLSurfaceViewPaint1);
		mCanvas.setParameters(mCharacter);
		
		buttonPencil = (IconImageButton) rootView.findViewById(R.id.imageButtonPaint1);
		buttonBucket = (IconImageButton) rootView.findViewById(R.id.imageButtonPaint2);
		buttonColor = (IconImageButton) rootView.findViewById(R.id.imageButtonPaint3);
		buttonSize = (IconImageButton) rootView.findViewById(R.id.imageButtonPaint4);
		buttonSticker = (IconImageButton) rootView.findViewById(R.id.imageButtonPaint5);
		buttonHand = (IconImageButton) rootView.findViewById(R.id.imageButtonPaint6);
		buttonPrev = (IconImageButton) rootView.findViewById(R.id.imageButtonPaint7);
		buttonNext = (IconImageButton) rootView.findViewById(R.id.imageButtonPaint8);
		buttonDelete = (IconImageButton) rootView.findViewById(R.id.imageButtonPaint9);
		buttonReady = (IconImageButton) rootView.findViewById(R.id.imageButtonPaint10);

		buttonPencil.setOnClickListener(new OnPencilClickListener());
		buttonBucket.setOnClickListener(new OnBucketClickListener());
		buttonColor.setOnClickListener(new OnColorClickListener());
		buttonSize.setOnClickListener(new OnSizeClickListener());
		buttonSticker.setOnClickListener(new OnStickerClickListener());
		buttonHand.setOnClickListener(new OnHandClickListener());
		buttonNext.setOnClickListener(new OnNextClickListener());
		buttonPrev.setOnClickListener(new OnPrevClickListener());
		buttonDelete.setOnClickListener(new OnDeleteClickListener());
		buttonReady.setOnClickListener(new OnReadyClickListener());

		setCanvasListener(mCanvas);
		
		if (dataSaved != null)
		{
			mCanvas.restoreData(dataSaved);
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

		mCanvas = null;
		colorDialog = null;
		sizeDialog = null;
		stickerDialog = null;
		buttonPencil = null;
		buttonBucket = null;
		buttonHand = null;
		buttonNext = null;
		buttonPrev = null;
		buttonDelete = null;
		buttonReady = null;
		buttonColor = null;
		buttonSize = null;
		buttonSticker = null;
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		
		mCallback = null;
		mCharacter = null;
		mStatistics = null;
		dataSaved = null;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		
		if (dataSaved != null)
		{
			mCanvas.restoreData(dataSaved);

			resetInterface();
			updateInterface();
		}
		
		mCanvas.onResume();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		mCanvas.onPause();

		dataSaved = mCanvas.saveData();
	}

	/* Métodos Abstráctos OpenGLFragment */

	@Override
	protected void updateInterface()
	{
		if (!mCanvas.isNextBufferEmpty())
		{
			buttonNext.setVisibility(View.VISIBLE);
		}

		if (!mCanvas.isPrevBufferEmpty())
		{
			buttonPrev.setVisibility(View.VISIBLE);
			buttonDelete.setVisibility(View.VISIBLE);
		}
		
		buttonPencil.setActivo(mCanvas.isStatePencil());
		buttonBucket.setActivo(mCanvas.isStateBucket());
		buttonHand.setActivo(mCanvas.isStateHand());
		buttonSticker.setActivo(mCanvas.isStateSticker());
	}

	@Override
	protected void resetInterface()
	{
		buttonNext.setVisibility(View.INVISIBLE);
		buttonPrev.setVisibility(View.INVISIBLE);
		buttonDelete.setVisibility(View.INVISIBLE);
	}

	/* Métodos Listener onClick */

	private class OnPencilClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCanvas.selectPencil();

			resetInterface();
			updateInterface();
		}
	}

	private class OnBucketClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCanvas.selectBucket();

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
						mCanvas.selectColor(color);
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
						mCanvas.selectSize(size);
					}
				};
			}
			sizeDialog.show(v);
		}
	}

	private class OnStickerClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if (stickerDialog == null)
			{
				stickerDialog = new StickerDialog(getActivity(), mStatistics) {
					@Override
					public void onAddSticker(int tag, TTypeSticker type)
					{
						mCanvas.addSticker(tag, type);

						resetInterface();
						updateInterface();
					}
					
					@Override
					public void onDeleteSticker(TTypeSticker type)
					{
						mCanvas.deleteSticker(type);
						
						resetInterface();
						updateInterface();
					}
					
					@Override
					public void onEditSticker(TTypeSticker type)
					{
						mCanvas.editSticker(type);
						
						resetInterface();
						updateInterface();
					}
				};
			}
			
			stickerDialog.show(v);
			mCanvas.selectNothing();
			
			resetInterface();
			updateInterface();
		}
	}

	private class OnHandClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCanvas.selectHand();

			resetInterface();
			updateInterface();
		}
	}

	private class OnPrevClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCanvas.prevAction();

			resetInterface();
			updateInterface();
		}
	}

	private class OnNextClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCanvas.nextAction();

			resetInterface();
			updateInterface();
		}
	}

	private class OnDeleteClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCanvas.selectReset();

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
			
			if(mCharacterIndex == -1)
			{
				mCallback.onPaintReady(mCanvas.getTexture(), mCanvas.saveData());
			}
			else
			{
				mCallback.onRepaintReady(mCanvas.getTexture(), mCharacterIndex);
			}	
		}
	}
}

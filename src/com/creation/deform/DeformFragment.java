package com.creation.deform;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.android.view.IconImageButton;
import com.android.view.OpenGLFragment;
import com.creation.data.TTypeMovement;
import com.game.data.Character;
import com.lib.buffer.VertexArray;
import com.project.main.R;

public class DeformFragment extends OpenGLFragment implements OnDeformListener
{
	private OnDeformationListener mListener;
	
	private Character mCharacter;
	private TTypeMovement mMovement;
	
	private DeformOpenGLSurfaceView mCanvas;
	private IconImageButton buttonAdd, buttonDelete, buttonMove, buttonReset, buttonRecord, buttonPlay;

	private DeformDataSaved dataSaved;
	
	/* Constructora */

	public static final DeformFragment newInstance(OnDeformationListener listener, Character character, TTypeMovement movement)
	{
		DeformFragment fragment = new DeformFragment();
		fragment.setParameters(listener, character, movement);
		return fragment;
	}

	private void setParameters(OnDeformationListener listener, Character character, TTypeMovement movement)
	{
		mListener = listener;
		mCharacter = character;
		mMovement = movement;
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_creation_deform_layout, container, false);

		// Instanciar Elementos de la GUI
		mCanvas = (DeformOpenGLSurfaceView) rootView.findViewById(R.id.deformGLSurfaceViewDeform1);
		mCanvas.setParameters(this, mCharacter, mMovement);
		setCanvasListener(mCanvas);
		
		buttonAdd = (IconImageButton) rootView.findViewById(R.id.imageButtonDeform1);
		buttonDelete = (IconImageButton) rootView.findViewById(R.id.imageButtonDeform2);
		buttonMove = (IconImageButton) rootView.findViewById(R.id.imageButtonDeform3);
		buttonReset = (IconImageButton) rootView.findViewById(R.id.imageButtonDeform4);
		buttonRecord = (IconImageButton) rootView.findViewById(R.id.imageButtonDeform5);
		buttonPlay = (IconImageButton) rootView.findViewById(R.id.imageButtonDeform6);

		buttonAdd.setOnClickListener(new OnAddClickListener());
		buttonDelete.setOnClickListener(new OnRemoveClickListener());
		buttonMove.setOnClickListener(new OnMoveClickListener());
		buttonReset.setOnClickListener(new OnResetClickListener());
		buttonRecord.setOnClickListener(new OnRecordClickListener());
		buttonPlay.setOnClickListener(new OnPlayClickListener());

		resetInterface();
		updateInterface();
		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		mCanvas = null;
		
		buttonAdd = null;
		buttonDelete = null;
		buttonMove = null;
		buttonReset = null;
		buttonRecord = null;
		buttonPlay = null;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		mCanvas.onResume();

		if (dataSaved != null)
		{
			mCanvas.restoreData(dataSaved);

			resetInterface();
			updateInterface();
		}
	}

	@Override
	public void onPause()
	{
		super.onPause();
		mCanvas.onPause();

		dataSaved = mCanvas.saveData();
	}

	/* Métodos Abstractos OpenGLFramgent */

	@Override
	protected void updateInterface()
	{
		if (mCanvas.isHandlesEmpty())
		{
			buttonAdd.setVisibility(View.VISIBLE);
		}
		else
		{
			buttonRecord.setVisibility(View.VISIBLE);

			if (!mCanvas.isStateRecording())
			{
				buttonAdd.setVisibility(View.VISIBLE);
				buttonDelete.setVisibility(View.VISIBLE);
				buttonMove.setVisibility(View.VISIBLE);
				buttonReset.setVisibility(View.VISIBLE);
			}
		}
		
		if (mCanvas.isAnimationReady())
		{
			buttonPlay.setVisibility(View.VISIBLE);
		}

		buttonAdd.setActivo(mCanvas.isStateAdding());
		buttonDelete.setActivo(mCanvas.isStateDeleting());
		buttonRecord.setActivo(mCanvas.isStateRecording());
		buttonMove.setActivo(mCanvas.isStateMoving());
		buttonPlay.setActivo(mCanvas.isStatePlaying());
	}

	@Override
	protected void resetInterface()
	{
		buttonAdd.setVisibility(View.INVISIBLE);
		buttonDelete.setVisibility(View.INVISIBLE);
		buttonMove.setVisibility(View.INVISIBLE);
		buttonReset.setVisibility(View.INVISIBLE);
		buttonRecord.setVisibility(View.INVISIBLE);
		buttonPlay.setVisibility(View.INVISIBLE);
	}

	/* Métodos Listener onClick */

	private class OnAddClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCanvas.selectAdding();

			resetInterface();
			updateInterface();
		}
	}

	private class OnRemoveClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCanvas.selectDeleting();

			resetInterface();
			updateInterface();
		}
	}

	private class OnMoveClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCanvas.selectMoving();

			resetInterface();
			updateInterface();
		}
	}

	private class OnResetClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCanvas.selectReset();

			resetInterface();
			updateInterface();
		}
	}

	private class OnRecordClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCanvas.selectRecording();

			resetInterface();
			updateInterface();
		}
	}

	private class OnPlayClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCanvas.selectPlaying();

			resetInterface();
			updateInterface();
		}
	}

	/* Métodos de Obtención de Información */

	public List<VertexArray> getMovements()
	{
		if (mCanvas != null)
		{
			return mCanvas.getMovement();
		}

		return null;
	}
	
	/* Métodos abstractos de OnDeformListener */

	@Override
	public void onPlaySoundEffect()
	{
		mListener.onPlaySoundEffect();
	}

	@Override
	public void onAnimationFinished()
	{
		getActivity().runOnUiThread(new Runnable() {
	        @Override
	        public void run()
	        {
	        	resetInterface();
	        	updateInterface();
	        }
	    });
	}
}

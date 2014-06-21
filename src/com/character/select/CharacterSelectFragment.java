package com.character.select;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.alert.TextInputAlert;
import com.android.view.IconImageButton;
import com.android.view.OpenGLFragment;
import com.character.display.DisplayOpenGLSurfaceView;
import com.character.display.OnDisplayListener;
import com.creation.data.TTypeMovement;
import com.game.data.Character;
import com.main.model.GameResources;
import com.project.main.R;

public class CharacterSelectFragment extends OpenGLFragment implements OnDisplayListener
{	
	private OnCharacterListener mListener;
	
	private DisplayOpenGLSurfaceView mCanvas;
	private IconImageButton buttonCamera, buttonRun, buttonJump, buttonCrouch, buttonAttack, buttonReady, buttonRepaint, buttonRedeform, buttonDelete, buttonRename, buttonExport;

	private Character mCharacter;
	
	/* Constructora */

	public static final CharacterSelectFragment newInstance(OnCharacterListener listener, Character character)
	{
		CharacterSelectFragment fragment = new CharacterSelectFragment();
		fragment.setParameters(listener, character);
		return fragment;
	}

	private void setParameters(OnCharacterListener listener, Character character)
	{
		mListener = listener;
		mCharacter = character;
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_character_select_layout, container, false);

		// Instanciar Elementos de la GUI
		mCanvas = (DisplayOpenGLSurfaceView) rootView.findViewById(R.id.displayGLSurfaceViewCharacterSelect1);
		mCanvas.setParameters(this, mCharacter, false);
		
		Typeface textFont = Typeface.createFromAsset(getActivity().getAssets(), GameResources.FONT_LOGO_PATH);
		
		TextView textBackground = (TextView) rootView.findViewById(R.id.textViewCharacterSelect1);
		textBackground.setText(mCharacter.getName());
		textBackground.setTypeface(textFont);

		buttonCamera = (IconImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect1);
		buttonRun = (IconImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect2);
		buttonJump = (IconImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect3);
		buttonCrouch = (IconImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect4);
		buttonAttack = (IconImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect5);
		buttonReady = (IconImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect6);
		buttonRepaint = (IconImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect8);
		buttonRedeform = (IconImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect11);
		buttonRename = (IconImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect9);
		buttonDelete = (IconImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect7);
		buttonExport = (IconImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect10);
		
		buttonCamera.setOnClickListener(new OnCameraClickListener());
		buttonRun.setOnClickListener(new OnRunClickListener());
		buttonJump.setOnClickListener(new OnJumpClickListener());
		buttonCrouch.setOnClickListener(new OnCrouchClickListener());
		buttonAttack.setOnClickListener(new OnAttackClickListener());
		buttonReady.setOnClickListener(new OnReadyClickListener());
		buttonRepaint.setOnClickListener(new OnRepaintClickListener());
		buttonRedeform.setOnClickListener(new OnRedeformClickListener());
		buttonDelete.setOnClickListener(new OnDeleteClickListener());
		buttonRename.setOnClickListener(new OnRenameClickListener());
		buttonExport.setOnClickListener(new OnExportClickListener());

		setCanvasListener(mCanvas);

		resetInterface();
		updateInterface();
		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
		mCanvas = null;
		buttonReady = null;
		buttonDelete = null;
		buttonCamera = null;
		buttonRun = null;
		buttonJump = null;
		buttonCrouch = null;
		buttonAttack = null;
		buttonRepaint = null;
		buttonRedeform = null;
		buttonRename = null;
		buttonExport = null;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		mCanvas.onResume();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		mCanvas.saveData();
		mCanvas.onPause();
	}

	/* Métodos abstractos de OpenGLFragment */

	@Override
	protected void resetInterface()
	{
		buttonCamera.setVisibility(View.INVISIBLE);
		buttonRun.setVisibility(View.INVISIBLE);
		buttonJump.setVisibility(View.INVISIBLE);
		buttonCrouch.setVisibility(View.INVISIBLE);
		buttonAttack.setVisibility(View.INVISIBLE);
		buttonReady.setVisibility(View.INVISIBLE);
		buttonDelete.setVisibility(View.INVISIBLE);
		buttonRepaint.setVisibility(View.INVISIBLE);
		buttonRedeform.setVisibility(View.INVISIBLE);
		buttonRename.setVisibility(View.INVISIBLE);
		buttonExport.setVisibility(View.INVISIBLE);

		buttonCamera.setBackgroundResource(R.drawable.icon_share_picture);
	}

	@Override
	protected void updateInterface()
	{
		if (mCanvas.isStateNothing() || !mCanvas.isStateAnimation())
		{
			buttonCamera.setVisibility(View.VISIBLE);
		}

		if (mCanvas.isStateNothing() || mCanvas.isStateAnimation())
		{
			buttonRun.setVisibility(View.VISIBLE);
			buttonJump.setVisibility(View.VISIBLE);
			buttonCrouch.setVisibility(View.VISIBLE);
			buttonAttack.setVisibility(View.VISIBLE);
			buttonReady.setVisibility(View.VISIBLE);
			buttonDelete.setVisibility(View.VISIBLE);
			buttonRepaint.setVisibility(View.VISIBLE);
			buttonRedeform.setVisibility(View.VISIBLE);
			buttonRename.setVisibility(View.VISIBLE);
			buttonExport.setVisibility(View.VISIBLE);
		}

		if (mCanvas.isStatePreparing())
		{
			buttonCamera.setBackgroundResource(R.drawable.icon_share_camara);
		}
		else if (mCanvas.isStateFinished())
		{
			buttonCamera.setBackgroundResource(R.drawable.icon_share_post);
		}
	}

	/* Métodos Listener onClick */

	public class OnCameraClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if (mCanvas.isStateNothing())
			{
				mCanvas.selectPreparing();
				mListener.onSetSwipeable(false);
			}
			else if (mCanvas.isStatePreparing())
			{				
				final Bitmap bitmap = mCanvas.getScreenshot();
				String text = getString(R.string.text_social_create_character_initial) + " " + mCharacter.getName() + " " + getString(R.string.text_social_create_character_final);

				TextInputAlert alert = new TextInputAlert(getActivity(), R.string.text_social_share_title, R.string.text_social_share_description, text, R.string.text_button_send, R.string.text_button_cancel, false) {
					@Override
					public void onPossitiveButtonClick(String text)
					{						
						mListener.onPostPublished(text, bitmap);
						mListener.onSetSwipeable(true);
						
						mCanvas.selectFinished();

						resetInterface();
						updateInterface();
					}

					@Override
					public void onNegativeButtonClick(String text)
					{
						mCanvas.selectFinished();
						mListener.onSetSwipeable(true);

						resetInterface();
						updateInterface();
					}

				};

				resetInterface();
				updateInterface();
				alert.show();

				mListener.onSetSwipeable(false);
			}

			resetInterface();
			updateInterface();
		}
	}

	private class OnRunClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCanvas.startAnimation(TTypeMovement.Run);
		}
	}

	private class OnJumpClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCanvas.startAnimation(TTypeMovement.Jump);
		}
	}

	private class OnCrouchClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCanvas.startAnimation(TTypeMovement.Crouch);
		}
	}

	private class OnAttackClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCanvas.startAnimation(TTypeMovement.Attack);
		}
	}
	
	private class OnReadyClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mListener.onCharacterSelected();
		}
	}
	
	private class OnRepaintClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mListener.onCharacterRepainted();
		}
	}
	
	private class OnRedeformClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mListener.onCharacterRedeformed();
		}
	}

	private class OnDeleteClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mListener.onCharacterDeleted();
		}
	}
	
	private class OnRenameClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mListener.onCharacterRenamed();
		}
	}
	
	private class OnExportClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mListener.onCharacterExported();
		}
	}

	@Override
	public void onDisplayPlaySoundEffect(int sound)
	{
		mListener.onPlaySoundEffect(sound);
	}
}

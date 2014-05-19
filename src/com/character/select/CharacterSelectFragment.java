package com.character.select;

import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.creation.data.TTipoMovimiento;
import com.game.data.Personaje;
import com.main.model.GameResources;
import com.project.main.R;

public class CharacterSelectFragment extends OpenGLFragment implements OnDisplayListener
{	
	private OnCharacterListener mListener;
	
	private DisplayOpenGLSurfaceView canvas;
	private IconImageButton botonCamara, botonRun, botonJump, botonCrouch, botonAttack, botonReady, botonRepaint, botonDelete, botonRename, botonExport;

	private Personaje personaje;
	
	/* Constructora */

	public static final CharacterSelectFragment newInstance(OnCharacterListener listener, Personaje personaje)
	{
		CharacterSelectFragment fragment = new CharacterSelectFragment();
		fragment.setParameters(listener, personaje);
		return fragment;
	}

	private void setParameters(OnCharacterListener l, Personaje p)
	{
		mListener = l;
		personaje = p;
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_character_select_layout, container, false);

		// Instanciar Elementos de la GUI
		canvas = (DisplayOpenGLSurfaceView) rootView.findViewById(R.id.displayGLSurfaceViewCharacterSelect1);
		canvas.setParameters(this, personaje, false);
		
		Typeface textFont = Typeface.createFromAsset(getActivity().getAssets(), GameResources.FONT_LOGO_PATH);
		
		TextView textBackground = (TextView) rootView.findViewById(R.id.textViewCharacterSelect1);
		textBackground.setText(personaje.getNombre());
		textBackground.setTextColor(Color.BLACK);
		textBackground.setTypeface(textFont);

		botonCamara = (IconImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect1);
		botonRun = (IconImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect2);
		botonJump = (IconImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect3);
		botonCrouch = (IconImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect4);
		botonAttack = (IconImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect5);
		botonReady = (IconImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect6);
		botonRepaint = (IconImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect8);
		botonRename = (IconImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect9);
		botonDelete = (IconImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect7);
		botonExport = (IconImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect10);
		
		botonCamara.setOnClickListener(new OnCamaraClickListener());
		botonRun.setOnClickListener(new OnRunClickListener());
		botonJump.setOnClickListener(new OnJumpClickListener());
		botonCrouch.setOnClickListener(new OnCrouchClickListener());
		botonAttack.setOnClickListener(new OnAttackClickListener());
		botonReady.setOnClickListener(new OnReadyClickListener());
		botonRepaint.setOnClickListener(new OnRepaintClickListener());
		botonDelete.setOnClickListener(new OnDeleteClickListener());
		botonRename.setOnClickListener(new OnRenameClickListener());
		botonExport.setOnClickListener(new OnExportClickListener());

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
		botonReady = null;
		botonDelete = null;
		botonCamara = null;
		botonRun = null;
		botonJump = null;
		botonCrouch = null;
		botonAttack = null;
		botonRepaint = null;
		botonRename = null;
		botonExport = null;
	}
	
	/*@Override
	public void onDetach()
	{
		super.onDetach();
		
		mListener = null;
		personaje = null;
	}*/

	@Override
	public void onResume()
	{
		super.onResume();
		canvas.onResume();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		canvas.saveData();
		canvas.onPause();
	}

	/* Métodos abstractos de OpenGLFragment */

	@Override
	protected void reiniciarInterfaz()
	{
		botonCamara.setVisibility(View.INVISIBLE);
		botonRun.setVisibility(View.INVISIBLE);
		botonJump.setVisibility(View.INVISIBLE);
		botonCrouch.setVisibility(View.INVISIBLE);
		botonAttack.setVisibility(View.INVISIBLE);
		botonReady.setVisibility(View.INVISIBLE);
		botonDelete.setVisibility(View.INVISIBLE);
		botonRepaint.setVisibility(View.INVISIBLE);
		botonRename.setVisibility(View.INVISIBLE);
		botonExport.setVisibility(View.INVISIBLE);

		botonCamara.setBackgroundResource(R.drawable.icon_share_picture);
	}

	@Override
	protected void actualizarInterfaz()
	{
		if (canvas.isEstadoReposo() || !canvas.isEstadoAnimacion())
		{
			botonCamara.setVisibility(View.VISIBLE);
		}

		if (canvas.isEstadoReposo() || canvas.isEstadoAnimacion())
		{
			botonRun.setVisibility(View.VISIBLE);
			botonJump.setVisibility(View.VISIBLE);
			botonCrouch.setVisibility(View.VISIBLE);
			botonAttack.setVisibility(View.VISIBLE);
			botonReady.setVisibility(View.VISIBLE);
			botonDelete.setVisibility(View.VISIBLE);
			botonRepaint.setVisibility(View.VISIBLE);
			botonRename.setVisibility(View.VISIBLE);
			botonExport.setVisibility(View.VISIBLE);
		}

		if (canvas.isEstadoRetoque())
		{
			botonCamara.setBackgroundResource(R.drawable.icon_share_camara);
		}
		else if (canvas.isEstadoTerminado())
		{
			botonCamara.setBackgroundResource(R.drawable.icon_share_post);
		}
	}

	/* Métodos Listener onClick */

	public class OnCamaraClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if (canvas.isEstadoReposo())
			{
				canvas.seleccionarRetoque();
				mListener.onSetSwipeable(false);
			}
			else if (canvas.isEstadoRetoque())
			{				
				final Bitmap bitmap = canvas.getCapturaPantalla();
				String text = getString(R.string.text_social_create_character_initial) + " " + personaje.getNombre() + " " + getString(R.string.text_social_create_character_final);

				TextInputAlert alert = new TextInputAlert(getActivity(), R.string.text_social_share_title, R.string.text_social_share_description, text, R.string.text_button_send, R.string.text_button_cancel, false) {
					@Override
					public void onPossitiveButtonClick(String text)
					{						
						mListener.onPostPublished(text, bitmap);
						mListener.onSetSwipeable(true);
						
						canvas.seleccionarTerminado();

						reiniciarInterfaz();
						actualizarInterfaz();
					}

					@Override
					public void onNegativeButtonClick(String text)
					{
						canvas.seleccionarTerminado();
						mListener.onSetSwipeable(true);

						reiniciarInterfaz();
						actualizarInterfaz();
					}

				};

				reiniciarInterfaz();
				actualizarInterfaz();
				alert.show();

				mListener.onSetSwipeable(false);
			}

			reiniciarInterfaz();
			actualizarInterfaz();
		}
	}

	private class OnRunClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarAnimacion(TTipoMovimiento.Run);
		}
	}

	private class OnJumpClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarAnimacion(TTipoMovimiento.Jump);
		}
	}

	private class OnCrouchClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarAnimacion(TTipoMovimiento.Crouch);
		}
	}

	private class OnAttackClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarAnimacion(TTipoMovimiento.Attack);
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

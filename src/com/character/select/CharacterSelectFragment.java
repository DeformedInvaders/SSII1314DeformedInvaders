package com.character.select;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.alert.TextInputAlert;
import com.android.social.SocialConnector;
import com.android.storage.ExternalStorageManager;
import com.android.view.OpenGLFragment;
import com.android.view.ViewPagerSwipeable;
import com.character.display.DisplayGLSurfaceView;
import com.character.display.TDisplayTipo;
import com.game.data.Personaje;
import com.project.main.GamePreferences;
import com.project.main.R;

public class CharacterSelectFragment extends OpenGLFragment
{
	private ExternalStorageManager manager;
	private SocialConnector connector;
	private ViewPagerSwipeable pager;

	private Personaje personaje;
	
	private OnCharacterListener listener;
	private DisplayGLSurfaceView canvas;
	private ImageButton botonCamara, botonRun, botonJump, botonCrouch, botonAttack, botonReady, botonDelete;

	/* Constructora */

	public static final CharacterSelectFragment newInstance(OnCharacterListener l, Personaje p, ViewPagerSwipeable s, ExternalStorageManager m, SocialConnector c)
	{
		CharacterSelectFragment fragment = new CharacterSelectFragment();
		fragment.setParameters(l, p, s, m, c);
		return fragment;
	}

	private void setParameters(OnCharacterListener l, Personaje p, ViewPagerSwipeable s, ExternalStorageManager m, SocialConnector c)
	{
		listener = l;
		personaje = p;
		pager = s;
		manager = m;
		connector = c;
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_character_select_layout, container, false);

		// Instanciar Elementos de la GUI
		canvas = (DisplayGLSurfaceView) rootView.findViewById(R.id.displayGLSurfaceViewCharacterSelect1);
		canvas.setParameters(personaje, manager, TDisplayTipo.Selection);
		
		Typeface textFont = Typeface.createFromAsset(getActivity().getAssets(), GamePreferences.FONT_LOGO_PATH);
		
		TextView textBackground = (TextView) rootView.findViewById(R.id.textViewCharacterSelect1);
		textBackground.setText(personaje.getNombre());
		textBackground.setTextColor(Color.BLACK);
		textBackground.setTypeface(textFont);

		botonCamara = (ImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect1);
		botonRun = (ImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect2);
		botonJump = (ImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect3);
		botonCrouch = (ImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect4);
		botonAttack = (ImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect5);
		botonReady = (ImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect6);
		botonDelete = (ImageButton) rootView.findViewById(R.id.imageButtonCharacterSelect7);

		botonCamara.setOnClickListener(new OnCamaraClickListener());
		botonRun.setOnClickListener(new OnRunClickListener());
		botonJump.setOnClickListener(new OnJumpClickListener());
		botonCrouch.setOnClickListener(new OnCrouchClickListener());
		botonAttack.setOnClickListener(new OnAttackClickListener());
		botonReady.setOnClickListener(new OnReadyClickListener());
		botonDelete.setOnClickListener(new OnDeleteClickListener());

		setCanvasListener(canvas);

		reiniciarInterfaz();
		actualizarInterfaz();
		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		botonReady = null;
		botonDelete = null;
		botonCamara = null;
		botonRun = null;
		botonJump = null;
		botonCrouch = null;
		botonAttack = null;
		canvas = null;
		pager = null;
	}

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
				pager.setSwipeable(false);
			}
			else if (canvas.isEstadoRetoque())
			{
				Bitmap bitmap = canvas.getCapturaPantalla();
				if (manager.guardarImagen(bitmap, personaje.getNombre()))
				{
					String text = getString(R.string.text_social_create_character_initial) + " " + personaje.getNombre() + " " + getString(R.string.text_social_create_character_final);

					TextInputAlert alert = new TextInputAlert(getActivity(), getString(R.string.text_social_share_title), getString(R.string.text_social_share_description), text, getString(R.string.text_button_send), getString(R.string.text_button_cancel)) {
						@Override
						public void onPossitiveButtonClick(String text)
						{
							connector.publicar(text, manager.cargarImagen(personaje.getNombre()));
							canvas.seleccionarTerminado();
							pager.setSwipeable(true);

							reiniciarInterfaz();
							actualizarInterfaz();
						}

						@Override
						public void onNegativeButtonClick(String text)
						{
							canvas.seleccionarTerminado();
							pager.setSwipeable(true);

							reiniciarInterfaz();
							actualizarInterfaz();
						}

					};

					reiniciarInterfaz();
					actualizarInterfaz();
					alert.show();
				}
				else
				{
					Toast.makeText(getActivity(), R.string.error_picture_character, Toast.LENGTH_SHORT).show();
				}

				pager.setSwipeable(false);
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
			canvas.seleccionarRun();
		}
	}

	private class OnJumpClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarJump();
		}
	}

	private class OnCrouchClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarCrouch();
		}
	}

	private class OnAttackClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarAttack();
		}
	}
	
	private class OnReadyClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			listener.onCharacterSelected();
		}
	}

	private class OnDeleteClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			listener.onCharacterDeleted();
		}
	}
}

package com.view.select;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.storage.ExternalStorageManager;
import com.android.view.SwipeableViewPager;
import com.project.data.Esqueleto;
import com.project.data.Movimientos;
import com.project.data.Textura;
import com.project.main.R;
import com.project.social.SocialConnector;
import com.view.display.DisplayGLSurfaceView;

public class SelectFragment extends Fragment
{
	private ExternalStorageManager manager;
	private SocialConnector connector;
	private SwipeableViewPager pager;
	
	private Esqueleto esqueletoActual;
	private Textura texturaActual;
	private Movimientos movimientoActual;
	private String nombreActual;

	private DisplayGLSurfaceView canvas;
	private ImageButton botonCamara, botonRun, botonJump, botonCrouch, botonAttack;
	private boolean estadoCamara;
	
	/* Constructora */
	
	public static final SelectFragment newInstance(Esqueleto e, Textura t, Movimientos v, String n, SwipeableViewPager s, ExternalStorageManager m, SocialConnector c)
	{
		SelectFragment fragment = new SelectFragment();
		fragment.setParameters(e, t, v, n, s, m, c);
		return fragment;
	}
	
	private void setParameters(Esqueleto e, Textura t, Movimientos v, String n, SwipeableViewPager s, ExternalStorageManager m, SocialConnector c)
	{	
		esqueletoActual = e;
		texturaActual = t;
		movimientoActual = v;
		nombreActual = n;
		pager = s;
		manager = m;
		connector = c;
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		estadoCamara = false;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
        View rootView = inflater.inflate(R.layout.fragment_select_layout, container, false);
 		
		// Instanciar Elementos de la GUI
		canvas = (DisplayGLSurfaceView) rootView.findViewById(R.id.displayGLSurfaceViewSelect1);
		canvas.setParameters(esqueletoActual, texturaActual, movimientoActual);
		
		botonCamara = (ImageButton) rootView.findViewById(R.id.imageButtonSelect1);
		botonCamara.setOnClickListener(new OnCamaraClickListener());
		botonRun = (ImageButton) rootView.findViewById(R.id.imageButtonSelect2);
		botonRun.setOnClickListener(new OnRunClickListener());
		botonJump = (ImageButton) rootView.findViewById(R.id.imageButtonSelect3);
		botonJump.setOnClickListener(new OnJumpClickListener());
		botonCrouch = (ImageButton) rootView.findViewById(R.id.imageButtonSelect4);
		botonCrouch.setOnClickListener(new OnCrouchClickListener());
		botonAttack = (ImageButton) rootView.findViewById(R.id.imageButtonSelect5);
		botonAttack.setOnClickListener(new OnAttackClickListener());
		
		canvas.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event)
			{
				canvas.onTouch(view, event);
				actualizarBotones();
				
				return true;
			}
		});
		
        return rootView;
    }
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
		botonCamara = null;
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
	
	private void actualizarBotones()
	{
		if(estadoCamara)
		{
			botonCamara.setBackgroundResource(R.drawable.icon_camera);
		}
		else
		{
			botonCamara.setBackgroundResource(R.drawable.icon_social_picture);
		}
	}
	
	private class OnCamaraClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(estadoCamara)
			{
				// Captura
				Bitmap bitmap = canvas.capturaPantalla();
				if(manager.guardarImagen(bitmap, nombreActual))
				{
					connector.publicar(getString(R.string.text_social_photo_initial)+" "+nombreActual+" "+getString(R.string.text_social_photo_final), manager.cargarImagen(nombreActual));					
				}
				else
				{
					Toast.makeText(getActivity(), R.string.error_picture_character, Toast.LENGTH_SHORT).show();
				}
				pager.setSwipeable(true);
			}
			else
			{
				//Retoque
				canvas.retoquePantalla();
				pager.setSwipeable(false);
			}			
			
			estadoCamara = !estadoCamara;
			actualizarBotones();
		}
	}
	
	private class OnRunClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.selecionarRun();
		}
	}
	
	private class OnJumpClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.selecionarJump();
		}
	}
	
	private class OnCrouchClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.selecionarCrouch();
		}
	}
	
	private class OnAttackClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.selecionarAttack();
		}
	}
}

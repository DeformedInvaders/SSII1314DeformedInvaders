package com.creation.deform;

import java.util.Iterator;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.view.ViewPagerFragment;
import com.android.view.ViewPagerSwipeable;
import com.creation.data.Movimientos;
import com.creation.data.TTipoMovimiento;
import com.game.data.Personaje;
import com.lib.buffer.VertexArray;
import com.project.main.R;
import com.project.model.GamePreferences;
import com.project.model.GameResources;

public class DeformationFragment extends ViewPagerFragment implements OnDeformationListener
{
	private AnimationFragmentListener mCallback;

	private ImageButton botonReady;
	
	private Personaje personaje;
	private Movimientos movimientos;

	/* Constructora */

	public static final DeformationFragment newInstance(AnimationFragmentListener c, Personaje p)
	{
		DeformationFragment fragment = new DeformationFragment();
		fragment.setParameters(c, p);
		return fragment;
	}

	private void setParameters(AnimationFragmentListener c, Personaje p)
	{
		mCallback = c;
		personaje = p;
		movimientos = new Movimientos();
	}

	public interface AnimationFragmentListener
	{
		public void onAnimationReady(final Movimientos movimientos);
		public void onAnimationStartRecording(final TTipoMovimiento movimiento);
		public void onAnimationStopRecording();
		public void onAnimationDiscardRecording(final TTipoMovimiento movimiento);
		public void onAnimationPlaySound(final TTipoMovimiento movimiento);
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_creation_animation_layout, container, false);

		// Instanciar Elementos de la GUI
		botonReady = (ImageButton) rootView.findViewById(R.id.imageButtonAnimation1);
		botonReady.setOnClickListener(new OnReadyClickListener());

		viewPager = (ViewPagerSwipeable) rootView.findViewById(R.id.pagerViewAnimation1);
		viewPager.setAdapter(this, getActivity().getSupportFragmentManager(), getActivity().getActionBar());
		viewPager.setSwipeable(false);

		TTipoMovimiento[] movimientos = TTipoMovimiento.values();
		for(int i = 0; i < GamePreferences.NUM_TYPE_MOVIMIENTOS; i++)
		{
			viewPager.addView(DeformFragment.newInstance(this, personaje), getString(movimientos[i].getTitle()));
		}

		sendAlertMessage(R.string.text_tip_deform_handles_title, R.string.text_tip_deform_handles_description, GameResources.VIDEO_DEFORM_HANDLES_PATH);
		
		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		botonReady = null;
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		
		mCallback = null;
		personaje = null;
		movimientos = null;
	}

	/* Métodos Listener onClick */

	private void actualizarMovimientos()
	{
		int i = 0;
		Iterator<DeformFragment> it = viewPager.iterator();
		while (it.hasNext())
		{
			List<VertexArray> movimiento = it.next().getMovimientos();

			if (movimiento != null && movimiento.size() > 0)
			{
				movimientos.set(movimiento, TTipoMovimiento.values()[i]);
			}

			i++;
		}
	}

	private class OnReadyClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			actualizarMovimientos();

			if (!movimientos.isReady())
			{
				sendMessage(R.string.text_tip_problem_title, R.string.text_tip_deform_undefined_description, GameResources.VIDEO_DEFORM_UNDEFINED_PATH, R.string.error_deform);
			}
			else
			{
				mCallback.onAnimationReady(movimientos);
			}
		}
	}

	/* Métodos Abstractos ViewPagerFragment */

	@Override
	public void onPageSelected(int position)
	{
		actualizarMovimientos();
	}
	
	/* Métodos Abstractos OnDeformListener */

	@Override
	public void onStartRecording()
	{
		mCallback.onAnimationStartRecording(TTipoMovimiento.values()[viewPager.getPosition()]);
	}

	@Override
	public void onStopRecording()
	{
		mCallback.onAnimationStopRecording();	
	}

	@Override
	public void onDiscardRecording()
	{
		mCallback.onAnimationDiscardRecording(TTipoMovimiento.values()[viewPager.getPosition()]);	
	}

	@Override
	public void onPlaySound()
	{
		mCallback.onAnimationPlaySound(TTipoMovimiento.values()[viewPager.getPosition()]);
	}
}

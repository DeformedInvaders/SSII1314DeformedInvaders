package com.creation.deform;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.storage.ExternalStorageManager;
import com.android.view.ViewPagerFragment;
import com.android.view.ViewPagerSwipeable;
import com.creation.data.Esqueleto;
import com.creation.data.Movimientos;
import com.creation.data.TTipoMovimiento;
import com.creation.data.Textura;
import com.lib.utils.FloatArray;
import com.project.main.GamePreferences;
import com.project.main.R;

public class DeformationFragment extends ViewPagerFragment
{
	private AnimationFragmentListener mCallback;

	private ImageButton botonReady;

	private Esqueleto esqueleto;
	private Textura textura;

	private Movimientos movimientos;
	private ExternalStorageManager manager;

	/* Constructora */

	public static final DeformationFragment newInstance(Esqueleto e, Textura t, ExternalStorageManager m)
	{
		DeformationFragment fragment = new DeformationFragment();
		fragment.setParameters(e, t, m);
		return fragment;
	}

	private void setParameters(Esqueleto e, Textura t, ExternalStorageManager m)
	{
		esqueleto = e;
		textura = t;
		manager = m;
	}

	public interface AnimationFragmentListener
	{
		public void onAnimationReadyButtonClicked(Movimientos m);
	}

	/* Métodos Fragment */

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mCallback = (AnimationFragmentListener) activity;
		movimientos = new Movimientos();
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
		View rootView = inflater.inflate(R.layout.fragment_creation_animation_layout, container, false);

		// Instanciar Elementos de la GUI
		botonReady = (ImageButton) rootView.findViewById(R.id.imageButtonAnimation1);
		botonReady.setOnClickListener(new OnReadyClickListener());

		viewPager = (ViewPagerSwipeable) rootView.findViewById(R.id.pagerViewAnimation1);
		viewPager.setAdapter(this, getActivity().getSupportFragmentManager(), getActivity().getActionBar());
		viewPager.setSwipeable(false);

		viewPager.addView(DeformFragment.newInstance(manager, esqueleto, textura, getString(R.string.title_animation_section_run)), getString(R.string.title_animation_section_run));
		viewPager.addView(DeformFragment.newInstance(manager, esqueleto, textura, getString(R.string.title_animation_section_jump)), getString(R.string.title_animation_section_jump));
		viewPager.addView(DeformFragment.newInstance(manager, esqueleto, textura, getString(R.string.title_animation_section_crouch)), getString(R.string.title_animation_section_crouch));
		viewPager.addView(DeformFragment.newInstance(manager, esqueleto, textura, getString(R.string.title_animation_section_attack)), getString(R.string.title_animation_section_attack));

		sendAlertMessage(R.string.text_tip_deform_handles_title, R.string.text_tip_deform_handles_description, GamePreferences.VIDEO_DEFORM_HANDLES_PATH);
		
		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		botonReady = null;
	}

	/* Métodos Listener onClick */

	private void actualizarMovimientos()
	{
		int i = 0;
		Iterator<DeformFragment> it = viewPager.iterator();
		while (it.hasNext())
		{
			List<FloatArray> movimiento = it.next().getMovimientos();

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
				sendMessage(R.string.text_tip_problem_title, R.string.text_tip_deform_undefined_description, GamePreferences.VIDEO_DEFORM_UNDEFINED_PATH, R.string.error_deform);
			}
			else
			{
				mCallback.onAnimationReadyButtonClicked(movimientos);
			}
		}
	}

	/* Métodos Abstractos ViewPagerFragment */

	@Override
	public void onPageSelected(int position)
	{
		actualizarMovimientos();
	}
}

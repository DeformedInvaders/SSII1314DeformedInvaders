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
import com.android.view.ViewPagerSwipeable;
import com.android.view.ViewPagerFragment;
import com.creation.data.Esqueleto;
import com.creation.data.Movimientos;
import com.creation.data.Textura;
import com.lib.utils.FloatArray;
import com.project.main.R;

public class AnimationFragment extends ViewPagerFragment
{
	private AnimationFragmentListener mCallback;
	
	private ImageButton botonReady;
	
	private Esqueleto esqueleto;
	private Textura textura;
	
	private Movimientos movimientos;
	private ExternalStorageManager manager;
	
	/* SECTION Constructora */
	
	public static final AnimationFragment newInstance(Esqueleto e, Textura t, ExternalStorageManager m)
	{
		AnimationFragment fragment = new AnimationFragment();
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
	
	/* SECTION Métodos Fragment */
	
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
		
		viewPager.addView(DeformFragment.newInstance(manager, esqueleto, textura, getString(R.string.title_animation_section_run), TDeformTipo.Run, 18), getString(R.string.title_animation_section_run));
		viewPager.addView(DeformFragment.newInstance(manager, esqueleto, textura, getString(R.string.title_animation_section_jump), TDeformTipo.Jump, 34), getString(R.string.title_animation_section_jump));
		viewPager.addView(DeformFragment.newInstance(manager, esqueleto, textura, getString(R.string.title_animation_section_crouch), TDeformTipo.Crouch, 34), getString(R.string.title_animation_section_crouch));
		viewPager.addView(DeformFragment.newInstance(manager, esqueleto, textura, getString(R.string.title_animation_section_attack), TDeformTipo.Attack, 18), getString(R.string.title_animation_section_attack));

        return rootView;
    }
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
		botonReady = null;
	}
	
	/* SECTION Métodos Listener onClick */
	
	private void actualizarMovimientos()
	{
		int i = 0;
		Iterator<DeformFragment> it = viewPager.iterator();
		while(it.hasNext())
		{
			List<FloatArray> movimiento = it.next().getMovimientos();
			
			if(movimiento != null && movimiento.size() > 0)
			{
				movimientos.set(movimiento, i);
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
			
			mCallback.onAnimationReadyButtonClicked(movimientos);
		}
    }
    
    /* SECTION Métodos Abstractos ViewPagerFragment */
    
    @Override
    public void onPageSelected(int position)
    {
    	actualizarMovimientos();
    }
}

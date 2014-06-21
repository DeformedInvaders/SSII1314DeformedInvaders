package com.creation.deform;

import java.util.Iterator;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.android.view.IconImageButton;
import com.android.view.ViewFrameFragment;
import com.android.view.ViewFrameSwipeable;
import com.creation.data.Movements;
import com.creation.data.TTypeMovement;
import com.game.data.Character;
import com.lib.buffer.VertexArray;
import com.main.model.GamePreferences;
import com.main.model.GameResources;
import com.project.main.R;

public class DeformationFragment extends ViewFrameFragment implements OnDeformationListener
{
	private AnimationFragmentListener mCallback;

	private IconImageButton botonReady;
	
	private Character personaje;
	private Movements movimientos;
	private int personajeIndice;

	/* Constructora */

	public static final DeformationFragment newInstance(AnimationFragmentListener c, Character p)
	{
		DeformationFragment fragment = new DeformationFragment();
		fragment.setParameters(c, p, -1);
		return fragment;
	}
	
	public static final DeformationFragment newInstance(AnimationFragmentListener c, Character p, int n)
	{
		DeformationFragment fragment = new DeformationFragment();
		fragment.setParameters(c, p, n);
		return fragment;
	}

	private void setParameters(AnimationFragmentListener c, Character p, int n)
	{
		mCallback = c;
		personaje = p;
		personajeIndice = n;
		
		if (personajeIndice == -1)
		{
			movimientos = new Movements();
		}
		else
		{
			movimientos = personaje.getMovements();
		}
	}

	public interface AnimationFragmentListener
	{
		public void onDeformationReady(final Movements movimientos);
		public void onRedeformationReady(final Movements movimientos, int indice);
		public void onDeformationPlaySoundEffect(int sound);
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_creation_animation_layout, container, false);

		// Instanciar Elementos de la GUI
		botonReady = (IconImageButton) rootView.findViewById(R.id.imageButtonAnimation1);
		botonReady.setOnClickListener(new OnReadyClickListener());
		
		frameLayout = (ViewFrameSwipeable) rootView.findViewById(R.id.frameViewAnimation1);
		frameLayout.setAdapter(this, getActivity().getSupportFragmentManager(), getActivity().getActionBar());		
		
		TTypeMovement[] movimientos = TTypeMovement.values();
		for(int i = 0; i < GamePreferences.NUM_TYPE_MOVIMIENTOS; i++)
		{
			frameLayout.addView(DeformFragment.newInstance(this, personaje, movimientos[i]), getString(movimientos[i].getTitle()));
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
		Iterator<DeformFragment> it = frameLayout.iterator();
		while (it.hasNext())
		{
			List<VertexArray> movimiento = it.next().getMovimientos();

			if (movimiento != null && movimiento.size() > 0)
			{
				movimientos.set(movimiento, TTypeMovement.values()[i]);
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
				if (personajeIndice == -1)
				{
					mCallback.onDeformationReady(movimientos);
				}
				else
				{
					mCallback.onRedeformationReady(movimientos, personajeIndice);
				}
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
	public void onPlaySoundEffect()
	{
		int sound = TTypeMovement.values()[frameLayout.getPosition()].getSound();
		if (sound != -1)
		{
			mCallback.onDeformationPlaySoundEffect(sound);
		}
	}
}

package com.test.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.view.SwipeableViewPager;
import com.project.main.R;
import com.test.audio.AudioFragment;
import com.test.multitouch.MultitouchFragment;
import com.test.social.SocialFragment;

public class TestFragment extends Fragment
{
	private SwipeableViewPager viewPager;
	
	/* Constructora */
	
	public static final TestFragment newInstance()
	{
		TestFragment fragment = new TestFragment();
		return fragment;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{		
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_test_layout, container, false);
		
		// Instanciar Elementos de la GUI

		viewPager = (SwipeableViewPager) rootView.findViewById(R.id.pagerViewTest1);
		viewPager.setAdapter(getActivity().getSupportFragmentManager(), getActivity().getActionBar());
		viewPager.setSwipeable(false);
		
		viewPager.addView(MultitouchFragment.newInstance(), getString(R.string.title_test_section_multitouch));
		viewPager.addView(AudioFragment.newInstance(), getString(R.string.title_test_section_audio));
		viewPager.addView(SocialFragment.newInstance(), getString(R.string.title_test_secton_social));

        return rootView;
    }
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
		viewPager = null;
	}
}

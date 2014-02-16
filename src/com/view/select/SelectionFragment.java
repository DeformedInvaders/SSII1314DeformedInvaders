package com.view.select;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.storage.ExternalStorageManager;
import com.project.data.Personaje;
import com.project.main.R;

public class SelectionFragment extends Fragment
{
	private ExternalStorageManager manager;
	
	private ActionBar actionBar;
	private SelectionFragmentListener mCallback;
	
	private ImageButton botonReady, botonDelete;
	private SectionViewPagerAdapter pageAdapter;
	private ViewPager viewPager;
	private List<SelectFragment> listaFragmentos;
	
	private List<Personaje> listaPersonajes;
	
	/* Constructora */
	
	public static final SelectionFragment newInstance(List<Personaje> lista)
	{
		SelectionFragment fragment = new SelectionFragment();
		fragment.setParameters(lista);
		return fragment;
	}
	
	private void setParameters(List<Personaje> lista)
	{
		listaPersonajes = lista;
	}
	
	public interface SelectionFragmentListener
	{
        public void onSelectionSelectClicked(int indice);
        public void onSelectionDeleteButtonClicked(int indice);
    }
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mCallback = (SelectionFragmentListener) activity;
		
		manager = new ExternalStorageManager();
		
		listaFragmentos = new ArrayList<SelectFragment>();
		
		Iterator<Personaje> it = listaPersonajes.iterator();
		while(it.hasNext())
		{
			Personaje p = it.next();
			listaFragmentos.add(SelectFragment.newInstance(p.getEsqueleto(), p.getTextura(), p.getNombre(), manager));
		}
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		mCallback = null;

		manager = null;
		listaFragmentos = null;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_selection_layout, container, false);
		
		// Instanciar Elementos de la GUI
		botonReady = (ImageButton) rootView.findViewById(R.id.imageButtonSelection1);
		botonDelete = (ImageButton) rootView.findViewById(R.id.imageButtonSelection2);
		
		botonReady.setOnClickListener(new OnReadyClickListener());		
		botonDelete.setOnClickListener(new OnDeleteClickListener());
		
		actionBar = getActivity().getActionBar();

		pageAdapter = new SectionViewPagerAdapter(getActivity().getSupportFragmentManager());

		viewPager = (ViewPager) rootView.findViewById(R.id.pagerViewSelection1);
		viewPager.removeAllViews();
		viewPager.setAdapter(pageAdapter);

		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position)
			{
				actionBar.setSelectedNavigationItem(position);
			}
		});

		for(int i = 0; i < pageAdapter.getCount(); i++)
		{
			actionBar.addTab(actionBar.newTab().setText(pageAdapter.getPageTitle(i)).setTabListener(pageAdapter));
		}
			
        return rootView;
    }
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
		actionBar = null;
		botonReady = null;
		botonDelete = null;
		pageAdapter = null;
		viewPager = null;
	}
	
	/* Listeners de Botones */
	 
	private class OnReadyClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			ActionBar actionBar = getActivity().getActionBar();
			mCallback.onSelectionSelectClicked(actionBar.getSelectedNavigationIndex());
		}
    }
    
    private class OnDeleteClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			ActionBar actionBar = getActivity().getActionBar();
			mCallback.onSelectionDeleteButtonClicked(actionBar.getSelectedNavigationIndex());
		}
    }
    
    /* Adaptador de ViewPager */
	
	public class SectionViewPagerAdapter extends FragmentStatePagerAdapter implements ActionBar.TabListener
	{
		public SectionViewPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			if(position >= 0 && position < listaFragmentos.size())
			{
				return listaFragmentos.get(position);
			}
			
			return null;
		}

		@Override
		public int getCount()
		{
			return listaFragmentos.size();
		}

		@Override
		public CharSequence getPageTitle(int position)
		{			
			if(position >= 0 && position < listaFragmentos.size())
			{
				return listaPersonajes.get(position).getNombre();
			}
			
			return null;
		}
		
		@Override
		public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
		{
			viewPager.setCurrentItem(tab.getPosition());
		}
	
		@Override
		public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) { }
	
		@Override
		public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) { }
	}
}

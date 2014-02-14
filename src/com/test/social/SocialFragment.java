package com.test.social;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.dialog.ConnectDialog;
import com.android.dialog.ShareDialog;
import com.project.main.R;

public class SocialFragment extends Fragment
{
	private Context mContext;
	
	private ImageButton botonTwitter, botonFacebook, botonShare;
	private boolean twitterConectado, facebookConectado;
	
	private ConnectDialog twitterDialog, facebookDialog;
	private ShareDialog shareDialog;

	public static final SocialFragment newInstance()
	{
		SocialFragment fragment = new SocialFragment();
		return fragment;
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mContext = activity.getApplicationContext();
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		mContext = null;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{        
		View rootView = inflater.inflate(R.layout.fragment_social_layout, container, false);
		
		twitterConectado = false;
		facebookConectado = false;
		
		botonTwitter = (ImageButton) rootView.findViewById(R.id.imageButtonSocial1);
		botonFacebook = (ImageButton) rootView.findViewById(R.id.imageButtonSocial2);
		botonShare = (ImageButton) rootView.findViewById(R.id.imageButtonSocial3);
		
		botonTwitter.setOnClickListener(new OnTwitterClickListener());
		botonFacebook.setOnClickListener(new OnFacebookClickListener());
		botonShare.setOnClickListener(new OnShareClickListener());
		
		actualizarBotones();		
        return rootView;
    }
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
		botonTwitter = null;
		botonFacebook = null;
		botonShare = null;
	}
	
	private void actualizarBotones()
	{
		if(twitterConectado || facebookConectado)
		{
			botonShare.setVisibility(View.VISIBLE);
		}
		else
		{
			botonShare.setVisibility(View.INVISIBLE);
		}
	}
	
	/* Conexión - Desconexión */
	
	public void conectar(String name, String password, int id)
	{
		if(id == R.string.title_dialog_twitter)
		{
			conectarTwitter(name, password);
		}
		else if(id == R.string.title_dialog_facebook)
		{
			conectarFacebook(name, password);
		}
	}
	
	private void conectarTwitter(String name, String password)
	{
		twitterConectado = true;
		botonTwitter.setBackgroundResource(R.drawable.icon_social_twitter_connected);

		// TODO Conectar Twitter
		Log.d("TEST", "Twitter Conectado: "+name+" "+password);
		actualizarBotones();
	}
	
	private void desconectarTwitter()
	{
		twitterConectado = false;
		botonTwitter.setBackgroundResource(R.drawable.icon_social_twitter);

		// TODO Desconectar Twitter
		Log.d("TEST", "Twitter Desconectado");
		actualizarBotones();
	}
	
	private void conectarFacebook(String name, String password)
	{
		facebookConectado = true;
		botonFacebook.setBackgroundResource(R.drawable.icon_social_facebook_connected);
		
		// TODO Conectar Facebook
		Log.d("TEST", "Facebook Conectado: "+name+" "+password);
		actualizarBotones();
	}
	
	private void desconectarFacebook()
	{
		facebookConectado = false;
		botonFacebook.setBackgroundResource(R.drawable.icon_social_facebook);
		
		// TODO Desconectar Facebook
		Log.d("TEST", "Facebook Desconectado");
		actualizarBotones();
	}
	
	/* Publicación */
	
	public void publicar(String text)
	{
		if(twitterConectado)
		{
			publicarTwitter(text);
		}
		
		if(facebookConectado)
		{
			publicarFacebook(text);
		}
	}
	
	private void publicarTwitter(String text)
	{
		// TODO Publicar Twitter
		Log.d("TEST", "Twitter Publicado: "+text);
	}
	
	private void publicarFacebook(String text)
	{
		// TODO Publicar Facebook
		Log.d("TEST", "Facebook Publicado: "+text);
	}
	
	private class OnTwitterClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(twitterConectado)
			{
				desconectarTwitter();
			}
			else
			{
				if(twitterDialog == null)
				{
					twitterDialog = new ConnectDialog(mContext, SocialFragment.this, R.string.title_dialog_twitter);
				}
				twitterDialog.show(v);
			}
		}
	}
	
	private class OnFacebookClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(facebookConectado)
			{
				desconectarFacebook();
			}
			else
			{
				if(facebookDialog == null)
				{
					facebookDialog = new ConnectDialog(mContext, SocialFragment.this, R.string.title_dialog_facebook);
				}
				facebookDialog.show(v);
			}
		}
	}
	
	private class OnShareClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(shareDialog == null)
			{
				shareDialog = new ShareDialog(mContext, SocialFragment.this);
			}
			shareDialog.show(v);
		}
	}
}

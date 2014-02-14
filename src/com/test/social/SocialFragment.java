package com.test.social;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.alert.AlertDialogTextInput;
import com.project.main.R;
import com.temboo.Library.Twitter.OAuth.FinalizeOAuth;
import com.temboo.Library.Twitter.OAuth.FinalizeOAuth.FinalizeOAuthInputSet;
import com.temboo.Library.Twitter.OAuth.FinalizeOAuth.FinalizeOAuthResultSet;
import com.temboo.Library.Twitter.OAuth.InitializeOAuth;
import com.temboo.Library.Twitter.OAuth.InitializeOAuth.InitializeOAuthInputSet;
import com.temboo.Library.Twitter.OAuth.InitializeOAuth.InitializeOAuthResultSet;
import com.temboo.Library.Twitter.Tweets.StatusesUpdate;
import com.temboo.Library.Twitter.Tweets.StatusesUpdate.StatusesUpdateInputSet;
import com.temboo.core.TembooException;
import com.temboo.core.TembooSession;

public class SocialFragment extends Fragment
{
	private TembooSession session;
	
	private TTwitterEstado estadoTwitter;
	
	private ImageButton botonTwitter, botonFacebook, botonShare;
	private boolean facebookConectado;
	private WebView webView;
	
	/* Twitter */	
	private InitializeOAuthResultSet twitterOAuthInicial;
	private FinalizeOAuthResultSet twitterOAuthFinal;

	public static final SocialFragment newInstance()
	{
		SocialFragment fragment = new SocialFragment();
		return fragment;
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		try
		{
			session = new TembooSession(SocialInformation.ACCOUNT_NAME, SocialInformation.APP_NAME, SocialInformation.APP_KEY);
		}
		catch (TembooException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		
		session = null;
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{        
		View rootView = inflater.inflate(R.layout.fragment_social_layout, container, false);
		
		estadoTwitter = TTwitterEstado.Desconectado;
		facebookConectado = false;
		
		botonTwitter = (ImageButton) rootView.findViewById(R.id.imageButtonSocial1);
		botonFacebook = (ImageButton) rootView.findViewById(R.id.imageButtonSocial2);
		botonShare = (ImageButton) rootView.findViewById(R.id.imageButtonSocial3);
		
		botonTwitter.setOnClickListener(new OnTwitterClickListener());
		botonFacebook.setOnClickListener(new OnFacebookClickListener());
		botonShare.setOnClickListener(new OnShareClickListener());
		
		webView = (WebView) rootView.findViewById(R.id.webViewSocial1);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new webClient());
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		actualizarBotones();		
        return rootView;
    }
	
	private class webClient extends WebViewClient
	{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            return false;
        }
        
        @Override  
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);
            
            if(estadoTwitter == TTwitterEstado.OAuth)
            {
            	conectarTwitterFinal();
            }
        }  

    }
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
		botonTwitter = null;
		botonFacebook = null;
		botonShare = null;
		webView = null;
	}
	
	private void actualizarBotones()
	{
		if(estadoTwitter == TTwitterEstado.Conectado || facebookConectado)
		{
			botonShare.setVisibility(View.VISIBLE);
		}
		else
		{
			botonShare.setVisibility(View.INVISIBLE);
		}
	}
	
	/* Conexión - Desconexión */
	
	private void conectarTwitterInicial()
	{		
		// Instantiate the Choreo, using a previously instantiated TembooSession object, eg:
		InitializeOAuth initializeOAuthChoreo = new InitializeOAuth(session);

		// Get an InputSet object for the choreo
		InitializeOAuthInputSet initializeOAuthInputs = initializeOAuthChoreo.newInputSet();
		initializeOAuthInputs.set_ConsumerSecret(SocialInformation.CONSUMER_SECRET);
		initializeOAuthInputs.set_ConsumerKey(SocialInformation.CONSUMER_KEY);

		// Execute Choreo
		try
		{
			twitterOAuthInicial = initializeOAuthChoreo.execute(initializeOAuthInputs);	
			
			estadoTwitter = TTwitterEstado.OAuth;
			webView.loadUrl(twitterOAuthInicial.get_AuthorizationURL());
		}
		catch (TembooException e)
		{
			Toast.makeText(getActivity(), R.string.error_twitter_oauth_permission, Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	private void conectarTwitterFinal()
	{		
		// Instantiate the Choreo, using a previously instantiated TembooSession object, eg:
		FinalizeOAuth finalizeOAuthChoreo = new FinalizeOAuth(session);

		// Get an InputSet object for the choreo
		FinalizeOAuthInputSet finalizeOAuthInputs = finalizeOAuthChoreo.newInputSet();
		finalizeOAuthInputs.set_CallbackID(twitterOAuthInicial.get_CallbackID());
		finalizeOAuthInputs.set_OAuthTokenSecret(twitterOAuthInicial.get_OAuthTokenSecret());
		finalizeOAuthInputs.set_ConsumerSecret(SocialInformation.CONSUMER_SECRET);
		finalizeOAuthInputs.set_ConsumerKey(SocialInformation.CONSUMER_KEY);

		// Execute Choreo
		try
		{
			twitterOAuthFinal = finalizeOAuthChoreo.execute(finalizeOAuthInputs);
			
			estadoTwitter = TTwitterEstado.Conectado;
			botonTwitter.setBackgroundResource(R.drawable.icon_social_twitter_connected);
			actualizarBotones();			
			
			Toast.makeText(getActivity(), R.string.text_twitter_oauth_sign_in, Toast.LENGTH_SHORT).show();
		}
		catch (TembooException e)
		{
			Toast.makeText(getActivity(), R.string.error_twitter_oauth_sign_in, Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	private void desconectarTwitter()
	{
		twitterOAuthInicial = null;
		twitterOAuthFinal = null;	
		
		estadoTwitter = TTwitterEstado.Desconectado;
		botonTwitter.setBackgroundResource(R.drawable.icon_social_twitter);
		actualizarBotones();
	}
	
	private void conectarFacebook()
	{
		facebookConectado = true;
		botonFacebook.setBackgroundResource(R.drawable.icon_social_facebook_connected);
		
		// TODO Conectar Facebook
		Log.d("TEST", "Facebook Conectado");
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
		if(estadoTwitter == TTwitterEstado.Conectado)
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
		// Instantiate the Choreo, using a previously instantiated TembooSession object, eg:
		StatusesUpdate statusesUpdateChoreo = new StatusesUpdate(session);

		// Get an InputSet object for the choreo
		StatusesUpdateInputSet statusesUpdateInputs = statusesUpdateChoreo.newInputSet();
		statusesUpdateInputs.set_AccessToken(twitterOAuthFinal.get_AccessToken());
		statusesUpdateInputs.set_AccessTokenSecret(twitterOAuthFinal.get_AccessTokenSecret());
		statusesUpdateInputs.set_ConsumerSecret(SocialInformation.CONSUMER_SECRET);
		statusesUpdateInputs.set_ConsumerKey(SocialInformation.CONSUMER_KEY);
		statusesUpdateInputs.set_StatusUpdate(text);

		// Execute Choreo
		try
		{
			statusesUpdateChoreo.execute(statusesUpdateInputs);
			Toast.makeText(getActivity(), R.string.text_twitter_tweet, Toast.LENGTH_SHORT).show();
		}
		catch (TembooException e)
		{
			Toast.makeText(getActivity(), R.string.error_twitter_tweet, Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
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
			switch(estadoTwitter)
			{
				case Desconectado:
					conectarTwitterInicial();
				break;
				case Conectado:
					desconectarTwitter();
				break;
				default:
				break;
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
				conectarFacebook();
			}
		}
	}
	
	private class OnShareClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			AlertDialogTextInput alert = new AlertDialogTextInput(getActivity(), getString(R.string.text_social_title), getString(R.string.text_social_description), getString(R.string.text_button_tweet), getString(R.string.text_button_cancel)) {

				@Override
				public void onPossitiveButtonClick()
				{
					publicar(getText()+" "+getString(R.string.text_social_via));
				}

				@Override
				public void onNegativeButtonClick() { }
				
			};

			alert.show();
		}
	}
}

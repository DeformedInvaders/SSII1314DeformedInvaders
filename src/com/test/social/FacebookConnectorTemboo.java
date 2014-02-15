package com.test.social;

import android.net.Uri;

import com.temboo.Library.Facebook.OAuth.FinalizeOAuth;
import com.temboo.Library.Facebook.OAuth.FinalizeOAuth.FinalizeOAuthInputSet;
import com.temboo.Library.Facebook.OAuth.FinalizeOAuth.FinalizeOAuthResultSet;
import com.temboo.Library.Facebook.OAuth.InitializeOAuth;
import com.temboo.Library.Facebook.OAuth.InitializeOAuth.InitializeOAuthInputSet;
import com.temboo.Library.Facebook.OAuth.InitializeOAuth.InitializeOAuthResultSet;
import com.temboo.Library.Facebook.Publishing.SetStatus;
import com.temboo.Library.Facebook.Publishing.SetStatus.SetStatusInputSet;
import com.temboo.core.TembooException;
import com.temboo.core.TembooSession;

public class FacebookConnectorTemboo implements SocialConnector
{
	private TembooSession session;
	
	private InitializeOAuthResultSet facebookOAuthInicial;
	private FinalizeOAuthResultSet facebookOAuthFinal;
	
	public FacebookConnectorTemboo()
	{
		try
		{
			session = new TembooSession(SocialInformation.TEMBOO_ACCOUNT_NAME, SocialInformation.TEMBOO_APP_NAME, SocialInformation.TEMBOO_APP_KEY);
		}
		catch (TembooException e)
		{
			e.printStackTrace();
		}
	}
	
	public FacebookConnectorTemboo(TembooSession session)
	{
		this.session = session;
	}
	
	@Override
	public boolean iniciarAutorizacion()
	{
		// Instantiate the Choreo, using a previously instantiated TembooSession object, eg:
		InitializeOAuth initializeOAuthChoreo = new InitializeOAuth(session);

		// Get an InputSet object for the choreo
		InitializeOAuthInputSet initializeOAuthInputs = initializeOAuthChoreo.newInputSet();
		initializeOAuthInputs.set_AppID(SocialInformation.FACEBOOK_APP_ID);
		initializeOAuthInputs.set_ForwardingURL(SocialInformation.FACEBOOK_CALLBACK_URL);

		// Execute Choreo
		try
		{
			facebookOAuthInicial = initializeOAuthChoreo.execute(initializeOAuthInputs);
			return true;
		}
		catch (TembooException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	public String getAuthorizationURL()
	{
		return facebookOAuthInicial.get_AuthorizationURL();
	}

	@Override
	public boolean finalizarAutorizacion(Uri uri)
	{
		// Instantiate the Choreo, using a previously instantiated TembooSession object, eg:
		FinalizeOAuth finalizeOAuthChoreo = new FinalizeOAuth(session);

		// Get an InputSet object for the choreo
		FinalizeOAuthInputSet finalizeOAuthInputs = finalizeOAuthChoreo.newInputSet();

		// Set inputs
		finalizeOAuthInputs.set_CallbackID(facebookOAuthInicial.get_CallbackID());
		finalizeOAuthInputs.set_AppSecret(SocialInformation.FACEBOOK_APP_SECRET);
		finalizeOAuthInputs.set_AppID(SocialInformation.FACEBOOK_APP_ID);

		// Execute Choreo
		try
		{
			facebookOAuthFinal = finalizeOAuthChoreo.execute(finalizeOAuthInputs);
			return true;
		}
		catch (TembooException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean desconexion()
	{
		facebookOAuthInicial = null;
		facebookOAuthFinal = null;	
		return true;
	}

	@Override
	public boolean enviarPost(String text)
	{
		// Instantiate the Choreo, using a previously instantiated TembooSession object, eg:
		SetStatus setStatusChoreo = new SetStatus(session);

		// Get an InputSet object for the choreo
		SetStatusInputSet setStatusInputs = setStatusChoreo.newInputSet();

		// Set inputs
		setStatusInputs.set_AccessToken(facebookOAuthFinal.get_AccessToken());
		setStatusInputs.set_Message(text);

		// Execute Choreo
		try
		{
			setStatusChoreo.execute(setStatusInputs);
			return true;
		}
		catch (TembooException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
}

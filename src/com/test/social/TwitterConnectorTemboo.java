package com.test.social;

import android.net.Uri;

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

public class TwitterConnectorTemboo implements SocialConnector
{
	private TembooSession session;
	
	private InitializeOAuthResultSet twitterOAuthInicial;
	private FinalizeOAuthResultSet twitterOAuthFinal;
	
	public TwitterConnectorTemboo()
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
	
	public TwitterConnectorTemboo(TembooSession session)
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
		initializeOAuthInputs.set_ConsumerSecret(SocialInformation.TWITTER_CONSUMER_SECRET);
		initializeOAuthInputs.set_ConsumerKey(SocialInformation.TWITTER_CONSUMER_KEY);
		initializeOAuthInputs.set_ForwardingURL(SocialInformation.TWITTER_CALLBACK_URL);

		// Execute Choreo
		try
		{
			twitterOAuthInicial = initializeOAuthChoreo.execute(initializeOAuthInputs);
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
		return twitterOAuthInicial.get_AuthorizationURL();
	}
	
	@Override
	public boolean finalizarAutorizacion(Uri uri)
	{
		// Instantiate the Choreo, using a previously instantiated TembooSession object, eg:
		FinalizeOAuth finalizeOAuthChoreo = new FinalizeOAuth(session);

		// Get an InputSet object for the choreo
		FinalizeOAuthInputSet finalizeOAuthInputs = finalizeOAuthChoreo.newInputSet();
		finalizeOAuthInputs.set_CallbackID(twitterOAuthInicial.get_CallbackID());
		finalizeOAuthInputs.set_OAuthTokenSecret(twitterOAuthInicial.get_OAuthTokenSecret());
		finalizeOAuthInputs.set_ConsumerSecret(SocialInformation.TWITTER_CONSUMER_SECRET);
		finalizeOAuthInputs.set_ConsumerKey(SocialInformation.TWITTER_CONSUMER_KEY);

		// Execute Choreo
		try
		{
			twitterOAuthFinal = finalizeOAuthChoreo.execute(finalizeOAuthInputs);
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
		twitterOAuthInicial = null;
		twitterOAuthFinal = null;	
		return true;
	}

	@Override
	public boolean enviarPost(String text)
	{
		// Instantiate the Choreo, using a previously instantiated TembooSession object, eg:
		StatusesUpdate statusesUpdateChoreo = new StatusesUpdate(session);

		// Get an InputSet object for the choreo
		StatusesUpdateInputSet statusesUpdateInputs = statusesUpdateChoreo.newInputSet();
		statusesUpdateInputs.set_AccessToken(twitterOAuthFinal.get_AccessToken());
		statusesUpdateInputs.set_AccessTokenSecret(twitterOAuthFinal.get_AccessTokenSecret());
		statusesUpdateInputs.set_ConsumerSecret(SocialInformation.TWITTER_CONSUMER_SECRET);
		statusesUpdateInputs.set_ConsumerKey(SocialInformation.TWITTER_CONSUMER_KEY);
		statusesUpdateInputs.set_StatusUpdate(text);

		// Execute Choreo
		try
		{
			statusesUpdateChoreo.execute(statusesUpdateInputs);
			return true;
		}
		catch (TembooException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
}

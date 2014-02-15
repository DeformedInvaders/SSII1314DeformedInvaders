package com.test.social;

import android.net.Uri;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.auth.AccessToken;

public class FacebookConnectorFacebook4J implements SocialConnector
{
	private static final String URL_FACEBOOK_OAUTH_VERIFIER = "code";
	
	private Facebook facebook;

	@Override
	public boolean iniciarAutorizacion()
	{
        facebook = new FacebookFactory().getInstance();
        facebook.setOAuthAppId(SocialInformation.FACEBOOK_APP_ID, SocialInformation.FACEBOOK_APP_SECRET);
        facebook.setOAuthPermissions("publish_actions");
        facebook.setOAuthAccessToken(new AccessToken(SocialInformation.FACEBOOK_CLIENT_TOKEN, null));

        return true;
	}
	
	@Override
	public String getAuthorizationURL()
	{
		return facebook.getOAuthAuthorizationURL(SocialInformation.FACEBOOK_CALLBACK_URL);
	}
	
	@Override
	public boolean finalizarAutorizacion(Uri uri)
	{
		try
		{
			facebook.getOAuthAccessToken(uri.getQueryParameter(URL_FACEBOOK_OAUTH_VERIFIER));
			return true;
		}
		catch (FacebookException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean desconexion()
	{		
		return true;
	}

	@Override
	public boolean enviarPost(String text)
	{
        try
        {
        	facebook.postStatusMessage(text);
			return true;
		}
        catch (FacebookException e)
        {
			e.printStackTrace();
		}
        
		return true;
	}	
}

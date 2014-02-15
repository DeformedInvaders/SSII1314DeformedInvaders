package com.test.social;

import android.net.Uri;

public interface SocialConnector
{
	public boolean iniciarAutorizacion();
	public String getAuthorizationURL();
	public boolean finalizarAutorizacion(Uri uri);
	public boolean desconexion();
	public boolean enviarPost(String text);
}

package com.android.alert;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;

@SuppressLint("SetJavaScriptEnabled")
public abstract class WebAlert extends WindowAlert
{
	private WebView webView;

	/* Constructora */

	public WebAlert(Context context, String title, String textNo)
	{
		super(context, title);

		setNegativeButton(textNo, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				dismiss();
			}
		});

		LinearLayout layout = new LinearLayout(context);

		webView = new WebView(context);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebClient());

		EditText keyboardHack = new EditText(context);
		keyboardHack.setVisibility(View.GONE);

		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(webView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layout.addView(keyboardHack, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		setView(layout);
	}

	/* Métodos Abstractos */

	public abstract boolean evaluarURL(String url);

	/* Métodos Públicos */

	public void loadURL(String url)
	{
		webView.loadUrl(url);
	}

	/* Listener WebClient */

	public class WebClient extends WebViewClient
	{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			return false;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			if (evaluarURL(url))
			{
				super.onPageStarted(view, url, favicon);
			}
		}
	}
}

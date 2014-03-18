package com.project.main;

import java.util.List;

import android.app.ActionBar;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.alert.ConfirmationAlert;
import com.android.alert.TextInputAlert;
import com.android.social.SocialConnector;
import com.android.storage.ExternalStorageManager;
import com.android.storage.InternalStorageManager;
import com.creation.data.Esqueleto;
import com.creation.data.Movimientos;
import com.creation.data.Textura;
import com.creation.deform.AnimationFragment;
import com.creation.design.DesignFragment;
import com.creation.paint.PaintFragment;
import com.game.data.Personaje;
import com.game.game.GameFragment;
import com.game.select.LevelSelectionFragment;
import com.game.select.LevelsGenerator;
import com.project.loading.LoadingFragment;
import com.selection.select.CharacterSelectionFragment;

public class MainActivity extends FragmentActivity implements LoadingFragment.LoadingFragmentListener, MainFragment.MainFragmentListener, DesignFragment.DesignFragmentListener, PaintFragment.PaintFragmentListener, AnimationFragment.AnimationFragmentListener, CharacterSelectionFragment.CharacterSelectionFragmentListener, LevelSelectionFragment.LevelSelectionFragmentListener, GameFragment.GameFragmentListener 
{	
	/* Estructura de Datos */
	private List<Personaje> listaPersonajes;
	private Personaje personajeActual;
	private int personajeSeleccionado;
	private boolean[] estadoNiveles;

	/* Almacenamiento */
	private InternalStorageManager internalManager;
	private ExternalStorageManager externalManager;
	
	/* Conectores Sociales */
	private SocialConnector connector;
	
	/* Elementos de la Interafaz */
	private ActionBar actionBar;
	private MenuItem botonTwitter, botonFacebook;
	
	/* Estado */
	private TEstado estado;
	
	private LevelsGenerator levelGenerator;

	/* SECTION Métodos Activity */
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_layout);
		
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		internalManager = new InternalStorageManager(this);
		externalManager = new ExternalStorageManager();
		connector = new SocialConnector(this);
		levelGenerator = new LevelsGenerator(this);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		changeFragment(LoadingFragment.newInstance(internalManager));
	}
	
	@Override
	public void onBackPressed()
	{
		if(estado != TEstado.Main)// && estado != TEstado.Game)
		{	
			limpiarActionBar();
			
			super.onBackPressed();
			actualizarEstado();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    
	    botonTwitter = menu.getItem(0);
	    botonFacebook = menu.getItem(1);
	    
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
	    switch (item.getItemId())
	    {
	        case R.id.menuIcon1:
	            onMenuTwitterButtonClicked();
	            return true;
	        case R.id.menuIcon2:
	        	onMenuFacebookButtonClicked();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	/* SECTION Métodos de Modificación del FrameLayout */
	
	private void changeFragment(Fragment fragmento)
	{
		boolean clearBackStack = false;
		boolean addToBackStack = true;
		
		if(estado == TEstado.CharacterSelection) addToBackStack = false;
		
		actualizarEstado(fragmento);
		limpiarActionBar();
		
		if(estado == TEstado.Main) clearBackStack = true;
		
		FragmentManager manager = getSupportFragmentManager();
		
		// Limpiar la BackStack
		if(clearBackStack) clearBackStack(manager);
		
		// Reemplazar el Fragmento
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.frameLayoutMain1, fragmento, estado.toString());
		
		// Añadir a la BackStack
		if(addToBackStack) transaction.addToBackStack(estado.toString());
		
		transaction.commit();
	}
	
	private void clearBackStack(FragmentManager manager)
	{
		manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}
	
	/* SECTION Métodos Loading Fragment */
	
	@Override
	public void onLoadingListCharacters(List<Personaje> lista, int seleccionado, boolean[] niveles)
	{
		listaPersonajes = lista;
		personajeSeleccionado = seleccionado;
		estadoNiveles = niveles;
		
		changeFragment(MainFragment.newInstance(listaPersonajes, personajeSeleccionado, externalManager));
	}
	
	/* SECTION Métodos Main Fragment */
	
	@Override
	public void onMainCreateButtonClicked()
	{
		personajeActual = new Personaje();
		
		changeFragment(DesignFragment.newInstance());
	}
	
	@Override
    public void onMainSelectButtonClicked()
    {
    	changeFragment(CharacterSelectionFragment.newInstance(listaPersonajes, externalManager, connector));
    }
    
	@Override
    public void onMainPlayButtonClicked()
    {
		changeFragment(LevelSelectionFragment.newInstance(estadoNiveles));
    }
	
	/* SECTION Métodos Design Fragment */
    
	@Override
    public void onDesignReadyButtonClicked(Esqueleto esqueleto)
    {
    	if(esqueleto == null)
    	{
    		Toast.makeText(getApplication(), R.string.error_design, Toast.LENGTH_SHORT).show();
    	}
    	else
    	{
    		personajeActual.setEsqueleto(esqueleto);    		
    		changeFragment(PaintFragment.newInstance(personajeActual.getEsqueleto()));
    	}
    }
	
	/* SECTION Métodos Paint Fragment */
    
	@Override
    public void onPaintReadyButtonClicked(Textura textura)
    {
    	if(textura == null)
    	{
    		Toast.makeText(getApplication(), R.string.error_paint, Toast.LENGTH_SHORT).show();
    	}
    	else
    	{
    		personajeActual.setTextura(textura);
    		changeFragment(AnimationFragment.newInstance(personajeActual.getEsqueleto(), personajeActual.getTextura(), externalManager));
    	}
    }
    
	/* SECTION Métodos Animation Fragment */
    
	@Override
    public void onAnimationReadyButtonClicked(Movimientos movimientos)
    {
		if(movimientos == null)
		{
			Toast.makeText(getApplication(), R.string.error_animation, Toast.LENGTH_SHORT).show();
		}
		else
		{
			if(movimientos.isReady())
			{
				personajeActual.setMovimientos(movimientos);
			
				TextInputAlert alert = new TextInputAlert(this, getString(R.string.text_save_character_title), getString(R.string.text_save_character_description), getString(R.string.text_button_yes), getString(R.string.text_button_no)) {
	
					@Override
					public void onPossitiveButtonClick(String text)
					{						
						personajeActual.setNombre(text);
						
						if(internalManager.guardarPersonaje(personajeActual))
						{
							externalManager.guardarAudio(text, getString(R.string.title_animation_section_run));
							externalManager.guardarAudio(text, getString(R.string.title_animation_section_jump));
							externalManager.guardarAudio(text, getString(R.string.title_animation_section_crouch));
							externalManager.guardarAudio(text, getString(R.string.title_animation_section_attack));
							
							listaPersonajes.add(personajeActual);
							personajeActual = null;
							
							Toast.makeText(getApplication(), R.string.text_save_character_confirmation, Toast.LENGTH_SHORT).show();
						}
						else
						{
							Toast.makeText(getApplication(), R.string.error_save_character, Toast.LENGTH_SHORT).show();
						}
							
						changeFragment(MainFragment.newInstance(listaPersonajes, personajeSeleccionado, externalManager));
					}
	
					@Override
					public void onNegativeButtonClick(String text)
					{
						changeFragment(MainFragment.newInstance(listaPersonajes, personajeSeleccionado, externalManager));
					}
					
				};
	
				alert.show();
			}
			else
			{
				Toast.makeText(getApplication(), R.string.error_deform, Toast.LENGTH_SHORT).show();
			}
		}
	}
    
	/* SECTION Métodos Character Selection Fragment */
    
	@Override
    public void onCharacterSelectionSelectClicked(int indice)
    {
		personajeSeleccionado = indice;
		
		internalManager.guardarSeleccionado(personajeSeleccionado);
		Toast.makeText(getApplication(), R.string.text_select_character_confirmation, Toast.LENGTH_SHORT).show();

		changeFragment(MainFragment.newInstance(listaPersonajes, personajeSeleccionado, externalManager));
    }
    
	@Override
    public void onCharacterSelectionDeleteButtonClicked(final int indice)
    {   	
    	ConfirmationAlert alert = new ConfirmationAlert(this, getString(R.string.text_delete_character_title), getString(R.string.text_delete_character_description), getString(R.string.text_button_ok), getString(R.string.text_button_no)) {

			@Override
			public void onPossitiveButtonClick()
			{
				if(internalManager.eliminarPersonaje(listaPersonajes.get(indice)))
		    	{
					externalManager.eliminarDirectorioPersonaje(listaPersonajes.get(indice).getNombre());
		    		listaPersonajes.remove(indice);
		    		
		    		if(personajeSeleccionado == indice)
		    		{
		    			personajeSeleccionado = -1;
		    			internalManager.guardarSeleccionado(personajeSeleccionado);
		    		}
		    		
		    		Toast.makeText(getApplication(), R.string.text_delete_character_confirmation, Toast.LENGTH_SHORT).show();
		    	}
		    	else
		    	{
		    		Toast.makeText(getApplication(), R.string.error_delete_character, Toast.LENGTH_SHORT).show();
		    	}
		    	
				changeFragment(MainFragment.newInstance(listaPersonajes, personajeSeleccionado, externalManager));
			}

			@Override
			public void onNegativeButtonClick() { }
    	};

		alert.show();
    }
	
	/* SECTION Métodos Level Selection Fragment */
    
	@Override
    public void onLevelSelectionSelectClicked(int level)
    {
		changeFragment(GameFragment.newInstance(listaPersonajes.get(personajeSeleccionado), externalManager, levelGenerator.getLevel(level)));
    }
	
	/* SECTION Métodos Game Fragment */
	
	@Override
	public void onGameFinished(int level)
	{
		Toast.makeText(this, getString(R.string.text_game_finish), Toast.LENGTH_SHORT).show();
		
		int nextLevel = (level + 1) % estadoNiveles.length;
		
		estadoNiveles[nextLevel] = true;
		internalManager.guardarNiveles(estadoNiveles);
		
		changeFragment(LevelSelectionFragment.newInstance(estadoNiveles));
	}

	@Override
	public void onGameFailed(int level)
	{
		Toast.makeText(this, getString(R.string.text_game_fail), Toast.LENGTH_SHORT).show();
		changeFragment(GameFragment.newInstance(listaPersonajes.get(personajeSeleccionado), externalManager, levelGenerator.getLevel(level)));
	}
	
	/* SECTION Métodos de Modificación de la ActionBar */
    
	public void onMenuTwitterButtonClicked()
	{
		if(connector.isTwitterConnected())
		{
			connector.desconectarTwitter();
		}
		else
		{
			connector.conectarTwitter();
		}
	}
	
	public void onMenuFacebookButtonClicked()
	{
		if(connector.isFacebookConnected())
		{
			connector.desconectarFacebook();
		}
		else
		{
			connector.conectarFacebook();
		}
	}
	
	public void actualizarActionBar()
	{
		if(connector.isTwitterConnected())
		{
			botonTwitter.setIcon(R.drawable.menu_twitter_connected);
		}
		else
		{
			botonTwitter.setIcon(R.drawable.menu_twitter);
		}
		
		if(connector.isFacebookConnected())
		{
			botonFacebook.setIcon(R.drawable.menu_facebook_connected);
		}
		else
		{
			botonFacebook.setIcon(R.drawable.menu_facebook);
		}
	}
	
    private void limpiarActionBar()
    {
    	actionBar.removeAllTabs();
    }
    
    /* SECTION Métodos de Modificación del Estado */
    
    private void actualizarEstado(Fragment fragmento)
    {
    	if(fragmento != null)
    	{
    		if(fragmento instanceof LoadingFragment)
    		{
    			estado = TEstado.Loading;
    			setTitle(R.string.title_app);
    		}
    		else if(fragmento instanceof MainFragment)
    		{
    			estado = TEstado.Main;
    			setTitle(R.string.title_app);
    		}
    		else if(fragmento instanceof DesignFragment)
    		{
    			estado = TEstado.Design;
    			setTitle(R.string.title_design_phase);
    		}
    		else if(fragmento instanceof PaintFragment)
    		{
    			estado = TEstado.Paint;
    			setTitle(R.string.title_paint_phase);
    		}
    		else if(fragmento instanceof AnimationFragment)
    		{
    			estado = TEstado.Animation;
    			setTitle(R.string.title_animation_phase);
    		}
    		else if(fragmento instanceof CharacterSelectionFragment)
    		{
    			estado = TEstado.CharacterSelection;
    			setTitle(R.string.title_character_selection_phase);
    		}
    		else if(fragmento instanceof LevelSelectionFragment)
    		{
    			estado = TEstado.LevelSelection;
    			setTitle(R.string.title_level_selection_phase);
    		}
    		else if(fragmento instanceof GameFragment)
    		{
    			estado = TEstado.Game;
    			setTitle(R.string.title_game_phase);
    		}
    	}
    }
    
    private void actualizarEstado()
    {
    	String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
		actualizarEstado(getSupportFragmentManager().findFragmentByTag(tag));
    }
}

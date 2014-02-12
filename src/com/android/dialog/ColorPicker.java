package com.android.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.create.paint.PaintFragment;
import com.project.main.R;

public class ColorPicker extends WindowPicker
{
	private PaintFragment fragmento;
	private float[] colorActual = new float[3];
	
	private ColorPickerKotak paletaPrincipal;
	private ImageView paletaSecundaria;
	
	private Button botonAceptar, botonCancelar;
	private ImageView imagenCursorPrincipal, imagenCursorSecundario, imagenSeleccionado;
	
	public ColorPicker(Context context, PaintFragment view)
	{
		super(context, R.layout.dialog_color_layout);
		
		fragmento = view;
		
		botonAceptar = (Button) findViewById(R.id.imageButtonColor1);
		botonCancelar = (Button) findViewById(R.id.imageButtonColor2);
		
		botonAceptar.setOnClickListener(new OnAceptarClickListener());
		botonCancelar.setOnClickListener(new OnCancelarClickListener());
		
		paletaPrincipal = (ColorPickerKotak) findViewById(R.id.paletteColor1);
		paletaSecundaria = (ImageView) findViewById(R.id.paletteColor2);
		
		paletaPrincipal.setOnTouchListener(new OnPaletaPrincipalTouchListener());
		paletaSecundaria.setOnTouchListener(new OnPaletaSecundariaTouchListener());
		
		imagenCursorPrincipal = (ImageView) findViewById(R.id.imageViewColor1);
		imagenCursorSecundario = (ImageView) findViewById(R.id.imageViewColor2);
		imagenSeleccionado = (ImageView) findViewById(R.id.imageViewColor3);
		
		Color.colorToHSV(Color.RED, colorActual);
		moverCursorPrincipal();
		moverCursorSecundario();
		imagenSeleccionado.setBackgroundColor(getColor());
		
        // Posicion inicial de los Cursores
        final ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            
        	@Override 
            public void onGlobalLayout()
            {
        		moverCursorPrincipal();
        		moverCursorSecundario();
                removeGlobalLayoutListener(this);
            }
        });
	}

	@Override
	protected void onTouchOutsidePopUp(View v, MotionEvent event)
	{
		dismiss();
	}
	
	private class OnAceptarClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			fragmento.seleccionarColor(getColor());
			dismiss();
		}
	}
	
	private class OnCancelarClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			dismiss();
		}
	}
	
	
	private int getColor()
	{
	    return Color.HSVToColor(colorActual);
	}
	
	private float getHue()
	{
	    return colorActual[0];
	}
	
	private float getSat()
	{
	    return colorActual[1];
	}
	
	private float getVal()
	{
	    return colorActual[2];
	}
	
	private void setHue(float hue)
	{
		colorActual[0] = hue;
	}
	
	private void setSat(float sat)
	{
		colorActual[1] = sat;
	}
	
	private void setVal(float val)
	{
		colorActual[2] = val;
	}
	
	private void moverCursorPrincipal()
	{
        float x = getSat() * paletaPrincipal.getMeasuredWidth();
        float y = (1.0f - getVal()) * paletaPrincipal.getMeasuredHeight();
        
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imagenCursorPrincipal.getLayoutParams();
        layoutParams.leftMargin = (int) (paletaPrincipal.getLeft() + x - Math.floor(imagenCursorPrincipal.getMeasuredWidth() / 2));
        layoutParams.topMargin = (int) (paletaPrincipal.getTop() + y - Math.floor(imagenCursorPrincipal.getMeasuredHeight() / 2));
        imagenCursorPrincipal.setLayoutParams(layoutParams);		
	}
	
	private void moverCursorSecundario()
	{
	    float y = paletaSecundaria.getMeasuredHeight() - (getHue() * paletaSecundaria.getMeasuredHeight() / 360.f);
	    if (y == paletaSecundaria.getMeasuredHeight()) y = 0.0f;
	    
	    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imagenCursorSecundario.getLayoutParams();
	    layoutParams.leftMargin = (int) (paletaSecundaria.getLeft() - Math.floor(imagenCursorSecundario.getMeasuredWidth() / 2));
	    layoutParams.topMargin = (int) (paletaSecundaria.getTop() + y - Math.floor(imagenCursorSecundario.getMeasuredHeight() / 2));
	    imagenCursorSecundario.setLayoutParams(layoutParams);
	}
	
	private class OnPaletaPrincipalTouchListener implements OnTouchListener
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			int action = event.getAction();
			
			if (action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP)
			{
				float x = event.getX();
                float y = event.getY();

                // Control de Dimensiones
                
                if (x < 0) x = 0;
                if (y < 0) y = 0;
                
                if (x > paletaPrincipal.getMeasuredWidth()) x = paletaPrincipal.getMeasuredWidth();
                if (y > paletaPrincipal.getMeasuredHeight()) y = paletaPrincipal.getMeasuredHeight();

                setSat(1.f / paletaPrincipal.getMeasuredWidth() * x);
                setVal(1.f - (1.f / paletaPrincipal.getMeasuredHeight() * y));

                // Actualizar la vista
                
                moverCursorPrincipal();
                imagenSeleccionado.setBackgroundColor(getColor());

                return true;
            }
            
            return false;
		}
	}
	
	private class OnPaletaSecundariaTouchListener implements OnTouchListener
	{
		@Override
		public boolean onTouch(View arg0, MotionEvent event)
		{
			int action = event.getAction();
			
			if (action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP)
			{
                float y = event.getY();
                
                // Control de Dimensiones
                
                if (y < 0) y = 0;
                
                if (y > paletaSecundaria.getMeasuredHeight()) y = paletaSecundaria.getMeasuredHeight() - 0.001f;
                
                float hue = 360.f - 360.f / paletaSecundaria.getMeasuredHeight() * y;
                if (hue == 360.f) hue = 0.f;
                setHue(hue);

                // Actualizar la vista
                
                paletaPrincipal.setHue(getHue());
                moverCursorSecundario();
                imagenSeleccionado.setBackgroundColor(getColor());
              
                return true;
            }
			
            return false;
		}
	}
}

/*public class ColorPicker extends PopupWindows implements OnDismissListener {
	private View mRootView;
	private ImageView mArrowDown;
	private LayoutInflater mInflater;
	private ViewGroup mTrack;
	private OnDismissListener mDismissListener;
	
	private boolean mDidAction;

//	private int mOrientation;
	private int rootWidth = 0;

	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	
	View viewCuadroColores;
    ColorPickerKotak viewCuadroPrincipal;
    ImageView viewCursor;
    private View viewNewColor;
    ImageView viewTarget;
    ViewGroup viewContainer;
    final float[] currentColorHsv = new float[3];


	public ColorPicker(final Context context, final PaintGLSurfaceView canvas, int color) {
		this(context, VERTICAL, canvas, color);
		final Button ibAceptar = (Button)this.getView().findViewById(R.id.ibAceptar);
		final Button ibCancelar = (Button)this.getView().findViewById(R.id.ibCancelar);
		
		ibCancelar.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						
			
					}
		});
		
		
		ibAceptar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				canvas.seleccionarColor(getColor());	
				
			}
		});
		
		Color.colorToHSV(color, currentColorHsv);
		
        viewCuadroColores = mRootView.findViewById(R.id.color_cuadroColores);
        viewCuadroPrincipal = (ColorPickerKotak) mRootView.findViewById(R.id.color_viewCuadroPrincipal);
        viewCursor = (ImageView) mRootView.findViewById(R.id.color_cursor);
        viewNewColor = mRootView.findViewById(R.id.color_cuadroSeleccionado);
        viewTarget = (ImageView) mRootView.findViewById(R.id.color_target);
        viewContainer = (ViewGroup) mRootView.findViewById(R.id.color_viewContainer);

        viewCuadroPrincipal.setHue(getHue());
        viewNewColor.setBackgroundColor(color);

        viewCuadroColores.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE
                        || event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_UP) {

                    float y = event.getY();
                    if (y < 0.f) y = 0.f;
                    if (y > viewCuadroColores.getMeasuredHeight())
                    	y = viewCuadroColores.getMeasuredHeight() - 0.001f; // para permitir looping desde el final al principio.
                    float hue = 360.f - 360.f / viewCuadroColores.getMeasuredHeight() * y;
                    if (hue == 360.f) hue = 0.f;
                    setHue(hue);

                    // Actualizar la vista
                    viewCuadroPrincipal.setHue(getHue());
                    moveCursor();
                    viewNewColor.setBackgroundColor(getColor());
                  

                    return true;
                }
                return false;
            }
        });
        viewCuadroPrincipal.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE
                        || event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_UP) {

                    float x = event.getX(); // evento touch en unidades dp.
                    float y = event.getY();

                    if (x < 0.f) x = 0.f;
                    if (x > viewCuadroPrincipal.getMeasuredWidth()) x = viewCuadroPrincipal.getMeasuredWidth();
                    if (y < 0.f) y = 0.f;
                    if (y > viewCuadroPrincipal.getMeasuredHeight()) y = viewCuadroPrincipal.getMeasuredHeight();

                    setSat(1.f / viewCuadroPrincipal.getMeasuredWidth() * x);
                    setVal(1.f - (1.f / viewCuadroPrincipal.getMeasuredHeight() * y));

                    // Actualizar la vista
                    moveTarget();
                    viewNewColor.setBackgroundColor(getColor());

                    return true;
                }
                return false;
            }
        });
        
        // mueve el cursor y el color objetivo en su primer dibujo
        ViewTreeObserver vto = mRootView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {
                moveCursor();
                moveTarget();
                mRootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
	}

	public ColorPicker(final Context context, int orientation, final PaintGLSurfaceView canvas, final int color) {
		super(context);

		//mOrientation = orientation;

		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		setRootViewId(R.layout.dialog_color_layout);
	
	
		final Button ibAceptar = (Button)this.getView().findViewById(R.id.ibAceptar);
		final Button ibCancelar = (Button)this.getView().findViewById(R.id.ibCancelar);
		
		ibCancelar.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						dismiss();
					}
		});
		
		ibAceptar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				canvas.seleccionarColor(getColor());	
				dismiss();
			}
		});
		
		Color.colorToHSV(color, currentColorHsv);
		canvas.seleccionarColor(color);

        viewCuadroColores = mRootView.findViewById(R.id.color_cuadroColores);
        viewCuadroPrincipal = (ColorPickerKotak) mRootView.findViewById(R.id.color_viewCuadroPrincipal);
        viewCursor = (ImageView) mRootView.findViewById(R.id.color_cursor);
        viewNewColor = mRootView.findViewById(R.id.color_cuadroSeleccionado);
        viewTarget = (ImageView) mRootView.findViewById(R.id.color_target);
        viewContainer = (ViewGroup) mRootView.findViewById(R.id.color_viewContainer);

        viewCuadroPrincipal.setHue(getHue());
        viewNewColor.setBackgroundColor(color);
        viewCuadroColores.setOnTouchListener(new View.OnTouchListener() {
            @Override 
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE
                        || event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_UP) {

                    float y = event.getY();
                    if (y < 0.f) y = 0.f;
                    if (y > viewCuadroColores.getMeasuredHeight())
                    	y = viewCuadroColores.getMeasuredHeight() - 0.001f; // para permitir looping desde el final al principio.
                    float hue = 360.f - 360.f / viewCuadroColores.getMeasuredHeight() * y;
                    if (hue == 360.f) hue = 0.f;
                    setHue(hue);

                    // Actualizar la vista
                    viewCuadroPrincipal.setHue(getHue());
                    moveCursor();
                    viewNewColor.setBackgroundColor(getColor());
                    return true;
                }
                return false;
            }
        });
        viewCuadroPrincipal.setOnTouchListener(new View.OnTouchListener() {
            @Override 
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE
                        || event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_UP) {

                    float x = event.getX(); // evento touch en unidades dp.
                    float y = event.getY();

                    if (x < 0.f) x = 0.f;
                    if (x > viewCuadroPrincipal.getMeasuredWidth()) x = viewCuadroPrincipal.getMeasuredWidth();
                    if (y < 0.f) y = 0.f;
                    if (y > viewCuadroPrincipal.getMeasuredHeight()) y = viewCuadroPrincipal.getMeasuredHeight();

                    setSat(1.f / viewCuadroPrincipal.getMeasuredWidth() * x);
                    setVal(1.f - (1.f / viewCuadroPrincipal.getMeasuredHeight() * y));

                    // Actualizar la vista
                    moveTarget();
                    viewNewColor.setBackgroundColor(getColor());
                   
                    return true;
                }
                return false;
            }
        });
       
        // mueve el cursor y el color objetivo en su primer dibujo
        ViewTreeObserver vto = mRootView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override 
            public void onGlobalLayout() {
                moveCursor();
                moveTarget();
                mRootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
       
	}

	
	public View getView(){
		return mRootView;
	}

	public void setRootViewId(int id) {
		mRootView = (ViewGroup) mInflater.inflate(id, null);
		mTrack = (ViewGroup) mRootView.findViewById(R.id.tracks);

		mArrowDown = (ImageView) mRootView.findViewById(R.id.arrow_down);

		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		setContentView(mRootView);
	}


	public void show(View anchor) {
		preShow();

		int xPos, yPos, arrowPos;

		mDidAction = false;

		int[] location = new int[2];

		anchor.getLocationOnScreen(location);

		Rect anchorRect = new Rect(location[0], location[1], location[0]
				+ anchor.getWidth(), location[1] + anchor.getHeight());

		

		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int rootHeight = mRootView.getMeasuredHeight();

		if (rootWidth == 0) {
			rootWidth = mRootView.getMeasuredWidth();
		}

		int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
		int screenHeight = mWindowManager.getDefaultDisplay().getHeight();

		// automatically get X coord of popup (top left)
		if ((anchorRect.left + rootWidth) > screenWidth) {
			xPos = anchorRect.left - (rootWidth - anchor.getWidth());
			xPos = (xPos < 0) ? 0 : xPos;

			arrowPos = anchorRect.centerX() - xPos;

		} else {
			if (anchor.getWidth() > rootWidth) {
				xPos = anchorRect.centerX() - (rootWidth / 2);
			} else {
				xPos = anchorRect.left;
			}

			arrowPos = anchorRect.centerX() - xPos;
		}

		int dyTop = anchorRect.top;
		int dyBottom = screenHeight - anchorRect.bottom;

		boolean onTop = (dyTop > dyBottom) ? true : false;

		if (onTop) {
			if (rootHeight > dyTop) {
				yPos = 15;
				LayoutParams l = (LayoutParams) mTrack.getLayoutParams();
				l.height = dyTop - anchor.getHeight();
			} else {
				yPos = anchorRect.top - rootHeight;
			}
		} else {
			yPos = anchorRect.bottom;

			if (rootHeight > dyBottom) {
				LayoutParams l = (LayoutParams) mTrack.getLayoutParams();
				l.height = dyBottom;
			}
		}

		showArrow((R.id.arrow_down), arrowPos);

		
		mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}

	private void showArrow(int whichArrow, int requestedX) {
		final View showArrow =mArrowDown;

		final int arrowWidth = mArrowDown.getMeasuredWidth();

		showArrow.setVisibility(View.VISIBLE);

		ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) showArrow
				.getLayoutParams();

		param.leftMargin = requestedX - arrowWidth / 2;

	}


	public void setOnDismissListener(ColorPicker.OnDismissListener listener) {
		setOnDismissListener( (OnDismissListener) this);

		mDismissListener = listener;
	}

	@Override
	public void onDismiss() {
		if (!mDidAction && mDismissListener != null) {
			mDismissListener.onDismiss();
		}
	}

	public interface OnActionItemClickListener {
		public abstract void onItemClick(SizePicker source, int pos,
				int actionId);
	}

	public interface OnDismissListener {
		public abstract void onDismiss();
	}

	//Metodos modificacion del Cuadro de Colores
	protected void moveCursor() {
	    float y = viewCuadroColores.getMeasuredHeight() - (getHue() * viewCuadroColores.getMeasuredHeight() / 360.f);
	    if (y == viewCuadroColores.getMeasuredHeight()) y = 0.f;
	    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewCursor.getLayoutParams();
	    layoutParams.leftMargin = (int) (viewCuadroColores.getLeft() - Math.floor(viewCursor.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
	    ;
	    layoutParams.topMargin = (int) (viewCuadroColores.getTop() + y - Math.floor(viewCursor.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
	    ;
	    viewCursor.setLayoutParams(layoutParams);
	}
	protected void moveTarget() {
        float x = getSat() * viewCuadroPrincipal.getMeasuredWidth();
        float y = (1.f - getVal()) * viewCuadroPrincipal.getMeasuredHeight();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewTarget.getLayoutParams();
        layoutParams.leftMargin = (int) (viewCuadroPrincipal.getLeft() + x - Math.floor(viewTarget.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
        layoutParams.topMargin = (int) (viewCuadroPrincipal.getTop() + y - Math.floor(viewTarget.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
        viewTarget.setLayoutParams(layoutParams);
    }
	
	
	private int getColor() {
	    return Color.HSVToColor(currentColorHsv);
	}
	
	private float getHue() {
	    return currentColorHsv[0];
	}
	
	private float getSat() {
	    return currentColorHsv[1];
	}
	
	private float getVal() {
	    return currentColorHsv[2];
	}
	
	private void setHue(float hue) {
	    currentColorHsv[0] = hue;
	}
	
	private void setSat(float sat) {
	    currentColorHsv[1] = sat;
	}
	
	private void setVal(float val) {
	    currentColorHsv[2] = val;
	}
*/
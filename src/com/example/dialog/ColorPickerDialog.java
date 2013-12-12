package com.example.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.content.DialogInterface.OnCancelListener;

import com.example.main.R;


public class ColorPickerDialog {
    public interface OnColorPickerListener {
        void onCancel(ColorPickerDialog dialog);
        void onOk(ColorPickerDialog dialog, int color);
    }

    final AlertDialog dialog;
    final OnColorPickerListener listener;
    final View viewCuadroColores;
    final ColorPickerKotak viewCuadroPrincipal;
    final ImageView viewCursor;
    private View viewNewColor;
    final ImageView viewTarget;
    final ViewGroup viewContainer;
    final float[] currentColorHsv = new float[3];
    


    public ColorPickerDialog(final Context context, int color, OnColorPickerListener listener) {
        this.listener = listener;
        Color.colorToHSV(color, currentColorHsv);

        final View view = LayoutInflater.from(context).inflate(R.layout.dialog, null);
        viewCuadroColores = view.findViewById(R.id.color_cuadroColores);
        viewCuadroPrincipal = (ColorPickerKotak) view.findViewById(R.id.color_viewCuadroPrincipal);
        viewCursor = (ImageView) view.findViewById(R.id.color_cursor);
        viewNewColor = view.findViewById(R.id.color_cuadroSeleccionado);
        viewTarget = (ImageView) view.findViewById(R.id.color_target);
        viewContainer = (ViewGroup) view.findViewById(R.id.color_viewContainer);

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

        dialog = new AlertDialog.Builder(context)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override 
                    public void onClick(DialogInterface dialog, int which) {
                        if (ColorPickerDialog.this.listener != null) {
                            ColorPickerDialog.this.listener.onOk(ColorPickerDialog.this, getColor());
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override 
                    public void onClick(DialogInterface dialog, int which) {
                        if (ColorPickerDialog.this.listener != null) {
                            ColorPickerDialog.this.listener.onCancel(ColorPickerDialog.this);
                        }
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    // si el boton de vuelta atrás es usado, vuelta a llamar al listener.
                    @Override 
                    public void onCancel(DialogInterface paramDialogInterface) {
                        if (ColorPickerDialog.this.listener != null) {
                            ColorPickerDialog.this.listener.onCancel(ColorPickerDialog.this);
                        }

                    }
                })
                .create();
        // Elimina el padding de la ventana de dialogo
        dialog.setView(view, 0, 0, 0, 0);

        // mueve el cursor y el color objetivo en su primer dibujo
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {
                moveCursor();
                moveTarget();
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

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

    public void show() {
        dialog.show();
    }

    public AlertDialog getDialog() {
        return dialog;
    }
}

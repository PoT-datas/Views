package com.pot.views.test;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pot.views.R;
import api.pot.view.tools.Forgrounder;
import api.pot.view.xl.XLayout;
import api.pot.view.xsb.Xnackbar;
import api.pot.view.xsb.tools.Duration;
import api.pot.view.xsb.tools.Type;

import static api.pot.view.tools.Global.getViewBoundFrom;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class MainActivity extends AppCompatActivity {

    boolean hasBeenSelected = false;
    XLayout cl, xl;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cl = (XLayout) findViewById(R.id.cl);

        isViewReady();
    }

    public void isViewReady(){
        ViewTreeObserver viewTreeObserver = cl.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                cl.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                init();
            }
        });
    }

    private void init() {
        cl.getFramer().setEnabled(true);
        cl.getFramer().canFrame(false, true, false, false);
        //
        cl.getScroller().setEnabled(false);
        cl.getScroller().canScroll(false, false, true, false);
        //
        cl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //cl.getForgrounder().setSelected(true);
                return true;
            }
        });
        cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cl.getForgrounder().isSelected)
                    hasBeenSelected = true;
                if(!hasBeenSelected)
                    cl.getForgrounder().setSelected(false);
                else
                    cl.getForgrounder().setSelected(!cl.getForgrounder().isSelected);
            }
        });
        //
        TextView textView = cl.findViewById(R.id.texto);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "PoT", Toast.LENGTH_SHORT).show();
                if(!cl.isBlurCover()) {
                    cl.lock();
                    cl.setBlurCover(true, getViewBoundFrom(imageView, cl));
                }else {
                    cl.unlock();
                    cl.setBlurCover(false);
                }
            }
        });
        TextView textView2 = cl.findViewById(R.id.textoo);
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "GES", Toast.LENGTH_SHORT).show();
                cl.getForgrounder().setEnabled(!cl.getForgrounder().isEnabled);
            }
        });
        //
        imageView = cl.findViewById(R.id.content);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                snake("yep");
                return true;
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xl.setVisibility(View.VISIBLE);
                cl.setBlurCover(true, xl.getBoundFrom(cl));
                cl.lock();
            }
        });
        //
        xl = (XLayout) findViewById(R.id.xl);
        xl.setOnFgClickListener(new Forgrounder.OnClickListener() {
            @Override
            public void onClick(View view) {
                xl.setVisibility(View.INVISIBLE);
                cl.setBlurCover(false);
                cl.unlock();
            }
        });
    }

    public void snake(String text){
        View v = getLayoutInflater().inflate(R.layout.complex_snackbar_view, null);
        //
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Xnackbar.dismiss();
                Toast.makeText(MainActivity.this, "dismiss", Toast.LENGTH_LONG).show();
            }
        });
        //
        (v.findViewById(R.id.info)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "info", Toast.LENGTH_LONG).show();
            }
        });
        //
        ((TextView)v.findViewById(R.id.textView3)).setText(text);
        Xnackbar.with(MainActivity.this,null)
                .type(Type.CUSTOM, Color.TRANSPARENT)
                .fillParent(true)
                .contentView(v)
                .duration(Duration.INFINITE)
                .show();
    }

}


package api.pot.view.tools;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import java.util.Random;

import api.pot.view.xl.XLayout;

public class Forgrounder {
    public boolean isEnabled = false;
    private View main;
    //
    private Paint paint = new Paint();
    private final Random rnd = new Random();
    //
    private RectF bounds = null;
    private float radius = -1;
    private int alpha = Global.foreground_anim_alpha;
    private float cx=-1, cy=-1;
    //
    private boolean isPlaying = false, isInit = false;
    private float c_radius = 0, c_position = 0.5f;
    private int c_alpha=alpha;
    private ValueAnimator progresser, progresser2;
    public boolean touchUp = false;
    //
    public long time = Global.foreground_anim_time;
    //
    private int c_color = Color.rgb(255, 255, 255);
    //
    public boolean isSelected = false;
    //
    public boolean dontCheck = false;

    public void setDontCheck(boolean dontCheck) {
        this.dontCheck = dontCheck;
    }

    public Forgrounder(View view) {
        this.main = view;
    }

    public Forgrounder(View view, boolean enabled) {
        this.main = view;
        this.isEnabled = enabled;
    }

    public void init(RectF bound){
        if(bounds!=null) bounds.set(bound);
        //
        if(isInit || !isEnabled) return;
        //
        isInit = false;
        //
        if(bounds==null) bounds = new RectF(bound.left, bound.top, bound.right, bound.bottom);
        if(cx==-1 || !isDisableCircularTransformation()) {
            cx = bound.centerX();
        }
        if(cy==-1 || !isDisableCircularTransformation()) {
            cy = bound.centerY();
        }
        if(radius==-1) {
            if( isDisableCircularTransformation() )
                radius = Math.max(Math.max(getDistance(new ImgPainter.FloatPoint(cx, cy), new ImgPainter.FloatPoint(bound.left, bound.top)),
                    getDistance(new ImgPainter.FloatPoint(cx, cy), new ImgPainter.FloatPoint(bound.left, bound.bottom))),
                    Math.max(getDistance(new ImgPainter.FloatPoint(cx, cy), new ImgPainter.FloatPoint(bound.right, bound.top)),
                            getDistance(new ImgPainter.FloatPoint(cx, cy), new ImgPainter.FloatPoint(bound.right, bound.bottom))));
            else
                radius = Math.min(bounds.width()/2, bounds.height()/2);
        }
        //
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        RadialGradient radialGradient = new RadialGradient(cx, cy, radius, Color.TRANSPARENT, c_color, Shader.TileMode.CLAMP);
        paint.setShader(radialGradient);
        //
        progresser = ValueAnimator.ofFloat(radius/4/*0*/, radius);
        progresser.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float val = (Float) valueAnimator.getAnimatedValue();
                setC_radius(val);
            }
        });
        progresser.setDuration(2*time/3);
        progresser.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                //touchUp = false;
                setTouchUp(false);
                //c_color = Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                c_alpha = alpha;
                isPlaying = true;
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(touchUp==true) {
                    if(!isSelected)
                        progresser2.start();
                }
            }
        });
        progresser.setInterpolator(new AccelerateInterpolator());
        //
        progresser2 = ValueAnimator.ofInt(alpha, 0);
        progresser2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                setC_alpha(val);
            }
        });
        progresser2.setDuration(time/3);
        progresser2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isPlaying = false;

                //notify listener
                if(Forgrounder.this.clickedView!=null){
                    Forgrounder.this.onClickListener.onClick(clickedView);
                    Forgrounder.this.clickedView=null;
                }
            }
        });
        progresser2.setInterpolator(new AccelerateInterpolator());
        //
        isInit = true;
    }

    public void setColor(int color) {
        this.c_color = color;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    private float getDistance(ImgPainter.FloatPoint fp, ImgPainter.FloatPoint fp2) {
        return (float) Math.sqrt(Math.pow(fp2.x-fp.x, 2)+Math.pow(fp2.y-fp.y, 2));
    }

    public void setC_alpha(int c_alpha) {
        this.c_alpha = c_alpha;
        //
        main.invalidate();
    }

    public void setC_radius(float c_radius) {
        this.c_radius = c_radius;
        //
        int translucide_color = Color.argb(3*Color.alpha(c_color)/4, Color.red(c_color), Color.green(c_color), Color.blue(c_color));
        int translucide_color2 = Color.argb(Color.alpha(c_color)/2, Color.red(c_color), Color.green(c_color), Color.blue(c_color));
        RadialGradient radialGradient = null;
        if( !isDisableCircularTransformation() ) {
            radialGradient = new RadialGradient(cx, cy, radius,
                    new int[]{Color.TRANSPARENT, translucide_color, c_color, translucide_color2, Color.TRANSPARENT},
                    new float[]{0f, 2f * this.c_radius / this.radius / 3f, this.c_radius / this.radius, 4f * this.c_radius / this.radius / 3f, 1f}, Shader.TileMode.CLAMP);
        }else {
            radialGradient = new RadialGradient(cx, cy, radius,
                    new int[]{Color.TRANSPARENT, translucide_color, c_color, translucide_color2, Color.TRANSPARENT},
                    new float[]{0f, this.c_radius / this.radius / 3f, this.c_radius / this.radius / 2f, 2f * this.c_radius / this.radius / 3f, 1f}, Shader.TileMode.CLAMP);
        }
        paint.setShader(null);
        paint.setShader(radialGradient);
        //
        main.invalidate();
    }

    public void update(Canvas canvas){
        if(!isInit || c_radius<=0 || !isPlaying || c_alpha<=0) return;
        //
        if(isDisableCircularTransformation()) {
            Bitmap bmp = Bitmap.createBitmap((int) (bounds.width()), (int) (bounds.height()), Bitmap.Config.ARGB_8888);
            Canvas cvs = new Canvas(bmp);
            //
            paint.setAlpha(c_alpha);
            cvs.drawCircle(cx - bounds.left, cy - bounds.top, radius, paint);
            //
            canvas.drawBitmap(bmp, bounds.left, bounds.top, null);
        }else {
            paint.setAlpha(c_alpha);
            canvas.drawCircle(cx, cy, radius, paint);
        }
    }

    public void onTouch(MotionEvent event, RectF bound) {
        //
        if(!isEnabled || dontCheck) return;
        //
        if(isIntoRect(bound, new ImgPainter.FloatPoint(event.getX(), event.getY()))) {
            if( isDisableCircularTransformation() ) {
                cx = event.getX();
                cy = event.getY();
                //
                if( isDisableCircularTransformation() )
                    radius = Math.max(Math.max(getDistance(new ImgPainter.FloatPoint(cx, cy), new ImgPainter.FloatPoint(bound.left, bound.top)),
                            getDistance(new ImgPainter.FloatPoint(cx, cy), new ImgPainter.FloatPoint(bound.left, bound.bottom))),
                            Math.max(getDistance(new ImgPainter.FloatPoint(cx, cy), new ImgPainter.FloatPoint(bound.right, bound.top)),
                                    getDistance(new ImgPainter.FloatPoint(cx, cy), new ImgPainter.FloatPoint(bound.right, bound.bottom))));
                else
                    radius = Math.min(bounds.width()/2, bounds.height()/2);
                radius*=2;
            }
        }else {
            setTouchUp(true);
            return;
        }
        //
        init(bound);
        //
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            if(!progresser.isRunning() && !progresser2.isRunning())
                progresser.start();
        }else{
            /*if(event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_MOVE ||
                    event.getAction()==MotionEvent.ACTION_HOVER_MOVE){*/
                if(progresser.isRunning())
                    setTouchUp(true);
                else if(!isSelected)
                        progresser2.start();
                if(event.getAction()==MotionEvent.ACTION_UP)
                    can_change_selected = true;
            //}
        }
    }

    private boolean isIntoRect(RectF bound, ImgPainter.FloatPoint fp) {
        return (bound.left<=fp.x && fp.x<=bound.right) && (bound.top<=fp.y && fp.y<=bound.bottom);
    }

    private boolean isDisableCircularTransformation(){
        return true;
    }

    public void setSelected(boolean isSelected) {
        if(this.isSelected == isSelected) return;
        this.isSelected = isSelected;
    }

    boolean can_change_selected = true;

    public void setTouchUp(boolean touchUp) {
        this.touchUp = touchUp;
        if(this.touchUp) can_change_selected = true;
    }



    private View clickedView = null;
    private OnClickListener onClickListener;

    public interface OnClickListener{
        void onClick(View view);
    }

    public void setOnClickListener(final OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEnabled) clickedView = view;
                else if(onClickListener!=null) Forgrounder.this.onClickListener.onClick(view);
            }
        });
    }
}

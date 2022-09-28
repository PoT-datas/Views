package api.pot.view.xl.tools;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import api.pot.view.xl.XLayout;

public class Scroller {
    private View view;

    private RectF VIEW_BOUNDS;

    public boolean isEnabled = false;

    private Framer framer;

    public boolean canScrollLeft = false, canScrollTop = false, canScrollRight = false, canScrollBottom = false, canScroll = false;

    private GestureDetector gestureDetectorLeft;
    private GestureDetector gestureDetectorTop;
    private GestureDetector gestureDetectorRight;
    private GestureDetector gestureDetectorBottom;

    public Scroller(final View view) {
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                onViewReady(view);
            }
        });
    }

    public void onViewReady(View view) {
        this.view = view;
        initStaticVar(view.getContext());
        init();
        listener();
    }

    public void canScroll(boolean left, boolean top, boolean right, boolean bottom){
        scroll(left, top, right, bottom);
        //
        framer.frame(left?false:framer.canFrameLeft, top?false:framer.canFrameTop, right?false:framer.canFrameRight, bottom?false:framer.canFrameBottom);
    }

    public void scroll(boolean left, boolean top, boolean right, boolean bottom){
        canScrollLeft = left;
        canScrollTop = top;
        canScrollRight = right;
        canScrollBottom = bottom;
        //
        if(!canScrollLeft && !canScrollTop && !canScrollRight && !canScrollBottom) canScroll = false;
        else canScroll = true;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public void onTouch(MotionEvent event) {
        if(!isEnabled) return;
        if(canScrollLeft) gestureDetectorLeft.onTouchEvent(event);
        if(canScrollTop) gestureDetectorTop.onTouchEvent(event);
        if(canScrollRight) gestureDetectorRight.onTouchEvent(event);
        if(canScrollBottom) gestureDetectorBottom.onTouchEvent(event);
    }

    public void setFramer(Framer framer) {
        this.framer = framer;
    }

    public void setVIEW_BOUNDS(RectF VIEW_BOUNDS) {
        this.VIEW_BOUNDS = VIEW_BOUNDS;
    }

    public void init() {
        if(VIEW_BOUNDS!=null) return;
        //
        VIEW_BOUNDS = new RectF(0, 0, view.getWidth(), view.getHeight());
    }

    private void listener() {
        gestureDetectorLeft = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                //layoutParams.leftMargin -= distanceX;
                layoutParams.leftMargin +=  e2.getX()-e1.getX();
                view.setLayoutParams(checkMarginValue(layoutParams, Side.LEFT));
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                stopTongle();
                return super.onDown(e);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(velocityY!=0){
                    float velocityPercentX = velocityX/MAXIMUM_FING_VELOCITY;
                    float normalizedVelocityX = velocityPercentX * PIXELS_PER_SECOND;
                    long duration = (long) Math.abs(MAX_MVT_DURATION*normalizedVelocityX);
                    if(duration<=0) duration = 1;
                    final float distance = view.getWidth()*normalizedVelocityX;

                    final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    final int init = layoutParams.leftMargin;
                    fling_valueAnimator = ValueAnimator.ofInt(0, (int) distance);
                    fling_valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int d = (int) valueAnimator.getAnimatedValue();
                            layoutParams.leftMargin = init+d;
                            view.setLayoutParams(checkMarginValue(layoutParams, Side.LEFT));
                        }
                    });
                    fling_valueAnimator.setDuration(duration);
                    fling_valueAnimator.setInterpolator(new DecelerateInterpolator());
                    fling_valueAnimator.start();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
        gestureDetectorTop = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.topMargin += e2.getY() - e1.getY();
                view.setLayoutParams(checkMarginValue(layoutParams, Side.TOP));
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                stopTongle();
                return super.onDown(e);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(velocityY!=0){
                    float velocityPercentY = velocityY/MAXIMUM_FING_VELOCITY;
                    float normalizedVelocityY = velocityPercentY * PIXELS_PER_SECOND;
                    long duration = (long) Math.abs(MAX_MVT_DURATION*normalizedVelocityY);
                    if(duration<=0) duration = 1;
                    final float distance = view.getHeight()*normalizedVelocityY;

                    final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    final int init = layoutParams.topMargin;
                    fling_valueAnimator = ValueAnimator.ofInt(0, (int) distance);
                    fling_valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int d = (int) valueAnimator.getAnimatedValue();
                            layoutParams.topMargin = init+d;
                            view.setLayoutParams(checkMarginValue(layoutParams, Side.TOP));
                        }
                    });
                    fling_valueAnimator.setDuration(duration);
                    fling_valueAnimator.setInterpolator(new DecelerateInterpolator());
                    fling_valueAnimator.start();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
        gestureDetectorRight = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.rightMargin += distanceX;
                view.setLayoutParams(checkMarginValue(layoutParams, Side.RIGHT));
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                stopTongle();
                return super.onDown(e);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(velocityY!=0){
                    float velocityPercentX = velocityX/MAXIMUM_FING_VELOCITY;
                    float normalizedVelocityX = velocityPercentX * PIXELS_PER_SECOND;
                    long duration = (long) Math.abs(MAX_MVT_DURATION*normalizedVelocityX);
                    if(duration<=0) duration = 1;
                    final float distance = view.getWidth()*normalizedVelocityX;

                    final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    final int init = layoutParams.rightMargin;
                    fling_valueAnimator = ValueAnimator.ofInt(0, (int) distance);
                    fling_valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int d = (int) valueAnimator.getAnimatedValue();
                            layoutParams.rightMargin = init-d;
                            view.setLayoutParams(checkMarginValue(layoutParams, Side.RIGHT));
                        }
                    });
                    fling_valueAnimator.setDuration(duration);
                    fling_valueAnimator.setInterpolator(new DecelerateInterpolator());
                    fling_valueAnimator.start();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
        gestureDetectorBottom = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.bottomMargin += e1.getY() - e2.getY();
                view.setLayoutParams(checkMarginValue(layoutParams, Side.BOTTOM));
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                stopTongle();
                return super.onDown(e);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(velocityY!=0){
                    float velocityPercentY = velocityY/MAXIMUM_FING_VELOCITY;
                    float normalizedVelocityY = velocityPercentY * PIXELS_PER_SECOND;
                    long duration = (long) Math.abs(MAX_MVT_DURATION*normalizedVelocityY);
                    if(duration<=0) duration = 1;
                    final float distance = view.getHeight()*normalizedVelocityY;

                    final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    final int init = layoutParams.bottomMargin;
                    fling_valueAnimator = ValueAnimator.ofInt(0, (int) distance);
                    fling_valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int d = (int) valueAnimator.getAnimatedValue();
                            layoutParams.bottomMargin = init-d;
                            view.setLayoutParams(checkMarginValue(layoutParams, Side.BOTTOM));
                        }
                    });
                    fling_valueAnimator.setDuration(duration);
                    fling_valueAnimator.setInterpolator(new DecelerateInterpolator());
                    fling_valueAnimator.start();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    private RelativeLayout.LayoutParams checkMarginValue(RelativeLayout.LayoutParams margin, Side side) {
        init();
        try {
            isDisplayed = true;
            int value = side== Side.LEFT?margin.leftMargin:side== Side.TOP?margin.topMargin:side== Side.RIGHT?margin.rightMargin:side== Side.BOTTOM?margin.bottomMargin:0;
            float VIEW_MIN_MARGIN = side== Side.LEFT?VIEW_BOUNDS.left:side== Side.TOP?VIEW_BOUNDS.top:side== Side.RIGHT?VIEW_BOUNDS.left:side== Side.BOTTOM?VIEW_BOUNDS.top:0;
            float VIEW_MAX_MARGIN = side== Side.LEFT?VIEW_BOUNDS.right:side== Side.TOP?VIEW_BOUNDS.bottom:side== Side.RIGHT?VIEW_BOUNDS.right:side== Side.BOTTOM?VIEW_BOUNDS.bottom:0;
            if (VIEW_MIN_MARGIN > value){
                if(side== Side.LEFT) margin.leftMargin = (int) VIEW_MIN_MARGIN;
                if(side== Side.TOP) margin.topMargin = (int) VIEW_MIN_MARGIN;
                if(side== Side.RIGHT) margin.rightMargin = (int) VIEW_MIN_MARGIN;
                if(side== Side.BOTTOM) margin.bottomMargin = (int) VIEW_MIN_MARGIN;
            }else if (value > VIEW_MAX_MARGIN){
                if(side== Side.LEFT) margin.leftMargin = (int) VIEW_MAX_MARGIN;
                if(side== Side.TOP) margin.topMargin = (int) VIEW_MAX_MARGIN;
                if(side== Side.RIGHT) margin.rightMargin = (int) VIEW_MAX_MARGIN;
                if(side== Side.BOTTOM) margin.bottomMargin = (int) VIEW_MAX_MARGIN;
                isDisplayed = false;
            }
        }catch (Exception e){}
        return margin;
    }

    private boolean isDisplayed = false;
    public void tongle() {
        if(isDisplayed) hide();
        else show();
    }

    ValueAnimator show_valueAnimator, hide_valueAnimator, fling_valueAnimator;
    public void stopTongle(){
        if(show_valueAnimator!=null && show_valueAnimator.isRunning()){
            show_valueAnimator.cancel();
            show_valueAnimator.removeAllListeners();
        }
        if(hide_valueAnimator!=null && hide_valueAnimator.isRunning()){
            hide_valueAnimator.cancel();
            hide_valueAnimator.removeAllListeners();
        }
        if(fling_valueAnimator!=null && fling_valueAnimator.isRunning()){
            fling_valueAnimator.cancel();
            fling_valueAnimator.removeAllListeners();
        }
    }

    public void tongleTop(float ratio) {
        tongle(Side.TOP, ratio);
    }

    public void tongle(Side side, float ratio) {
        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        switch (side){
            case TOP:{
                show_valueAnimator = ValueAnimator.ofInt(layoutParams.topMargin, (int) (ratio*(view.getHeight()+layoutParams.topMargin)));
                show_valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int d = (int) valueAnimator.getAnimatedValue();
                        layoutParams.topMargin = d;
                        view.setLayoutParams(checkMarginValue(layoutParams, Side.TOP));
                    }
                });
                break;
            }
        }
        show_valueAnimator.setDuration(TONGLE_DURATION);
        show_valueAnimator.setInterpolator(new DecelerateInterpolator());
        show_valueAnimator.start();
    }

    public void show() {
        if(view==null) return;

        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

        show_valueAnimator = ValueAnimator.ofInt(layoutParams.topMargin, 2*view.getHeight()/3);
        show_valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int d = (int) valueAnimator.getAnimatedValue();
                layoutParams.topMargin = d;
                view.setLayoutParams(checkMarginValue(layoutParams, Side.TOP));
            }
        });
        show_valueAnimator.setDuration(TONGLE_DURATION);
        show_valueAnimator.setInterpolator(new DecelerateInterpolator());
        show_valueAnimator.start();
    }

    public void hide() {
        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

        hide_valueAnimator = ValueAnimator.ofInt(layoutParams.topMargin, view.getHeight()-200);
        hide_valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int d = (int) valueAnimator.getAnimatedValue();
                layoutParams.topMargin = d;
                view.setLayoutParams(checkMarginValue(layoutParams, Side.TOP));
            }
        });
        hide_valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isDisplayed = false;
            }
        });

        hide_valueAnimator.setDuration(TONGLE_DURATION);
        hide_valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        hide_valueAnimator.start();
    }

    private void initStaticVar(Context context) {
        TONGLE_DURATION = 300;
        MAX_MVT_DURATION = 500;
        PIXELS_PER_SECOND = 5;
        MAXIMUM_FING_VELOCITY = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
    }

    public long TONGLE_DURATION;
    public long MAX_MVT_DURATION;
    public float PIXELS_PER_SECOND;
    public float MAXIMUM_FING_VELOCITY;

    public void setView(XLayout mView) {
        view = mView;
    }

    private enum Side{
        LEFT, TOP, RIGHT, BOTTOM;
    }

}

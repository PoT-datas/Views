package api.pot.view.xl.tools;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

public class Framer {
    private View child;
    private boolean isInit = false;
    //
    public boolean isEnabled = false;
    //
    private float deltaX=0, deltaY=0, x=0, y=0, nx=0, ny=0;
    //
    public boolean canFrameLeft = true, canFrameTop = true, canFrameRight = true, canFrameBottom = true, canFrame = true;
    //
    private int minMgL=0;
    private int minMgT=0;
    private int minMgR=0;
    private int minMgB=0;
    //
    private int deltaL=0;
    private int deltaT=0;
    private int deltaR=0;
    private int deltaB=0;
    //
    private int mL=0;
    private int mT=0;
    private int mR=0;
    private int mB=0;
    //
    public long time=1000;//300
    private ValueAnimator progresser;

    private Scroller scroller;

    public Framer(View child) {
        this.child = child;
    }

    public void canFrame(boolean left, boolean top, boolean right, boolean bottom){
        frame(left, top, right, bottom);
        //
        scroller.scroll(left?false:scroller.canScrollLeft, top?false:scroller.canScrollTop, right?false:scroller.canScrollRight, bottom?false:scroller.canScrollBottom);
    }

    public void frame(boolean left, boolean top, boolean right, boolean bottom){
        canFrameLeft = left;
        canFrameTop = top;
        canFrameRight = right;
        canFrameBottom = bottom;
        //
        if(!canFrameLeft && !canFrameTop && !canFrameRight && !canFrameBottom) canFrame = false;
        else canFrame = true;
    }

    private void init() {
        if(!isEnabled) return;
        //
        try {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) child.getLayoutParams();
            minMgL = params.leftMargin;
            minMgT = params.topMargin;
            minMgR = params.rightMargin;
            minMgB = params.bottomMargin;
        }catch (Exception e){
            return;
        }
        //
        if(!canFrameLeft && !canFrameTop && !canFrameRight && !canFrameBottom) canFrame = false;
        else canFrame = true;
        //
        isInit = true;
    }

    private void animToDefault() {
        progresser = ValueAnimator.ofFloat(0, 1);
        progresser.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float val = (Float) valueAnimator.getAnimatedValue();
                //
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) child.getLayoutParams();
                if(canFrameLeft) params.leftMargin = mL + (int) (-val*deltaL);
                if(canFrameTop) params.topMargin = mT + (int) (-val*deltaT);
                if(canFrameRight) params.rightMargin = mR + (int) (-val*deltaR);
                if(canFrameBottom) params.bottomMargin = mB + (int) (-val*deltaB);
                //
                child.setLayoutParams(params);
            }
        });
        progresser.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) child.getLayoutParams();
                deltaL = Math.abs(params.leftMargin-minMgL);
                mL = params.leftMargin;
                deltaT = Math.abs(params.topMargin-minMgT);
                mT = params.topMargin;
                deltaR = Math.abs(params.rightMargin-minMgR);
                mR = params.rightMargin;
                deltaB = Math.abs(params.bottomMargin-minMgB);
                mB = params.bottomMargin;
            }
        });
        progresser.setDuration(time);
        progresser.setInterpolator(new DecelerateInterpolator());
        progresser.start();
    }

    public void cancelAnim(){
        if(progresser==null || !progresser.isRunning()) return;
        progresser.removeAllUpdateListeners();
        progresser.cancel();
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public void onTouch(MotionEvent event){
        if(!isInit) init();

        if(!isEnabled) return;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                cancelAnim();
                break;

            case MotionEvent.ACTION_UP:
                animToDefault();
                break;

            case MotionEvent.ACTION_MOVE:
                nx = event.getX();
                ny = event.getY();
                deltaX = nx-x;
                deltaY = ny-y;
                //
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) child.getLayoutParams();
                if(canFrameLeft) params.leftMargin += deltaX/3;
                if(canFrameTop) params.topMargin += deltaY/3;
                if(canFrameRight) params.rightMargin += -deltaX/3;
                if(canFrameBottom) params.bottomMargin += -deltaY/3;
                //
                if(params.leftMargin<minMgL) params.leftMargin = minMgL;
                if(params.topMargin<minMgT) params.topMargin = minMgT;
                if(params.rightMargin<minMgR) params.rightMargin = minMgR;
                if(params.bottomMargin<minMgB) params.bottomMargin = minMgB;
                //
                child.setLayoutParams(params);
                //
                x = nx;
                y = ny;
                break;
        }
    }

    public void setScroller(Scroller scroller) {
        this.scroller = scroller;
    }
}

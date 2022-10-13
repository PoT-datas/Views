package api.pot.view.xl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.Toast;

import api.pot.view.R;
import api.pot.view.tools.BlurBuilder;
import api.pot.view.tools.Forgrounder;
import api.pot.view.xl.tools.Framer;
import api.pot.view.xl.tools.Scroller;

import java.util.ArrayList;
import java.util.List;

import static api.pot.view.tools.Global.getViewBoundFrom;

@SuppressLint("NewApi")
public class XLayout extends RelativeLayout {
    private Forgrounder forgrounder;
    private Framer framer;
    private Scroller scroller;

    private List<Path> bounds = new ArrayList<>();
    private final int BOUND_DEFAULT = 0;
    private final int BOUND_1 = 1;
    private int ibound=BOUND_DEFAULT;
    private Path bound;
    private Path clipBound;
    private RectF mClipBound;
    private RectF mBound;

    private final int XL_PADDING_NO_VALUE = -1;
    private int xlPaddingLeft = 0;
    private int xlPaddingTop = 0;
    private int xlPaddingRight = 0;
    private int xlPaddingBottom = 0;

    private Bitmap mBitmap, blurBmp;
    private Canvas cvs;

    private int shadowRad=10, shadowDx=0, shadowDy=10, shadowColor= Color.BLACK;
    private int borderWidth = 0;
    private int borderSoftness = 0;
    private int borderColor = Color.RED;
    private float cornerRX = 0.5f;//0-1
    private float cornerRY = 0.2f;//0-1
    private Paint shadowPaint;
    private Paint borderPaint;

    private boolean isBlurCover = false;
    private float blurPercent = 0.5f;
    private RectF blurBound;
    private Path blurPath;
    private Paint blurPaint = new Paint();

    private boolean isDefaultBound = true;

    private boolean isReachable = true;

    private boolean mForegrounderEnabled = false;
    private boolean mFramerLeftEnabled = false;
    private boolean mFramerTopEnabled = false;
    private boolean mFramerRightEnabled = false;
    private boolean mFramerBottomEnabled = false;
    private boolean mScrollerLeftEnabled = false;
    private boolean mScrollerTopEnabled = false;
    private boolean mScrollerRightEnabled = false;
    private boolean mScrollerBottomEnabled = false;



    //++++++++++++++++++++++++methodes++++++++++++++++++++++++++++++++++++++++++++++++++++

    public void setOnFgClickListener(@Nullable Forgrounder.OnClickListener l) {
        if(forgrounder==null) forgrounder = new Forgrounder(this);
        forgrounder.setOnClickListener(l);
    }

    public Path getBoundFrom(View from) {
        setup();
        //
        RectF cont = getViewBoundFrom(this, from);
        Path out = new Path();
        out.addPath(bound, cont.left, cont.top);
        return out;//
    }

    public void setCornerRatio(float cornerRX, float cornerRY) {
        this.cornerRX = Math.abs(cornerRX)>1?0:Math.abs(cornerRX);
        this.cornerRY = Math.abs(cornerRY)>1?0:Math.abs(cornerRY);
    }

    public void setBorderSoftness(int borderSoftness) {
        this.borderSoftness = borderSoftness;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public void setShadowRad(int shadowRad) {
        this.shadowRad = shadowRad;
        setup();
    }

    public void setShadowRadRes(int resId) {
        this.shadowRad = (int) getResources().getDimension(resId);
        setup();
    }

    public void setShadowOffset(int shadowDx, int shadowDy) {
        this.shadowDx = shadowDx;
        this.shadowDy = shadowDy;
    }

    public void setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
    }

    public void setXlPadding(int xlPaddingLeft, int xlPaddingTop, int xlPaddingRight, int xlPaddingBottom) {
        setXlPaddingLeft(xlPaddingLeft);
        setXlPaddingTop(xlPaddingTop);
        setXlPaddingRight(xlPaddingRight);
        setXlPaddingBottom(xlPaddingBottom);
    }

    public void setXlPaddingLeft(int xlPaddingLeft) {
        if(xlPaddingLeft==XL_PADDING_NO_VALUE) return;
        this.xlPaddingLeft = xlPaddingLeft;
    }

    public void setXlPaddingTop(int xlPaddingTop) {
        if(xlPaddingTop==XL_PADDING_NO_VALUE) return;
        this.xlPaddingTop = xlPaddingTop;
    }

    public void setXlPaddingRight(int xlPaddingRight) {
        if(xlPaddingRight==XL_PADDING_NO_VALUE) return;
        this.xlPaddingRight = xlPaddingRight;
    }

    public void setXlPaddingBottom(int xlPaddingBottom) {
        if(xlPaddingBottom==XL_PADDING_NO_VALUE) return;
        this.xlPaddingBottom = xlPaddingBottom;
        setup();
    }

    public void setXlPaddingBottomRes(int resId) {
        setXlPaddingBottom((int) getResources().getDimension(resId));
    }

    //don't work! use touchableArea
    public void locking() {
        for(int i=0;i<getChildCount();i++){
            getChildAt(i).setEnabled(isReachable);
        }
    }

    public void lock() {
        isReachable = false;
        locking();
    }

    public void unlock() {
        isReachable = true;
        locking();
    }

    public boolean isBlurCover() {
        return isBlurCover;
    }

    public void setBlurCover(boolean blurCover) {
        isBlurCover = blurCover;
        blurBmp = null;
        mBitmap = null;
        setup();
    }

    public float getBlurPercent() {
        return blurPercent;
    }

    public void setBlurPercent(float blurPercent) {
        this.blurPercent = blurPercent;
        setup();
    }

    public void setBlurCover(boolean blurCover, RectF blurBound) {
        setBlurCover(blurCover);
        setBlurBound(blurBound);
        setup();
    }

    public void setBlurCover(boolean blurCover, float blurPercent, RectF blurBound) {
        setBlurCover(blurCover);
        setBlurPercent(blurPercent);
        setBlurBound(blurBound);
        setup();
    }

    public void setBlurCover(boolean blurCover, Path blurPath) {
        setBlurCover(blurCover);
        setBlurPath(blurPath);
        setup();
    }

    public void setBlurCover(boolean blurCover, float blurPercent, Path blurPath) {
        setBlurCover(blurCover);
        setBlurPercent(blurPercent);
        setBlurPath(blurPath);
        setup();
    }

    public void setBlurBound(RectF blurBound) {
        this.blurBound = blurBound;
        this.blurPath = null;
        blurBmp = null;
        setup();
    }

    public void setBlurPath(Path blurPath) {
        this.blurPath = blurPath;
        this.blurBound = null;
        blurBmp = null;
        setup();
    }



    public XLayout(Context context) {
        super(context);
        isViewReady();
    }

    public XLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XLayout, defStyle, 0);

        ibound = a.getInt(R.styleable.XLayout_xl_bound, BOUND_DEFAULT);
        setCornerRatio(a.getFloat(R.styleable.XLayout_xl_corner_rx, 0),
                a.getFloat(R.styleable.XLayout_xl_corner_ry, 0));
        borderWidth = a.getDimensionPixelSize(R.styleable.XLayout_xl_border_width, 0);
        borderColor = a.getColor(R.styleable.XLayout_xl_border_color, Color.WHITE);
        shadowRad = a.getDimensionPixelSize(R.styleable.XLayout_xl_shadow_rad, 0);
        shadowDx = a.getDimensionPixelSize(R.styleable.XLayout_xl_shadow_dx, 0);
        shadowDy = a.getDimensionPixelSize(R.styleable.XLayout_xl_shadow_dy, 0);
        shadowColor = a.getColor(R.styleable.XLayout_xl_shadow_color, Color.BLACK);
        //
        int pad = a.getDimensionPixelSize(R.styleable.XLayout_xl_padding, XL_PADDING_NO_VALUE);
        setXlPadding(pad, pad, pad, pad);
        setXlPaddingLeft(a.getDimensionPixelSize(R.styleable.XLayout_xl_padding_left, XL_PADDING_NO_VALUE));
        setXlPaddingTop(a.getDimensionPixelSize(R.styleable.XLayout_xl_padding_top, XL_PADDING_NO_VALUE));
        setXlPaddingRight(a.getDimensionPixelSize(R.styleable.XLayout_xl_padding_right, XL_PADDING_NO_VALUE));
        setXlPaddingBottom(a.getDimensionPixelSize(R.styleable.XLayout_xl_padding_bottom, XL_PADDING_NO_VALUE));
        //
        mForegrounderEnabled = a.getBoolean(R.styleable.XLayout_xl_foregrounder_enabled, mForegrounderEnabled);
        mFramerLeftEnabled = a.getBoolean(R.styleable.XLayout_xl_framer_left_enabled, mFramerLeftEnabled);
        mFramerTopEnabled = a.getBoolean(R.styleable.XLayout_xl_framer_top_enabled, mFramerTopEnabled);
        mFramerRightEnabled = a.getBoolean(R.styleable.XLayout_xl_framer_right_enabled, mFramerRightEnabled);
        mFramerBottomEnabled = a.getBoolean(R.styleable.XLayout_xl_framer_bottom_enabled, mFramerBottomEnabled);
        mScrollerLeftEnabled = a.getBoolean(R.styleable.XLayout_xl_scroller_left_enabled, mScrollerLeftEnabled);
        mScrollerTopEnabled = a.getBoolean(R.styleable.XLayout_xl_scroller_top_enabled, mScrollerTopEnabled);
        mScrollerRightEnabled = a.getBoolean(R.styleable.XLayout_xl_scroller_right_enabled, mScrollerRightEnabled);
        mScrollerBottomEnabled = a.getBoolean(R.styleable.XLayout_xl_scroller_bottom_enabled, mScrollerBottomEnabled);

        isViewReady();
    }

    public void isViewReady(){
        ViewTreeObserver viewTreeObserver = this.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                XLayout.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                init();
            }
        });
    }

    private void init() {
        if(forgrounder==null) forgrounder = new Forgrounder(this, mForegrounderEnabled);
        else forgrounder.setEnabled(mForegrounderEnabled);
        //
        framer = new Framer(this);
        scroller = new Scroller(this);
        //
        framer.setScroller(scroller);
        scroller.setFramer(framer);
        //
        framer.setEnabled(mFramerLeftEnabled || mFramerTopEnabled || mFramerRightEnabled || mFramerBottomEnabled);
        framer.canFrame(mFramerLeftEnabled, mFramerTopEnabled, mFramerRightEnabled, mFramerBottomEnabled);
        //
        scroller.setEnabled(mScrollerLeftEnabled || mScrollerTopEnabled || mScrollerRightEnabled || mScrollerBottomEnabled);
        scroller.canScroll(mScrollerLeftEnabled, mScrollerTopEnabled, mScrollerRightEnabled, mScrollerBottomEnabled);

        setup();
    }

    private void setup() {
        if(getBackground()==null)
            setBackgroundColor(getResources().getColor(R.color.transparent));

        calculateBound();

        bound = getBound(ibound, mBound);

        invalidate();
    }

    private float blurAlpha = 0;
    private long blurAnimDuration = 1000;
    private long blurAnimPeriod = 50;
    @Override
    public void draw(Canvas canvas) {

        int save = canvas.save();

        ///
        //if(clipBound==null || bound==null){
            calculateBound();
            clipBound = getBound(ibound, mClipBound);
            bound = getBound(ibound, mBound);
        //}
        if(clipBound==null || bound==null) {
            invalidate();
            return;
        }
        ///

        //canvas.clipPath(clipPath);
        canvas.clipPath(clipBound);

        ///shadow
        if(shadowRad>0){
            if(shadowPaint==null){
                shadowPaint = new Paint();
                shadowPaint.setStyle(Paint.Style.STROKE);
                shadowPaint.setColor(shadowColor);
                shadowPaint.setStrokeWidth(Math.max(Math.max(shadowDx, shadowDy), shadowRad));
                shadowPaint.setShadowLayer(shadowRad,shadowDx, shadowDy, shadowColor);
                if(shadowRad>0) shadowPaint.setMaskFilter(new BlurMaskFilter(Math.max(Math.max(shadowDx, shadowDy), shadowRad),
                        BlurMaskFilter.Blur.NORMAL));
            }
            canvas.drawPath(bound, shadowPaint);
        }
        ///

        canvas.clipPath(bound);

        if(isBlurCover && blurBmp!=null && mBitmap!=null){
            canvas.drawBitmap(mBitmap, 0, 0, null);
            onDrawForeground(canvas);
            //
            canvas.restoreToCount(save);
            return;
        }

        super.draw(canvas);

        ///blur
        if(isBlurCover && blurBmp==null){
            mBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
            cvs = new Canvas(mBitmap);
            super.draw(cvs);
            blurBmp = Bitmap.createBitmap(mBitmap);
            //
            if(blurPath!=null) BlurBuilder.blur(getContext(), blurBmp, blurPercent,blurPath);
            else if(blurBound!=null) BlurBuilder.blur(getContext(), blurBmp, blurPercent,blurBound);
            else blurBmp = BlurBuilder.blur(getContext(), blurBmp, blurPercent);
            //
            blurPaint.setShader(new BitmapShader(blurBmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            blurAlpha = 0;
            blurPaint.setAlpha((int) (blurAlpha*255));
            //
            canvas.drawPath(bound, blurPaint);
            //
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    blurAlpha += 1f/(blurAnimDuration/blurAnimPeriod);
                    invalidate();
                }
            }, blurAnimPeriod);
        }
        ///

        ///bodure
        if(borderWidth>0){
            if(borderPaint==null){
                borderPaint = new Paint();
                ///
                if(borderSoftness>0) borderPaint.setMaskFilter(new BlurMaskFilter(borderSoftness, BlurMaskFilter.Blur.NORMAL));
                ///
                borderPaint.setStyle(Paint.Style.STROKE);
                borderPaint.setColor(borderColor);
                borderPaint.setStrokeWidth(borderWidth);
            }
            canvas.drawPath(bound, borderPaint);
        }
        ///

        canvas.restoreToCount(save);
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        if(forgrounder!=null)forgrounder.update(canvas);
        //
        if (isBlurCover && blurBmp!=null && mBitmap!=null){
            blurPaint.setAlpha((int) (blurAlpha*255));
            canvas.drawPath(bound, blurPaint);
            if(1>blurAlpha && blurBmp!=null) {
                new Handler().postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void run() {
                        blurAlpha += 1f/(blurAnimDuration/blurAnimPeriod);
                        invalidate();
                    }
                }, blurAnimPeriod);
            }
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        requestLayout();
        int v = getVisibility();
        setVisibility(GONE);
        setVisibility(v);
    }

    private void calculateBound() {
        /*int p = 20;
        xlPaddingLeft = p;
        xlPaddingTop = p;
        xlPaddingRight = p;
        xlPaddingBottom = p;*/
        mClipBound = new RectF(0+getPaddingLeft(), 0+getPaddingTop(),
                getWidth()-getPaddingRight(), getHeight()-getPaddingBottom());
        mBound = new RectF(mClipBound.left+xlPaddingLeft, mClipBound.top+xlPaddingTop,
                mClipBound.right-xlPaddingRight, mClipBound.bottom-xlPaddingBottom);
    }

    private final int BOUND_ROUNDED_RECT = 0;
    private final int BOUND_ZIG_ZAG_BOTTOM = 1;
    private final int BOUND_ZIG_ZAG_LEFT_BOTTOM = 2;
    private final int BOUND_SNAKE_TOP = 3;
    private final int BOUND_DIAMOND_RIGHT = 4;
    private final int BOUND_DIAMOND_LEFT = 5;
    private final int BOUND_ARROW_RIGHT = 6;
    private final int BOUND_ARROW_LEFT = 7;
    private final int BOUND_CIRCLE = 8;
    private final int BOUND_NIKE_LEFT = 9;
    private Path getBound(int i, RectF rect) {
        if(rect==null) return null;
        //
        Path p = null;
        int rx = (int) (cornerRX*rect.width()/2);
        int ry = (int) (cornerRY*rect.height()/2);
        int offset;
        Point s;
        switch (i){
            case BOUND_ROUNDED_RECT:
                p = new Path();
                p.moveTo(rect.centerX(), rect.top);
                p.lineTo(rect.left+rx, rect.top);
                p.quadTo(rect.left, rect.top, rect.left, rect.top+ry);
                p.lineTo(rect.left, rect.bottom-ry);
                p.quadTo(rect.left, rect.bottom, rect.left+rx, rect.bottom);
                p.lineTo(rect.right-rx, rect.bottom);
                p.quadTo(rect.right, rect.bottom, rect.right, rect.bottom-ry);
                p.lineTo(rect.right, rect.top+ry);
                p.quadTo(rect.right, rect.top, rect.right-rx, rect.top);
                p.close();
                break;
            case BOUND_ZIG_ZAG_BOTTOM:
                int base = (int) (9*rect.height()/10);
                p = new Path();
                p.moveTo(rect.right, rect.top+base);
                p.rLineTo(0, -base);
                p.rLineTo(-rect.width(), 0);
                p.rLineTo(0, base);
                p.quadTo(rect.left+rect.width()/4, rect.bottom, rect.centerX(), rect.top+base);
                p.quadTo(rect.left+3*rect.width()/4, rect.top+base*2-rect.height(), rect.right, rect.top+base);
                p.close();
                break;
            case BOUND_ZIG_ZAG_LEFT_BOTTOM:
                int b1 = (int) (rect.height()/4), b2 = (int) (rect.height()-b1);
                p = new Path();
                p.moveTo(rect.right, rect.top+b2);
                p.rLineTo(0, -b2);
                p.rLineTo(-rect.width(), 0);
                p.rLineTo(0, b1);
                p.rQuadTo(b1/4, 3*b1/4, b1, b1);
                p.rQuadTo(b1, b1/3, 3*b1/2, b1);
                p.quadTo(rect.left+rect.width()/2+b1/2, rect.bottom, rect.right, rect.top+b2);
                p.close();
                break;
            case BOUND_SNAKE_TOP:
                offset = (int) (1*rect.height()/10);
                p = new Path();
                p.moveTo(rect.left, rect.top+2*offset);
                p.quadTo(rect.left, rect.top+offset, rect.left+offset, rect.top+offset);
                p.lineTo(rect.right-offset, rect.top+offset);
                p.quadTo(rect.right, rect.top+offset, rect.right, rect.top);
                p.lineTo(rect.right, rect.bottom);
                p.lineTo(rect.left, rect.bottom);
                p.close();
                break;
            case BOUND_DIAMOND_RIGHT:
                offset = (int) (1*rect.height()/4);
                p = new Path();
                p.moveTo(rect.left, rect.top+ry);
                s = getPointInRaw(rect.left, rect.top, rect.right, rect.top+offset, rx);
                //
                p.quadTo(rect.left, rect.top, s.x, s.y);
                s = getPointInRaw(rect.right, rect.top+offset, rect.left, rect.top, rx);
                p.lineTo(s.x, s.y);
                s = getPointInRaw(rect.right, rect.top+offset, rect.right, rect.bottom, ry);
                //
                p.quadTo(rect.right, rect.top+offset, s.x, s.y);
                s = getPointInRaw(rect.right, rect.bottom, rect.right, rect.top+offset, ry);
                p.lineTo(s.x, s.y);
                s = getPointInRaw(rect.right, rect.bottom, rect.left, rect.bottom-offset, rx);
                //
                p.quadTo(rect.right, rect.bottom, s.x, s.y);
                s = getPointInRaw(rect.left, rect.bottom-offset, rect.right, rect.bottom, rx);
                p.lineTo(s.x, s.y);
                s = getPointInRaw(rect.left, rect.bottom-offset, rect.left, rect.top, ry);
                //
                p.quadTo(rect.left, rect.bottom-offset, s.x, s.y);
                p.close();
                break;
            case BOUND_DIAMOND_LEFT:
                offset = (int) (1*rect.height()/5);
                p = new Path();
                s = getPointInRaw(rect.left, rect.top+offset, rect.left, rect.bottom, ry);
                p.moveTo(s.x, s.y);
                s = getPointInRaw(rect.left, rect.top+offset, rect.right, rect.top, rx);
                //
                p.quadTo(rect.left, rect.top+offset, s.x, s.y);
                s = getPointInRaw(rect.right, rect.top, rect.left, rect.top+offset, rx);
                p.lineTo(s.x, s.y);
                s = getPointInRaw(rect.right, rect.top, rect.right, rect.bottom-offset, ry);
                //
                p.quadTo(rect.right, rect.top, s.x, s.y);
                s = getPointInRaw(rect.right, rect.bottom-offset, rect.right, rect.top, ry);
                p.lineTo(s.x, s.y);
                s = getPointInRaw(rect.right, rect.bottom-offset, rect.left, rect.bottom, rx);
                //
                p.quadTo(rect.right, rect.bottom-offset, s.x, s.y);
                s = getPointInRaw(rect.left, rect.bottom, rect.right, rect.bottom-offset, rx);
                p.lineTo(s.x, s.y);
                s = getPointInRaw(rect.left, rect.bottom, rect.left, rect.top+offset, ry);
                //
                p.quadTo(rect.left, rect.bottom, s.x, s.y);
                p.close();
                break;
            case BOUND_ARROW_RIGHT:
                offset = (int) (1*rect.height()/2);
                p = new Path();
                p.moveTo(rect.left, rect.top+ry);
                s = getPointInRaw(rect.left, rect.top, rect.right, rect.top+offset, rx);
                //
                p.quadTo(rect.left, rect.top, s.x, s.y);
                s = getPointInRaw(rect.right, rect.top+offset, rect.left, rect.top, rx);
                p.lineTo(s.x, s.y);
                s = getPointInRaw(rect.right, rect.top+offset, rect.left, rect.bottom, rx);
                //
                p.quadTo(rect.right, rect.top+offset, s.x, s.y);
                s = getPointInRaw(rect.left, rect.bottom, rect.right, rect.top+offset, rx);
                p.lineTo(s.x, s.y);
                s = getPointInRaw(rect.left, rect.bottom, rect.left, rect.top, ry);
                //
                p.quadTo(rect.left, rect.bottom, s.x, s.y);
                p.close();
                break;
            case BOUND_ARROW_LEFT:
                offset = (int) (1*rect.height()/2);
                p = new Path();
                s = getPointInRaw(rect.left, rect.top+offset, rect.right, rect.top, rx);
                p.moveTo(s.x, s.y);
                s = getPointInRaw(rect.right, rect.top, rect.left, rect.top+offset, rx);
                p.lineTo(s.x, s.y);
                s = getPointInRaw(rect.right, rect.top, rect.right, rect.bottom, ry);
                //
                p.quadTo(rect.right, rect.top, s.x, s.y);
                s = getPointInRaw(rect.right, rect.bottom, rect.right, rect.top, ry);
                p.lineTo(s.x, s.y);
                s = getPointInRaw(rect.right, rect.bottom, rect.left, rect.top+offset, rx);
                //
                p.quadTo(rect.right, rect.bottom, s.x, s.y);
                s = getPointInRaw(rect.left, rect.top+offset, rect.right, rect.bottom, rx);
                p.lineTo(s.x, s.y);
                s = getPointInRaw(rect.left, rect.top+offset, rect.right, rect.top, rx);
                //
                p.quadTo(rect.left, rect.top+offset, s.x, s.y);
                p.close();
                break;
            case BOUND_CIRCLE:
                p = new Path();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    p.addArc(rect.left,rect.top,rect.right,rect.bottom,0,360);
                }
                p.close();
                break;
            case BOUND_NIKE_LEFT:
                offset = (int) (2*rect.height()/5);
                p = new Path();
                p.moveTo(rect.left, rect.bottom-offset);
                p.lineTo(rect.left, rect.top);
                p.lineTo(rect.right, rect.top);
                p.lineTo(rect.right, rect.bottom-offset/2);
                p.lineTo(rect.left+offset, rect.bottom-offset/5);
                p.quadTo(rect.left, rect.bottom, rect.left, rect.bottom-offset);
                p.close();
                break;
        }
        return p;
    }

    private Point getPointInRaw(float startX, float startY, float stopX, float stopY, float distance) {
        return getPointInRaw((int)(startX),(int)(startY),(int)(stopX),(int)(stopY), (int)(distance));
    }

    private Point getPointInRaw(int startX, int startY, int stopX, int stopY, int distance) {
        Point p;
        Point vector = new Point(stopX-startX, stopY-startY);
        p = new Point((int) (startX+distance*(vector.x/getLength(vector))),
                (int) (startY+distance*(vector.y/getLength(vector))));
        return p;
    }

    private float getLength(Point vector){
        return (float) Math.sqrt(Math.pow(vector.x, 2)+Math.pow(vector.y, 2));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(forgrounder==null || framer==null || scroller==null) return false;

        if (inTouchableArea(event.getX(), event.getY()))
            forgrounder.onTouch(event, new RectF(0, 0, getWidth(), getHeight()));
        else
            forgrounder.touchUp = true;
        //
        if(isReachable){
            framer.onTouch(event);
            scroller.onTouch(event);
        }
        //
        return inTouchableArea(event.getX(), event.getY()) && ( /**/super.onTouchEvent(event) || forgrounder.isEnabled || framer.isEnabled || scroller.isEnabled);
    }

    private boolean inTouchableArea(float x, float y) {
        return true;
    }

    public Forgrounder getForgrounder() {
        if(forgrounder==null) forgrounder = new Forgrounder(this);
        return forgrounder;
    }

    public Framer getFramer() {
        return framer;
    }

    public Scroller getScroller() {
        if(scroller==null) scroller = new Scroller(this);
        else scroller.setView(this);
        return scroller;
    }

    public void blurBackground(XLayout background) {
        if(background==null) return;
        background.setBlurCover(true, getBoundFrom(background));
    }

    public void unBlurBackground(XLayout background) {
        background.setBlurCover(false);
    }

    private int ERROR_COLOR = Color.RED;
    private long ERROR_DURATION = 500;
    private float ERROR_BORDER_RATIO = 1f/30;
    private boolean notifColor = true, notifVibrate = true;
    public void notifyError() {
        if(notifColor) {
            int maxBorder = (int) (Math.min(mBound.width(), mBound.height()) * ERROR_BORDER_RATIO);
            setBorderColor(ERROR_COLOR);
            setBorderWidth(maxBorder);
            setBorderSoftness(maxBorder);
        }
        if(notifVibrate) makeVibration();
        //
        invalidate();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(notifColor){
                    setBorderWidth(0);
                    setBorderSoftness(0);
                }
                invalidate();
            }
        }, ERROR_DURATION);
    }

    public void notifyError(boolean notifColor, boolean notifVibrate) {
        this.notifColor = notifColor;
        this.notifVibrate = notifVibrate;
        notifyError();
    }

    @SuppressLint("MissingPermission")
    private void makeVibration() {
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(ERROR_DURATION/2, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(ERROR_DURATION);
        }
    }

    public void setMargin(int left, int top, int right, int bottom) {
        try {
            LayoutParams layoutParams = (LayoutParams) getLayoutParams();
            layoutParams.leftMargin = left;
            layoutParams.topMargin = top;
            layoutParams.rightMargin = right;
            layoutParams.bottomMargin = bottom;
            setLayoutParams(layoutParams);
        }catch (Exception e){}
    }
}

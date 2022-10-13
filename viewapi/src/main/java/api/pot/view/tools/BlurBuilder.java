package api.pot.view.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class BlurBuilder {
    private static float BITMAP_SCALE = 1f;//
    private static float BLUR_RADIUS = 25f;
    private static float bitmap_scale = BITMAP_SCALE;
    private static float blur_radius = BLUR_RADIUS;

    public static  void blur(Context context, Bitmap bmp, float blurPercent, RectF blurBound) {
        Canvas cvs = new Canvas(bmp);
        Paint p = new Paint();
        p.setShader(new BitmapShader(blur(context, bmp, blurPercent), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        cvs.drawRect(blurBound, p);
    }

    public static  void blur(Context context, Bitmap bmp, float blurPercent, Path blurPath) {
        Canvas cvs = new Canvas(bmp);
        Paint p = new Paint();
        p.setShader(new BitmapShader(blur(context, bmp, blurPercent), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        cvs.drawPath(blurPath, p);
    }

    public static Bitmap blur(Context context, Bitmap image, float blurPercent) {
        bitmap_scale = 0.5f;//BITMAP_SCALE;
        blur_radius = BLUR_RADIUS*blurPercent;

        int width = Math.round(image.getWidth() * bitmap_scale);
        int height = Math.round(image.getHeight() * bitmap_scale);

        if(width==0 || height==0 || !(0<blur_radius && blur_radius<=BLUR_RADIUS)) return image;

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(blur_radius);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return Bitmap.createScaledBitmap(outputBitmap, (int) (width*1/bitmap_scale), (int) (height*1/bitmap_scale), false);//outputBitmap;
    }


    /////////+++++++++++++++++++News++++++++++++++++++++++++
    public void blurUnlyByPaint() {
        Paint p = new Paint();
        p.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
    }
}

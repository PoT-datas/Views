package api.pot.view.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

public class Global {
    //permission
    public static boolean permissions_write_external_storage = false;
    public static boolean permissions_read_external_storage = false;
    //directories
    private String cahe_directory_path = null;
    private String internal_storage_directory_path = null;
    private String external_storage_directory_path = null;

    public static final int FIRST_GES_GRADIENT_COLOR = Color.parseColor("#d105c6");
    public static final int SECOND_GES_GRADIENT_COLOR = Color.parseColor("#ff0059");
    public static final int THIRD_GES_GRADIENT_COLOR = Color.parseColor("#ede100");
    public static final int MAIN_GES_COLOR = Color.parseColor("#4b0082");
    public static final int MAIN_GES_GOLD_COLOR = Color.parseColor("#FFD700");
    //
    public static final int MAIN_GES_TEXT_COLOR = Color.parseColor("#ffffff");
    public static final int TEXT_COLOR_ON_MAIN_GES_TEXT_COLOR = MAIN_GES_COLOR;
    //
    public static final int MAIN_GES_FG_TOUCH_COLOR = Color.parseColor("#000000");
    public static final int MAIN_GES_FG_TOUCH_START_ALPHA = 50;
    public static final int MAIN_GES_FG_TOUCH_END_ALPHA = 0;
    public static final int MAIN_GES_FG_TOUCH_DURATION = 300;
    //
    public static final int GES_DESCRIPTOR_TEXT_SIZE = 40;
    //
    public static NumberFormat numberFormat = new DecimalFormat("0.##");
    //
    public static final int DRACULA_STYLE_GES_CARD_BG_COLOR = Color.parseColor("#868686");
    public static final int NORMAL_STYLE_GES_CARD_BG_COLOR = Color.WHITE;
    //

    public static int ges_card_bg_color = NORMAL_STYLE_GES_CARD_BG_COLOR;

    public static long invalidate_time = 40;//ms(30)
    public static long ctv_invalidate_time = 50;//ms(30)

    public static final long foreground_anim_time = 500;//ms
    public static final int foreground_anim_alpha = 70;

    public static String civ_backup_color_selector_image_name = "color_selector_bitmap_backup.png";


    public static void useContext(Context context) {
        try {
            setGESMainTypeFace(context);
        }catch (Exception e){}
    }

    public static Typeface ges_main_type_face = null;
    public static Typeface ges_large_type_face = null;
    public static Typeface ges_design_type_face = null;

    public static String downloader_cache_dir = Environment.getExternalStorageDirectory() + "/PoTViews/cache/";

    public static String HOST_NAME = "192.168.43.216";
    public static String SERVER_ADDRESS = "http://"+HOST_NAME+"/";
    public static String PACKofTHINKERS_ADDRESS = SERVER_ADDRESS+"packofthinkers/";

    public static void setGESMainTypeFace(Context context){
        ges_main_type_face =  Typeface.create(
                Typeface.createFromAsset(context.getAssets(), "fonts/AlluraRegular.ttf"),
                Typeface.BOLD);
        ges_large_type_face =  Typeface.create(
                Typeface.createFromAsset(context.getAssets(), "fonts/coiny-regular.ttf"),
                Typeface.BOLD);
        ges_design_type_face =  Typeface.create(
                Typeface.createFromAsset(context.getAssets(), "fonts/Amadeus.ttf"),
                Typeface.BOLD);
    }

    public static String getCacheDirForBackupCV(Context context){//CV=Custom View
        return context.getCacheDir() + File.separator + "CV" + File.separator + "backup" + File.separator + "images";
    }

    public static boolean isViewIntoContainer(View view, RectF containerBound){
        if(view==null) return false;
        //
        if(containerBound==null) containerBound = new RectF(0, 0, getScreenSize(view.getContext())[0], getScreenSize(view.getContext())[1]);
        //
        RectF viewBound = getViewBoundFrom(view);
        //
        return (viewBound.bottom>containerBound.top) && (containerBound.bottom>viewBound.top) &&
                (viewBound.right>containerBound.left) && (containerBound.right>viewBound.left);
    }

    public static RectF getViewBoundFrom(View view) {
        return getViewBoundFrom(view, null);
    }

    public static RectF getViewBoundFrom(View view, View from) {
        int[] location = new int[2];

        /*view.getLocationOnScreen(location);
        view.getLocationInWindow(location);
        RectF viewBound = new RectF();
        //
        viewBound = new RectF(location[0], location[1],
                location[0]+view.getWidth(), location[1]+view.getHeight());
        return viewBound;*/




        Rect bound = new Rect();

        //For coordinates location relative to the parent
        view.getLocalVisibleRect(bound);


        if(from!=null){
            bound = new Rect();

            //For coordinates location relative to the screen/display
            view.getGlobalVisibleRect(bound);

            Rect boundF = new Rect();
            from.getGlobalVisibleRect(boundF);
            return new RectF(bound.left-boundF.left, bound.top-boundF.top, bound.right-boundF.left, bound.bottom-boundF.top);
        }


        return new RectF(bound.left, bound.top, bound.right, bound.bottom);
    }

    public static double math_round(double nbr, int order){
        return Math.round(nbr * Math.pow(10, order))/Math.pow(10, order);
    }

    public static int[] getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        //
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        return new int[]{width, height};
    }

    public static String getRightDivisionNumber(long nbr){
        String ret = "", textVersion = ""+nbr;
        while (textVersion.length()!=0){
            if(textVersion.length()>=3) {
                ret = textVersion.substring(textVersion.length()-3) + (ret.length()==0?ret:" "+ret);
                textVersion = textVersion.substring(0, textVersion.length()-3);
            }else {
                ret = textVersion + (ret.length()==0?ret:" "+ret);
                textVersion = "";
            }
        }
        return  ret;
    }

    /*this function must run first
    * when this API start using
    * */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void checkStateAndInit(Context context){
        /*
         * power save mode
         * */
        //check state
        final PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm.isPowerSaveMode()) {
            invalidate_time = 500;
        } else {
            invalidate_time = 40;
        }
        //set listener
        BroadcastReceiver powerSaverChangeReceiver = new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onReceive(Context context, Intent intent) {
                final PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                if (pm.isPowerSaveMode()) {
                    Global.invalidate_time = 500;
                } else {
                    Global.invalidate_time = 40;
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
        context.registerReceiver(powerSaverChangeReceiver, filter);
        /*
         * end power save mode
         * */
    }

    public static double random(double a, double b){
        return (a+(b-a)*Math.random());
    }

    public static class DateDatas{
        CharSequence c = "jfjj";
        public static String getDayOfWeek(Date date, Language language){
            return (String) DateFormat.format("EEEE", date);
        }
        public static String getMonth(Date date, Language language){
            return (String) DateFormat.format("MMM",  date);
        }
        public static String getDay(Date date, Language language){
            return (String) DateFormat.format("dd",   date);
        }

        public static String getYear(Date date, Language french) {
            return (String) DateFormat.format("yyyy", date);
        }
        /*public static String getMonth(int id, Language language){
            String dayOfTheWeek = (String) DateFormat.format("EEEE", date); // Thursday
            String day          = (String) DateFormat.format("dd",   date); // 20
            String monthString  = (String) DateFormat.format("MMM",  date); // Jun
            String monthNumber  = (String) DateFormat.format("MM",   date); // 06
            String year         = (String) DateFormat.format("yyyy", date); // 2013
            return "Août";
        }*/
    }

    public enum Language{
        ENGLAND, FRENCH;
    }

    public static int dp(Context context, int value) {
        return (int) (context.getResources().getDisplayMetrics().density * value + 0.5f);
    }

}

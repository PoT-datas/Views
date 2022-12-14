package api.pot.view.xsb;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import api.pot.view.xsb.tools.Align;
import api.pot.view.xsb.tools.Duration;
import api.pot.view.xsb.tools.Type;

public class Xnackbar {
    // main context items
    private static Context snackContext;
    private static android.support.design.widget.Snackbar snackbar;
    private static Xnackbar singleton;

    // variables
    private static int colorCode = Type.getColorCode(Type.SUCCESS);
    private static String snackMessage = "Hi there !";
    private static int snackDuration = Duration.getDuration(Duration.SHORT);
    private static View view;

    private static boolean isCustomView;
    private static boolean isFillParent;
    private static Align textAlign;

    public static Xnackbar with(Context context, View fab) {
        snackContext = context.getApplicationContext();
        if (singleton == null)
            singleton = new Xnackbar();

        if (fab == null) {
            View rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
            view = rootView;
            snackbar = android.support.design.widget.Snackbar
                    .make(view, "", snackDuration);
        } else {
            view = fab;
            snackbar = android.support.design.widget.Snackbar
                    .make(view, "", snackDuration);
        }

        isCustomView = false;
        isFillParent = false;
        textAlign = Align.LEFT;

        return singleton;
    }

    public static Xnackbar type(Type type) {
        colorCode = Type.getColorCode(type);
        return singleton;
    }

    public static Xnackbar type(Type type, int color) {
        if (type == Type.CUSTOM)
            colorCode = color;
        else
            colorCode = Type.getColorCode(type);
        return singleton;
    }

    public static Xnackbar message(CharSequence displayingMessage) {
        snackMessage = displayingMessage.toString();
        return singleton;
    }

    public static Xnackbar duration(Duration duration) {
        if (duration != Duration.CUSTOM) {
            snackDuration = Duration.getDuration(duration);
        }
        return singleton;
    }

    public static Xnackbar duration(Duration durationType, int duration) {
        if (durationType == Duration.CUSTOM) {
            snackDuration = duration;
        }
        return singleton;
    }

    public static Xnackbar contentView(final View view) {
        return contentView(view, 70);
    }

    public static Xnackbar contentView(final View view, int heightInDp) {
        isCustomView = true;

        final android.support.design.widget.Snackbar.SnackbarLayout snackLayout = (android.support.design.widget.Snackbar.SnackbarLayout) snackbar.getView();
        android.support.design.widget.Snackbar.SnackbarLayout.LayoutParams params =
                (android.support.design.widget.Snackbar.SnackbarLayout.LayoutParams) snackLayout.getLayoutParams();

        params.height = (int) pxFromDp(heightInDp);

        TextView textView = (TextView) snackLayout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

        snackLayout.addView(view, 0, params);
        return singleton;
    }

    public static Xnackbar fillParent(boolean fillParent) {
        isFillParent = fillParent;
        return singleton;
    }

    public static Xnackbar textAlign(Align align) {
        textAlign = align;
        return singleton;
    }

    private static View getSnackBarLayout() {
        if (snackbar != null) {
            return snackbar.getView();
        }
        return null;
    }

    private static Xnackbar setColor(int colorId) {
        View snackBarView = getSnackBarLayout();
        if (snackBarView != null) {
            snackBarView.setBackgroundColor(colorId);
        }

        return singleton;
    }

    private static void setTextAlignment(android.support.design.widget.Snackbar snackbar) {
        TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        textView.setLayoutParams(params);

        switch (textAlign) {
            case CENTER:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                else
                    textView.setGravity(Gravity.CENTER_HORIZONTAL);
                break;
            case RIGHT:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                else
                    textView.setGravity(Gravity.RIGHT);
                break;
            case LEFT:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                else
                    textView.setGravity(Gravity.LEFT);
                break;
            default:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                else
                    textView.setGravity(Gravity.LEFT);
                break;
        }
    }

    public static void show() {
        if (isCustomView) {
            snackbar.setDuration(snackDuration);
            //
            setColor(colorCode);
            //
            snackbar.show();
        } else {
            snackbar = android.support.design.widget.Snackbar
                    .make(view, snackMessage, snackDuration)
                    .setDuration(snackDuration);

            if (isFillParent)
                snackbar.getView().getLayoutParams().width = AppBarLayout.LayoutParams.MATCH_PARENT;

            setTextAlignment(snackbar);

            setColor(colorCode);

            View snackbarView = snackbar.getView();
            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setMaxLines(10);
        }
        snackbar.show();
    }

    private static float pxFromDp(int dp) {
        return dp * snackContext.getResources().getDisplayMetrics().density;
    }

    public static void dismiss() {
        if (snackbar != null) {
            if (snackbar.isShown()) {
                snackbar.dismiss();
            }
        }
    }

    public Xnackbar setAction(String label, View.OnClickListener onClickListener) {
        //use perso listener for a view
        //snackbar.setAction(label, onClickListener);
        return singleton;
    }
}

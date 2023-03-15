package api.pot.view.tips;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;

public class XTip{
    private XTip xTip;
    private ToolTipsManager toolTipsManager;

    /*public static XTip with(){

    }*/

    public static void displayToolTip(ToolTipsManager toolTipsManager, String msg, int position, int align, View anchorView, View root) {
        displayToolTip(toolTipsManager, msg, position, align, anchorView, root, Color.BLUE);
    }

    public static void displayToolTip(ToolTipsManager toolTipsManager, String msg, int position, int align, View anchorView, View root, int color) {
        toolTipsManager.findAndDismiss(anchorView);
        ///
        if(!isVisible(anchorView)) return;
        ///
        ToolTip.Builder builder=new ToolTip.Builder(root.getContext(),anchorView, (ViewGroup) root,msg,position);
        builder.setAlign(align);
        builder.setBackgroundColor(color);
        toolTipsManager.show(builder.build());
    }

    private static boolean isVisible(View anchorView) {
        if(anchorView.getVisibility()==View.VISIBLE) return true;
        View p= (View) anchorView.getParent();
        try{
            while ( p.getVisibility()!=View.VISIBLE ){
                p= (View) p.getParent();
            }
        }catch (Exception e){}
        return p.getVisibility()==View.VISIBLE;
    }
}

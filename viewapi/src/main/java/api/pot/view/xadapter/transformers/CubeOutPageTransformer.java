package api.pot.view.xadapter.transformers;

import android.support.v4.view.ViewPager;
import android.view.View;

public class CubeOutPageTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage( View page, float pos ) {
        page.setPivotX( pos < 0f ? page.getWidth() : 0f );
        page.setPivotY( page.getHeight() * 0.5f );
        page.setRotationY( 90f * pos );
    }
}

package api.pot.view.xadapter.transformers;

import android.support.v4.view.ViewPager;
import android.view.View;

public class FlipVerticalPageTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage( View page, float pos ) {
        final float rotation = -180f * pos;

        page.setAlpha( rotation > 90f || rotation < -90f ? 0f : 1f );
        page.setPivotX( page.getWidth() * 0.5f );
        page.setPivotY( page.getHeight() * 0.5f );
        page.setRotationX( rotation );
    }
}
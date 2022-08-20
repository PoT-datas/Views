package api.pot.view.xadapter.transformers;

import android.support.v4.view.ViewPager;
import android.view.View;

public class FlipHorizontalPageTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage( View page, float pos ) {
        final float rotation = 180f * pos;

        page.setAlpha( rotation > 90f || rotation < -90f ? 0 : 1 );
        page.setPivotX( page.getWidth() * 0.5f );
        page.setPivotY( page.getHeight() * 0.5f );
        page.setRotationY( rotation );
    }
}
package api.pot.view.xlistview;

import android.view.View;

public interface XItemViewTouchListener {
    void onLeftSwipe(View view, int position);
    void onRightSwipe(View view, int position);
    void onClick(View view, int position);
    void onLongClick(View view, int position);
    void onTouchDown(View view, int position);
}

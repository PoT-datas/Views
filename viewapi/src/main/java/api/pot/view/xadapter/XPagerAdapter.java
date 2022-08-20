package api.pot.view.xadapter;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class XPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private List<Fragment> pageList;

    public XPagerAdapter(Context context, List<Fragment> pageList, FragmentManager fm) {
        super(fm);
        mContext = context;
        this.pageList = pageList;
    }

    @Override
    public Fragment getItem(int position) {
        return pageList.get(position);
    }

    @Override
    public int getCount() {
        return pageList.size();
    }

}
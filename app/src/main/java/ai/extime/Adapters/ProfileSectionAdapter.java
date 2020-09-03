package ai.extime.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrew on 10.03.2018.
 */

public class ProfileSectionAdapter extends FragmentPagerAdapter {

    private final List<Fragment> listOfFragment = new ArrayList<>();

    private final List<String> listOfTitleFragment = new ArrayList<>();

    public void addFragment(Fragment fragment, String title){
        listOfFragment.add(fragment);
        listOfTitleFragment.add(title);
    }

    public ProfileSectionAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position){
        return listOfTitleFragment.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return listOfFragment.get(position);
    }

    @Override
    public int getCount() {
        return listOfFragment.size();
    }
}

package ai.extime.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.extime.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

public class ContactsSectionAdapter extends FragmentPagerAdapter implements SmartTabLayout.TabProvider {

    private final List<Fragment> listOfFragment = new ArrayList<>();

    private final List<String> listOfTitleFragment = new ArrayList<>();

    private final List<Long> listOfTitleCount = new ArrayList<>();

    private Context context;

    public void addFragment(Fragment fragment, String title, long count){
        listOfFragment.add(fragment);
        listOfTitleFragment.add(title);
        listOfTitleCount.add(count);
    }

    public ContactsSectionAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;

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

    @Override
    public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
        View v = LayoutInflater.from(this.context).inflate(R.layout.kanban_row_head, null);

        int px_start = (int) (17 * context.getResources().getDisplayMetrics().density);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if(listOfTitleFragment.get(position).equals("All contacts")){
            lp.setMargins(px_start,0,30,0);
        }else{
            lp.setMargins(30,0,30,0);
        }

        if(listOfTitleFragment.get(position).equals("All contacts")){

            ((ImageView) v.findViewById(R.id.imageTab)).setImageDrawable(context.getResources().getDrawable(R.drawable.icn_contacts_all_favorites));
            ((TextView) v.findViewById(R.id.text_title_tab)).setText("All");
        }else if(listOfTitleFragment.get(position).equals("Favorites")){

            ((ImageView) v.findViewById(R.id.imageTab)).setImageDrawable(context.getResources().getDrawable(R.drawable.star));
            ((TextView) v.findViewById(R.id.text_title_tab)).setText("Favorites");
        }else if(listOfTitleFragment.get(position).equals("Important")){
            lp.setMargins(30,0,30,0);
            ((ImageView) v.findViewById(R.id.imageTab)).setImageDrawable(context.getResources().getDrawable(R.drawable.icn_important));
            ((TextView) v.findViewById(R.id.text_title_tab)).setText("Important");
        }else if(listOfTitleFragment.get(position).equals("Pause")){
            lp.setMargins(30,0,30,0);
            ((ImageView) v.findViewById(R.id.imageTab)).setImageDrawable(context.getResources().getDrawable(R.drawable.pause));
            ((TextView) v.findViewById(R.id.text_title_tab)).setText("Pause");
        }else if(listOfTitleFragment.get(position).equals("Finished")){
            lp.setMargins(30,0,30,0);
            ((ImageView) v.findViewById(R.id.imageTab)).setImageDrawable(context.getResources().getDrawable(R.drawable.icn_finished));
            ((TextView) v.findViewById(R.id.text_title_tab)).setText("Finished");
        }else if(listOfTitleFragment.get(position).equals("Crown")){
            lp.setMargins(30,0,30,0);
            ((ImageView) v.findViewById(R.id.imageTab)).setImageDrawable(context.getResources().getDrawable(R.drawable.crown));
            ((TextView) v.findViewById(R.id.text_title_tab)).setText("Crown");
        }else if(listOfTitleFragment.get(position).equals("VIP")){
            lp.setMargins(30,0,30,0);
            ((ImageView) v.findViewById(R.id.imageTab)).setImageDrawable(context.getResources().getDrawable(R.drawable.vip_new));
            ((TextView) v.findViewById(R.id.text_title_tab)).setText("VIP");
        }else if(listOfTitleFragment.get(position).equals("Investor")){
            lp.setMargins(30,0,30,0);
            ((ImageView) v.findViewById(R.id.imageTab)).setImageDrawable(context.getResources().getDrawable(R.drawable.investor_));
            ((TextView) v.findViewById(R.id.text_title_tab)).setText("Investor");
        }else if(listOfTitleFragment.get(position).equals("Startup")){
            lp.setMargins(30,0,30,0);
            ((ImageView) v.findViewById(R.id.imageTab)).setImageDrawable(context.getResources().getDrawable(R.drawable.startup));
            ((TextView) v.findViewById(R.id.text_title_tab)).setText("Startup");
        }
        ((TextView) v.findViewById(R.id.text_count_tab)).setText(String.valueOf(listOfTitleCount.get(position)));

        v.setLayoutParams(lp);

        v.requestLayout();

        return v;
    }



}

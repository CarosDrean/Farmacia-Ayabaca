package xyz.drean.ayabacafarm.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import xyz.drean.ayabacafarm.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Products extends Fragment {

    AppBarLayout appBarLayout = null;
    TabLayout tabLayout = null;
    ViewPager viewPager = null;

    public Products() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_products, container, false);
        if(savedInstanceState == null) {
            insertTabs(container);
            viewPager = v.findViewById(R.id.pager);
            populateViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
        }
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        appBarLayout.removeView(tabLayout);
    }

    private void insertTabs(ViewGroup container){
        Activity activity = getActivity();
        View padre = (View) container.getParent();
        appBarLayout = padre.findViewById(R.id.appbar);
        assert activity != null;
        tabLayout = new TabLayout(activity);
        tabLayout.setTabTextColors(getResources().getColor(R.color.semi_banco), getResources().getColor(R.color.blanco));
        appBarLayout.addView(tabLayout);
    }

    private void populateViewPager(ViewPager viewPager){
        AdapterSections adapter = new AdapterSections(getFragmentManager());
        adapter.addFragment(Categories.newInstance(0), getString(R.string.pastillas));
        adapter.addFragment(Categories.newInstance(1), getString(R.string.jarabes));
        adapter.addFragment(Categories.newInstance(2), getString(R.string.injectables));
        viewPager.setAdapter(adapter);
    }

    class AdapterSections extends FragmentStatePagerAdapter {
        List<Fragment> fragments = new ArrayList();
        List<String> titles = new ArrayList();

        AdapterSections(FragmentManager fm) {
            super(fm);
        }

        void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

}

package com.csuft.phoneinterception.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.csuft.phoneinterception.R;
import com.csuft.phoneinterception.fragment.AboutFragment;
import com.csuft.phoneinterception.fragment.PhoneFragment;
import com.csuft.phoneinterception.fragment.RulesFragment;
import com.csuft.phoneinterception.util.ToastShow;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.view_pager)
    ViewPager viewPager;


    List<Fragment> fragmentList  = new ArrayList<>();

    TabAdapter tabAdapter;

    long exitTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initView() {
        tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.setScrollBarSize(4);
//        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }


    class TabAdapter extends FragmentPagerAdapter{


        String[] title = {"拦截记录","拦截规则","关于"};

        public TabAdapter(FragmentManager fm) {
            super(fm);
            fragmentList.add(new PhoneFragment());
            fragmentList.add(new RulesFragment());
            fragmentList.add(new AboutFragment());
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }
    }


    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis()-exitTime>2000){
            ToastShow.showToast(this,"程序退出后不再拦截，再按一次退出");
            exitTime = System.currentTimeMillis();
            return;
        }else {
            finish();;
            System.exit(0);
        }
        super.onBackPressed();
    }
}

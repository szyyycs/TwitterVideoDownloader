package com.ycs.smartcanteen.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.tencent.mmkv.MMKV;
import com.ycs.smartcanteen.ui.CookGenerateFragment;
import com.ycs.smartcanteen.R;
import com.ycs.smartcanteen.ui.buy.BuyFragment;
import com.ycs.smartcanteen.ui.home.HomeFragment;
import com.ycs.smartcanteen.ui.notifications.NotificationsFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView navigationView;
    private ViewPager viewPager;
    private BuyFragment bf=new BuyFragment();
    //private DashboardFragment df=new DashboardFragment();
    private CookGenerateFragment cgf=new CookGenerateFragment();
    private HomeFragment hf=new HomeFragment();
    private NotificationsFragment nf=new NotificationsFragment();
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MMKV.initialize(this);
        Toasty.Config.getInstance()
                .setGravity(Gravity.CENTER)
               // .allowQueue(false)
                .apply();
        viewPager=findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(this);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setLabelVisibilityMode(NavigationBarView.FIND_VIEWS_WITH_TEXT);
        navigationView.setOnNavigationItemSelectedListener(this);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        return hf;
                    case 1:
                        return cgf;
                    case 2:
                        return bf;
                    case 3:
                        return nf;
                }
                return null;
            }

            @Override
            public int getCount() {
                return 4;
            }
        });
        viewPager.setOffscreenPageLimit(4);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        navigationView.getMenu().getItem(position).setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        viewPager.setCurrentItem(item.getOrder());
        return true;
    }
}
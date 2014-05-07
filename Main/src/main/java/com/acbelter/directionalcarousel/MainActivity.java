package com.acbelter.directionalcarousel;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.acbelter.directionalcarousel.page.PageItem;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    private CarouselPagerAdapter mPagerAdapter;
    private CarouselViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int size = 5;
        ArrayList<PageItem> items = new ArrayList<PageItem>(size);
        for (int i = 0; i < size; i++) {
            items.add(new PageItem("Item " + i));
        }

        mViewPager = (CarouselViewPager) findViewById(R.id.carousel_pager);
        mPagerAdapter = new CarouselPagerAdapter(getSupportFragmentManager(), items);

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(mPagerAdapter);
        mViewPager.setCurrentItem(mPagerAdapter.getFirstPage());
    }
}
package com.acbelter.directionalcarouseldemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.acbelter.directionalcarousel.CarouselPagerAdapter;
import com.acbelter.directionalcarousel.CarouselViewPager;
import com.acbelter.directionalcarousel.page.OnPageClickListener;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnPageClickListener<MyPageItem> {
    private CarouselPagerAdapter<MyPageItem> mPagerAdapter;
    private CarouselViewPager mViewPager;
    private ArrayList<MyPageItem> mItems;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    ProgressBar loading;
    boolean canLoadMore = false;
    boolean isMeasuringLoadMore = false;
    boolean isBusyLoadingMore = false;
    int measureLoadMoreStart = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.viewTopic);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.timer_progress);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.GONE);

        loading = (ProgressBar) findViewById(R.id.loading_progress);
        loading.setVisibility(View.GONE);

        if (savedInstanceState == null) {
            int size = 20;
            mItems = new ArrayList<MyPageItem>(size);
            for (int i = 0; i < size; i++) {
                mItems.add(new MyPageItem("Item " + i));
            }
        } else {
            mItems = savedInstanceState.getParcelableArrayList("items");
        }

        mViewPager = (CarouselViewPager) findViewById(R.id.carousel_pager);
        mPagerAdapter = new CarouselPagerAdapter<MyPageItem>(getSupportFragmentManager(),
                MyPageFragment.class, R.layout.page_layout, mItems);
        mPagerAdapter.setOnPageClickListener(this);

        mViewPager.setAdapter(mPagerAdapter);


        mViewPager.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View view, MotionEvent motionEvent) {
                Log.e("onGenericMotion", "" + motionEvent.getAction() + "   " + motionEvent.getY());
                return false;
            }
        });
        mViewPager.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                Log.e("onDrag", "" + dragEvent.getAction() + "   " + dragEvent.getY());
                return false;
            }
        });
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e("onTouch", "" + motionEvent.getAction() + "   " + motionEvent.getX());
                if (canLoadMore && !isBusyLoadingMore) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_POINTER_DOWN:
                            initMeasureProgress((int) motionEvent.getX());
                            break;

                        case MotionEvent.ACTION_MOVE:
                            refreshMeasureProgress((int) motionEvent.getX());
                            break;

                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_POINTER_UP:
                        case MotionEvent.ACTION_CANCEL:
                            stopMeasureProgress();
                            break;

                        default:
                            break;
                    }
                    return isMeasuringLoadMore;
                }
                return false;
            }
        });
        mPagerAdapter.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
                Log.e("onPageScrolled", " POS: " + position + ",  v: " + v + ", i1: " + i1 + "");
                // if position is zero, v is lower than / equal to zero, i1 is lower than / equal to zero,
                // enable swipe to refresh
                if (position <= 0 && v <= 0 && i1 <= 0) {
                    swipeRefreshLayout.setEnabled(true);
                    canLoadMore = false;
                } else if (position >= mItems.size() - 2 && v >= 0.9) {
                    canLoadMore = true;
                } else {
                    swipeRefreshLayout.setEnabled(false);
                    canLoadMore = false;
                }

                if (isBusyLoadingMore) {
                    swipeRefreshLayout.setEnabled(false);
                }
            }

            @Override
            public void onPageSelected(int position) {
                Log.e(" onPageSelected", " position: " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e("  onPageScrollStateCh", " state: " + state);
                Log.e("  current Page", " getCurrentItem(): " + mViewPager.getCurrentItem());
            }
        });
    }



    void initMeasureProgress(int startPos) {
        isMeasuringLoadMore = true;
        measureLoadMoreStart = startPos;
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);
    }
    void refreshMeasureProgress(int pos) {
        int offset = pos - measureLoadMoreStart;
        if (offset > 0) {
            stopMeasureProgress();
            return;
        }
        Log.e("OFFSET", offset + "");
        progressBar.setProgress(-offset / 10);
    }
    void stopMeasureProgress() {
        isMeasuringLoadMore = false;
        measureLoadMoreStart = 0;
        if (progressBar.getProgress() >= 60) {
            loadMore();

        }
        progressBar.setProgress(0);
        progressBar.setVisibility(View.GONE);
    }


    void refresh() {
        Log.e("REFRESH", "YEAH");
        isBusyLoadingMore = true;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isBusyLoadingMore = false;
                loading.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }


    void loadMore() {
        Log.e("LOAD MORE", "YEAH");
        isBusyLoadingMore = true;
        loading.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isBusyLoadingMore = false;
                loading.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("items", mItems);
    }

    @Override
    public void onSingleTap(View view, MyPageItem item) {
        Toast.makeText(getApplicationContext(), "Single tap: " + item.getTitle(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDoubleTap(View view, MyPageItem item) {
        Toast.makeText(getApplicationContext(), "Double tap: " + item.getTitle(),
                Toast.LENGTH_SHORT).show();
    }
}

package bingo.android.demo.ui;

import java.util.ArrayList;
import java.util.List;

import bingo.android.demo.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

//TODO:添加循环功能
public class ViewPagerDemoActivity extends Activity implements OnClickListener, OnPageChangeListener {
	// 引导图片资源
	private static final int[] pics = { R.drawable.cloud, R.drawable.station,
			R.drawable.park };

	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;

	// 底部小店图片
	private ImageView[] dots;

	// 记录当前选中位置
	private int currentIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpagerdemo);

		views = new ArrayList<View>();

		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		
		// 初始化引导图片列表
		for (int i = 0; i < pics.length; i++) {
			ImageView iv = new ImageView(this);
			iv.setLayoutParams(mParams);
			iv.setImageResource(pics[i]);
			views.add(iv);
		}
		
		vp = (ViewPager) findViewById(R.id.viewpager);
		// 初始化Adapter
		vpAdapter = new ViewPagerAdapter(views);
		vp.setAdapter(vpAdapter);
		// 绑定回调
		vp.setOnPageChangeListener(this);

		// 初始化底部小点
		initDots();
	}

	private void initDots() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

		dots = new ImageView[pics.length];

		//TODO:如何设置dp等单位
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(50, 50);
		
		// 循环取得小点图片
		for (int i = 0; i < pics.length; i++) {
			
			//dots[i] = (ImageView) ll.getChildAt(i);
			dots[i] = new ImageView(this);
			dots[i].setLayoutParams(mParams);
			dots[i].setImageResource(R.drawable.dot);
			
			dots[i].setEnabled(true);// 都设为灰色
			dots[i].setOnClickListener(this);
			dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
			
			ll.addView(dots[i]);
		}
		
		/*
		 <ImageView
         android:layout_width="50dp"
         android:layout_height="50dp"
         android:scaleType="fitXY"
         android:layout_gravity="center_vertical"
         android:clickable="true"
         android:padding="15.0dip"
         android:src="@drawable/dot" />
         */
		
		currentIndex = 0;
		dots[currentIndex].setEnabled(false);// 设置为白色，即选中状态
	}

	/**
	 * 设置当前的引导页
	 */
	private void setCurView(int position) {
		if (position < 0 || position >= pics.length) {
			return;
		}

		vp.setCurrentItem(position);
	}

	/**
	 * 这只当前引导小点的选中
	 */
	private void setCurDot(int positon) {
		if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
			return;
		}

		dots[positon].setEnabled(false);
		dots[currentIndex].setEnabled(true);

		currentIndex = positon;
	}

	@Override
	public void onClick(View v) {
        int position = (Integer)v.getTag();
        setCurView(position);
        setCurDot(position);
    }

	//当滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int arg0) {
    	Log.v("ViewPagerDemoActivity", "onPageScrollStateChanged");
        
    }

    //当当前页面被滑动时调用
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    	Log.v("ViewPagerDemoActivity", "onPageScrolled");
    }

    //当新的页面被选中时调用
    @Override
    public void onPageSelected(int arg0) {
    	Log.v("ViewPagerDemoActivity", "onPageSelected");
        //设置底部小点选中状态
        setCurDot(arg0);
    }
    
    class ViewPagerAdapter extends PagerAdapter{
        
        //界面列表
        private List<View> views;
        
        public ViewPagerAdapter (List<View> views){
            this.views = views;
        }

        //销毁arg1位置的界面
        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(views.get(arg1));        
        }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub
            
        }

        //获得当前界面数
        @Override
        public int getCount() {
            if (views != null)
            {
                return views.size();
            }
            
            return 0;
        }
        

        //初始化arg1位置的界面
        @Override
        public Object instantiateItem(View arg0, int arg1) {
            
            ((ViewPager) arg0).addView(views.get(arg1), 0);
            
            return views.get(arg1);
        }

        //判断是否由对象生成界面
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return (arg0 == arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub
            
        }

    }
}

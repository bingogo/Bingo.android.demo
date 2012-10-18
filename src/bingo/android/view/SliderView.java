package bingo.android.view;

import bingo.android.demo.R;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class SliderView extends RelativeLayout {
	private static final String TAG = "SliderView";
	// private List<View> views;
	private ViewPager sliderViewPager;
	private LinearLayout sliderDots;
	private PagerAdapter adapter;
	// 记录当前选中位置
	private int currentIndex;
	// 底部小店图片
	private ImageView[] dots;
	private Context context;
	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		// 当滑动状态改变时调用
		@Override
		public void onPageScrollStateChanged(int arg0) {
			Log.v(TAG, "onPageScrollStateChanged");

		}

		// 当当前页面被滑动时调用
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			Log.v(TAG, "onPageScrolled");
		}

		// 当新的页面被选中时调用
		@Override
		public void onPageSelected(int arg0) {
			Log.v(TAG, "onPageSelected");
			// 设置底部小点选中状态
			setCurDot(arg0);
		}

	};

	public SliderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.slider_basic, this);

		sliderDots = (LinearLayout) view.findViewById(R.id.slider_dots);
		sliderViewPager = (ViewPager) view.findViewById(R.id.slider_viewpager);

		sliderViewPager.setOnPageChangeListener(this.pageChangeListener);

		// int childCount = sliderViewPager.getChildCount();
		// Log.v(TAG, "sliderViewPager.getChildCount:" + childCount);
		// initDots();
	}

	public void setAdapter(PagerAdapter adapter) {
		this.adapter = adapter;
		if (adapter != null && sliderViewPager != null)
			sliderViewPager.setAdapter(adapter);
		initDots();
	}

	// 初始化底部小点
	private void initDots() {
		if (this.adapter != null) {
			int count = this.adapter.getCount();
			dots = new ImageView[count];
			// LinearLayout.LayoutParams mParams = new
			// LinearLayout.LayoutParams(50, 50);
			LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			// 循环取得小点图片
			for (int i = 0; i < count; i++) {
				dots[i] = new ImageView(this.context);
				dots[i].setLayoutParams(mParams);
				dots[i].setImageResource(R.drawable.dot);

				dots[i].setEnabled(false);// 都设为灰色
				// dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
				sliderDots.addView(dots[i]);
			}

			currentIndex = 0;
			dots[currentIndex].setEnabled(true);
		}
	}

	/**
	 * 设置当前的引导页
	 */
	// private void setCurView(int position) {
	// if (position < 0 || position >= adapter.getCount()) {
	// return;
	// }
	//
	// sliderViewPager.setCurrentItem(position);
	// }

	/**
	 * 这只当前引导小点的选中
	 */
	private void setCurDot(int positon) {
		if (positon < 0 || positon > adapter.getCount() - 1
				|| currentIndex == positon) {
			return;
		}

		dots[positon].setEnabled(true);
		dots[currentIndex].setEnabled(false);

		currentIndex = positon;
	}

}

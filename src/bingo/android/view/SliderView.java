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
	// ��¼��ǰѡ��λ��
	private int currentIndex;
	// �ײ�С��ͼƬ
	private ImageView[] dots;
	private Context context;
	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		// ������״̬�ı�ʱ����
		@Override
		public void onPageScrollStateChanged(int arg0) {
			Log.v(TAG, "onPageScrollStateChanged");

		}

		// ����ǰҳ�汻����ʱ����
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			Log.v(TAG, "onPageScrolled");
		}

		// ���µ�ҳ�汻ѡ��ʱ����
		@Override
		public void onPageSelected(int arg0) {
			Log.v(TAG, "onPageSelected");
			// ���õײ�С��ѡ��״̬
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

	// ��ʼ���ײ�С��
	private void initDots() {
		if (this.adapter != null) {
			int count = this.adapter.getCount();
			dots = new ImageView[count];
			// LinearLayout.LayoutParams mParams = new
			// LinearLayout.LayoutParams(50, 50);
			LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			// ѭ��ȡ��С��ͼƬ
			for (int i = 0; i < count; i++) {
				dots[i] = new ImageView(this.context);
				dots[i].setLayoutParams(mParams);
				dots[i].setImageResource(R.drawable.dot);

				dots[i].setEnabled(false);// ����Ϊ��ɫ
				// dots[i].setTag(i);// ����λ��tag������ȡ���뵱ǰλ�ö�Ӧ
				sliderDots.addView(dots[i]);
			}

			currentIndex = 0;
			dots[currentIndex].setEnabled(true);
		}
	}

	/**
	 * ���õ�ǰ������ҳ
	 */
	// private void setCurView(int position) {
	// if (position < 0 || position >= adapter.getCount()) {
	// return;
	// }
	//
	// sliderViewPager.setCurrentItem(position);
	// }

	/**
	 * ��ֻ��ǰ����С���ѡ��
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

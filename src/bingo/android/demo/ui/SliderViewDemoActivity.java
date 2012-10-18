package bingo.android.demo.ui;

import java.util.ArrayList;
import java.util.List;

import bingo.android.demo.R;
import bingo.android.demo.ui.ViewPagerDemoActivity.ViewPagerAdapter;
import bingo.android.view.SliderView;
import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SliderViewDemoActivity extends Activity {
	// ����ͼƬ��Դ
	private static final int[] pics = { R.drawable.cloud, R.drawable.station };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slider_basic_demo);

		SliderView slider = (SliderView) this.findViewById(R.id.slider);

		List<View> views = new ArrayList<View>();
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		// ��ʼ������ͼƬ�б�
		for (int i = 0; i < pics.length; i++) {
			ImageView iv = new ImageView(this);
			iv.setLayoutParams(mParams);
			iv.setImageResource(pics[i]);
			views.add(iv);
		}
		slider.setAdapter(new ViewPagerAdapter(views));
	}

	class ViewPagerAdapter extends PagerAdapter {

		// �����б�
		private List<View> views;

		public ViewPagerAdapter(List<View> views) {
			this.views = views;
		}

		// ����arg1λ�õĽ���
		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(views.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

		// ��õ�ǰ������
		@Override
		public int getCount() {
			if (views != null) {
				return views.size();
			}

			return 0;
		}

		// ��ʼ��arg1λ�õĽ���
		@Override
		public Object instantiateItem(View arg0, int arg1) {

			((ViewPager) arg0).addView(views.get(arg1), 0);

			return views.get(arg1);
		}

		// �ж��Ƿ��ɶ������ɽ���
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

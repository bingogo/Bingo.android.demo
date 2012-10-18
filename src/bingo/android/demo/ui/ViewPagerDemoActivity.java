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

//TODO:���ѭ������
public class ViewPagerDemoActivity extends Activity implements OnClickListener, OnPageChangeListener {
	// ����ͼƬ��Դ
	private static final int[] pics = { R.drawable.cloud, R.drawable.station,
			R.drawable.park };

	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;

	// �ײ�С��ͼƬ
	private ImageView[] dots;

	// ��¼��ǰѡ��λ��
	private int currentIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpagerdemo);

		views = new ArrayList<View>();

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
		
		vp = (ViewPager) findViewById(R.id.viewpager);
		// ��ʼ��Adapter
		vpAdapter = new ViewPagerAdapter(views);
		vp.setAdapter(vpAdapter);
		// �󶨻ص�
		vp.setOnPageChangeListener(this);

		// ��ʼ���ײ�С��
		initDots();
	}

	private void initDots() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

		dots = new ImageView[pics.length];

		//TODO:�������dp�ȵ�λ
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(50, 50);
		
		// ѭ��ȡ��С��ͼƬ
		for (int i = 0; i < pics.length; i++) {
			
			//dots[i] = (ImageView) ll.getChildAt(i);
			dots[i] = new ImageView(this);
			dots[i].setLayoutParams(mParams);
			dots[i].setImageResource(R.drawable.dot);
			
			dots[i].setEnabled(true);// ����Ϊ��ɫ
			dots[i].setOnClickListener(this);
			dots[i].setTag(i);// ����λ��tag������ȡ���뵱ǰλ�ö�Ӧ
			
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
		dots[currentIndex].setEnabled(false);// ����Ϊ��ɫ����ѡ��״̬
	}

	/**
	 * ���õ�ǰ������ҳ
	 */
	private void setCurView(int position) {
		if (position < 0 || position >= pics.length) {
			return;
		}

		vp.setCurrentItem(position);
	}

	/**
	 * ��ֻ��ǰ����С���ѡ��
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

	//������״̬�ı�ʱ����
    @Override
    public void onPageScrollStateChanged(int arg0) {
    	Log.v("ViewPagerDemoActivity", "onPageScrollStateChanged");
        
    }

    //����ǰҳ�汻����ʱ����
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    	Log.v("ViewPagerDemoActivity", "onPageScrolled");
    }

    //���µ�ҳ�汻ѡ��ʱ����
    @Override
    public void onPageSelected(int arg0) {
    	Log.v("ViewPagerDemoActivity", "onPageSelected");
        //���õײ�С��ѡ��״̬
        setCurDot(arg0);
    }
    
    class ViewPagerAdapter extends PagerAdapter{
        
        //�����б�
        private List<View> views;
        
        public ViewPagerAdapter (List<View> views){
            this.views = views;
        }

        //����arg1λ�õĽ���
        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(views.get(arg1));        
        }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub
            
        }

        //��õ�ǰ������
        @Override
        public int getCount() {
            if (views != null)
            {
                return views.size();
            }
            
            return 0;
        }
        

        //��ʼ��arg1λ�õĽ���
        @Override
        public Object instantiateItem(View arg0, int arg1) {
            
            ((ViewPager) arg0).addView(views.get(arg1), 0);
            
            return views.get(arg1);
        }

        //�ж��Ƿ��ɶ������ɽ���
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

package bingo.android.demo;

import bingo.android.util.ThreadUtil;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HandlerActivity extends Activity implements OnClickListener {
	private Thread workerThread;
	private WorkHandler handler;
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.handler);

		Button startThread = (Button) this.findViewById(R.id.startThread);
		startThread.setOnClickListener(this);

		Button workerThreadState = (Button) this
				.findViewById(R.id.workerThreadState);
		workerThreadState.setOnClickListener(this);

		Button mainThreadState = (Button) this
				.findViewById(R.id.mainThreadState);
		mainThreadState.setOnClickListener(this);

		textView = (TextView) this.findViewById(R.id.textView);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v("HandlerActivity", "onDestroy!");
		if(workerThread!= null ) {
			workerThread.interrupt();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.startThread:
			handler = new WorkHandler(this);
			workerThread = new Thread() {
				// Context context = HandlerActivity.this.getApplication();
				@Override
				public void run() {
					while (true) {
						Message msg = handler.obtainMessage();
						msg.what = 1;
						handler.sendMessageDelayed(msg, 2000);
						Log.v("workerThread", ThreadUtil.getThreadSignature(Thread.currentThread()));
						Log.v("workerThread", "sendMessageDelayed ok!");
						try {
							Thread.sleep(20000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Log.v("workerThread", "worker one time work over!");

						if (handler == null) {
							Log.v("workerThread", "handler is null!");
						}

						if (HandlerActivity.this == null) {
							Log.v("workerThread", "activity is null!");
						}
					}
				}
			};
			workerThread.start();
			break;
		case R.id.workerThreadState:
			updateUI(ThreadUtil.getThreadSignature(workerThread));
			break;
		case R.id.mainThreadState:
			updateUI(ThreadUtil.getThreadSignature(Thread.currentThread()));
			break;
		}
	}

	public void updateUI(String text) {
		textView.setText(textView.getText() + "\r\n" + text);
	}

	private static class WorkHandler extends Handler {
		private HandlerActivity acticity;

		public WorkHandler(HandlerActivity acticity) {
			this.acticity = acticity;
		}

		@Override
		public void handleMessage(Message msg) {
			Log.v("WorkHandler", "handleMessage!");			
			acticity.updateUI(String.valueOf(msg.what));
			Log.v("WorkHandler", ThreadUtil.getThreadSignature(Thread.currentThread()));
		}
	}

}

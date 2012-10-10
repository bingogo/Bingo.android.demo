package bingo.android.demo.network;

import bingo.android.util.NetworkUtil;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class NetworkActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TextView tv = new TextView(this);
		tv.setText(String.valueOf(NetworkUtil.networkEnabled(this)));
		
		setContentView(tv);
	}

}

package bingo.android.util;

import android.content.Context;
import android.content.Intent;

public class UiUtil {
	public static void startActivity(Context context, Class<?> activity) {
		Intent intent = new Intent(context, activity);
		context.startActivity(intent);
	}
}

package bingo.android.util;

public class ThreadUtil {
	/*
	 * 获取线程id
	 */
	public static long getThreadId(Thread thread) {
		return thread.getId();
	}

	public static String getThreadSignature(Thread thread) {
		return String.format(
				"thread id:[%1$d], name:[%2$s], priority:[%3$s], state:[%4$s], group:[%5$s]", thread.getId(),
				thread.getName(), thread.getPriority(), thread.getState(),
				thread.getThreadGroup());
	}
}

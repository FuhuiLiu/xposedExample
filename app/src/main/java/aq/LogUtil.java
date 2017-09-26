package aq;

import android.util.Log;

/**
 * Created by Administrator on 2017/9/26 0026.
 */

public class LogUtil {
    public static final String TAG = "AQCXBOM";
    public static boolean isLogFile = false;
    public static void log(String ct) {
        Log.i(TAG, ct);
    }
}

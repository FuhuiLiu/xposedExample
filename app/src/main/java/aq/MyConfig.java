package aq;

import android.util.Log;

import org.json.JSONObject;

import java.io.File;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by Administrator on 2017/9/24 0024.
 */

public class MyConfig {
    public static String TAG = "AqCxBoM" ;
    public static void LOGI(String ct){ Log.i(TAG, ct); }
    public static void XLOGI(String ct){ XposedBridge.log(ct); }
    private static MyConfig ins = new MyConfig();
    public static String TargetPackageName = "";
    public static String TargetClassPath = "";
    public static String TargetFuncName = "";
    public static Object[] paremter3 = null;
    public static MyConfig getIns()
    {
        return ins;
    }

    //读取配置文件
    public boolean loadConfig(){
        File modelFile = new File("/data/local/tmp/aqConfig.txt");
        if(modelFile.exists()){
            LOGI("载入配置文件...");
            try {
                //
                String strFile = FileUtils.readFile(modelFile.getAbsolutePath());
                JSONObject jobj = new JSONObject(strFile);
                TargetPackageName = jobj.getString("TargetPackageName");
                TargetClassPath = jobj.getString("TargetClassPath");
                TargetFuncName = jobj.getString("TargetFuncName");
//                LOGI("配置文件目标包名 >>> " + TargetPackageName);
//                LOGI("配置文件目标类路径 >>> " + TargetClassPath);
//                LOGI("配置文件目标函数名 >>> " + TargetFuncName);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}

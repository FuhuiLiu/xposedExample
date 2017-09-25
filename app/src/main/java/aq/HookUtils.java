package aq;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Environment;
import android.util.Log;

import java.io.Externalizable;
import java.io.File;

import dalvik.system.DexFile;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Administrator on 2017/9/25 0025.
 */

public class HookUtils {
    public static ClassLoader loader;
    public static Context context;
    public static String curLoadPackageName;

    public static void doHook(XC_LoadPackage.LoadPackageParam lpparam){
        loader = lpparam.classLoader;
        curLoadPackageName = lpparam.packageName;

        //多DEX、动态DEX加载HOOK处理
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, mulDexHook);

        //调试所有APP
//        XposedBridge.hookAllMethods(XposedHelpers.findClass("com.android.server.pm.PackageManagerService",
//                lpparam.classLoader), "getPackageInfo", DebugAllAppHook);
    }

    public static void log(String ct) {
        XposedBridge.log(ct);
    }

    //打印调用栈
    public static void printStack(){
        Exception ep = new Exception();
        StackTraceElement[] tree = ep.getStackTrace();
        log("=================当前函数调用栈=================");
        for (StackTraceElement se:tree){
            log(se.getClassName() + ":->" + se.getMethodName() + " ==> " + se.getFileName() +  ":" + se.getLineNumber());
        }
        log("=================当前函数调用栈=================");
    }

    public static XC_MethodHook hook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            log("getPath:" + Environment.getExternalStorageDirectory().getPath());
            String dexPath = "/data/local/tmp/classes.zip";
            File file = new File(dexPath);
            if(file.exists()){
                log("file exist!!");
            }
            log("dexPath:" + dexPath);
            try {
                DexFile df = new DexFile(dexPath);
                Class clazz = df.loadClass("aq.HotFix", loader);
                if (clazz != null) {
                    clazz.getDeclaredMethod("invokeB").invoke(null);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            DexFile df = new DexFile("/data/local/tmp" + "/classes.zip");
            Class clazz = df.loadClass("aq.HotFix", loader);
            if(clazz != null){
                clazz.getDeclaredMethod("invokeA").invoke(null);
            }
        }
    };

    //多DEX、动态DEX加载HOOK处理
    public static XC_MethodHook mulDexHook = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            context = (Context) param.args[0];
            //读取配置文件
            MyConfig.getIns().loadConfig();
            if (curLoadPackageName.equals(MyConfig.TargetPackageName)) {
                log("目标包已加载" + " - " + curLoadPackageName);
            }
            if(curLoadPackageName.equals("aqcxbom.cracktools")){
                Object[] obj3 = new Object[3];
                obj3[0] = Context.class;
                obj3[1] = String.class;
                obj3[2] = hook;
                XposedHelpers.findAndHookMethod("com.crackUtil.AppInfoUtils", loader, "getAppSignature", obj3);
            }
        }
    };

    /**
     * 修改flag，添加 FLAG_DEBUGGABLE FLAG_ALLOW_BACKUP 标识
     */
    private static XC_MethodHook DebugAllAppHook = new XC_MethodHook(){
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            PackageInfo pi = (PackageInfo)param.getResult();
            if(pi != null) {
                ApplicationInfo ai = pi.applicationInfo;
                int flags = ai.flags;
                //添加备份flag
                if((flags & ApplicationInfo.FLAG_ALLOW_BACKUP) == 0) {
                    flags |= ApplicationInfo.FLAG_ALLOW_BACKUP;
                }
                //添加调试flag
                if((flags & ApplicationInfo.FLAG_DEBUGGABLE) == 0) {
                    flags |= ApplicationInfo.FLAG_DEBUGGABLE;
                }
                ai.flags = flags;
                param.setResult(pi);
            }
        }
    };

}

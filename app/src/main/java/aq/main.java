package aq;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import java.io.File;

import dalvik.system.DexFile;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Administrator on 2017/9/24 0024.
 */

public class main implements IXposedHookLoadPackage {
    private ClassLoader loader;
    public Context context;
    private String curLoadPackageName;

    public static void log(String ct) {
        XposedBridge.log(ct);
    }
    //打印调用栈
    public static void printStack(){
        Exception ep = new Exception();
        StackTraceElement[] tree = ep.getStackTrace();
        log("=================当前函数调用栈=================");
        for (StackTraceElement se:tree){
            log(se.getClassName() + ":" + se.getMethodName() + " ==> " + se.getFileName() +  ":" + se.getLineNumber());
        }
        log("=================当前函数调用栈=================");
    }
    public XC_MethodHook hook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            log("getPath:" + Environment.getExternalStorageDirectory().getPath());
            String dexPath = "/data/local/tmp/classes.zip";
            File file = new File(dexPath);
            if(file.exists()){
                log("file exist!!");
            }
            log("dexPath:" + dexPath);
            DexFile df = new DexFile(dexPath);
            Class clazz = df.loadClass("aq.HotFix", loader);
            if(clazz != null){
                clazz.getDeclaredMethod("invokeB").invoke(null);
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
    public XC_MethodHook mulDexHook = new XC_MethodHook() {
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

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        loader = loadPackageParam.classLoader;
        curLoadPackageName = loadPackageParam.packageName;
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, mulDexHook);
    }
}

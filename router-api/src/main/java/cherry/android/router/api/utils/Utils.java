package cherry.android.router.api.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;

/**
 * Created by Administrator on 2017/5/25.
 */

public class Utils {
    public static List<String> getFileNameByPackage(Context context, String packageName) {
        List<String> classNames = new ArrayList<>();
        String path = getSourcePaths(context);
        if (path != null) {
            DexFile dexFile;
            try {
                if (path.endsWith(".zip")) {
                    dexFile = DexFile.loadDex(path, path + ".tmp", 0);
                } else {
                    dexFile = new DexFile(path);
                }
                Enumeration<String> enumeration = dexFile.entries();
                while (enumeration.hasMoreElements()) {
                    String className = enumeration.nextElement();
                    if (className.contains(packageName)) {
                        classNames.add(className);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return classNames;
    }

    private static String getSourcePaths(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            File sourceApk = new File(applicationInfo.sourceDir);
            Log.i("Test", "sourceApk=" + sourceApk);
            return applicationInfo.sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}

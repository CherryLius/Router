package cherry.android.router.api.utils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cherry.android.router.api.Picker;
import cherry.android.router.api.RouteRule;
import cherry.android.router.api.Router;
import cherry.android.router.api.RouterInternal;
import cherry.android.router.api.convert.Converter;
import dalvik.system.DexFile;

import static cherry.android.router.api.RouteRule.TYPE_ACTIVITY;
import static cherry.android.router.api.RouteRule.TYPE_FRAGMENT;
import static cherry.android.router.api.RouteRule.TYPE_MATCHER;

/**
 * Created by Administrator on 2017/5/25.
 */

public class Utils {
    private static final String TAG = "Utils";

    public static List<String> getFileNameByPackage(Context context, String packageName) {
        List<String> classNames = new ArrayList<>();
        List<String> sourcePaths = getSourcePaths(context);
        for (String path : sourcePaths) {
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
        }
        return classNames;
    }

    private static List<String> getSourcePaths(Context context) {
        List<String> sourcePaths = new ArrayList<>();
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            Logger.i(TAG, "sourceApk=" + applicationInfo.sourceDir);
            sourcePaths.add(applicationInfo.sourceDir);

            if (Router.debuggable()) { // Search instant run support only debuggable
                sourcePaths.addAll(tryLoadInstantRunDexFile(applicationInfo));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return sourcePaths;
    }

    /**
     * Get instant run dex path, used to catch the branch usingApkSplits=false.
     */
    private static List<String> tryLoadInstantRunDexFile(ApplicationInfo applicationInfo) {
        List<String> instantRunSourcePaths = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && null != applicationInfo.splitSourceDirs) {
            // add the splite apk, normally for InstantRun, and newest version.
            instantRunSourcePaths.addAll(Arrays.asList(applicationInfo.splitSourceDirs));
            for (int i = 0; i < applicationInfo.splitSourceDirs.length; i++) {
                Logger.i("Test", "split=" + applicationInfo.splitSourceDirs[i]);
            }
            Logger.d(TAG, "Found InstantRun support");
        } else {
            try {
                // This man is reflection from Google instant run sdk, he will tell me where the dex files go.
                Class pathsByInstantRun = Class.forName("com.android.tools.fd.runtime.Paths");
                Method getDexFileDirectory = pathsByInstantRun.getMethod("getDexFileDirectory", String.class);
                String instantRunDexPath = (String) getDexFileDirectory.invoke(null, applicationInfo.packageName);

                File instantRunFilePath = new File(instantRunDexPath);
                if (instantRunFilePath.exists() && instantRunFilePath.isDirectory()) {
                    File[] dexFile = instantRunFilePath.listFiles();
                    for (File file : dexFile) {
                        if (null != file && file.exists() && file.isFile() && file.getName().endsWith(".dex")) {
                            instantRunSourcePaths.add(file.getAbsolutePath());
                        }
                    }
                    Logger.d(TAG, "Found InstantRun support");
                }
            } catch (Exception e) {
                Logger.e(TAG, "InstantRun support error, " + e.getMessage());
            }
        }

        return instantRunSourcePaths;
    }

    //(\w+)://([^/:]+)(:\d*)?([^# ]*)/
    //^(\w+://)?([\w\-]+(\.[\w\-]+)*\/)*[\w\-]+(\.[\w\-]+)*\/?(:\d*)?(\?([\w\-\.,@?^=%&:\/~\+#]*)+)?
    public static boolean checkRouteValid(String uri) {
        if (uri == null || uri.trim().isEmpty())
            return false;
        uri = uri.trim();
//        String regex = "^(\\w+://)?([\\w\\-]+(\\.[\\w\\-]+)*\\/)*(\\/?)[\\w\\-]+\\/?(\\.[\\w\\-]+)*\\/?(:\\d*)?(\\?([\\w\\-\\.,@?^=%&:\\/~\\+#]*)+)?";
        String regex = "^(\\w+://)?([\\w\\-]+(\\.[\\w\\-]+)*\\/)*[(\\/?)\\w\\-]+(\\.[\\w\\-]+)*\\/?(:\\d*)?(\\?([\\w\\-\\.,@?^=%&:\\/~\\+#]*)+)?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(uri);
        if (!matcher.matches()) {
            Logger.e(TAG, "invalid uri format: " + uri);
            return false;
        }
        while (matcher.find()) {
            Logger.e(TAG, "uri match ====> " + matcher.group());
        }
        return true;
    }

    public static void fillRouteTable(Map<String, RouteRule> routeTable, String uri, Class<?> destination,
                                      @RouteRule.Type int type, String... interceptors) {
        if (!routeTable.containsKey(uri)) {
            if (!Utils.checkRouteValid(uri))
                throw new IllegalArgumentException("Invalid uri: " + uri);
            RouteRule rule = Utils.findRouteRuleByClass(routeTable, destination);
            if (rule == null) {
                rule = RouteRule.newRule(uri, destination, type, interceptors);
            }
            routeTable.put(uri, rule);
        }
    }

    public static RouteRule findRouteRuleByClass(Map<String, RouteRule> routeTable, Class<?> cls) {
        if (routeTable == null || routeTable.isEmpty())
            return null;
        for (Map.Entry<String, RouteRule> entry : routeTable.entrySet()) {
            if (entry.getValue().getDestination().equals(cls)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static void putValue2Bundle(Bundle bundle, String key, Object value) {
        if (bundle == null) return;
        if (value instanceof String) {
            bundle.putString(key, (String) value);
        } else if (value instanceof Integer) {
            bundle.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            bundle.putBoolean(key, (Boolean) value);
        } else if (value instanceof Long) {
            bundle.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            bundle.putFloat(key, (Float) value);
        } else if (value instanceof Double) {
            bundle.putDouble(key, (Double) value);
        } else if (value instanceof Character) {
            bundle.putChar(key, (Character) value);
        } else if (value instanceof Short) {
            bundle.putShort(key, (Short) value);
        } else if (value instanceof CharSequence) {
            bundle.putCharSequence(key, (CharSequence) value);
        } else if (value instanceof Bundle) {
            bundle.putBundle(key, (Bundle) value);
        } else if (value instanceof Byte) {
            bundle.putByte(key, (Byte) value);
        } else if (value instanceof IBinder) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                bundle.putBinder(key, (IBinder) value);
            } else {
                throw new UnsupportedOperationException("Bundle cannot put Binder, sdk version is Lower than "
                        + Build.VERSION_CODES.JELLY_BEAN_MR2);
            }
        } else if (value instanceof Parcelable) {
            bundle.putParcelable(key, (Parcelable) value);
        } else if (value instanceof Serializable) {
            bundle.putSerializable(key, (Serializable) value);
        } else {
            /*else if (value instanceof byte[]) {
            bundle.putByteArray(key, (byte[]) value);
        } else if (value instanceof boolean[]) {
            bundle.putBooleanArray(key, (boolean[]) value);
        } else if (value instanceof char[]) {
            bundle.putCharArray(key, (char[]) value);
        } else if (value instanceof CharSequence[]) {
            bundle.putCharSequenceArray(key, (CharSequence[]) value);
        } else if (value instanceof double[]) {
            bundle.putDoubleArray(key, (double[]) value);
        } else if (value instanceof float[]) {
            bundle.putFloatArray(key, (float[]) value);
        } else if (value instanceof int[]) {
            bundle.putIntArray(key, (int[]) value);
        } else if (value instanceof long[]) {
            bundle.putLongArray(key, (long[]) value);
        } else if (value instanceof Parcelable[]) {
            bundle.putParcelableArray(key, (Parcelable[]) value);
        } else if (value instanceof short[]) {
            bundle.putShortArray(key, (short[]) value);
        } else if (value instanceof Size) {
            bundle.putSize(key, (Size) value);
        } else if (value instanceof SizeF) {
            bundle.putSizeF(key, (SizeF) value);
        } else if (value instanceof String[]) {
            bundle.putStringArray(key, (String[]) value);
        }*/
        }
    }

    public static void checkNonNull(Object object, String name, String className) {
        if (object == null)
            throw new NullPointerException(
                    String.format("field '%s' in '%s' is nonNull",
                            name, className));
    }

    public static Picker getPicker(@NonNull String className) {
        try {
            Class<?> cls = Class.forName(className);
            Constructor constructor = cls.getConstructor();
            return (Picker) constructor.newInstance();
        } catch (ClassNotFoundException e) {
            Logger.e(TAG, "ClassNotFound", e);
            throw new IllegalStateException("Class Not Found", e);
        } catch (NoSuchMethodException e) {
            Logger.e(TAG, "NoSuchMethodException", e);
        } catch (IllegalAccessException e) {
            Logger.e(TAG, "IllegalAccessException", e);
        } catch (InstantiationException e) {
            Logger.e(TAG, "InstantiationException", e);
        } catch (InvocationTargetException e) {
            Logger.e(TAG, "InvocationTargetException", e);
        }
        throw new IllegalArgumentException("cant newInstance by class: " + className);
    }

    public static <T> void validateServiceInterface(Class<T> service) {
        if (!service.isInterface()) {
            throw new IllegalArgumentException("API declarations must be interfaces.");
        }
        // Prevent API interfaces from extending other interfaces. This not only avoids a bug in
        // Android (http://b.android.com/58753) but it forces composition of API declarations which is
        // the recommended pattern.
        if (service.getInterfaces().length > 0) {
            throw new IllegalArgumentException("API interfaces must not extend other interfaces.");
        }
    }

    public static boolean checkValidDestination(Class<?> destination) {
        return isActivity(destination) || isFragment(destination);
    }

    public static boolean isActivity(Class<?> destination) {
        return destination != null && Activity.class.isAssignableFrom(destination);
    }

    public static boolean isFragment(Class<?> destination) {
        return destination != null && (Fragment.class.isAssignableFrom(destination)
                || android.support.v4.app.Fragment.class.isAssignableFrom(destination));
    }

    @RouteRule.Type
    public static int getDestinationType(Class<?> destination) {
        if (destination == null)
            return TYPE_MATCHER;
        if (isActivity(destination)) {
            return TYPE_ACTIVITY;
        } else if (isFragment(destination)) {
            return TYPE_FRAGMENT;
        }
        return TYPE_MATCHER;
    }

    public static Map<String, String> splitQueryParameters(Uri rawUri) {
        String query = rawUri.getEncodedQuery();

        if (query == null) {
            return Collections.emptyMap();
        }

        Map<String, String> paramMap = new LinkedHashMap<>();
        int start = 0;
        do {
            int next = query.indexOf('&', start);
            int end = (next == -1) ? query.length() : next;

            int separator = query.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            String name = query.substring(start, separator);

            if (!TextUtils.isEmpty(name)) {
                String value = (separator == end ? "" : query.substring(separator + 1, end));
                paramMap.put(Uri.decode(name), Uri.decode(value));
            }

            // Move start to end of name.
            start = end + 1;
        } while (start < query.length());

        return Collections.unmodifiableMap(paramMap);
    }

    public static Map<String, String> splitQueryParameters(@NonNull String query) {

        if (query == null) {
            return Collections.emptyMap();
        }

        Map<String, String> paramMap = new LinkedHashMap<>();
        int start = 0;
        do {
            int next = query.indexOf('&', start);
            int end = (next == -1) ? query.length() : next;

            int separator = query.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            String name = query.substring(start, separator);

            if (!TextUtils.isEmpty(name)) {
                String value = (separator == end ? "" : query.substring(separator + 1, end));
                paramMap.put(Uri.decode(name), Uri.decode(value));
            }

            // Move start to end of name.
            start = end + 1;
        } while (start < query.length());

        return Collections.unmodifiableMap(paramMap);
    }

    private static final Class<?>[] RAW_CLASS = {
            byte.class,
            char.class,
            int.class,
            long.class,
            short.class,
            float.class,
            double.class,
            boolean.class
    };

    public static boolean isJsonType(Type type) {
        if (type.equals(String.class)) {
            return false;
        }
        Class<?> typeClass = (Class<?>) type;
        if (Number.class.isAssignableFrom(typeClass))//基本数据类型的包装类，除Boolean
            return false;
        if (Boolean.class.isAssignableFrom(typeClass))
            return false;
        return !typeClass.isPrimitive();//8个基本数据类型
    }

    public static <T> T fromJson(Type type, String json) {
        if (TextUtils.isEmpty(json))
            return null;
        try {
            Converter<String, T> converter = RouterInternal.get().classConverter(type, null);
            if (converter != null)
                return converter.convert(json);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("convert from json Error.", e);
        }
    }

    public static boolean isGoodJson(String text) {
        if (TextUtils.isEmpty(text))
            return false;
        return (text.charAt(0) == '{' && text.charAt(text.length() - 1) == '}')
                || (text.startsWith("[{") && text.endsWith("}]"));
    }

    public static boolean isGoodJson(Class<?> clazz, Object value) {
        if (value == null)
            return false;
        if (clazz.isAssignableFrom(value.getClass())) {
            return false;
        }
        String text = String.valueOf(value);
        return isGoodJson(text);
    }

    public static <T> T cast(Class<T> cls, Object value, String name, String className) {
        if (value == null)
            return null;
        try {
            return cls.cast(value);
        } catch (Exception e) {
            String message = String.format("field '%s' in '%s' cast %s Error.",
                    name, className, cls.getCanonicalName());
            throw new RuntimeException(message, e);
        }
    }
}

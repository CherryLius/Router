package cherry.android.router.compiler.common;

import com.squareup.javapoet.ClassName;

/**
 * Created by Administrator on 2017/5/25.
 */

public interface Values {
    String ROUTER_PACKAGE_NAME = "cherry.android.router";
    String OPTION_MODULE_NAME = "ModuleName";
    String ACTIVITY_CLASS_NAME = "android.app.Activity";
    String FRAGMENT_CLASS_NAME = "android.app.Fragment";
    String SUPPORT_V4_FRAGMENT_CLASS_NAME = "android.support.v4.app.Fragment";

    ClassName PICKER = ClassName.get("cherry.android.router.api", "Picker");
    ClassName INTERCEPTOR = ClassName.get("cherry.android.router.api.intercept", "IInterceptor");
    ClassName ROUTE_RULE = ClassName.get("cherry.android.router.api", "RouteRule");
    ClassName ROUTE_RULE_TYPE = ClassName.get("cherry.android.router.api", "RouteMeta", "Type");

    ClassName ROUTE_INTERCEPTOR = ClassName.get("cherry.android.router.api", "RouteInterceptor");
    ClassName ROUTE_UTILS = ClassName.get("cherry.android.router.api.utils", "Utils");

    ClassName ROUTE_BUNDLE = ClassName.get("cherry.android.router.api.bundle", "FieldBundle");
    ClassName ANDROID_OS_BUNDLE = ClassName.get("android.os", "Bundle");
    ClassName ANDROID_OS_PARCELABLE = ClassName.get("android.os", "Parcelable");
    String PARCELABLE_NAME = "android.os.Parcelable";
}

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

    ClassName ROUTER_PICKER = ClassName.get("cherry.android.router.api", "IRoutePicker");
    ClassName INTERCEPTOR_PICKER = ClassName.get("cherry.android.router.api", "InterceptorPicker");
    ClassName INTERCEPTOR = ClassName.get("cherry.android.router.api.intercept", "IInterceptor");
    ClassName ROUTE_META = ClassName.get("cherry.android.router.api", "RouteMeta");
    ClassName INTERCEPTOR_META = ClassName.get("cherry.android.router.api", "InterceptorMeta");
}
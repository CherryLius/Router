package cherry.android.router.compiler.generate;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;

import cherry.android.router.compiler.RouteClass;

import static cherry.android.router.compiler.common.Values.ROUTER_PACKAGE_NAME;
import static cherry.android.router.compiler.common.Values.ROUTER_PICKER;
import static cherry.android.router.compiler.common.Values.ROUTE_META;

/**
 * Created by Administrator on 2017/5/25.
 */

public class RoutePickerGenerator implements IGenerator {

    private final String mModuleName;
    private List<RouteClass> mRouteList;

    public RoutePickerGenerator(String moduleName) {
        mModuleName = moduleName;
        mRouteList = new ArrayList<>();
    }

    public void addRoute(RouteClass routeClass) {
        mRouteList.add(routeClass);
    }

    @Override
    public JavaFile generate() {
        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(mModuleName + "_RoutePicker")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ROUTER_PICKER)
                .addMethod(buildPickMethod());
        return JavaFile.builder(ROUTER_PACKAGE_NAME, typeBuilder.build()).build();
    }

    private MethodSpec buildPickMethod() {
//        ParameterizedTypeName classArgument = ParameterizedTypeName.get(ClassName.get(Class.class),
//                WildcardTypeName.subtypeOf(Object.class));
        ParameterizedTypeName param = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class), ROUTE_META);

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("pick")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(ParameterSpec.builder(param, "routeTable").build());

        for (RouteClass route : mRouteList) {
            methodBuilder.addCode(route.generateCode());
        }
        return methodBuilder.build();
    }
}

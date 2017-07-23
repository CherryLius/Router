//package cherry.android.router.compiler.generate;
//
//import com.squareup.javapoet.ClassName;
//import com.squareup.javapoet.JavaFile;
//import com.squareup.javapoet.MethodSpec;
//import com.squareup.javapoet.ParameterizedTypeName;
//import com.squareup.javapoet.TypeSpec;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import javax.lang.model.element.Modifier;
//
//import cherry.android.router.compiler.InterceptorClass;
//import cherry.android.router.compiler.common.Values;
//
///**
// * Created by Administrator on 2017/5/25.
// */
//
//public class InterceptorGenerator implements Generator {
//    private String mModuleName;
//    private List<InterceptorClass> mInterceptorList;
//
//    public InterceptorGenerator(String moduleName) {
//        this.mModuleName = moduleName;
//        mInterceptorList = new ArrayList<>();
//    }
//
//    public void addInterceptor(InterceptorClass interceptor) {
//        mInterceptorList.add(interceptor);
//    }
//
//    @Override
//    public JavaFile generate() {
//        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(mModuleName + "_InterceptorPicker")
//                .addModifiers(Modifier.PUBLIC)
//                .addSuperinterface(Values.INTERCEPTOR_PICKER)
//                .addMethod(buildPickMethod());
//        return JavaFile.builder(Values.ROUTER_PACKAGE_NAME, typeBuilder.build()).build();
//    }
//
//    private MethodSpec buildPickMethod() {
//        ParameterizedTypeName mapType = ParameterizedTypeName.get(ClassName.get(Map.class),
//                ClassName.get(String.class),Values.INTERCEPTOR_META);
////      ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(Values.INTERCEPTOR))
//        MethodSpec.Builder method = MethodSpec.methodBuilder("pick")
//                .addAnnotation(Override.class)
//                .addModifiers(Modifier.PUBLIC)
//                .addParameter(mapType, "interceptors");
//        for (InterceptorClass interceptor : mInterceptorList) {
//            method.addCode(interceptor.generateCode());
//        }
//        return method.build();
//    }
//}

package cherry.android.router.compiler.generate;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import cherry.android.router.compiler.RoutingFiled;
import cherry.android.router.compiler.common.Values;
import cherry.android.router.compiler.util.Utils;

/**
 * Created by Administrator on 2017/6/8.
 */

public class RouteFieldGenerator implements IGenerator {

    private Elements mElementUtils;
    private Types mTypeUtils;
    private TypeElement mClassElement;
    private List<RoutingFiled> mFieldList;

    public RouteFieldGenerator(Types typeUtils, Elements elementUtils, TypeElement element) {
        mTypeUtils = typeUtils;
        mElementUtils = elementUtils;
        mClassElement = element;
        mFieldList = new ArrayList<>();
    }

    public void addRouteField(RoutingFiled routingFiled) {
        mFieldList.add(routingFiled);
    }

    private String getPackageName() {
        return mElementUtils.getPackageOf(mClassElement).getQualifiedName().toString();
    }

    private TypeName getTypeName() {
        return TypeName.get(mClassElement.asType());
    }

    private String getClassName() {
        String packageName = getPackageName();
        String fullClassName = mClassElement.getQualifiedName().toString();
        int packageLen = packageName.length() + 1;
        return fullClassName.substring(packageLen).replace(".", "$");
    }

    @Override
    public JavaFile generate() {
        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(getClassName() + "_Router")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(Values.ROUTE_BUNDLE, "mFieldBundle",
                        Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(buildConstructorMethod());
        return JavaFile.builder(getPackageName(), typeBuilder.build()).build();
    }

    private MethodSpec buildConstructorMethod() {
        MethodSpec.Builder methodBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(getTypeName(), "target", Modifier.FINAL)
                .addStatement("mFieldBundle = $T.newBundle(target)",
                        Values.ROUTE_BUNDLE);
        for (int i = 0; i < mFieldList.size(); i++) {
            RoutingFiled field = mFieldList.get(i);
            String statement = "target.$N = mFieldBundle.%s($S, target.$N)";
            methodBuilder.addStatement(String.format(statement, getMethodName(field)),
                    field.getFieldName(), field.getKey(), field.getFieldName());
            if (field.isNonNull()) {
                methodBuilder.addStatement("$T.checkNonNull(target.$N, $S, $S)",
                        Values.ROUTE_UTILS,
                        field.getFieldName(),
                        field.getFieldName(),
                        getClassName());
            }
        }
        return methodBuilder.build();
    }

    private String getMethodName(RoutingFiled field) {
        String format = "get";
        TypeName fieldType = field.getTypeName();
        System.err.println("typeName=" + fieldType);
        if (fieldType.equals(TypeName.INT)) {
            format = "getInt";
        } else if (fieldType.equals(TypeName.BOOLEAN)) {
            format = "getBoolean";
        } else if (fieldType.equals(TypeName.BYTE)) {
            format = "getByte";
        } else if (fieldType.equals(TypeName.DOUBLE)) {
            format = "getDouble";
        } else if (fieldType.equals(TypeName.FLOAT)) {
            format = "getFloat";
        } else if (fieldType.equals(TypeName.LONG)) {
            format = "getLong";
        } else if (fieldType.equals(TypeName.SHORT)) {
            format = "getShort";
        } else if (fieldType.equals(Utils.getTypeName(mElementUtils,
                String.class.getCanonicalName()))) {
            format = "getString";
        } else if (fieldType.equals(TypeName.CHAR)) {
            format = "getCharacter";
        } else if (fieldType.equals(Values.ANDROID_OS_BUNDLE)) {
            format = "getBundle";
        } else if (isSubType(field.asType(),
                CharSequence.class.getCanonicalName())) {
            format = "getCharSequence";
        } else if (isSubType(field.asType(), Values.PARCELABLE_NAME)) {
            format = "getParcelable";
        } else if (isSubType(field.asType(), Serializable.class.getCanonicalName())) {
            format = "getSerializable";
        }
        return format;
    }

    private boolean isSubType(TypeMirror typeMirror, String type) {
        return Utils.isSubType(mTypeUtils, typeMirror,
                Utils.getTypeMirror(mElementUtils, type));
    }
}

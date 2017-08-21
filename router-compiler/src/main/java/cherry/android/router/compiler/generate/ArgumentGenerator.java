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

import cherry.android.router.compiler.Argument;
import cherry.android.router.compiler.common.TypeKind;
import cherry.android.router.compiler.common.Values;
import cherry.android.router.compiler.util.Utils;

/**
 * Created by Administrator on 2017/6/8.
 */

public class ArgumentGenerator implements Generator<JavaFile> {

    private Elements mElementUtils;
    private Types mTypeUtils;
    private TypeElement mClassElement;
    private List<Argument> mFieldList;

    public ArgumentGenerator(Types typeUtils, Elements elementUtils, TypeElement element) {
        mTypeUtils = typeUtils;
        mElementUtils = elementUtils;
        mClassElement = element;
        mFieldList = new ArrayList<>();
    }

    public void addArgument(Argument argument) {
        mFieldList.add(argument);
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
                .addParameter(Values.ROUTE_BUNDLE, "bundle", Modifier.FINAL)
                .addStatement("this.mFieldBundle = bundle");
                /*.addStatement("mFieldBundle = $T.newBundle(target)",
                        Values.ROUTE_BUNDLE);*/
        methodBuilder.addCode("Object value;\n");
        for (int i = 0; i < mFieldList.size(); i++) {
            Argument field = mFieldList.get(i);
            String statement = "target.$N = mFieldBundle.%s($S, target.$N)";
            int type = getKindType(field);
            String methodName = getMethodName(field);

            if (type == TypeKind.OBJECT.ordinal()
                    || type == TypeKind.SERIALIZABLE.ordinal()
                    || type == TypeKind.PARCELABLE.ordinal()) {
                methodBuilder.addStatement("value=mFieldBundle.get($S, target.$N)", field.getKey(), field.getFieldName());
                //if ()
                methodBuilder.beginControlFlow("if ($T.isGoodJson($T.class, value))", Values.ROUTE_UTILS, field.getTypeName());
                methodBuilder.addStatement("target.$N = $T.fromJson($T.class, String.valueOf(value))",
                        field.getFieldName(), Values.ROUTE_UTILS, field.getTypeName());
                methodBuilder.endControlFlow();

                //else
                methodBuilder.beginControlFlow("else");
                methodBuilder.addStatement("target.$N = $T.cast($T.class, value, $S, $S)",
                        field.getFieldName(),
                        Values.ROUTE_UTILS,
                        field.getTypeName(),
                        field.getFieldName(),
                        getClassName());
                methodBuilder.endControlFlow();
            }

            if (type == TypeKind.OBJECT.ordinal()
                    || type == TypeKind.SERIALIZABLE.ordinal()
                    || type == TypeKind.PARCELABLE.ordinal()) {
                methodBuilder.beginControlFlow("if (target.$N == null)", field.getFieldName());
                methodBuilder.addStatement(String.format(statement, methodName),
                        field.getFieldName(), field.getKey(), field.getFieldName());
                methodBuilder.endControlFlow();
            } else {
                methodBuilder.addStatement(String.format(statement, methodName),
                        field.getFieldName(), field.getKey(), field.getFieldName());
            }
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

    private String getMethodName(Argument field) {
        String format = "get";
        final int filedType = getKindType(field);
        if (filedType == TypeKind.INT.ordinal()) {
            format = "getInt";
        } else if (filedType == TypeKind.BOOLEAN.ordinal()) {
            format = "getBoolean";
        } else if (filedType == TypeKind.BYTE.ordinal()) {
            format = "getByte";
        } else if (filedType == TypeKind.DOUBLE.ordinal()) {
            format = "getDouble";
        } else if (filedType == TypeKind.FLOAT.ordinal()) {
            format = "getFloat";
        } else if (filedType == TypeKind.LONG.ordinal()) {
            format = "getLong";
        } else if (filedType == TypeKind.SHORT.ordinal()) {
            format = "getShort";
        } else if (filedType == TypeKind.STRING.ordinal()) {
            format = "getString";
        } else if (filedType == TypeKind.CHAR.ordinal()) {
            format = "getCharacter";
        } else if (filedType == TypeKind.BUNDLE.ordinal()) {
            format = "getBundle";
        } else if (filedType == TypeKind.CHARSEQUENCE.ordinal()) {
            format = "getCharSequence";
        } else if (filedType == TypeKind.PARCELABLE.ordinal()) {
            format = "getParcelable";
        } else if (filedType == TypeKind.SERIALIZABLE.ordinal()) {
            format = "getSerializable";
        } else if (filedType == TypeKind.OBJECT.ordinal()) {
            format = "get";
        }
        return format;
    }

    private boolean isSubType(TypeMirror typeMirror, String type) {
        return Utils.isSubType(mTypeUtils, typeMirror,
                Utils.getTypeMirror(mElementUtils, type));
    }

    private int getKindType(Argument field) {
        TypeMirror typeMirror = field.asType();
        if (typeMirror.getKind().isPrimitive())
            return typeMirror.getKind().ordinal();
        TypeName fieldType = field.getTypeName();
        if (fieldType.equals(TypeName.INT)) {
            return TypeKind.INT.ordinal();
        } else if (fieldType.equals(TypeName.BOOLEAN)) {
            return TypeKind.BOOLEAN.ordinal();
        } else if (fieldType.equals(TypeName.BYTE)) {
            return TypeKind.BYTE.ordinal();
        } else if (fieldType.equals(TypeName.DOUBLE)) {
            return TypeKind.DOUBLE.ordinal();
        } else if (fieldType.equals(TypeName.FLOAT)) {
            return TypeKind.FLOAT.ordinal();
        } else if (fieldType.equals(TypeName.LONG)) {
            return TypeKind.LONG.ordinal();
        } else if (fieldType.equals(TypeName.SHORT)) {
            return TypeKind.SHORT.ordinal();
        } else if (fieldType.equals(Utils.getTypeName(mElementUtils,
                String.class.getCanonicalName()))) {
            return TypeKind.STRING.ordinal();
        } else if (fieldType.equals(TypeName.CHAR)) {
            return TypeKind.CHAR.ordinal();
        } else if (fieldType.equals(Values.ANDROID_OS_BUNDLE)) {
            return TypeKind.BUNDLE.ordinal();
        } else if (isSubType(field.asType(),
                CharSequence.class.getCanonicalName())) {
            return TypeKind.CHARSEQUENCE.ordinal();
        } else if (isSubType(field.asType(), Values.PARCELABLE_NAME)) {
            return TypeKind.PARCELABLE.ordinal();
        } else if (isSubType(field.asType(), Serializable.class.getCanonicalName())) {
            return TypeKind.SERIALIZABLE.ordinal();
        } else {
            return TypeKind.OBJECT.ordinal();
        }
    }
}

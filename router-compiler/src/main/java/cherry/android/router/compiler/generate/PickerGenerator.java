package cherry.android.router.compiler.generate;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import cherry.android.router.compiler.common.Values;

/**
 * Created by LHEE on 2017/7/22.
 */

public abstract class PickerGenerator<T extends Generator<CodeBlock>> implements Generator<JavaFile> {
    private final String mModuleName;
    private final List<T> mList;

    public PickerGenerator(String moduleName) {
        this.mModuleName = moduleName;
        mList = new ArrayList<>();
    }

    public void add(T t) {
        if (t == null)
            return;
        mList.add(t);
    }

    @Override
    public JavaFile generate() {
        if (getPickerName() == null || getPickerName().trim().length() == 0)
            throw new IllegalArgumentException("getPickerName() is Empty");
        TypeName generic = getGenericType();
        if (generic == null)
            generic = ClassName.get(Object.class);
        ParameterizedTypeName pickerType = ParameterizedTypeName.get(Values.PICKER, generic);
        TypeSpec typeSpec = TypeSpec.classBuilder(mModuleName + getPickerName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(pickerType)
                .addMethod(buildPickMethod())
                .build();
        return JavaFile.builder(Values.ROUTER_PACKAGE_NAME, typeSpec).build();
    }

    private MethodSpec buildPickMethod() {
        TypeName generic = getGenericType();
        if (generic == null)
            generic = ClassName.get(Object.class);
        MethodSpec.Builder method = MethodSpec.methodBuilder("pick")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(generic, "param");
        for (Generator<CodeBlock> generator : mList) {
            method.addCode(generator.generate());
        }
        return method.build();
    }


    abstract String getPickerName();

    abstract TypeName getGenericType();

}

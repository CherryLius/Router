package cherry.android.router.compiler;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.TypeElement;

import cherry.android.router.annotations.Interceptor;

/**
 * Created by Administrator on 2017/5/25.
 */

public class InterceptorClass {

    private TypeElement mTypeElement;
    private String mName;
    private int mPrority;

    public InterceptorClass(TypeElement typeElement) {
        mTypeElement = typeElement;
        Interceptor interceptor = mTypeElement.getAnnotation(Interceptor.class);
        mName = interceptor.value();
        mPrority = interceptor.priority();
    }

    public CodeBlock generateCode() {
        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        codeBuilder.addStatement("interceptors.put($S, $T.class)",
                mName, TypeName.get(mTypeElement.asType()));
        return codeBuilder.build();
    }

}

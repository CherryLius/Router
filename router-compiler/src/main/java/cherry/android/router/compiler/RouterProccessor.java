package cherry.android.router.compiler;

import com.google.auto.service.AutoService;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import cherry.android.router.annotations.Interceptor;
import cherry.android.router.annotations.Route;

/**
 * Created by Administrator on 2017/5/24.
 */
@AutoService(Processor.class)
public class RouterProccessor extends AbstractProcessor {

    private Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        mFiler = processingEnv.getFiler();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new HashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            annotationTypes.add(annotation.getCanonicalName());
        }
        return annotationTypes;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new HashSet<>();
        annotations.add(Route.class);
        annotations.add(Interceptor.class);
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }
}

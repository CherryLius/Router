package cherry.android.router.plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class RouterPlugin implements Plugin<Project> {

    static final String APT_OPTION_NAME = "ModuleName"

    @Override
    void apply(Project project) {
        def hasApp = project.plugins.withType(AppPlugin)
        def hasLib = project.plugins.withType(LibraryPlugin)
        if (!hasApp && !hasLib)
            throw new IllegalStateException("'android' or 'android-library' plugin required.")
        final def variants
        if (hasApp) {
            variants = project.android.applicationVariants
        } else {
            variants = project.android.libraryVariants
        }
        String moduleName = project.name.replace('.', '_').replace('-', "_");
        project.afterEvaluate {
            variants.all { variant ->
                variant.javaCompile.options.compilerArgs.add("-A${APT_OPTION_NAME}=${moduleName}")
            }
        }
    }
}
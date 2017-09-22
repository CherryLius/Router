# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keepattributes *Annotation*
-keepattributes InnerClasses
# 泛型
-keepattributes Signature
# 异常
-keepattributes Exceptions
-keep class **_Router { *; }
-keep class **_RoutePicker { *; }
-keep class **_InterceptorPicker { *; }
-keep class cherry.android.router.api.Picker { *; }
-keep class cherry.android.router.api.RouteRule { *; }
-keep class cherry.android.router.api.RouteInterceptor { *; }
-keep class cherry.android.router.api.utils.Utils { *; }
-keep class cherry.android.router.api.bundle.** { *; }
-keepclasseswithmembernames class * {
    @cherry.android.router.annotations.* <fields>;
}
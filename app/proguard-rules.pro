# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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

# 'com.github.w1010479088:RecyclerViewConfigor:1.7.0'对应的混淆
# ViewHolder需要的混淆配置 recyclerviewconfig,现在的RecyclerView已经属于SDK里面的东西了,需要注意
-keep class com.bruceewu.configor.**{*;}
-keep class android.view.View
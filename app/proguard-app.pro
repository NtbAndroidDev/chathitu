# Bỏ qua cảnh báo
#-ignorewarning

# Làm xáo trộn và bảo vệ một phần mã của dự án của riêng bạn và các gói jar của bên thứ ba được tham chiếu
#-libraryjars libs/xxxxxxxxx.jar

-keep class vn.hitu.ntb.http.api.** {
    <fields>;
}
-keep class vn.hitu.ntb.http.response.** {
    <fields>;
}
-keep class vn.hitu.ntb.http.model.** {
    <fields>;
}


-keepclassmembernames class ** {
    @vn.hitu.ntb.aop.Log <methods>;
}

-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
package vn.hitu.ntb.aop

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2019/12/06
 *    desc   : 权限申请注解
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.CLASS
)
annotation class Permissions constructor(
    /**
     * Các bộ sưu tập cần đăng ký quyền
     */
    vararg val value: String
)
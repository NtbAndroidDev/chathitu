package vn.hitu.ntb.utils

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.activity.result.ActivityResultLauncher
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.animators.AnimationType
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.tools.SdkVersionUtils
import vn.hitu.ntb.other.GlideEngine

object PhotoPickerUtils {
    fun showImagePickerChooseAvatar(
        activity: Activity,
    ) {
        PictureSelector.create(activity).openGallery(PictureMimeType.ofImage())
            .imageEngine(GlideEngine.createGlideEngine()).setLanguage(7)
            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .setRecyclerAnimationMode(AnimationType.DEFAULT_ANIMATION)
            .imageSpanCount(4) // Số lượng hình ảnh của mỗi dòng
            .isReturnEmpty(true) // Có thể quay lại khi nhấp vào nút khi không có dữ liệu nào được chọn không
            .isOriginalImageControl(true) // Sử dụng ảnh gốc
            .selectionMode(PictureConfig.SINGLE).isPreviewImage(false) // Cho review hình ảnh
            .isCamera(true) // Bật camera
            .isZoomAnim(true) // Nhấp vào danh sách hình ảnh Hiệu ứng thu phóng Mặc định true
            .isGif(false) // Không sử dụng ảnh gif
            .isOpenClickSound(true) // Sử dụng âm thanh
            .isEnableCrop(true).forResult(PictureConfig.CHOOSE_REQUEST)

    }

    fun showImagePickerCreatePost(
        activity: Activity, localMedia: List<LocalMedia>,intent: ActivityResultLauncher<Intent>
    ) {
        PictureSelector.create(activity).openGallery(PictureMimeType.ofAll())
            .imageEngine(GlideEngine.createGlideEngine()).setLanguage(7).maxSelectNum(30)
            .minSelectNum(0).maxVideoSelectNum(4).imageSpanCount(4).isReturnEmpty(true)
            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .isOriginalImageControl(true).selectionMode(PictureConfig.MULTIPLE).isPreviewImage(true)
            .isPreviewVideo(true).isCamera(true).isZoomAnim(true).isEnableCrop(false)
            .isCompress(true).isGif(true).circleDimmedLayer(false).isOpenClickSound(true).isWithVideoImage(true)
            .minimumCompressSize(100).selectionData(localMedia)
            .filterMaxFileSize(200000)
            .forResult(intent)
    }

    fun showImagePickerCreateComment(
        activity: Activity, intent: ActivityResultLauncher<Intent> , localMedia: List<LocalMedia>
    ) {
        PictureSelector.create(activity).openGallery(PictureMimeType.ofAll())
            .imageEngine(GlideEngine.createGlideEngine()).setLanguage(7).maxSelectNum(1)
            .minSelectNum(0).maxVideoSelectNum(1).imageSpanCount(4).isReturnEmpty(true)
            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .isOriginalImageControl(true).selectionMode(PictureConfig.MULTIPLE).isPreviewImage(true)
            .isPreviewVideo(true)
            .isCamera(true).isZoomAnim(true)
            .isEnableCrop(false)
            .isCompress(true).isGif(true).circleDimmedLayer(false).isOpenClickSound(true).isWithVideoImage(true)
            .minimumCompressSize(100)
            .selectionData(localMedia)
            .forResult(intent)

    }

    fun showImagePickerChooseAvatar(
        activity: Activity, intent: ActivityResultLauncher<Intent>
    ) {
        PictureSelector.create(activity).openGallery(PictureMimeType.ofImage())
            .imageEngine(GlideEngine.createGlideEngine()).setLanguage(7)
            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .setRecyclerAnimationMode(AnimationType.DEFAULT_ANIMATION)
            .imageSpanCount(4)// Số lượng hình ảnh của mỗi dòng
            .minSelectNum(0)
            .isReturnEmpty(false) // Có thể quay lại khi nhấp vào nút khi không có dữ liệu nào được chọn không
            .isOriginalImageControl(true) // Sử dụng ảnh gốc
            .selectionMode(PictureConfig.SINGLE).isPreviewImage(false) // Cho review hình ảnh
            .isCamera(true) // Bật camera
            .isZoomAnim(true) // Nhấp vào danh sách hình ảnh Hiệu ứng thu phóng Mặc định true
            .isGif(false) // Không sử dụng ảnh gif
            .isOpenClickSound(true) // Sử dụng âm thanh
            .isEnableCrop(true).forResult(intent)



    }

    //Choose Image Video NewsFeed
    fun showImagePickerChooseNewsFeed(
        activity: Activity,
    ) {
        PictureSelector.create(activity).openGallery(PictureMimeType.ofImage())
            .imageEngine(GlideEngine.createGlideEngine()).setLanguage(7).videoQuality(10)
            .videoMaxSecond(60).isPreviewImage(true)
            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .setRecyclerAnimationMode(AnimationType.DEFAULT_ANIMATION)
            .imageSpanCount(4) // Số lượng hình ảnh của mỗi dòng
            .isReturnEmpty(true) // Có thể quay lại khi nhấp vào nút khi không có dữ liệu nào được chọn không
            .isOriginalImageControl(true) // Sử dụng ảnh gốc
            .selectionMode(PictureConfig.SINGLE).isPreviewImage(false) // Cho review hình ảnh
            .isCamera(true) // Bật camera
            .isZoomAnim(true) // Nhấp vào danh sách hình ảnh Hiệu ứng thu phóng Mặc định true
            .isGif(false) // Không sử dụng ảnh gif
            .isOpenClickSound(true) // Sử dụng âm thanh
            .forResult(PictureConfig.CHOOSE_REQUEST)
    }




    fun showImagePickerChat(
        activity: Activity?,
    ) {
        val animationMode = AnimationType.DEFAULT_ANIMATION
        val language = 7
        PictureSelector.create(activity)
            .openGallery(PictureMimeType.ofAll())
            .imageEngine(GlideEngine.createGlideEngine())
            //.setPictureCropStyle(mCropParameterStyle)// 动态自定义裁剪主题
            .isWeChatStyle(false) // 是否开启微信图片选择风格
            .isUseCustomCamera(false) // 是否使用自定义相机
            .setLanguage(language) // 设置语言，默认中文
            .isPageStrategy(false) // 是否开启分页策略 & 每页多少条；默认开启
            .setRecyclerAnimationMode(animationMode) // 列表动画效果
            .isWithVideoImage(false) // 图片和视频是否可以同选,只在ofAll模式下有效
            .isMaxSelectEnabledMask(false) // 选择数到了最大阀值列表是否启用蒙层效果
            //.isAutomaticTitleRecyclerTop(false)// 连续点击标题栏RecyclerView是否自动回到顶部,默认true
            //.loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
            //.setOutputCameraPath(createCustomCameraOutPath())// 自定义相机输出目录
            //.setButtonFeatures(CustomCameraView.BUTTON_STATE_BOTH)// 设置自定义相机按钮状态
            .maxSelectNum(30) // 最大图片选择数量
            .minSelectNum(0) // 最小选择数量
            .maxVideoSelectNum(1) // 视频最大选择数量
            //.minVideoSelectNum(1)// 视频最小选择数量
            //.closeAndroidQChangeVideoWH(!SdkVersionUtils.checkedAndroid_Q())// 关闭在AndroidQ下获取图片或视频宽高相反自动转换
            .imageSpanCount(4) // 每行显示个数
            .isReturnEmpty(false) // 未选择数据时点击按钮是否可以返回
            .closeAndroidQChangeWH(true) //如果图片有旋转角度则对换宽高,默认为true
            .closeAndroidQChangeVideoWH(!SdkVersionUtils.checkedAndroid_Q()) // 如果视频有旋转角度则对换宽高,默认为false
            .isAndroidQTransform(false) // 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && .isEnableCrop(false);有效,默认处理
            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) // 设置相册Activity方向，不设置默认使用系统
            .isOriginalImageControl(true) // 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
            //.bindCustomPlayVideoCallback(new MyVideoSelectedPlayCallback(getContext()))// 自定义视频播放回调控制，用户可以使用自己的视频播放界面
            //.bindCustomPreviewCallback(new MyCustomPreviewInterfaceListener())// 自定义图片预览回调接口
            //.bindCustomCameraInterfaceListener(new MyCustomCameraInterfaceListener())// 提供给用户的一些额外的自定义操作回调
            //.cameraFileName(System.currentTimeMillis() +".jpg")    // 重命名拍照文件名、如果是相册拍照则内部会自动拼上当前时间戳防止重复，注意这个只在使用相机时可以使用，如果使用相机又开启了压缩或裁剪 需要配合压缩和裁剪文件名api
            //.renameCompressFile(System.currentTimeMillis() +".jpg")// 重命名压缩文件名、 如果是多张压缩则内部会自动拼上当前时间戳防止重复
            //.renameCropFileName(System.currentTimeMillis() + ".jpg")// 重命名裁剪文件名、 如果是多张裁剪则内部会自动拼上当前时间戳防止重复
            .selectionMode(PictureConfig.MULTIPLE) // 多选 or 单选
            .isSingleDirectReturn(false) // 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
            .isPreviewImage(false) // 是否可预览图片
            .isPreviewVideo(false) // 是否可预览视频
            //.querySpecifiedFormatSuffix(PictureMimeType.ofJPEG())// 查询指定后缀格式资源
            .isEnablePreviewAudio(false) // 是否可播放音频
            .isCamera(true) // 是否显示拍照按钮
            //.isMultipleSkipCrop(false)// 多图裁剪时是否支持跳过，默认支持
            //.isMultipleRecyclerAnimation(false)// 多图裁剪底部列表显示动画效果
            .isZoomAnim(true) // 图片列表点击 缩放效果 默认true
            //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg,Android Q使用PictureMimeType.PNG_Q
            .isEnableCrop(false) // 是否裁剪
            //.basicUCropConfig()//对外提供所有UCropOptions参数配制，但如果PictureSelector原本支持设置的还是会使用原有的设置
            .isCompress(true) // 是否压缩
            //.compressQuality(80)// 图片压缩后输出质量 0~ 100
            .synOrAsy(false) //同步true或异步false 压缩 默认同步
            //.queryMaxFileSize(10)// 只查多少M以内的图片、视频、音频  单位M
            //.compressSavePath(getPath())//压缩图片保存地址
            //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效 注：已废弃
            //.glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度 注：已废弃
            .isGif(true) // 是否显示gif图片
            //.isWebp(false)// 是否显示webp图片,默认显示
            //.isBmp(false)//是否显示bmp图片,默认显示
            .circleDimmedLayer(false) // 是否圆形裁剪
            //.setCropDimmedColor(ContextCompat.getColor(getContext(), R.color.app_color_white))// 设置裁剪背景色值
            //.setCircleDimmedBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.app_color_white))// 设置圆形裁剪边框色值
            //.setCircleStrokeWidth(3)// 设置圆形裁剪边框粗细
            .isOpenClickSound(true) // 是否开启点击声音
            //.isDragFrame(false)// 是否可拖动裁剪框(固定)
            //.videoMinSecond(10)// 查询多少秒以内的视频
            //.videoMaxSecond(15)// 查询多少秒以内的视频
            //.recordVideoSecond(10)//录制视频秒数 默认60s
            //.isPreviewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
            //.cropCompressQuality(90)// 注：已废弃 改用cutOutQuality()
            .cutOutQuality(90) // 裁剪输出质量 默认100
            .minimumCompressSize(100) // 小于多少kb的图片不压缩
            //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
            //.cropImageWideHigh()// 裁剪宽高比，设置如果大于图片本身宽高则无效
            //.rotateEnabled(false) // 裁剪是否可旋转图片
            //.scaleEnabled(false)// 裁剪是否可放大缩小图片
            //.videoQuality()// 视频录制质量 0 or 1
            //.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
            .selectionData(null)
            .forResult(PictureConfig.CHOOSE_REQUEST)
    }
}
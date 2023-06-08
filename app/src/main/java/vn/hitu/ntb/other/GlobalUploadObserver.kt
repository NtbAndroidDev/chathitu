package vn.hitu.ntb.other

import android.content.Context
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import net.gotev.uploadservice.data.UploadInfo
import net.gotev.uploadservice.network.ServerResponse
import net.gotev.uploadservice.observer.request.RequestObserverDelegate
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.eventbus.EventBusUploadComment
import vn.hitu.ntb.eventbus.EventBusUploadProgress
import vn.hitu.ntb.eventbus.EventBusUploadReview

class GlobalUploadObserver : RequestObserverDelegate {
    private var typeUpload = 0

    val mmkv: MMKV = MMKV.mmkvWithID(AppConstants.CACHE_UPLOAD_TYPE_KEY)

    override fun onCompleted(context: Context, uploadInfo: UploadInfo) {}

    override fun onCompletedWhileNotObserving() {}

    override fun onError(context: Context, uploadInfo: UploadInfo, exception: Throwable) {
        typeUpload = 0
        if (mmkv.getInt(AppConstants.CACHE_UPLOAD_TYPE_KEY, 0) == AppConstants.NEWSFEED_KEY) {
            EventBus.getDefault().post(EventBusUploadProgress(uploadInfo.progressPercent, typeUpload))
        } else if(mmkv.getInt(AppConstants.CACHE_UPLOAD_TYPE_KEY, 0) == AppConstants.COMMENT_KEY){
            EventBus.getDefault().post(EventBusUploadComment(uploadInfo.progressPercent))
        }else if(mmkv.getInt(AppConstants.CACHE_UPLOAD_TYPE_KEY, 0) == AppConstants.NEWSFEED_KEY_REVIEW){
            EventBus.getDefault().post(EventBusUploadReview(uploadInfo.progressPercent, typeUpload))
        }else{
            Timber.d("Tiến trình upload 4 %s", uploadInfo.progressPercent)
        }
    }

    override fun onProgress(context: Context, uploadInfo: UploadInfo) {
        if (mmkv.getInt(AppConstants.CACHE_UPLOAD_TYPE_KEY, 0) == AppConstants.NEWSFEED_KEY) {
            typeUpload = 1
            Timber.d("Tiến trình upload 1 %s", uploadInfo.progressPercent)
            EventBus.getDefault().post(EventBusUploadProgress(uploadInfo.progressPercent, typeUpload))
        }else if (mmkv.getInt(AppConstants.CACHE_UPLOAD_TYPE_KEY, 0) == AppConstants.COMMENT_KEY){
            Timber.d("Tiến trình upload 2 %s", uploadInfo.progressPercent)
            EventBus.getDefault().post(EventBusUploadComment(uploadInfo.progressPercent))
        }else if (mmkv.getInt(AppConstants.CACHE_UPLOAD_TYPE_KEY, 0) == AppConstants.NEWSFEED_KEY_REVIEW)
        {
            typeUpload = 1
            Timber.d("Tiến trình upload 3 %s", uploadInfo.progressPercent)
            EventBus.getDefault().post(EventBusUploadReview(uploadInfo.progressPercent, typeUpload))
        }else{
            Timber.d("Tiến trình upload 4 %s", uploadInfo.progressPercent)
        }
    }

    override fun onSuccess(
        context: Context, uploadInfo: UploadInfo, serverResponse: ServerResponse
    ) {
        Timber.d("Dữ liệu upload %s", Gson().toJson(uploadInfo))
        Timber.d("Dữ liệu upload %s", Gson().toJson(serverResponse))
    }
}
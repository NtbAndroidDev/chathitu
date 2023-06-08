package vn.hitu.ntb.http.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoader.LoadData
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import okhttp3.Call
import java.io.InputStream

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 03/10/2022
 */
class OkHttpLoader private constructor(private val factory: Call.Factory) :
    ModelLoader<GlideUrl, InputStream> {

    override fun handles(url: GlideUrl): Boolean {
        return true
    }

    override fun buildLoadData(model: GlideUrl, width: Int, height: Int, options: Options): LoadData<InputStream?> {
        return LoadData(model, OkHttpFetcher(factory, model))
    }

    class Factory constructor(private val factory: Call.Factory) : ModelLoaderFactory<GlideUrl, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<GlideUrl, InputStream> {
            return OkHttpLoader(factory)
        }

        override fun teardown() {}
    }
}
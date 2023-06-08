package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName
import com.luck.picture.lib.entity.LocalMedia

class DataPostNewFeed {
    @SerializedName("title")
    var title: String? = ""

    @SerializedName("content")
    var content: String? = ""

    @SerializedName("tag")
    var tag: ArrayList<TagNewsFeed> = ArrayList()

    @SerializedName("medias")
    var medias: ArrayList<MediasNode> = ArrayList()

    @SerializedName("service_rating")
    var serviceRating: Double? = 0.0

    @SerializedName("food_rating")
    var foodRating: Double? = 0.0

    @SerializedName("price_rating")
    var priceRating: Double? = 0.0

    @SerializedName("spatial_rating")
    var spatialRating: Double? = 0.0

    @SerializedName("hygiene_rating")
    var hygieneRating: Double? = 0.0

    @SerializedName("create_type")
    var createType: Int? = 0

    @SerializedName("create_id")
    var createId: String? = ""

    @SerializedName("post_type")
    var postType: Int? = 0

    @SerializedName("target_id")
    var targetId: String? = ""

    @SerializedName("thumbnail")
    var thumbnail = Thumbnail()

    @SerializedName("view")
    var view : Int? = 0

    @SerializedName("listMedia")
    var listMedia: List<LocalMedia> = listOf(LocalMedia())
}
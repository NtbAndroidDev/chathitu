package vn.hitu.ntb.other.sticker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aghajari.emojiview.sticker.Sticker
import com.aghajari.emojiview.sticker.StickerCategory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import vn.hitu.ntb.R
import vn.hitu.ntb.other.sticker.ShopStickers.IconAdapter.IconViewHolder
import vn.hitu.ntb.other.sticker.ShopStickers.ShopAdapter.ShopViewHolder

/**
 * @Author: Bùi Hữu Thắng
 * @Date: 30/08/2022
 */
class ShopStickers : StickerCategory<Int?> {
    override fun getStickers(): Array<ShopSticker> {
        return arrayOf(
            ShopSticker(
                arrayOf(
                    Sticker(""),
                    Sticker(""),
                    Sticker(""),
                    Sticker(""),
                    Sticker("")
                ), "Cuppy Pack 1", 16
            )
        )
    }

    override fun getCategoryData(): Int {
        return R.drawable.ic_masks_msk
    }

    override fun useCustomView(): Boolean {
        return true
    }

    override fun getView(viewGroup: ViewGroup): View {
        val recyclerView = RecyclerView(viewGroup.context)
        recyclerView.layoutManager = LinearLayoutManager(viewGroup.context)
        recyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        return recyclerView
    }

    override fun bindView(view: View) {
        (view as RecyclerView).adapter = ShopAdapter(this)
    }

    override fun getEmptyView(viewGroup: ViewGroup): View? {
        return null
    }

    inner class ShopAdapter(var provider: ShopStickers) : RecyclerView.Adapter<ShopViewHolder>() {
        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ShopViewHolder {
            val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.shop_recyclerview, viewGroup, false)
            val title = v.findViewById<TextView>(R.id.sticker_title)
            val subtitle = v.findViewById<TextView>(R.id.sticker_subtitle)
            val add = v.findViewById<Button>(R.id.sticker_add)
            val stickers = v.findViewById<RecyclerView>(R.id.stickers_rv)
            stickers.layoutManager = GridLayoutManager(viewGroup.context, 5)
            return ShopViewHolder(v, title, subtitle, add, stickers)
        }

        override fun onBindViewHolder(viewHolder: ShopViewHolder, i: Int) {
            val sticker = provider.stickers[i]
            viewHolder.title.text = sticker.title
            viewHolder.subTitle.text = sticker.count.toString() + " Sticker"
            viewHolder.add.setOnClickListener { view: View ->
                Toast.makeText(
                    view.context,
                    "Add " + sticker.title,
                    Toast.LENGTH_SHORT
                ).show()
            }
            viewHolder.stickers.adapter = IconAdapter(sticker.data as Array<Sticker<*>>?)
        }

        override fun getItemCount(): Int {
            return provider.stickers.size
        }

        inner class ShopViewHolder(
            itemView: View,
            var title: TextView,
            var subTitle: TextView,
            var add: Button,
            var stickers: RecyclerView
        ) : RecyclerView.ViewHolder(itemView) {
            init {
                if (UI.darkMode) {
                    title.setTextColor(Color.WHITE)
                }
            }
        }
    }

    inner class IconAdapter(var stickers: Array<Sticker<*>>?) :
        RecyclerView.Adapter<IconViewHolder>() {
        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): IconViewHolder {
            val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.shop_sticker_item, viewGroup, false)
            return IconViewHolder(v, v.findViewById(R.id.sticker_img))
        }

        override fun onBindViewHolder(viewHolder: IconViewHolder, i: Int) {
            Glide.with(viewHolder.imageView)
                .load(Integer.valueOf(stickers!![i].data.toString()))
                .apply(RequestOptions.fitCenterTransform())
                .into(viewHolder.imageView)
        }

        override fun getItemCount(): Int {
            return stickers!!.size
        }

        inner class IconViewHolder(itemView: View, var imageView: AppCompatImageView) :
            RecyclerView.ViewHolder(itemView)
    }
}
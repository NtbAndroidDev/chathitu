package vn.hitu.ntb.ui.dialog

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hjq.toast.ToastUtils
import vn.hitu.base.BaseAdapter
import vn.hitu.base.BaseDialog
import vn.hitu.ntb.R
import vn.hitu.ntb.aop.SingleClick
import vn.hitu.ntb.app.AppAdapter

/**
 * @Author: Nguyễn Khánh Duy
 * @Date: 16/10/2022
 */
class SelectDialog {
    class Builder(context: Context) : CommonDialog.Builder<Builder>(context),
        View.OnLayoutChangeListener, Runnable {

        private val recyclerView: RecyclerView? by lazy { findViewById(R.id.rv_select_list) }

        private val adapter: SelectAdapter

        private var listener: OnListener<out Any>? = null
        init {
            setCustomView(R.layout.select_dialog)
            recyclerView?.itemAnimator = null
            adapter = SelectAdapter(getContext())
            recyclerView?.adapter = adapter
        }

        fun setList(vararg ids: Int): Builder = apply {
            val data: MutableList<Any> = ArrayList(ids.size)
            for (id in ids) {
                data.add(getString(id)!!)
            }
            setList(data)
        }

        fun setList(vararg data: String): Builder = apply {
            setList(mutableListOf(*data))
        }

        fun setList(data: MutableList<Any>): Builder = apply {
            adapter.setData(data)
            recyclerView?.addOnLayoutChangeListener(this)
        }

        fun setSelect(vararg positions: Int): Builder = apply {
            adapter.setSelect(*positions)
        }

        fun setMaxSelect(count: Int): Builder = apply {
            adapter.setMaxSelect(count)
        }

        fun setMinSelect(count: Int): Builder = apply {
            adapter.setMinSelect(count)
        }

        fun setSingleSelect(): Builder = apply {
            adapter.setSingleSelect()
        }

        @Suppress("UNCHECKED_CAST")
        fun setListener(listener: OnListener<*>?): Builder = apply {
            this.listener = listener as OnListener<out Any>?
        }

        @SingleClick
        override fun onClick(view: View) {
            when (view.id) {
                R.id.tv_ui_confirm -> {
                    val data = adapter.getSelectSet()
                    if (data.size >= adapter.getMinSelect()) {
                        autoDismiss()
                        listener?.onSelfSelected(getDialog(), data)
                    } else {
                        ToastUtils.show(
                            String.format(
                                getString(R.string.select_min_hint)!!,
                                adapter.getMinSelect()
                            )
                        )
                    }
                }
                R.id.tv_ui_cancel -> {
                    autoDismiss()
                    listener?.onCancel(getDialog())
                }
            }
        }

        override fun onLayoutChange(
            v: View,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
            oldLeft: Int,
            oldTop: Int,
            oldRight: Int,
            oldBottom: Int
        ) {
            recyclerView?.removeOnLayoutChangeListener(this)
            post(this)
        }

        override fun run() {
            recyclerView?.let {
                val params = it.layoutParams ?: return
                val maxHeight = getScreenHeight() / 4 * 3
                if (it.height > maxHeight) {
                    if (params.height != maxHeight) {
                        params.height = maxHeight
                        it.layoutParams = params
                    }
                } else {
                    if (params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                        it.layoutParams = params
                    }
                }
            }
        }

        private fun getScreenHeight(): Int {
            val resources = getResources()
            val outMetrics = resources.displayMetrics
            return outMetrics.heightPixels
        }
    }

    private class SelectAdapter(context: Context) : AppAdapter<Any>(context),
        BaseAdapter.OnItemClickListener {

        private var minSelect = 1

        private var maxSelect = Int.MAX_VALUE

        private val selectSet: HashMap<Int, Any> = HashMap()

        init {
            setOnItemClickListener(this)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder()
        }

        fun setSelect(vararg positions: Int) {
            for (position in positions) {
                selectSet[position] = getItem(position)
            }
            notifyDataSetChanged()
        }

        fun setMaxSelect(count: Int) {
            maxSelect = count
        }

        fun setMinSelect(count: Int) {
            minSelect = count
        }

        fun getMinSelect(): Int {
            return minSelect
        }

        fun setSingleSelect() {
            setMaxSelect(1)
            setMinSelect(1)
        }

        fun isSingleSelect(): Boolean {
            return maxSelect == 1 && minSelect == 1
        }

        fun getSelectSet(): HashMap<Int, Any> {
            return selectSet
        }

        override fun onItemClick(recyclerView: RecyclerView?, itemView: View?, position: Int) {
            if (selectSet.containsKey(position)) {
                if (!isSingleSelect()) {
                    selectSet.remove(position)
                    notifyItemChanged(position)
                }
            } else {
                if (maxSelect == 1) {
                    selectSet.clear()
                    notifyDataSetChanged()
                }
                if (selectSet.size < maxSelect) {
                    selectSet[position] = getItem(position)
                    notifyItemChanged(position)
                } else {
                    ToastUtils.show(String.format(getString(R.string.select_max_hint)!!, maxSelect))
                }
            }
        }

        private inner class ViewHolder : AppViewHolder(R.layout.select_item) {

            private val textView: TextView? by lazy { findViewById(R.id.tvSelectText) }
            private val checkBox: CheckBox? by lazy { findViewById(R.id.tvSelectCheckbox) }

            override fun onBindView(position: Int) {
                textView?.text = getItem(position).toString()
                checkBox?.isChecked = selectSet.containsKey(position)
                if (maxSelect == 1) {
                    checkBox?.isClickable = false
                } else {
                    checkBox?.isEnabled = false
                }
            }
        }
    }

    interface OnListener<T> {

        @Suppress("UNCHECKED_CAST")
        fun onSelfSelected(dialog: BaseDialog?, data: HashMap<Int, out Any>) {
            onSelected(dialog, data as HashMap<Int, T>)
        }

        fun onSelected(dialog: BaseDialog?, data: HashMap<Int, T>)

        fun onCancel(dialog: BaseDialog?) {}
    }
}
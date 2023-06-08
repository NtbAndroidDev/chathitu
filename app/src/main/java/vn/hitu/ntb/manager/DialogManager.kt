package vn.hitu.ntb.manager

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import vn.hitu.base.BaseDialog
import java.util.*

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 03/10/2022
 */
class DialogManager private constructor(lifecycleOwner: LifecycleOwner) :
    LifecycleEventObserver, BaseDialog.OnDismissListener {

    companion object {

        private val DIALOG_MANAGER: HashMap<LifecycleOwner, DialogManager> = HashMap()

        fun getInstance(lifecycleOwner: LifecycleOwner): DialogManager {

            var manager: DialogManager? = DIALOG_MANAGER[lifecycleOwner]
            if (manager == null) {
                manager = DialogManager(lifecycleOwner)
                DIALOG_MANAGER[lifecycleOwner] = manager
            }
            return manager
        }
    }

    private val dialogs: MutableList<BaseDialog> = ArrayList()

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    /**
     * 排队显示 Dialog
     */
    fun addShow(dialog: BaseDialog) {
        if (dialog.isShowing) {
            throw IllegalStateException("are you ok?")
        }
        dialogs.add(dialog)
        val firstDialog: BaseDialog = dialogs[0]
        if (!firstDialog.isShowing) {
            firstDialog.addOnDismissListener(this)
            firstDialog.show()
        }
    }

    /**
     * 取消所有 Dialog 的显示
     */
    fun clearShow() {
        if (dialogs.isEmpty()) {
            return
        }
        val firstDialog: BaseDialog = dialogs[0]
        if (firstDialog.isShowing) {
            firstDialog.removeOnDismissListener(this)
            firstDialog.dismiss()
        }
        dialogs.clear()
    }

    override fun onDismiss(dialog: BaseDialog?) {
        dialog?.removeOnDismissListener(this)
        dialogs.remove(dialog)
        for (nextDialog: BaseDialog in dialogs) {
            if (!nextDialog.isShowing) {
                nextDialog.addOnDismissListener(this)
                nextDialog.show()
                break
            }
        }
    }

    /**
     * [LifecycleEventObserver]
     */
    override fun onStateChanged(lifecycleOwner: LifecycleOwner, event: Lifecycle.Event) {
        if (event != Lifecycle.Event.ON_DESTROY) {
            return
        }
        DIALOG_MANAGER.remove(lifecycleOwner)
        lifecycleOwner.lifecycle.removeObserver(this)
        clearShow()
    }
}
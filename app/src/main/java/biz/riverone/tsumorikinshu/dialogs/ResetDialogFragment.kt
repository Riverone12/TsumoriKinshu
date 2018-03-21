package biz.riverone.tsumorikinshu.dialogs

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import biz.riverone.tsumorikinshu.R

/**
 * ResetDialogFragment.ktgment.kt: リセットダイアログ
 * Created by kawahara on 2017/11/05.
 */
class ResetDialogFragment : SettingDialogBase() {

    companion object {

        private val TAG = ResetDialogFragment::class.java.simpleName
        const  val REQUEST_CODE: Int = 100
        const val ARG_KEY_RESET = "reset"

        fun show(manager: FragmentManager) {
            val dialog = ResetDialogFragment()
            dialog.setTargetFragment(null, REQUEST_CODE)
            dialog.show(manager, TAG)
        }
    }
    private var positiveButton: Button? = null

    override val dialogTitleResourceId: Int = R.string.title_reset_dialog

    override fun initializeControls(v: View) {

        val summary = TextView(activity)
        summary.setText(R.string.summary_reset)
        summary.textSize = TEXT_SIZE_SMALL

        // チェックボックス
        val checkBox = CheckBox(activity)
        checkBox.setText(R.string.caption_reset_check)
        checkBox.textSize = TEXT_SIZE_SMALL
        checkBox.setOnClickListener {
            positiveButton?.isEnabled = checkBox.isChecked
        }

        val layout = v.findViewById<LinearLayout>(R.id.settingDialogBaseLayout)
        layout.addView(summary)
        layout.addView(checkBox)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialog = super.onCreateDialog(savedInstanceState) as AlertDialog

        // OKボタンをグレーにする
        alertDialog.setOnShowListener {
            positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton?.isEnabled = false
        }

        return alertDialog
    }

    override fun putResult(result: Intent): Intent {
        result.putExtra(ARG_KEY_RESET, true)
        return result
    }
}
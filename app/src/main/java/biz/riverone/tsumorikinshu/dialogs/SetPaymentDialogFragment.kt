package biz.riverone.tsumorikinshu.dialogs

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.text.InputType
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import biz.riverone.tsumorikinshu.R

/**
 * SetPaymentDialogFragment.kt: 入金額入力ダイアログ
 * Created by kawahara on 2017/11/09.
 */
class SetPaymentDialogFragment : SettingDialogBase() {

    companion object {

        private val TAG = SetPaymentDialogFragment::class.java.simpleName
        const val REQUEST_CODE: Int = 110
        const val ARG_KEY_PAYMENT = "payment"


        fun show(manager: FragmentManager) {
            val dialog = SetPaymentDialogFragment()
            dialog.setTargetFragment(null, REQUEST_CODE)
            dialog.show(manager, TAG)
        }
    }

    private var yenEdit: EditText? = null
    override val dialogTitleResourceId: Int
        get() = R.string.title_payment_dialog

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        // ソフトウェアキーボードを表示する
        yenEdit?.requestFocus()
        yenEdit?.selectAll()
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        return view
    }

    override fun initializeControls(v: View) {
        val summary = TextView(activity)
        summary.setText(R.string.summary_payment)
        summary.textSize = TEXT_SIZE_SMALL

        val labelLayoutParam = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        labelLayoutParam.weight = 1.0f

        val inputLayoutParam = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        inputLayoutParam.weight = 1.0f

        val preLabel = TextView(activity)
        preLabel.setText(R.string.payment_pre_label)
        preLabel.textSize = TEXT_SIZE_MIDDLE
        preLabel.setTextColor(ContextCompat.getColor(context, R.color.colorBlue))
        preLabel.gravity = Gravity.END

        val postLabel = TextView(activity)
        postLabel.setText(R.string.caption_post_yen)
        postLabel.textSize = TEXT_SIZE_MIDDLE

        // 入力コントロール
        yenEdit = EditText(activity)
        yenEdit?.inputType = InputType.TYPE_CLASS_NUMBER
        yenEdit?.textSize = TEXT_SIZE_LARGE

        // レイアウト
        val linearLayout = LinearLayout(activity)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.setHorizontalGravity(Gravity.CENTER)
        linearLayout.addView(preLabel, labelLayoutParam)
        linearLayout.addView(yenEdit, inputLayoutParam)
        linearLayout.addView(postLabel, labelLayoutParam)

        val layout = v.findViewById<LinearLayout>(R.id.settingDialogBaseLayout)
        layout.addView(summary)
        layout.addView(linearLayout)
    }

    override fun putResult(result: Intent): Intent {
        var strValue = yenEdit?.text ?: "0"
        if (strValue.isEmpty()) {
            strValue = "0"
        }
        var value = 0
        try {
            value = Integer.parseInt(strValue.toString())
        }
        catch (e: NumberFormatException) {
            Toast.makeText(context, R.string.message_int_value_error, Toast.LENGTH_SHORT).show()
        }
        result.putExtra(ARG_KEY_PAYMENT, value)

        return result
    }
}
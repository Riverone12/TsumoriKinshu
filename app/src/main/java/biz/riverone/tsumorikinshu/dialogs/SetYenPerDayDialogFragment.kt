package biz.riverone.tsumorikinshu.dialogs

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.text.InputType
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import biz.riverone.tsumorikinshu.R

/**
 * SetYenPerDayDialogFragment.kt: 1日当たりの貯金額を設定するダイアログ
 * Created by kawahara on 2017/11/06.
 */
class SetYenPerDayDialogFragment : SettingDialogBase() {

    companion object {

        private val TAG = SetYenPerDayDialogFragment::class.java.simpleName
        const val REQUEST_CODE: Int = 102
        const val ARG_KEY_YEN_PER_DAY = "yen_per_day"

        fun show(manager: FragmentManager, currentValue: Int) {
            val dialog = SetYenPerDayDialogFragment()
            dialog.setTargetFragment(null, REQUEST_CODE)
            val arg = Bundle()
            arg.putInt(ARG_KEY_YEN_PER_DAY, currentValue)
            dialog.arguments = arg

            dialog.show(manager, TAG)
        }
    }

    private var yenEdit: EditText? = null
    private var currentValue: Int = 0

    override val dialogTitleResourceId: Int
        get() = R.string.title_yen_per_day_dialog

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        // ソフトウェアキーボードを表示する
        yenEdit?.requestFocus()
        yenEdit?.selectAll()
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        return view
    }

    override fun initializeControls(v: View) {
        currentValue = 0
        if (arguments != null && arguments.containsKey(ARG_KEY_YEN_PER_DAY)) {
            currentValue = arguments.getInt(ARG_KEY_YEN_PER_DAY, 0)
        }

        val labelLayoutParam = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        labelLayoutParam.weight = 1.0f

        val inputLayoutParam = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        inputLayoutParam.weight = 1.0f

        // ラベル
        val summary = TextView(activity)
        summary.setText(R.string.summary_yen_per_day)
        summary.textSize = TEXT_SIZE_SMALL

        val preLabel = TextView(activity)
        preLabel.setText(R.string.yen_per_day_pre_label)
        preLabel.textSize = TEXT_SIZE_MIDDLE
        preLabel.gravity = Gravity.END

        val postLabel = TextView(activity)
        postLabel.setText(R.string.caption_post_yen)
        postLabel.textSize = TEXT_SIZE_MIDDLE

        // 入力コントロール
        yenEdit = EditText(activity)
        yenEdit?.inputType = InputType.TYPE_CLASS_NUMBER
        if (currentValue > 0) {
            yenEdit?.setText(currentValue.toString())
            yenEdit?.selectAll()
        }
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
        var value = currentValue
        try {
            value = Integer.parseInt(strValue.toString())
        }
        catch (e: NumberFormatException) {
            Toast.makeText(context, R.string.message_int_value_error, Toast.LENGTH_SHORT).show()
        }
        result.putExtra(ARG_KEY_YEN_PER_DAY, value)
        return result
    }
}
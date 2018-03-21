package biz.riverone.tsumorikinshu.views

import android.annotation.SuppressLint
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import biz.riverone.tsumorikinshu.AppPreference
import biz.riverone.tsumorikinshu.R
import biz.riverone.tsumorikinshu.common.MyCalendarUtil
import biz.riverone.tsumorikinshu.dialogs.SettingDialogBase
import java.util.*

/**
 * 初期設定ウィザード
 * Created by kawahara on 2017/11/11.
 */
class WizardActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_KEY_PAGE = "wizard_mode"
        const val EXTRA_KEY_YEN = "yen_per_day"
    }

    private var currentPage: Int = 0
    private var baseLayout: LinearLayout? = null
    private var yenEdit: EditText? = null
    private var previousButton: Button? = null
    private var nextButton: Button? = null

    // 画面遷移時のアニメーション関連
    private var closeEnterAnimationId = 0
    private var closeExitAnimationId = 0
    private var openEnterAnimationId = 0
    private var openExitAnimationId = 0


    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wizard)

        // 画面を閉じた時のアニメーション用の処理
        val typedArray = theme.obtainStyledAttributes(intArrayOf(android.R.attr.windowAnimationStyle))
        val windowAnimationStyleResId = typedArray.getResourceId(0, 0)

        val attr = intArrayOf(
                android.R.attr.activityCloseEnterAnimation,
                android.R.attr.activityCloseExitAnimation)
        val activityStyle = theme.obtainStyledAttributes(windowAnimationStyleResId, attr)
        closeEnterAnimationId = activityStyle.getResourceId(0, 0)
        closeExitAnimationId = activityStyle.getResourceId(1, 0)

        // 画面を開いた時のアニメーション用の処理
        val attr2 = intArrayOf(
                android.R.attr.activityOpenEnterAnimation,
                android.R.attr.activityOpenExitAnimation)
        val activityStyle2 = theme.obtainStyledAttributes(windowAnimationStyleResId, attr2)
        openEnterAnimationId = activityStyle2.getResourceId(0, 0)
        openExitAnimationId = activityStyle2.getResourceId(1, 0)

        // 画面間のパラメータ処理
        var changedPreference = false
        currentPage = intent.getIntExtra(EXTRA_KEY_PAGE, 0)
        if (currentPage == 0 && AppPreference.startDay <= 0) {
            // 開始日が登録されていない場合、初回実行日を開始日に設定する
            AppPreference.startDay = MyCalendarUtil.calendarToInt(Calendar.getInstance())
            changedPreference = true
        }
        if (intent.hasExtra(EXTRA_KEY_YEN)) {
            // 1日あたりの貯金額を登録する
            AppPreference.yenPerDay = intent.getIntExtra(EXTRA_KEY_YEN, 0)
            changedPreference = true
        }
        if (currentPage == 3) {
            AppPreference.initialized = true
            changedPreference = true
        }
        if (changedPreference) {
            AppPreference.saveAll(applicationContext)
        }

        baseLayout = findViewById(R.id.wizardDialogBaseLayout)
        previousButton = findViewById(R.id.buttonPreviousWizard)
        nextButton = findViewById(R.id.buttonNextWizard)
        previousButton?.setOnClickListener { openPreviousPage() }
        nextButton?.setOnClickListener { openNextPage() }

        when (currentPage) {
            1 -> { create01() }
            2 -> { create02() }
            3 -> { create03() }
            else -> {
                create00()
            }
        }
    }

    private fun createTitle(titleId: Int) {
        val titleView = findViewById<TextView>(R.id.wizardTitle)
        titleView.setText(titleId)
    }

    private fun addText(messageId: Int) {
        val textView = TextView(this)
        textView.setText(messageId)
        textView.textSize = 18.0f

        baseLayout?.addView(textView)
    }

    private fun addText(message: String) {
        val textView = TextView(this)
        textView.text = message
        textView.textSize = 18.0f

        baseLayout?.addView(textView)
    }

    private fun create00() {
        // ウィザードの最初の画面
        createTitle(R.string.wizard00_title)

        addText(R.string.wizard00_message1)
        addText(R.string.wizard00_message2)
        addText(R.string.wizard00_message3)

        previousButton?.visibility = View.INVISIBLE
        nextButton?.visibility = View.VISIBLE
        nextButton?.setText(R.string.caption_next)
    }

    private fun create01() {
        // 1/3 1日あたりの貯金額を入力する
        val WC = ViewGroup.LayoutParams.WRAP_CONTENT
        val labelLayoutParam = LinearLayout.LayoutParams(WC, WC)
        labelLayoutParam.weight = 1.0f

        val inputLayoutParam = LinearLayout.LayoutParams(WC, WC)
        inputLayoutParam.weight = 1.0f

        createTitle(R.string.wizard01_title)

        addText(R.string.wizard01_message1)
        addText(R.string.wizard01_message2)

        val preLabel = TextView(this)
        preLabel.setText(R.string.yen_per_day_pre_label)
        preLabel.textSize = SettingDialogBase.TEXT_SIZE_MIDDLE
        preLabel.gravity = Gravity.END

        val postLabel = TextView(this)
        postLabel.setText(R.string.caption_post_yen)
        postLabel.textSize = SettingDialogBase.TEXT_SIZE_MIDDLE

        // 入力コントロール
        yenEdit = EditText(this)
        yenEdit?.inputType = InputType.TYPE_CLASS_NUMBER
        yenEdit?.textSize = SettingDialogBase.TEXT_SIZE_LARGE

        // レイアウト
        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.setHorizontalGravity(Gravity.CENTER)
        linearLayout.addView(preLabel, labelLayoutParam)
        linearLayout.addView(yenEdit, inputLayoutParam)
        linearLayout.addView(postLabel, labelLayoutParam)

        baseLayout?.addView(linearLayout)

        previousButton?.visibility = View.VISIBLE
        previousButton?.setText(R.string.caption_previous)

        nextButton?.visibility = View.VISIBLE
        nextButton?.setText(R.string.caption_next)

        // ソフトウェアキーボードを表示する
        yenEdit?.requestFocus()
        yenEdit?.selectAll()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    private fun create02() {
        // 2/3 貯金箱を用意する
        createTitle(R.string.wizard02_title)
        addText(R.string.wizard02_message1)

        previousButton?.visibility = View.VISIBLE
        previousButton?.setText(R.string.caption_previous)

        if (AppPreference.yenPerDay > 0) {
            val strMonth = String.format("%1$,3d", AppPreference.yenPerDay * 30)
            val strYear = String.format("%1$,3d", AppPreference.yenPerDay * 30 * 12)
            addText(String.format(getString(R.string.wizard02_add), strMonth, strYear))
        }

        nextButton?.visibility = View.VISIBLE
        nextButton?.setText(R.string.caption_next)
    }

    private fun create03() {
        // 3/3 開始
        createTitle(R.string.wizard03_title)
        addText(R.string.wizard03_message1)
        addText(R.string.wizard03_message2)
        addText(R.string.wizard03_message3)

        previousButton?.visibility = View.VISIBLE
        previousButton?.setText(R.string.caption_previous)

        nextButton?.visibility = View.VISIBLE
        nextButton?.setText(R.string.caption_complete)
    }

    private var vector: Int = 0

    private fun openNextPage() {
        if (currentPage < 3) {
            val nextIntent = Intent(this, WizardActivity::class.java)
            nextIntent.putExtra(EXTRA_KEY_PAGE, currentPage + 1)

            if (currentPage == 1
                    && yenEdit != null) {
                var strYen = yenEdit?.text ?: "0"
                if (strYen.isEmpty()) {
                    strYen = "0"
                }
                try {
                    val yen = Integer.parseInt(strYen.toString())
                    nextIntent.putExtra(EXTRA_KEY_YEN, yen)
                }
                catch (e: NumberFormatException) {
                    Toast.makeText(applicationContext, R.string.message_int_value_error, Toast.LENGTH_SHORT).show()
                    yenEdit?.text?.clear()
                    yenEdit?.isSelected = true
                    return
                }
            }

            startActivity(nextIntent)
        }
        vector = 0
        finish()
    }

    private fun openPreviousPage() {
        val prevIntent = Intent(this, WizardActivity::class.java)
        prevIntent.putExtra(EXTRA_KEY_PAGE, currentPage - 1)
        startActivity(prevIntent)

        vector = 1
        finish()
    }

    override fun finish() {
        super.finish()

        if (vector == 1) {
            overridePendingTransition(closeEnterAnimationId, closeExitAnimationId)
        } else {
            overridePendingTransition(openEnterAnimationId, openExitAnimationId)
        }
    }
}
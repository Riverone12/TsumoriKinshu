package biz.riverone.tsumorikinshu

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import biz.riverone.tsumorikinshu.common.Database
import biz.riverone.tsumorikinshu.common.MyCalendarUtil
import biz.riverone.tsumorikinshu.dialogs.SetPaymentDialogFragment
import biz.riverone.tsumorikinshu.dialogs.SetWithdrawalDialogFragment
import biz.riverone.tsumorikinshu.models.CheckedLog
import biz.riverone.tsumorikinshu.models.CheckedLogSummary
import biz.riverone.tsumorikinshu.models.WithdrawalLog
import biz.riverone.tsumorikinshu.views.CalendarActivity
import biz.riverone.tsumorikinshu.views.MemoActivity
import biz.riverone.tsumorikinshu.views.PreferenceActivity
import biz.riverone.tsumorikinshu.views.WizardActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import java.util.*

/**
 * 禁酒でつもり貯金
 * Copyright (C) 2017 J.Kawahara
 * 2017.10.29 J.Kawahara 新規作成
 * 2017.11.21 J.Kawahara 1.01 初版公開
 * 2017.11.22 J.Kawahara 1.02 開始日が固定値に設定されるバグを修正。禁酒メモHTML更新
 * 2018.1.1   J.Kawahara 1.06 1月のカレンダーが表示されないバグを修正
 * 2018.2.16 J.Kawahara 1.07 丸形アイコンを作成
 */

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_PREFERENCE = 999
        private const val UPDATE_MILLISECONDS: Long = 60 * 1000
    }

    private var currentDays: Int = 0
    private var database: Database? = null
    private var openWizardCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 画面をポートレートに固定する
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initializeControls()

        // AdMob
        MobileAds.initialize(applicationContext, "ca-app-pub-1882812461462801~6385270211")
        val adView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    override fun onStart() {
        super.onStart()

        // データベース接続を準備する
        database = Database(applicationContext)
        database?.openWritable()
    }

    override fun onStop() {
        super.onStop()

        // データベース接続を解除する
        database?.close()
    }

    private fun initializeControls() {

        // カレンダーアクティビティへのリンク
        val toCalendarLink = findViewById<FrameLayout>(R.id.linkToCalendar)
        toCalendarLink.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivityForResult(intent, 999)
            overridePendingTransition(R.anim.in_right, R.anim.out_left)
        }
    }

    override fun onResume() {
        super.onResume()

        prepareControls()
        prepareDoneButton()

        // 日付の表示を更新するタイマーをセットする
        displayCurrentDateTime()
        startTimer()

        if (!AppPreference.initialized) {
            if (openWizardCount > 0) {
                finish()
            } else {
                // 初期設定ウィザードを表示する
                openWizardCount += 1
                val wizardIntent = Intent(this, WizardActivity::class.java)
                startActivity(wizardIntent)
            }
        } else {
            openWizardCount = 0
        }
    }

    override fun onPause() {
        super.onPause()
        stopTimer()
    }

    private fun prepareControls() {
        val logSummary = CheckedLogSummary()
        val currentDay = MyCalendarUtil.calendarToInt(Calendar.getInstance())
        logSummary.load(database, currentDay)
        currentDays = logSummary.totalDays

        val postDays = getString(R.string.caption_post_day)
        val postYen = getString(R.string.caption_post_yen)

        val textViewTotalDays = findViewById<TextView>(R.id.textViewTotalDays)
        textViewTotalDays.text = numberFormat(logSummary.totalDays, "", postDays)

        val textViewDoneDays = findViewById<TextView>(R.id.textViewDoneDays)
        textViewDoneDays.text = numberFormat(logSummary.doneDays, "", postDays)

        var preStr = getString(R.string.title_continuous_days)
        val textViewContinuousDays = findViewById<TextView>(R.id.textViewContinuousDays)
        textViewContinuousDays.text = numberFormat(logSummary.continuousDays, preStr, postDays)

        val textViewAmount = findViewById<TextView>(R.id.textViewAmount)
        textViewAmount.text = numberFormat(logSummary.amount, "", postYen)

        preStr = getString(R.string.title_accumulated_amount)
        val textViewAccumulatedAmount = findViewById<TextView>(R.id.textViewAccumulatedAmount)
        textViewAccumulatedAmount.text = numberFormat(logSummary.accumulatedAmount, preStr, postYen)

        preStr = getString(R.string.title_yen_per_day)
        val textViewYenPerDay = findViewById<TextView>(R.id.textViewYenPerDay)
        textViewYenPerDay.text = numberFormat(AppPreference.yenPerDay, preStr, postYen)

        val buttonDone = findViewById<Button>(R.id.buttonDone)
        if (logSummary.continuousDays >= 1) {
            buttonDone.setText(R.string.caption_button_done1)
        } else {
            buttonDone.setText(R.string.caption_button_done0)
        }
    }

    private fun prepareDoneButton() {
        val currentDate = MyCalendarUtil.calendarToInt(Calendar.getInstance())
        val buttonDone = findViewById<Button>(R.id.buttonDone)
        val checkedLog = CheckedLog()
        if (checkedLog.find(ApplicationControl.database, currentDate)
                && checkedLog.isDone) {
            // チェック済み
            buttonDone.isEnabled = false
        } else {
            buttonDone.tag = currentDate
            buttonDone.setOnClickListener(doneButtonClickListener)
            buttonDone.isEnabled = true
        }
    }

    private val doneButtonClickListener = View.OnClickListener {
        sender ->
        val currentDate = sender.tag as Int
        val checkedLog = CheckedLog()
        checkedLog.checkDate = currentDate
        checkedLog.isDone = true

        checkedLog.register(ApplicationControl.database)

        prepareControls()
        prepareDoneButton()
    }

    private fun numberFormat(value: Int, pre: String = "", post: String = ""): String =
            (pre + String.format("%1$,3d", value) + post)

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_settings -> {
                // 設定画面を表示する
                val intent = Intent(this, PreferenceActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE_PREFERENCE)
                return true
            }
            R.id.menu_payment -> {
                // 入金
                SetPaymentDialogFragment.show(supportFragmentManager)
            }
            R.id.menu_withdrawal -> {
                // 貯金額を引き出す
                SetWithdrawalDialogFragment.show(supportFragmentManager)
            }
            R.id.menu_memo -> {
                // メモ
                val intent = Intent(this, MemoActivity::class.java)
                startActivity(intent)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var updated = false
        when (requestCode) {
            SetPaymentDialogFragment.REQUEST_CODE -> {
                // 入金
                if (data != null
                        && data.hasExtra(SetPaymentDialogFragment.ARG_KEY_PAYMENT)) {
                    val amount = data.getIntExtra(SetPaymentDialogFragment.ARG_KEY_PAYMENT, 0)
                    if (amount != 0) {
                        WithdrawalLog.register(database, amount)
                        updated = true
                    }
                }
            }
            SetWithdrawalDialogFragment.REQUEST_CODE -> {
                // 出金
                if (data != null
                        && data.hasExtra(SetWithdrawalDialogFragment.ARG_KEY_WITHDRAWAL)) {
                    val amount = data.getIntExtra(SetWithdrawalDialogFragment.ARG_KEY_WITHDRAWAL, 0)
                    if (amount != 0) {
                        WithdrawalLog.register(database, -amount)
                        updated = true
                    }
                }
            }
        }
        if (updated) {
            prepareControls()
        }
    }

    // 日付を表示する
    private val timerHandler = Handler()
    private val textViewCurrentDate by lazy { findViewById<TextView>(R.id.textViewCurrentDate) }
    private var lastMonth = -1
    private var lastDate = -1


    private fun startTimer() {
        timerHandler.postDelayed(timerProcess, UPDATE_MILLISECONDS)
    }

    private fun stopTimer() {
        timerHandler.removeCallbacksAndMessages(null)
    }

    private val timerProcess = object : Runnable {
        override fun run() {
            displayCurrentDateTime()
            timerHandler.postDelayed(this, UPDATE_MILLISECONDS)
        }
    }

    // 現在の日時を表示する
    private fun displayCurrentDateTime() {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val date = calendar.get(Calendar.DAY_OF_MONTH)

        // 日付を表示する
        if (lastMonth != month || lastDate != date) {
            lastMonth = month
            lastDate = date


            val wday = calendar.get(Calendar.DAY_OF_WEEK) - 1
            val wdayList = resources.getStringArray(R.array.week_array_short)
            val strWday = wdayList[wday]

            val capMon = getString(R.string.captionMonth)
            val capDate = getString(R.string.captionDate)
            val strDate = "$month$capMon$date$capDate($strWday)"

            textViewCurrentDate.text = strDate
        }
    }
}

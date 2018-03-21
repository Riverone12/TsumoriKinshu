package biz.riverone.tsumorikinshu.views

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout
import biz.riverone.tsumorikinshu.AppPreference
import biz.riverone.tsumorikinshu.R
import biz.riverone.tsumorikinshu.common.MyCalendarUtil
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import java.util.*

/**
 * CalendarActivity.kt: カレンダー表示アクテビティ
 * Copyright (C) 2017 J.Kawahara
 * 2017.10.30 J.Kawahara 新規作成
 */

class CalendarActivity : AppCompatActivity() {

    companion object {
        // 最大12か月分のカレンダーを表示する
        private const val MAX_MONTHS = 12
    }

    private val pager: ViewPager by lazy { findViewById<ViewPager>(R.id.pager) }
    private lateinit var pagerAdapter: MyFragmentPagerAdapter

    // 画面遷移時のアニメーション関連
    private var closeEnterAnimationId = 0
    private var closeExitAnimationId = 0

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        // 画面を閉じた時のアニメーション用の処理
        val typedArray = theme.obtainStyledAttributes(intArrayOf(android.R.attr.windowAnimationStyle))
        val windowAnimationStyleResId = typedArray.getResourceId(0, 0)

        val attr = intArrayOf(
                android.R.attr.activityCloseEnterAnimation,
                android.R.attr.activityCloseExitAnimation)
        val activityStyle = theme.obtainStyledAttributes(windowAnimationStyleResId, attr)
        closeEnterAnimationId = activityStyle.getResourceId(0, 0)
        closeExitAnimationId = activityStyle.getResourceId(1, 0)

        // 画面をポートレートに固定する
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // ページャーの準備
        pagerAdapter = MyFragmentPagerAdapter(this.supportFragmentManager)
        pager.adapter = pagerAdapter
        prepareFragments()

        initializeControls()

        // AdMob
        MobileAds.initialize(applicationContext, "ca-app-pub-1882812461462801~6385270211")
        val adView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(closeEnterAnimationId, closeExitAnimationId)
    }

    private fun initializeControls() {
        val linkBackToMain = findViewById<FrameLayout>(R.id.linkBackToMain)
        linkBackToMain.setOnClickListener {
            finish()
        }
    }

    private fun prepareFragments() {
        val currentCal = Calendar.getInstance()

        var startCal = currentCal.clone() as Calendar
        val months = -(MAX_MONTHS - 1)
        startCal.add(Calendar.MONTH, months)

        if (AppPreference.startDay > 0) {
            val startDateCal = MyCalendarUtil.intToCalendar(AppPreference.startDay)
            if (startDateCal > startCal) {
                startCal = startDateCal.clone() as Calendar
            }
        }

        pagerAdapter.initialize(startCal, currentCal)
    }

    override fun onResume() {
        super.onResume()

        val pageCount = pagerAdapter.count
        if (pageCount > 0) {
            pager.currentItem = pageCount - 1
        }
    }
}

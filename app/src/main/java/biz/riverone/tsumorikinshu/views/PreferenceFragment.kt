package biz.riverone.tsumorikinshu.views


import android.content.Intent
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import biz.riverone.tsumorikinshu.AppPreference
import biz.riverone.tsumorikinshu.R
import biz.riverone.tsumorikinshu.dialogs.ResetDialogFragment

/**
 * 禁酒でつもり貯金の設定
 * Copyright (C) 2017 J.Kawahara
 * 2017.11.6 J.Kawahara 新規作成
 */
class PreferenceFragment : PreferenceFragmentCompat() {

    override fun onResume() {
        super.onResume()

        // 禁酒開始日時の設定値を表示する
        val infoStartDay = findPreference(AppPreference.PREF_KEY_START_DAY)
        infoStartDay.summary = toStartDayCaption(AppPreference.startDay)

        // 1日当たりの貯金額の設定値を表示する
        val infoYenPerDay = findPreference(AppPreference.PREF_KEY_YEN_PER_DAY)
        infoYenPerDay.summary = toYenPerDayCaption(AppPreference.yenPerDay)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val resetPreference = findPreference("pref_reset")
        resetPreference.setOnPreferenceClickListener {
            val intent = Intent(context, PreferenceSubActivity::class.java)
            intent.putExtra(PreferenceSubActivity.EXTRA_KEY_MODE, ResetDialogFragment.REQUEST_CODE)
            startActivityForResult(intent, ResetDialogFragment.REQUEST_CODE)
            true
        }
    }

    private fun toStartDayCaption(startDay: Int) : String {
        val year = startDay / 10000
        val month = (startDay % 10000) / 100
        val day = startDay % 100

        return year.toString() + "年" + month + "月" + day + "日"
    }

    private fun toYenPerDayCaption(price: Int) : String {
        val strDay = String.format("%1$,3d", price) + "円"

        val yenPerMonth = price * 30
        val strMonth = " (1ヶ月で" + String.format("%1$,3d", yenPerMonth) + "円ぐらい)"

        return strDay + strMonth
    }
}
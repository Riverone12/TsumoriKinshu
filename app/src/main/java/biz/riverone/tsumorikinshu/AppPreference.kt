package biz.riverone.tsumorikinshu

import android.content.Context
import android.preference.PreferenceManager
import biz.riverone.tsumorikinshu.common.MyCalendarUtil
import java.util.*

/**
 * このアプリの設定項目
 * Created by kawahara on 2017/11/02.
 */
object AppPreference {

    const private val PREFERENCE_VERSION = 1

    // 初回起動の有無
    var initialized: Boolean = false

    // 禁酒開始日（y * 10000 + m * 100 + d）
    var startDay: Int = 0

    // 1日当たりの酒量（円）
    var yenPerDay: Int = 0

    // PREFERENCE_VERSION = 1
    private const val PREF_KEY_VERSION = "pref_version"
    private const val PREF_KEY_INITIALIZED = "pref_initialized"
    const val PREF_KEY_START_DAY = "pref_start_day"
    const val PREF_KEY_YEN_PER_DAY = "pref_yen_per_day"

    fun reset(applicationContext: Context) {
        initialized = false
        startDay = 0
        yenPerDay = 0
        saveAll(applicationContext)
    }

    fun initialize(applicationContext: Context) {
        val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        initialized = pref.getBoolean(PREF_KEY_INITIALIZED, false)
        startDay = pref.getInt(PREF_KEY_START_DAY, 0)
        yenPerDay = pref.getInt(PREF_KEY_YEN_PER_DAY, 0)

        val version = pref.getInt(PREF_KEY_VERSION, 0)
        if (version < PREFERENCE_VERSION) {

            AppPreference.startDay = MyCalendarUtil.calendarToInt(Calendar.getInstance())
            AppPreference.yenPerDay = 0

            saveAll(applicationContext)
        }
    }

    fun saveAll(applicationContext: Context) {
        val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = pref.edit()

        editor.putInt(PREF_KEY_VERSION, PREFERENCE_VERSION)
        editor.putBoolean(PREF_KEY_INITIALIZED, initialized)
        editor.putInt(PREF_KEY_START_DAY, startDay)
        editor.putInt(PREF_KEY_YEN_PER_DAY, yenPerDay)

        editor.apply()
    }
}
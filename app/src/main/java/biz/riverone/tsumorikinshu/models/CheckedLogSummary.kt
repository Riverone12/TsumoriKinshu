package biz.riverone.tsumorikinshu.models

import biz.riverone.tsumorikinshu.AppPreference
import biz.riverone.tsumorikinshu.common.Database
import biz.riverone.tsumorikinshu.common.MyCalendarUtil
import java.util.*

/**
 * CheckedLogSummary.kt: チェック履歴の集計
 * Created by kawahara on 2017/11/02.
 */
class CheckedLogSummary {

    var totalDays: Int = 0

    // 禁酒できた日の合計
    var doneDays: Int = 0

    // 禁酒できた連続日数
    var continuousDays: Int = 0

    // 貯金の累計額
    var accumulatedAmount: Int = 0

    // 現在の貯金額
    var amount: Int = 0

    private fun clear() {
        totalDays = 0
        doneDays = 0
        continuousDays = 0
        accumulatedAmount = 0
        amount = 0
    }

    fun load(database: Database?, currentDay: Int) {
        clear()

        countTotalDays(currentDay)
        countCheckedDays(database)
        countContinuousDays(database, currentDay)
        calcAmount(database)
    }

    private fun countTotalDays(currentDay: Int) {
        // 開始日からの日数をカウントする
        if (AppPreference.startDay <= 0) {
            totalDays = 0
        } else {
            val currentCal = MyCalendarUtil.intToCalendar(currentDay)
            val startCal = MyCalendarUtil.intToCalendar(AppPreference.startDay)
            totalDays = MyCalendarUtil.calcDayDiff(startCal, currentCal) + 1
            if (totalDays < 0) {
                totalDays = 0
            }
        }
    }

    private fun countCheckedDays(database: Database?) {
        // チェック履歴の数をカウントする
        val dbResource = database?.resource ?: return

        val chkColumns = arrayOf("COUNT(_id)")
        val chkWhere = "_id >= ? AND _id <= ? AND is_done = 1"
        val chkWhereArgs = arrayOf(
                AppPreference.startDay.toString(),
                MyCalendarUtil.calendarToInt(Calendar.getInstance()).toString())

        val chkCursor = dbResource.query(
                CheckedLog.TABLE_NAME,
                chkColumns,
                chkWhere,
                chkWhereArgs,
                null,
                null,
                null,
                null
        )
        if (chkCursor?.moveToFirst() == true) {
            doneDays = chkCursor.getInt(0)
        }
        chkCursor?.close()
    }

    private fun countContinuousDays(database: Database?, currentDay: Int) {
        // 連続日数をカウントする
        val dbResource = database?.resource ?: return

        var lastDoneDate = 0
        val lastLog = CheckedLog()

        val cal = MyCalendarUtil.intToCalendar(currentDay)
        if (lastLog.find(database, currentDay) && lastLog.isDone) {
            lastDoneDate = currentDay
        } else {
            cal.add(Calendar.DAY_OF_MONTH, -1)
            val yesterday = MyCalendarUtil.calendarToInt(cal)
            if (lastLog.find(database, yesterday) && lastLog.isDone) {
                lastDoneDate = yesterday
            }
        }
        if (lastDoneDate <= 0) {
            continuousDays = 0
            return
        }

        val chkColumns = arrayOf("MAX(_id)")
        val chkWhere = "_id < ? AND is_done = 0"
        val chkWhereArgs = arrayOf(currentDay.toString())

        val chkCursor = dbResource.query(
                CheckedLog.TABLE_NAME,
                chkColumns,
                chkWhere,
                chkWhereArgs,
                null,
                null,
                null,
                null
        )
        var lastNotDoneDate = 0
        if (chkCursor?.moveToFirst() == true) {
            lastNotDoneDate = chkCursor.getInt(0)
        }
        chkCursor?.close()

        if (lastNotDoneDate > 0) {
            // 連続日数の開始日が禁酒開始日よりも前の日付の場合、
            // 禁酒開始日を連続日数の開始日にする
            val stCal = MyCalendarUtil.intToCalendar(AppPreference.startDay)
            stCal.add(Calendar.DAY_OF_MONTH, -1)
            val stCalInt = MyCalendarUtil.calendarToInt(stCal)

            if (lastNotDoneDate < stCalInt) {
                lastNotDoneDate = stCalInt
            }
            val lastDate = MyCalendarUtil.intToCalendar(lastNotDoneDate)
            continuousDays = MyCalendarUtil.calcDayDiff(lastDate, cal)
        }
    }

    private fun calcAmount(database: Database?) {
        // 貯金額を計算する
        val dbResource = database?.resource ?: return

        var addition = 0

        // 出金履歴を集計する
        val balColumns = arrayOf("SUM(amount)")
        val balCursor = dbResource.query(
                "withdrawal_log",
                balColumns,
                null,
                null,
                null,
                null,
                null,
                null
        )
        if (balCursor?.moveToFirst() == true) {
            addition = balCursor.getInt(0)
        }
        balCursor?.close()

        // 貯金額の累計（1日の酒量 x 禁酒できた日数）
        accumulatedAmount = AppPreference.yenPerDay * doneDays

        // 現在の貯金額（貯金額の累計 + 出金額累計）
        amount = accumulatedAmount + addition
    }
}
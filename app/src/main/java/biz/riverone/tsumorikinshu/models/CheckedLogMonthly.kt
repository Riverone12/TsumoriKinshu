package biz.riverone.tsumorikinshu.models

import biz.riverone.tsumorikinshu.common.Database
import biz.riverone.tsumorikinshu.common.MyCalendarUtil
import java.util.*

/**
 * CheckedLogMonthly.kt: チェック履歴（1ヶ月分）
 * Created by kawahara on 2017/11/04.
 */
class CheckedLogMonthly {

    private val targetList = ArrayList<CheckedLog>()

    fun clear() {
        targetList.clear()
    }

    fun find(dt: Int) : CheckedLog {
        targetList
                .filter { it.checkDate == dt }
                .forEach { return it }

        val dummy = CheckedLog()
        dummy.checkDate = dt
        dummy.isDone = false
        return dummy
    }

    fun update(checkedLog: CheckedLog) {
        var found = false
        for (i in targetList.indices) {
            if (targetList[i].checkDate == checkedLog.checkDate) {
                targetList[i] = checkedLog
                found = true
                break
            }
        }
        if (!found) {
            targetList.add(checkedLog)
        }
    }

    fun load(database: Database?, year: Int, month: Int) {
        // 目的の月と前後7日ずつのチェック履歴一覧を読み込む
        clear()
        val db = database?.resource ?: return

        val startDate = MyCalendarUtil.startOfMonth(year, month)
        val endDate = MyCalendarUtil.endOfMonth(year, month)

        val selection = "_id >= ? and _id <= ?"
        val selectionArgs = arrayOf(startDate.toString(), endDate.toString())
        val orderBy = "_id"

        val cursor = db.query(
                CheckedLog.TABLE_NAME,
                CheckedLog.columns,
                selection,
                selectionArgs,
                null, null, orderBy
        )

        while (cursor.moveToNext()) {
            val checkedLog = CheckedLog()
            checkedLog.checkDate = cursor.getInt(0)
            checkedLog.isDone = (cursor.getInt(1) == 1)
            targetList.add(checkedLog)
        }
        cursor.close()
    }
}
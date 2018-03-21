package biz.riverone.tsumorikinshu.models

import android.content.ContentValues
import biz.riverone.tsumorikinshu.common.Database
import biz.riverone.tsumorikinshu.common.MyCalendarUtil
import java.util.*

/**
 * CheckedLog.kt: チェック履歴
 * Created by kawahara on 2017/11/04.
 */
class CheckedLog {

    companion object {
        const val TABLE_NAME = "checked_log"
        val columns = arrayOf("_id", "is_done")

        fun deleteAll(database: Database?) {
            val db = database?.resource
            db?.delete(TABLE_NAME, null, null)
        }
    }

    var checkDate: Int = 0
    var isDone: Boolean = false

    private fun clear() {
        checkDate = 0
        isDone = false
    }

    fun find(database: Database?, dateValue: Int) : Boolean {
        clear()
        val db = database?.resource ?: return false

        val selection = "_id = ?"
        val selectionArgs = arrayOf(dateValue.toString())

        val cursor = db.query(
                TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null, null, null
        )

        var result = false
        if (cursor.moveToFirst()) {
            checkDate = cursor.getInt(0)
            isDone = (cursor.getInt(1) == 1)
            result = true
        }
        cursor.close()
        return result
    }

    private fun insert(database: Database?) {
        val isDoneInt = if (isDone) 1 else 0

        val contentValues = ContentValues()
        contentValues.put("_id", checkDate)
        contentValues.put("is_done", isDoneInt)

        val db = database?.resource
        db?.insert(TABLE_NAME, null, contentValues)
    }

    private fun update(database: Database?) {
        val isDoneInt = if (isDone) 1 else 0

        val whereClause = "_id = ?"
        val whereArgs = arrayOf(checkDate.toString())

        val contentValues = ContentValues()
        contentValues.put("is_done", isDoneInt)

        val db = database?.resource
        db?.update(TABLE_NAME, contentValues, whereClause, whereArgs)
    }

    fun register(database: Database?) {
        val temp = CheckedLog()
        if (temp.find(database, checkDate)) {
            update(database)
        } else {
            insert(database)
        }

        // 1日前の未チェックレコードが存在しない場合、新たに作成する
        // （連続日数のカウントに必要）
        if (isDone) {
            val cal = MyCalendarUtil.intToCalendar(checkDate)
            cal.add(Calendar.DAY_OF_MONTH, -1)
            val lastDay = MyCalendarUtil.calendarToInt(cal)
            if (!temp.find(database, lastDay)) {
                temp.checkDate = lastDay
                temp.isDone = false
                temp.insert(database)
            }
        }
    }
}
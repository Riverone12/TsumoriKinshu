package biz.riverone.tsumorikinshu.models

import android.content.ContentValues
import biz.riverone.tsumorikinshu.common.Database
import biz.riverone.tsumorikinshu.common.MyCalendarUtil
import java.util.*

/**
 * WithdrawalLog.kt: 出金履歴
 * Created by kawahara on 2017/11/09.
 */
class WithdrawalLog {

    companion object {
        private const val TABLE_NAME = "withdrawal_log"

        fun register(database: Database?, amount: Int) {
            val currentDateTime = Calendar.getInstance()
            val currentDate = MyCalendarUtil.calendarToInt(currentDateTime)

            val hour = currentDateTime.get(Calendar.HOUR)
            val minute = currentDateTime.get(Calendar.MINUTE)
            val second = currentDateTime.get(Calendar.SECOND)
            val currentTime = (hour * 10000) + (minute + 100) + second

            val contentValues = ContentValues()
            contentValues.put("w_date", currentDate)
            contentValues.put("w_time", currentTime)
            contentValues.put("amount", amount)

            val db = database?.resource
            db?.insert(TABLE_NAME, null, contentValues)
        }

        fun deleteAll(database: Database?) {
            val db = database?.resource
            db?.delete(TABLE_NAME, null, null)
        }
    }
}
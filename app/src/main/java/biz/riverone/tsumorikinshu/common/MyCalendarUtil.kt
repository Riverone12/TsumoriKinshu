package biz.riverone.tsumorikinshu.common

import java.util.*

/**
 * カレンダー関連の関数群
 * Created by kawahara on 2017/10/31.
 */
object MyCalendarUtil {

    fun calendarToInt(calendar: Calendar) : Int {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val date = calendar.get(Calendar.DATE)

        return (year * 10000) + (month * 100) + date
    }

    fun intToCalendar(value: Int) : Calendar {
        val calendar = Calendar.getInstance()
        calendar.clear()
        val year = value / 10000
        val month = (value % 10000) / 100
        val date = value % 100
        calendar.set(year, month - 1, date)

        return calendar
    }

    fun calcDayDiff(from: Calendar, to: Calendar): Int {
        val diff = to.timeInMillis - from.timeInMillis
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }

    fun startOfMonth(year: Int, month: Int) : Int {
        return (year * 10000 + month * 100 + 1)
    }

    fun endOfMonth(year: Int, month: Int) : Int {
        val cal = intToCalendar(year * 10000 + month * 100 + 1)
        cal.add(Calendar.MONTH, 1)
        cal.add(Calendar.DAY_OF_MONTH, -1)
        return calendarToInt(cal)
    }
}
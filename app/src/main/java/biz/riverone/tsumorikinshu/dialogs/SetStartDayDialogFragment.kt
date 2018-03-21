package biz.riverone.tsumorikinshu.dialogs

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.View
import android.widget.DatePicker
import android.widget.LinearLayout
import biz.riverone.tsumorikinshu.R
import biz.riverone.tsumorikinshu.common.MyCalendarUtil
import java.util.*

/**
 * SetStartDayDialogFragment.kt: 開始日を設定するダイアログ
 * Created by kawahara on 2017/11/05.
 */
class SetStartDayDialogFragment : SettingDialogBase() {

    companion object {

        private val TAG = SetStartDayDialogFragment::class.java.simpleName
        const val REQUEST_CODE: Int = 101
        const val ARG_KEY_START_DAY = "start_day"

        fun show(manager: FragmentManager, currentValue: Int) {
            val dialog = SetStartDayDialogFragment()
            dialog.setTargetFragment(null, REQUEST_CODE)
            val arg = Bundle()
            arg.putInt(ARG_KEY_START_DAY, currentValue)
            dialog.arguments = arg
            dialog.show(manager, TAG)
        }
    }

    private var datePicker: DatePicker? = null

    override val dialogTitleResourceId: Int
        get() = R.string.title_start_day_dialog

    @Suppress("DEPRECATION")
    override fun initializeControls(v: View) {
        val currentCal = Calendar.getInstance()

        datePicker = DatePicker(activity)
        datePicker?.calendarViewShown = false
        datePicker?.spinnersShown = true
        datePicker?.maxDate = currentCal.timeInMillis

        var startDay = 0
        if (arguments != null && arguments.containsKey(ARG_KEY_START_DAY)) {
            startDay = arguments.getInt(ARG_KEY_START_DAY, 0)
        }
        if (startDay <= 0) {
            startDay = MyCalendarUtil.calendarToInt(currentCal)
        }
        val year = startDay / 10000
        val month = (startDay % 10000) / 100
        val day = startDay % 100

        datePicker?.updateDate(year, month - 1, day)

        val layout = v.findViewById<LinearLayout>(R.id.settingDialogBaseLayout)
        layout.addView(datePicker)
    }

    override fun putResult(result: Intent): Intent {
        val year = datePicker?.year ?: 0
        val month = (datePicker?.month ?: 0) + 1
        val day = datePicker?.dayOfMonth ?: 0

        val dt = (year * 10000) + (month * 100) + day
        result.putExtra(ARG_KEY_START_DAY, dt)

        return result
    }
}
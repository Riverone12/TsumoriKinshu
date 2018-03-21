package biz.riverone.tsumorikinshu.views

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import biz.riverone.tsumorikinshu.AppPreference
import biz.riverone.tsumorikinshu.ApplicationControl
import biz.riverone.tsumorikinshu.R
import biz.riverone.tsumorikinshu.common.EqualWidthHeightTextView
import biz.riverone.tsumorikinshu.common.MyCalendarUtil
import biz.riverone.tsumorikinshu.models.CheckedLogMonthly
import java.util.*

/**
 * CalendarFragment.kt: 1ヶ月分のカレンダー表示フラグメント
 * Copyright (C) 2017 J.Kawahara
 * 2017.10.30 J.Kawahara
 * 2018.1.1   J.Kawahara 1月のカレンダーが表示されないバグを修正
 */
class CalendarFragment : Fragment() {

    private var calendar = Calendar.getInstance()
    private var rowCache = ArrayList<View>()
    private val checkedLogMonthly = CheckedLogMonthly()
    private var defaultTextColor = -1

    companion object {
        fun create(calendar: Calendar): CalendarFragment {

            val bundle = Bundle()
            bundle.putInt(ARG_KEY_TARGET_YMD, MyCalendarUtil.calendarToInt(calendar))

            val fragment = CalendarFragment()
            fragment.arguments = bundle

            return fragment
        }

        private const val ARG_KEY_TARGET_YMD = "TARGET_YMD"
        private const val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT
        private const val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater!!.inflate(R.layout.fragment_calendar, container, false)

        // データを受け取る
        val ymd = arguments.getInt(ARG_KEY_TARGET_YMD)
        calendar = MyCalendarUtil.intToCalendar(ymd)

        initializeControls(v)

        return v
    }

    private fun initializeControls(v: View) {

        // カレンダーを表示する
        val cal = calendar.clone() as Calendar
        createCalendarView(v, cal)
    }

    val title: String get() {
        if (arguments.containsKey(ARG_KEY_TARGET_YMD)) {
            val ymd = arguments.getInt(ARG_KEY_TARGET_YMD)
            calendar = MyCalendarUtil.intToCalendar(ymd)
        }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1

        return year.toString() + "年" + month + "月"
    }

    private val dateClickListener = View.OnClickListener {
        sender ->

        val dt = sender.tag as Int
        val log = checkedLogMonthly.find(dt)

        log.isDone = !(log.isDone)
        log.register(ApplicationControl.database)
        checkedLogMonthly.update(log)

        if (sender is TextView) {
            setBackground(sender, log.isDone)
        }
    }

    private fun createCalendarView(v: View, cal: Calendar) {
        val calendarLayout = v.findViewById<TableLayout>(R.id.calendarLayout)

        // すでに表示済みの行がある場合、削除する
        for (row in rowCache) {
            calendarLayout.removeView(row)
        }
        rowCache.clear()

        if (!isAdded) {
            return
        }

        val targetYear = cal.get(Calendar.YEAR)
        val targetMonth = cal.get(Calendar.MONTH) + 1

        val today = MyCalendarUtil.calendarToInt(Calendar.getInstance())

        // データベースからチェック履歴を読み込む
        checkedLogMonthly.load(ApplicationControl.database, targetYear, targetMonth)

        // カレンダー表示の最初の日を特定する（当月1日が日曜日でない場合、前月最後の日曜日）
        while (cal.get(Calendar.DAY_OF_WEEK) > 1) {
            cal.add(Calendar.DATE, -1)
            Log.d("TAG", cal.get(Calendar.DAY_OF_WEEK).toString())
        }

        // 行のレイアウトパラメータ
        val rowLayoutParams = TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        rowLayoutParams.weight = 1.0f

        // 列のレイアウトパラメータ
        val colLayoutParams = TableRow.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
        colLayoutParams.weight = 1.0f
        val marginLayout = colLayoutParams as ViewGroup.MarginLayoutParams
        marginLayout.setMargins(12, 12, 12, 12)

        val textSizeLarge = resources.getDimension(R.dimen.textSizeLarge)
        // val textSizeMiddle = resources.getDimension(R.dimen.textSizeMiddle)
        // val textSizeSmall = resources.getDimension(R.dimen.textSizeSmall)

        for (rowIndex in 0..5) {
            val currentMonth = cal.get(Calendar.MONTH) + 1
            if (currentMonth > targetMonth) {
                if (targetMonth != 1 || currentMonth != 12) {
                    break
                }
            }
            val row = TableRow(context)

            for (i in 0..6) {
                val month = cal.get(Calendar.MONTH) + 1
                val dt = MyCalendarUtil.calendarToInt(cal)
                // val child = TextView(context)
                val child = EqualWidthHeightTextView(context)
                if (defaultTextColor < 0) {
                    defaultTextColor = child.currentTextColor
                }

                if (month == targetMonth) {
                    child.text = cal.get(Calendar.DATE).toString()
                    child.gravity = Gravity.CENTER
                    child.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeLarge)

                    if (dt in AppPreference.startDay..today) {
                        // 開始日から今日までの間
                        // child.typeface = Typeface.DEFAULT_BOLD
                        child.tag = dt

                        val log = checkedLogMonthly.find(dt)
                        setBackground(child, log.isDone)
                        child.setOnClickListener(dateClickListener)
                    }
                }

                row.addView(child, colLayoutParams)
                cal.add(Calendar.DATE, 1)
            }
            calendarLayout.addView(row, rowLayoutParams)
            rowCache.add(row)
        }
    }

    private fun setBackground(textView: TextView, isDone: Boolean) {
        if (isDone) {
            textView.setBackgroundResource(R.drawable.calendar_done_background)
            textView.setTextColor(ContextCompat.getColor(context, R.color.colorLightGray))
        } else {
            textView.setBackgroundResource(R.drawable.calendar_not_done_background)
            textView.setTextColor(defaultTextColor)
        }
    }
}

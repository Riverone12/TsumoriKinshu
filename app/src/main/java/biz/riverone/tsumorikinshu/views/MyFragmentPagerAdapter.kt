package biz.riverone.tsumorikinshu.views

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import biz.riverone.tsumorikinshu.common.MyFragmentPagerAdapterBase
import java.util.*

/**
 * MyFragmentPagerAdapter.kt: スワイプでページを切り替える仕組み
 * Created by kawahara on 2017/10/30.
 */
class MyFragmentPagerAdapter(fragmentManager: FragmentManager)
    : MyFragmentPagerAdapterBase<String>(fragmentManager) {

    private val fragmentList = ArrayList<Fragment>()

    fun initialize(startDate: Calendar, currentDate: Calendar) {

        val lim = currentDate.clone() as Calendar
        lim.set(Calendar.DATE, 1)

        val cal = startDate.clone() as Calendar
        cal.set(Calendar.DATE, 1)

        while (cal <= lim) {
            val fragment = CalendarFragment.create(cal)
            fragmentList.add(fragment)
            add(fragment.title)

            cal.add(Calendar.MONTH, 1)
        }
    }

    private fun getFragmentByPosition(position: Int): Fragment {
        if (position < fragmentList.size) {
            return fragmentList[position]
        }
        return fragmentList[0]
    }

    override fun getFragment(item: String?, position: Int): Fragment {
        return getFragmentByPosition(position)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return getItem(position)
    }
}
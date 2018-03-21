package biz.riverone.tsumorikinshu.views

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import biz.riverone.tsumorikinshu.AppPreference
import biz.riverone.tsumorikinshu.ApplicationControl
import biz.riverone.tsumorikinshu.dialogs.ResetDialogFragment
import biz.riverone.tsumorikinshu.dialogs.SetStartDayDialogFragment
import biz.riverone.tsumorikinshu.dialogs.SetYenPerDayDialogFragment
import biz.riverone.tsumorikinshu.dialogs.SettingDialogBase
import biz.riverone.tsumorikinshu.models.CheckedLog
import biz.riverone.tsumorikinshu.models.WithdrawalLog

/**
 * 禁酒でつもり貯金の設定
 * Created by kawahara on 2017/11/06.
 */

// PreferenceScreen から飛ばすIntent を受けるため、
// このActivity を用意する

class PreferenceSubActivity
    : AppCompatActivity(), SettingDialogBase.OnCloseListener {

    companion object {
        const val EXTRA_KEY_MODE = "preference_mode"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mode = intent.getIntExtra(EXTRA_KEY_MODE, 0)
        when (mode) {
            SetStartDayDialogFragment.REQUEST_CODE -> {
                SetStartDayDialogFragment.show(supportFragmentManager, AppPreference.startDay)
            }
            SetYenPerDayDialogFragment.REQUEST_CODE -> {
                SetYenPerDayDialogFragment.show(supportFragmentManager, AppPreference.yenPerDay)
            }
            ResetDialogFragment.REQUEST_CODE -> {
                ResetDialogFragment.show(supportFragmentManager)
            }
            else -> {
                finish()
            }
        }
    }

    override fun onClose(dialog: SettingDialogBase) {
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var changedPreference = false
        when (requestCode) {
            SetStartDayDialogFragment.REQUEST_CODE -> {
                // 禁酒開始日設定ダイアログから戻ってきた
                if (data != null
                        && data.hasExtra(SetStartDayDialogFragment.ARG_KEY_START_DAY)) {
                    val startDate = data.getIntExtra(SetStartDayDialogFragment.ARG_KEY_START_DAY, 0)
                    if (startDate > 0 && startDate != AppPreference.startDay) {
                        AppPreference.startDay = startDate
                        changedPreference = true
                    }
                }
            }
            SetYenPerDayDialogFragment.REQUEST_CODE -> {
                // 1日当たりの貯金額設定ダイアログから戻ってきた
                if (data != null
                        && data.hasExtra(SetYenPerDayDialogFragment.ARG_KEY_YEN_PER_DAY)) {
                    val yenPerDay = data.getIntExtra(SetYenPerDayDialogFragment.ARG_KEY_YEN_PER_DAY, 0)
                    if (yenPerDay != AppPreference.yenPerDay) {
                        AppPreference.yenPerDay = yenPerDay
                        changedPreference = true
                    }

                }
            }
            ResetDialogFragment.REQUEST_CODE-> {
                // リセットダイアログから戻ってきた
                if (data != null
                        && data.hasExtra(ResetDialogFragment.ARG_KEY_RESET)) {
                    val doReset = data.getBooleanExtra(ResetDialogFragment.ARG_KEY_RESET, false)
                    if (doReset) {
                        CheckedLog.deleteAll(ApplicationControl.database)
                        WithdrawalLog.deleteAll(ApplicationControl.database)
                        AppPreference.reset(applicationContext)
                    }
                }
            }
        }

        if (changedPreference) {
            AppPreference.saveAll(applicationContext)
        }
    }
}
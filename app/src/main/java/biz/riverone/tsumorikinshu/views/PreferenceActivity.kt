package biz.riverone.tsumorikinshu.views

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import biz.riverone.tsumorikinshu.AppPreference
import biz.riverone.tsumorikinshu.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

/**
 * 禁酒でつもり貯金の設定
 * Created by kawahara on 2017/11/06.
 */

class PreferenceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference)

        // 画面をポートレートに固定する
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // AdMob
        MobileAds.initialize(applicationContext, "ca-app-pub-1882812461462801~6385270211")
        val adView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    override fun onResume() {
        super.onResume()
        this.title = getString(R.string.pref_dialog_title)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // ここに入るのは「リセット」を実行した場合のみ
        if (!AppPreference.initialized) {
            finish()
        }
    }
}

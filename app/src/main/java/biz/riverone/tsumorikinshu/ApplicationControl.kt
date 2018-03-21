package biz.riverone.tsumorikinshu

import android.app.Application
import android.content.Context
import biz.riverone.tsumorikinshu.common.Database

/**
 * Application 開始時と終了時の処理を定義
 * Created by kawahara on 2017/11/04.
 */
class ApplicationControl : Application() {

    companion object {
        private lateinit var instance: ApplicationControl

        val context: Context
            get() {
                return instance.applicationContext
            }

        val database: Database?
            get() {
                return instance.database
            }
    }

    private var database: Database? = null

    override fun onCreate() {
        super.onCreate()

        instance = this

        // 設定項目を初期化
        AppPreference.initialize(context)

        // データベース接続
        database = Database(context)
        database?.openWritable()
    }

    override fun onTerminate() {
        super.onTerminate()

        database?.close()
    }
}
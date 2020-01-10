package lyon.calculator.kotlinfloatview

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.view.WindowManager
import android.widget.ImageView
import java.util.*


class FLoatService : Service() {
    var context:Context =this

    lateinit var floatViewManager:FloatViewManager
    override fun onCreate() {
        super.onCreate()
        context = this
        val windowManager = getSystemService(Service.WINDOW_SERVICE) as WindowManager
        floatViewManager = FloatViewManager(this,windowManager)
        floatViewManager.showFloatViewOnWindow()
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onDestroy() {
        super.onDestroy()
        floatViewManager.onDestroy()
    }
}
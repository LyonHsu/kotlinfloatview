package lyon.calculator.kotlinfloatview

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    var TAG = "MainActivity"
    var OVERLAY_PERMISSION_REQ_CODE = 999
    var content:Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        content = this
    }

    override fun onStart() {
        super.onStart()
        if (hasOverlayPermission()) {
            val intent = Intent(this, FLoatService::class.java)
            startService(intent)
        } else {
            requestOverlayPermission(OVERLAY_PERMISSION_REQ_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === OVERLAY_PERMISSION_REQ_CODE) {
            if (hasOverlayPermission()) {
                Toast.makeText(this, "权限授予成功！", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, FLoatService::class.java)
                startService(intent)
            }else{
                Toast.makeText(this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //判斷是否以獲取  permission
    fun hasOverlayPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= 23)
            return Settings.canDrawOverlays(this)
        else
            return true
    }

    //進入Android permission權限獲取頁面
    fun requestOverlayPermission(requestCode: Int) {
        if (Build.VERSION.SDK_INT >= 23) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, requestCode)
        }
    }
}

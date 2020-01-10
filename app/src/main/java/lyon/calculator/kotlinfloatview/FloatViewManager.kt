package lyon.calculator.kotlinfloatview

import android.content.Context
import android.graphics.PixelFormat
import android.icu.text.Transliterator
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.Toast
import kotlin.math.roundToInt

class FloatViewManager() {
    val TAG = FloatViewManager::class.java.simpleName
    //自定義的FloatView
    private var mFloatView: View? = null
    //視窗管理類
    private var mTouchStartX = 0f
    private var mTouchStartY = 0f
    private var x = 0f
    private var y = 0f
    lateinit var mWindowManager:WindowManager
    lateinit var context: Context

    constructor(context: Context, mWindowManager:WindowManager) : this() {
        this.mWindowManager=mWindowManager
        this.context=context
        mFloatView = getFloatView(context)
        mFloatView!!.setOnClickListener {
            Toast.makeText(context, "You click the Float! contentId:42554", Toast.LENGTH_LONG).show()
        }
        mFloatView!!.setOnKeyListener { view, i, keyEvent -> false }
        mFloatView!!.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(view: View, event: MotionEvent): Boolean {
                Log.d(TAG, "motionEvent:$event")
                x = event.getRawX();
                y = event.getRawY() - 25; // 25是系統狀態列的高度
                Log.i("currP", "currX" + x + "====currY" + y);

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // 獲取相對View的座標，即以此View左上角為原點
                        mTouchStartX = event.getX();
                        mTouchStartY = event.getY();
                        Log.i("startP", "startX" + mTouchStartX + "====startY"
                                + mTouchStartY);
                    }
                    MotionEvent.ACTION_MOVE -> {
                        //获取相对View的坐标，即以此View左上角为原点
                        x =event.x
                        y = event.y //25是系统状态栏的高度
                        Log.i("currP", "floatposition event.x:"+event.x+",event.y:"+event.y)
                        updateViewPosition()
                    }
                    MotionEvent.ACTION_UP -> {
                        updateViewPosition()
                        mTouchStartX = 0f
                        mTouchStartY= 0f
                        Log.i("updateViewPosition", "floatposition parmas.x:" + parmas!!.x + "====parmas.y:" + parmas!!.y)
                    }
                }
                return true
            }
        })
    }

    private fun updateViewPosition() {
        //更新浮动窗口位置参数
        parmas.x = (x - mTouchStartX).toInt();
        parmas.y = (y - mTouchStartY).toInt();

        mWindowManager!!.updateViewLayout(mFloatView, parmas)
    }

    private fun getFloatView(context: Context): View? {
        return FloatView().getView(context)
    }

    private val parmas = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_PHONE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT)
        .apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }

    fun showFloatViewOnWindow() {
        var floatWidth = Tool().dpToPx(context,200)
        var  floatHeight =Tool().dpToPx(context,200)// mWindowManager.defaultDisplay.height / 2
        parmas.width = floatWidth
        parmas.height = floatHeight
        //視窗圖案放置位置
        parmas!!.gravity = Gravity.LEFT or Gravity.CENTER
        // 如果忽略gravity屬性，那麼它表示視窗的絕對X位置。
        parmas.x = mWindowManager!!.defaultDisplay.width - floatWidth
        //如果忽略gravity屬性，那麼它表示視窗的絕對Y位置。
        parmas.y = 0
        ////電話視窗。它用於電話互動（特別是呼入）。它置於所有應用程式之上，狀態列之下。
        parmas.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        //Android O以後要改為TYPE_APPLICATION_OVERLAY
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            parmas.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            parmas.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        //FLAG_NOT_FOCUSABLE讓window不能獲得焦點，這樣使用者快就不能向該window傳送按鍵事件及按鈕事件
        //FLAG_NOT_TOUCH_MODAL即使在該window在可獲得焦點情況下，仍然把該window之外的任何event傳送到該window之後的其他window.
        parmas.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        // 期望的點陣圖格式。預設為不透明。參考android.graphics.PixelFormat。
        parmas.format = PixelFormat.RGBA_8888
        mWindowManager!!.addView(mFloatView, parmas)
    }


    fun onDestroy() {
        if (mWindowManager != null && mWindowManager != null) {
            mWindowManager!!.removeView(mFloatView)
        }
    }
}
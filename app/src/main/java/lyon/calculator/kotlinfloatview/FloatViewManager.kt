package lyon.calculator.kotlinfloatview

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Point
import android.icu.text.Transliterator
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.Toast
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 *  ref.http://afra55.github.io/2017/04/26/floating_view/
 */
class FloatViewManager() {
    val TAG = FloatViewManager::class.java.simpleName
    //自定義的FloatView
    private var mFloatView: View? = null
    var floatView:FloatView = FloatView()
    //視窗管理類
    private var mTouchStartX = 0f
    private var mTouchStartY = 0f
    private var x = 0f
    private var y = 0f
    val floatW = 50
    val floatH = 50
     var floatWidth:Int=0
     var floatHeight:Int=0
    /**
     * 屏幕宽高
     */
    var mScreenWidth = 0
    var mScreenHeight:Int = 0
    /**
     * 透明度
     */
    private var mCurrentIconAlpha: Float = 1f

    lateinit var mWindowManager:WindowManager
    lateinit var context: Context
    var isMove:Boolean = false;
    constructor(context: Context, mWindowManager:WindowManager) : this() {
        this.mWindowManager=mWindowManager
        this.context=context
        getScreenSize()
        floatWidth = Tool().dpToPx(context,floatW)
        floatHeight =Tool().dpToPx(context,floatH)
        floatView = FloatView()
        mFloatView = floatView.getView(context)

        mFloatView!!.setOnClickListener {
            Toast.makeText(context, "You click the Float Image View!", Toast.LENGTH_LONG).show()
        }

        floatView!!.setOnFloatMoveTouch(object : FloatView.FloatMoveTouch {
            override fun OnTouch(view: View, event: MotionEvent) {
                Log.d(TAG, "motionEvent:$event")
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // 獲取相對View的座標，即以此View左上角為原點
                        mTouchStartX = event.getX();
                        mTouchStartY = event.getY();
                        isMove = false;
                        Log.i("startP", "startX" + mTouchStartX + "====startY"
                                + mTouchStartY);
                    }
                    MotionEvent.ACTION_MOVE -> {
                        //获取相对View的坐标，即以此View左上角为原点
                        x = event.getRawX();
                        y = event.getRawY()-Tool().dpToPx(context,25); // 25是系統狀態列的高度
                        Log.i("currP", "floatposition event.x:"+event.x+",event.y:"+event.y)
                        updateViewPosition()
                    }
                    MotionEvent.ACTION_UP -> {
                        isMove = false;
                        updateViewPosition()
                        mTouchStartX = 0f
                        mTouchStartY= 0f
                    }
                }
            }
        })
    }

    private fun updateViewPosition() {
        //更新浮动窗口位置参数
        val newX = (x - mTouchStartX).toInt();
        val newY = (y - mTouchStartY).toInt();

        if(abs(newX-parmas.x) >50 || abs(newY-parmas.y) >50) {
            isMove = true;
            //更新浮动窗口位置参数
            parmas.x =newX
            parmas.y =newY

            //判斷是否顯示移除floatView
            val offset = Tool().dpToPx(context,100)
            var ScreenX1 = (mScreenWidth-(parmas.width+offset))/2
            var ScreenX2 =(mScreenWidth+(parmas.width+offset))/2
            var ScreenH1 = (mScreenHeight-(parmas.height+offset))/5*4
            var ScreenH2 = (mScreenHeight+(parmas.height+offset))/5*4
            if(newX>ScreenX1 && newX<ScreenX2 && newY>ScreenH1 && newY <ScreenH2){
                parmas.x =(mScreenWidth-parmas.width)/2
                parmas.y = mScreenHeight/5*4
                floatView.closeImg.visibility= VISIBLE
            }else{
                floatView.closeImg.visibility= View.GONE
            }


            mWindowManager!!.updateViewLayout(mFloatView, parmas)
        }
        Log.i("updateViewPosition", "floatposition parmas.x:" + parmas.x + "====parmas.y:" + parmas.y)
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
            gravity=Gravity.TOP or Gravity.LEFT
            x=100
            y=100
        }

    fun showFloatViewOnWindow() {
        parmas.width = floatWidth
        parmas.height = floatHeight
        //視窗圖案放置位置
        parmas!!.gravity = Gravity.TOP or Gravity.LEFT
        // 如果忽略gravity屬性，那麼它表示視窗的絕對X位置。
        parmas.x = 100
        //如果忽略gravity屬性，那麼它表示視窗的絕對Y位置。
        parmas.y = 100
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

    private fun getScreenSize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val point = Point()
            mWindowManager.defaultDisplay.getSize(point)
            mScreenWidth = point.x
            mScreenHeight = point.y
        } else {
            mScreenWidth = mWindowManager.defaultDisplay.width
            mScreenHeight = mWindowManager.defaultDisplay.height
        }
        mCurrentIconAlpha = 70 / 100f
    }


    fun onDestroy() {
        if (mWindowManager != null && mWindowManager != null) {
            mWindowManager!!.removeView(mFloatView)
        }
    }
}
# kotlinfloatview

使用kotlin語言寫的浮動視窗，可以在android10上執行
permission
AndroidManifest.xml
   <manifest xmlns:android="http://schemas.android.com/apk/res/android"
          package="lyon.calculator.kotlinfloatview">
    <!--新增許可權-->
    <!--浮動視窗，需要添加权限-->
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <!--android 9.0上使用前台服务，需要添加权限-->
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />

    <application
            .........>
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>
        <service android:name=".FLoatService" />
    </application>
</manifest>
  
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
                   Toast.makeText(this, "permission授予成功！", Toast.LENGTH_SHORT).show()
                   val intent = Intent(this, FLoatService::class.java)
                   startService(intent)
               }else{
                   Toast.makeText(this, "permission授予失敗，無法開啟懸浮視窗", Toast.LENGTH_SHORT).show()
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

懸浮服務 FloatService.kt

        class FLoatService : Service() {
            var context:Context =this
            lateinit var floatViewManager:FloatViewManager

         override fun onBind(p0: Intent?): IBinder? {
             TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
         }

         override fun onCreate() {
             super.onCreate()
             context = this
             //獲取視窗
             val windowManager = getSystemService(Service.WINDOW_SERVICE) as WindowManager
             floatViewManager = FloatViewManager(this,windowManager)
             //顯示懸浮視窗
             floatViewManager.showFloatViewOnWindow()
         }

         override fun onDestroy() {
             super.onDestroy()
             floatViewManager.onDestroy()
         }
     }
  
FloatViewManager.kt 設定浮動視窗基本資料，包含移動的邏輯

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
           parmas!!.y = 0
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
 
FloatView.kt 浮動視窗View 的程式

     class FloatView: View.OnClickListener,View.OnTouchListener{
       var TAG: String = FloatView::class.java.getSimpleName()
       fun FloatView(){
       }

       fun  getView(context: Context):View
       {
           val view = LayoutInflater.from(context).inflate(R.layout.float_view,  null)
           var closeImg = view.findViewById<View>(R.id.closeImg) as ImageView
           closeImg.setOnClickListener(View.OnClickListener {
               Toast.makeText(context, "the float closeImg is clicked.", Toast.LENGTH_LONG).show()
               context.stopService(Intent(context, FLoatService::class.java))
           })
           return view
       }
       override fun onClick(p0: View?) {
           TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
       }

       override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
           TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
       }
     }

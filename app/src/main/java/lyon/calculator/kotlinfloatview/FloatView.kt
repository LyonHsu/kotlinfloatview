package lyon.calculator.kotlinfloatview

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast

class FloatView: View.OnClickListener,View.OnTouchListener{
    var TAG: String = FloatView::class.java.getSimpleName()

    interface FloatMoveTouch {
        fun OnTouch(view: View, event: MotionEvent)
    }
    lateinit var floatMoveTouch:FloatMoveTouch

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

        var title = view.findViewById<LinearLayout>(R.id.title)
        title.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View, event: MotionEvent): Boolean {
                Log.d(TAG, "motionEvent:$event")

                if(floatMoveTouch!=null)
                    floatMoveTouch.OnTouch(view,event)

                return true
            }
        })
        return view
    }

    public fun setOnFloatMoveTouch(floatMoveTouch:FloatMoveTouch ){
        this.floatMoveTouch=floatMoveTouch;
    }


    override fun onClick(p0: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
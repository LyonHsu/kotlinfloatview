package lyon.calculator.kotlinfloatview

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast

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
package lyon.calculator.kotlinfloatview

import android.content.Context
import android.util.Log

class Tool {
    var TAG: String = "Tool"
    fun dpToPx(context: Context, dp: Int): Int {
        if (context == null) {
            return -1;
        }
        val scale = context.resources.displayMetrics.density
        var px = ((dp * scale + 0.5f).toInt())
        Log.d(TAG, "dpToPx:" + px)
        return px

    }
}
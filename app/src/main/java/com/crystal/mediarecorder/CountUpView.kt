package com.crystal.mediarecorder

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CountUpView(context: Context, attrs: AttributeSet?) : AppCompatTextView(context, attrs) {

    private var startTimeStamp:Long = 0L

    private val countUpAction:Runnable = object : Runnable {
        override fun run() {
            val currentTimeStamp = SystemClock.elapsedRealtime()
            val countTime = ((currentTimeStamp - startTimeStamp)/1000L).toInt()
            updateCountText(countTime)
            invalidate()
            handler?.postDelayed(this, 1000)

        }
    }

    fun startCountUp(){
        startTimeStamp = SystemClock.elapsedRealtime()
        handler?.post(countUpAction)
    }

    fun stopCountUp(){
        updateCountText(0)
        handler?.removeCallbacks(countUpAction)
    }

    fun clearCountTime(){
        updateCountText(0)
    }

    fun updateCountText(countTimeSeconds: Int){
        val min = countTimeSeconds/60
        val sec = countTimeSeconds%60
        text = "%02d:%02d".format(min, sec)
    }
}
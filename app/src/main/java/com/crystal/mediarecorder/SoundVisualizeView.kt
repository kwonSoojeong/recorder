package com.crystal.mediarecorder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class SoundVisualizeView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    var onRequestCurrentAmplitude: (() -> Int)? = null
    private val amplitudePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.purple_500)
        strokeWidth = LINE_WIDTH
        strokeCap = Paint.Cap.ROUND
    }

    var drawingWidth: Int = 0
    var drawingHeight: Int = 0
    private var drawingAmplitudes: List<Int> = emptyList()        // (0..10).map { Random.nextInt(Short.MAX_VALUE.toInt()) }// For Test
    private var isReplaying :Boolean = false
    private var replayingPosition: Int = 0

    //데이터 가져오기, 에니메이션 효과, 화면 갱신
    private val visualizeRepeatAction: Runnable = object : Runnable {
        override fun run() {
            if(!isReplaying){
                val currentAmplitude = onRequestCurrentAmplitude?.invoke() ?: 0
                //마지막에 들어온 데이터를 앞에 넣어주고,
                // 그릴때는 앞에서 꺼내 그려야함. 왼쪽부터 오른쪽으로 그리니깐.
                drawingAmplitudes = listOf(currentAmplitude) + drawingAmplitudes

            }else{
                replayingPosition++
            }

            invalidate()//view 갱신
            handler?.postDelayed(this, ACTION_INTERVAL)
        }

    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawingWidth = w
        drawingHeight = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return

        val centerY = drawingHeight / 2f
        var offsetX = drawingWidth.toFloat()
        //오른쪽부터 왼쪽으로 그린다.
        drawingAmplitudes
            .let{
                amplitudes->
                if(isReplaying){
                    amplitudes.takeLast(replayingPosition)
                }else{
                    amplitudes
                }
            }
            .forEach { amplitude ->
            val lineLength = amplitude / MAX_AMPLITUDE * drawingHeight * 0.8F
            offsetX -= LINE_SPACE

            if (offsetX < 0) return@forEach

            canvas.drawLine(//sx,sy,ex,x
                offsetX,
                centerY - lineLength / 2F,
                offsetX,
                centerY + lineLength / 2F,
                amplitudePaint
            )
        }
    }

    fun startVisualizing(isReplaying:Boolean){
        this.isReplaying = isReplaying
        handler?.post(visualizeRepeatAction)
    }

    fun stopVisualizing(){
        replayingPosition = 0
        handler?.removeCallbacks(visualizeRepeatAction)
    }

    fun clearVisualization(){
        drawingAmplitudes = emptyList()
        this.invalidate()
    }

    companion object {
        private const val LINE_WIDTH = 10F
        private const val LINE_SPACE = 15F
        private const val MAX_AMPLITUDE = Short.MAX_VALUE.toFloat()
        private const val ACTION_INTERVAL = 20L
    }
}
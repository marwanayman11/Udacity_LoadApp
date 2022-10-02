package com.example.loadapp

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0f
    private var heightSize = 0f
    private var text = ""
    private var basicColor = 0
    private var loadingColor = 0
    private var circleColor = 0
    private var width = 0f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val valueAnimator = ValueAnimator()
    var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Clicked -> {
                text = resources.getString(R.string.button_loading)
                animation(true)
            }
            ButtonState.Loading -> {
                text = resources.getString(R.string.button_loading)
                animation(false)
            }
            ButtonState.Completed -> {
                if (valueAnimator.isRunning) {
                    valueAnimator.removeAllUpdateListeners()
                    valueAnimator.end()
                }
                text = resources.getString(R.string.button_download)
                width = 0f
                invalidate()
            }
        }
    }

    init {
        isClickable = true
        setupAttributes(attrs)

    }

    private fun animation(flag: Boolean) {
        valueAnimator.apply {
            setFloatValues(0f, widthSize)
            duration = 2000
            repeatCount = 0
            repeatMode = ValueAnimator.RESTART
            addUpdateListener {
                width = animatedValue as Float
                this@LoadingButton.invalidate()
                if (flag && width == widthSize) {
                    buttonState = ButtonState.Completed
                }
            }
            start()
        }
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        val typedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingButton, 0, 0)
        basicColor = typedArray.getColor(R.styleable.LoadingButton_backgroundColor, 0)
        text = typedArray.getText(R.styleable.LoadingButton_text).toString()
        loadingColor = typedArray.getColor(R.styleable.LoadingButton_loadingColor, 0)
        circleColor = typedArray.getColor(R.styleable.LoadingButton_circleColor, 0)
        typedArray.recycle()
    }

    private fun drawRec(canvas: Canvas) {
        val rect = RectF(0f, 0f, widthSize, heightSize)
        paint.color = basicColor
        canvas.drawRect(rect, paint)

    }

    private fun drawLoading(canvas: Canvas) {
        val loading = RectF(0f, 0f, width, heightSize)
        paint.color = loadingColor
        canvas.drawRect(loading, paint)

    }

    private fun drawCircle(canvas: Canvas) {
        val circle = RectF(
            widthSize / 1.4f,
            heightSize / 2f - 30f,
            widthSize / 1.4f + 60f,
            heightSize / 2f + 30f
        )
        paint.color = circleColor
        canvas.drawArc(circle, 0f, (width / widthSize) * 360, true, paint)
    }

    private fun drawText(canvas: Canvas) {
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 40f
        canvas.drawText(text, widthSize / 2f, heightSize / 1.7f, paint)

    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawRec(canvas)
        drawLoading(canvas)
        drawCircle(canvas)
        drawText(canvas)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minW: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minW, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w.toFloat()
        heightSize = h.toFloat()
        setMeasuredDimension(w, h)
    }

}
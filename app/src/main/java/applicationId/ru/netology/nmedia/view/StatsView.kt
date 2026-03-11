package applicationId.ru.netology.nmedia.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import applicationId.ru.netology.nmedia.R
import kotlin.math.max
import kotlin.math.min

class StatsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    enum class FillMode {
        PARALLEL,
        SEQUENTIAL,
        BIDIRECTIONAL,
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value.filter { it > 0f }
            startAnimation()
        }

    /**
     * Если null — круг считается полностью заполненным суммой data.
     * Если больше суммы data — появится незаполненная часть.
     */
    var maxValue: Float? = null
        set(value) {
            field = value?.takeIf { it > 0f }
            startAnimation()
        }

    var fillMode: FillMode = FillMode.PARALLEL
        set(value) {
            field = value
            invalidate()
        }

    private var animationProgress = 0f

    private var lineWidth = dp(20f)
    private var fontSize = dp(24f)
    private var animationDuration = 1500L
    private var emptyColor = 0xFFDADADA.toInt()

    private var colors = listOf(
        0xFFFF1F6A.toInt(),
        0xFF5A22EA.toInt(),
        0xFF1ECFC5.toInt(),
        0xFFF4D400.toInt(),
        0xFF4CAF50.toInt(),
        0xFFFF9800.toInt(),
        0xFF03A9F4.toInt(),
        0xFF9C27B0.toInt(),
        0xFFE91E63.toInt(),
        0xFF009688.toInt(),
    )

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val emptyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        color = Color.BLACK
    }

    private val oval = RectF()
    private var radius = 0f
    private var center = PointF()

    private var animator: ValueAnimator? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.StatsView,
            defStyleAttr,
            0
        ).apply {
            try {
                lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth)
                fontSize = getDimension(R.styleable.StatsView_fontSize, fontSize)
                animationDuration = getInt(
                    R.styleable.StatsView_animationDuration,
                    animationDuration.toInt()
                ).toLong()

                emptyColor = getColor(
                    R.styleable.StatsView_emptyColor,
                    emptyColor
                )

                fillMode = when (getInt(R.styleable.StatsView_fillMode, 0)) {
                    1 -> FillMode.SEQUENTIAL
                    2 -> FillMode.BIDIRECTIONAL
                    else -> FillMode.PARALLEL
                }
            } finally {
                recycle()
            }
        }

        arcPaint.strokeWidth = lineWidth
        emptyPaint.strokeWidth = lineWidth
        emptyPaint.color = emptyColor
        textPaint.textSize = fontSize
    }

    override fun onDetachedFromWindow() {
        animator?.cancel()
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val minSide = min(w - paddingLeft - paddingRight, h - paddingTop - paddingBottom)

        radius = minSide / 2f - lineWidth / 2f

        center = PointF(
            paddingLeft + (w - paddingLeft - paddingRight) / 2f,
            paddingTop + (h - paddingTop - paddingBottom) / 2f
        )

        oval.set(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (radius <= 0f) return

        val values = data.filter { it > 0f }
        val sum = values.sum()

        val targetMax = maxValue?.let { max(it, sum) } ?: sum
        if (targetMax <= 0f) return

        val filledFraction = (sum / targetMax).coerceIn(0f, 1f)

        val rotationAngle = FULL_ANGLE * animationProgress
        canvas.save()
        canvas.rotate(rotationAngle, center.x, center.y)

        canvas.drawArc(oval, START_ANGLE, FULL_ANGLE, false, emptyPaint)

        val targetSweeps = values.map { FULL_ANGLE * (it / targetMax) }

        when (fillMode) {
            FillMode.PARALLEL -> drawParallel(canvas, targetSweeps, filledFraction)
            FillMode.SEQUENTIAL -> drawSequential(canvas, targetSweeps, filledFraction)
            FillMode.BIDIRECTIONAL -> drawBidirectional(canvas, targetSweeps, filledFraction)
        }

        canvas.restore()

        drawCenterText(canvas, filledFraction * animationProgress)
    }

    private fun drawParallel(canvas: Canvas, sweeps: List<Float>, fraction: Float) {
        var start = START_ANGLE

        sweeps.forEachIndexed { index, sweep ->
            val animated = sweep * animationProgress

            arcPaint.color = colors[index % colors.size]

            canvas.drawArc(oval, start, animated, false, arcPaint)

            start += sweep
        }
    }

    private fun drawSequential(canvas: Canvas, sweeps: List<Float>, fraction: Float) {
        var start = START_ANGLE
        var remaining = FULL_ANGLE * fraction * animationProgress

        sweeps.forEachIndexed { index, sweep ->
            val drawSweep = min(sweep, remaining)

            arcPaint.color = colors[index % colors.size]

            canvas.drawArc(oval, start, drawSweep, false, arcPaint)

            remaining -= sweep
            start += sweep
        }
    }

    private fun drawBidirectional(canvas: Canvas, sweeps: List<Float>, fraction: Float) {
        var start = START_ANGLE

        sweeps.forEachIndexed { index, sweep ->
            val animated = sweep * animationProgress

            val half = animated / 2f

            arcPaint.color = colors[index % colors.size]

            canvas.drawArc(oval, start, half, false, arcPaint)
            canvas.drawArc(oval, start + sweep - half, half, false, arcPaint)

            start += sweep
        }
    }

    private fun drawCenterText(canvas: Canvas, fraction: Float) {
        canvas.drawText(
            "%.2f%%".format(fraction * 100f),
            center.x,
            center.y + textPaint.textSize / 3f,
            textPaint
        )
    }

    private fun startAnimation() {
        animator?.cancel()

        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = animationDuration
            interpolator = DecelerateInterpolator()

            addUpdateListener {
                animationProgress = it.animatedValue as Float
                invalidate()
            }

            start()
        }
    }

    private fun dp(v: Float) = resources.displayMetrics.density * v

    companion object {
        private const val START_ANGLE = -90f
        private const val FULL_ANGLE = 360f
    }
}
package applicationId.ru.netology.nmedia.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import applicationId.ru.netology.nmedia.R
import kotlin.math.min

class StatsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    /**
     * Сырые значения сегментов.
     */
    var data: List<Float> = emptyList()
        set(value) {
            field = value.filter { it > 0f }
            invalidate()
        }

    /**
     * Максимум шкалы.
     * Если null -> считаем, что весь круг = sum(data)
     * Тогда диаграмма всегда заполнена на 100%.
     * Если задано число больше суммы data, то появится незаполненная часть.
     */
    var maxValue: Float? = null
        set(value) {
            field = value?.takeIf { it > 0f }
            invalidate()
        }

    private var lineWidth = dp(20f).toFloat()
    private var fontSize = dp(24f).toFloat()
    private var emptyColor = 0xFFDADADA.toInt()

    private var colors = listOf(
        0xFFFF1F6A.toInt(), // pink
        0xFF5A22EA.toInt(), // purple
        0xFF1ECFC5.toInt(), // cyan
        0xFFF4D400.toInt(), // yellow
        0xFF4CAF50.toInt(),
        0xFFFF9800.toInt(),
        0xFF03A9F4.toInt(),
        0xFF9C27B0.toInt(),
        0xFFE91E63.toInt(),
        0xFF009688.toInt(),
    )

    private var radius = 0f
    private var center = PointF()
    private val oval = RectF()

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val emptyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        color = 0xFF000000.toInt()
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.StatsView,
            defStyleAttr,
            0
        ).apply {
            try {
                lineWidth = getDimension(
                    R.styleable.StatsView_lineWidth,
                    lineWidth
                )
                fontSize = getDimension(
                    R.styleable.StatsView_fontSize,
                    fontSize
                )
                emptyColor = getColor(
                    R.styleable.StatsView_emptyColor,
                    emptyColor
                )

                colors = listOf(
                    getColor(R.styleable.StatsView_color1, colors[0]),
                    getColor(R.styleable.StatsView_color2, colors[1]),
                    getColor(R.styleable.StatsView_color3, colors[2]),
                    getColor(R.styleable.StatsView_color4, colors[3]),
                    getColor(R.styleable.StatsView_color5, colors[4]),
                    getColor(R.styleable.StatsView_color6, colors[5]),
                    getColor(R.styleable.StatsView_color7, colors[6]),
                    getColor(R.styleable.StatsView_color8, colors[7]),
                    getColor(R.styleable.StatsView_color9, colors[8]),
                    getColor(R.styleable.StatsView_color10, colors[9]),
                )
            } finally {
                recycle()
            }
        }

        arcPaint.strokeWidth = lineWidth
        emptyPaint.strokeWidth = lineWidth
        emptyPaint.color = emptyColor
        textPaint.textSize = fontSize
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val horizontalPadding = paddingLeft + paddingRight
        val verticalPadding = paddingTop + paddingBottom
        val minSide = min(w - horizontalPadding, h - verticalPadding).toFloat()

        radius = minSide / 2f - lineWidth / 2f

        center = PointF(
            paddingLeft + (w - horizontalPadding) / 2f,
            paddingTop + (h - verticalPadding) / 2f
        )

        oval.set(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (radius <= 0f) return

        val values = data.filter { it > 0f }
        val sum = values.sum()

        val targetMax = maxValue?.coerceAtLeast(sum)?.takeIf { it > 0f } ?: sum

        if (targetMax <= 0f) {
            drawCenterText(canvas, 0f)
            return
        }

        val filledFraction = (sum / targetMax).coerceIn(0f, 1f)

        // Сначала рисуем серую базовую окружность
        canvas.drawArc(
            oval,
            START_ANGLE,
            FULL_ANGLE,
            false,
            emptyPaint
        )

        if (values.isNotEmpty() && sum > 0f) {
            var startFrom = START_ANGLE

            values.forEachIndexed { index, value ->
                val sweep = FULL_ANGLE * (value / targetMax)

                if (sweep <= 0f) return@forEachIndexed

                arcPaint.color = colors[index % colors.size]

                /**
                 * последний сегмент не доводим до полного 360,
                 * чтобы его круглый конец не налезал на начало первого.
                 */
                val adjustedSweep =
                    if (index == values.lastIndex && filledFraction >= 1f) {
                        (sweep - DOT_FIX_DEGREES).coerceAtLeast(0f)
                    } else {
                        sweep
                    }

                canvas.drawArc(
                    oval,
                    startFrom,
                    adjustedSweep,
                    false,
                    arcPaint
                )

                startFrom += sweep
            }
        }

        drawCenterText(canvas, filledFraction)
    }

    private fun drawCenterText(canvas: Canvas, filledFraction: Float) {
        canvas.drawText(
            "%.2f%%".format(filledFraction * 100f),
            center.x,
            center.y + textPaint.textSize / 3f,
            textPaint
        )
    }

    private fun dp(value: Float): Int =
        kotlin.math.ceil(resources.displayMetrics.density * value).toInt()

    private companion object {
        const val START_ANGLE = -90f
        const val FULL_ANGLE = 360f

        /**
         * Небольшой угол, который убираем у последнего сегмента,
         * чтобы круглая "шапка" не наползала на старт первого сегмента.
         */
        const val DOT_FIX_DEGREES = 0.8f
    }
}
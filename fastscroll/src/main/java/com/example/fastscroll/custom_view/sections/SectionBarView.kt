package com.example.fastscroll.custom_view.sections

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.view.MotionEvent
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.fastscroll.custom_view.sections.fastscroll.FastScroll
import com.example.fastscroll.custom_view.sections.popup.SectionCirclePopup
import com.example.fastscroll.custom_view.sections.popup.SectionPopup
import com.example.fastscroll.custom_view.view.WildScrollRecyclerView
import kotlin.math.max

class SectionBarView(val recyclerView: WildScrollRecyclerView) {
    @ColorInt
    var textColor: Int = 0
        set(value) {
            field = value
            textPaint.color = value
        }

    @ColorInt
    var highlightColor: Int = 0
        set(value) {
            field = value
            highLightTextPaint.color = value
        }

    @ColorInt
    var sectionBarBackgroundColor: Int = 0
        set(value) {
            field = value
            sectionsPaint.color = value
        }

    var sectionBarPaddingLeft: Float = 0f
        set(value) {
            field = value
            sections.paddingLeft = value
            onSizeChanged(recyclerView.width, recyclerView.height)
        }

    var sectionBarPaddingRight: Float = 0f
        set(value) {
            field = value
            sections.paddingRight = value
            onSizeChanged(recyclerView.width, recyclerView.height)
        }

    var textSize: Float = 0f
        set(value) {
            field = value
            textPaint.textSize = value
            onSizeChanged(recyclerView.width, recyclerView.height)
        }

    var highlightTextSize: Float = 0f
        set(value) {
            field = value
            highLightTextPaint.textSize = value
            onSizeChanged(recyclerView.width, recyclerView.height)
        }

    var typeFace: Typeface = Typeface.DEFAULT
        set(value) {
            field = value
            textPaint.typeface = value
        }

    var highlightTextFace: Typeface = Typeface.DEFAULT
        set(value) {
            field = value
            highLightTextPaint.typeface = value
        }

    var sectionBarCollapseDigital: Boolean = true
        set(value) {
            field = value
            sections.collapseDigital = value
        }

    var sectionBarGravity: Gravity = Gravity.RIGHT
        set(value) {
            field = value
            sections.gravity = value
            onSizeChanged(recyclerView.width, recyclerView.height)
        }

    var sectionPopup: SectionPopup = SectionCirclePopup(context)
        set(value) {
            field = value
            field.onDismissListener = { invalidateSectionPopup() }
        }

    var drawMarginTop: Float = 0f
        set(value) {
            field = value
        }
    var drawMarginBottom: Float = 0f
        set(value) {
            field = value
        }

    var modifiedWidth: Float = 0f
        set(value) {
            field = value
            sections.modifiedWidth = value
        }

    var modifiedHeight: Float = 0f
        set(value) {
            field = value
            sections.modifiedHeight = value
        }

    var setIndexBarCornerRadius: Int = 0
        set(value) {
            field = value
        }

    var sectionBarEnable: Boolean = true
    var popupEnable: Boolean = true

    val width: Int
        get() = (sectionsRect.right - sectionsRect.left).toInt()

    val height: Int
        get() = (sectionsRect.bottom - sectionsRect.top).toInt()

    var sections: Sections = Sections()

    private val fastScroll = FastScroll(this)

    private val textPaint = Paint()
    private val highLightTextPaint = Paint()
    private val sectionsPaint = Paint()
    private val sectionsRect = Rect()

    private val context
        get() = recyclerView.context
    private val resources
        get() = context.resources

    var firstIndexHeight = 0f
    var heightDiff = 0f
    var hightOfLast = 0f

    private val mDensity: Float = recyclerView.context.resources.displayMetrics.density

    init {
        with(textPaint) {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        with(highLightTextPaint) {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
    }

    fun draw(canvas: Canvas, isVisible: Boolean) {
        if (!sectionBarEnable || sections.getCount() == 0 || !isVisible) {
            return
        }

        canvas.drawRoundRect(
            RectF(sectionsRect),
            setIndexBarCornerRadius * mDensity,
            setIndexBarCornerRadius * mDensity,
            sectionsPaint
        )

        when (sections.gravity) {
            Gravity.RIGHT, Gravity.LEFT -> {
                val posX = (sections.left + sections.width / 2f)

                sections.sections.entries.forEachIndexed { index, section ->
                    var top =
                        (sections.top + (index) * sections.height - sections.height + recyclerView.height) / 2f
                    if (index == 0) {
                        top =
                            (sections.top - sections.height + recyclerView.height) / 5f
                        firstIndexHeight = top
                    } else if (index == 1) {
                        heightDiff = (recyclerView.height / sections.sections.entries.size) / 1.4f
                    }

                    val buildHeight = if (index == 0) top else firstIndexHeight + index * heightDiff

                    if (index == sections.sections.entries.size - 1) {
                        hightOfLast = firstIndexHeight + (index + 3) * heightDiff
                    }
                    when (sections.selected == index) {
                        true -> canvas.drawText(
                            section.key.toString(),
                            posX,
                            buildHeight,
                            highLightTextPaint
                        )

                        false -> canvas.drawText(
                            section.key.toString(),
                            posX,
                            buildHeight,
                            textPaint
                        )
                    }
                }
            }
        }

        if (popupEnable) {
            sectionPopup.draw(canvas)
        }
    }

    fun onTouchEvent(ev: MotionEvent): Boolean {
        return fastScroll.onTouchEvent(ev, firstIndexHeight, heightDiff)
    }

    fun onInterceptTouchEvent(ev: MotionEvent): Boolean =
        sectionBarEnable && fastScroll.onInterceptTouchEvent(ev)

    fun onSizeChanged(width: Int, height: Int) {
        if (width == 0 && height == 0) return

        val maxTextSize = max(textPaint.textSize, highLightTextPaint.textSize)
        sections.changeSize(width, height, maxTextSize)

        when (sections.gravity) {
            Gravity.LEFT, Gravity.RIGHT -> {
                if (textPaint.textSize > sections.width) {
                    textPaint.textSize = sections.width
                }

                if (highLightTextPaint.textSize > sections.width) {
                    highLightTextPaint.textSize = sections.width
                }


                with(sectionsRect) {
                    left = sections.left.toInt()
                    right = (sections.left + sections.width).toInt()
                    top = (height * drawMarginTop).toInt()
                    bottom = (height * drawMarginBottom).toInt()
                }
            }
        }
    }

    fun invalidateSectionBar() {
        fastScroll.selectSectionByFirstVisibleItem()
        recyclerView.invalidate(sectionsRect)
    }

    private fun invalidateSectionPopup() {
        recyclerView.invalidate(sectionPopup.getRect())
    }

    fun showPopup(section: SectionInfo, x: Int, y: Int) {
        if (!popupEnable) {
            return
        }

        val sectionName = section.shortName.toString()
        sectionPopup.show(sectionName, x, y, recyclerView.width, recyclerView.height, sections)
    }

    fun dismissPopup() {
        sectionPopup.dismiss()
    }

    fun invalidateLayout(adapter: RecyclerView.Adapter<*>?) {
        println("invalidateLayout ")
        sections.refresh(adapter, object : OnSectionChangedListener { //TODO check memory leaks here
            override fun onSectionChanged() {
                onSizeChanged(recyclerView.width, recyclerView.height)
                invalidateSectionBar()
            }
        })
    }
}
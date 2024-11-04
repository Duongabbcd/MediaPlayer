package com.example.fastscroll.custom_view.sections.popup

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Align.CENTER
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.example.fastscroll.R
import com.example.fastscroll.custom_view.sections.Gravity
import com.example.fastscroll.custom_view.sections.Sections

open class SectionLetterPopup(
    protected var context: Context,
    @ColorRes
    var textColorRes: Int = R.color.fastscroll_highlight_text,
    @DimenRes
    var textSizeDimen: Int = R.dimen.fastscroll_popup_section_text_size,
    var textTypeFace: Typeface = Typeface.DEFAULT,
    @DrawableRes
    backgroundResource: Int = R.drawable.fastscroll_background_popup,
    @DimenRes
    paddingRes: Int = R.dimen.fastscroll_popup_padding
) : SectionPopup {
    var sectionTextSizeDimen: Int = textSizeDimen
        set(value) {
            field = value
            textPaint.textSize = context.resources.getDimension(value)
        }

    var sectionTextColorRes: Int = textColorRes
        set(value) {
            field = value
            textPaint.color = ResourcesCompat.getColor(context.resources, value, context.theme)
        }

    var paddingRes: Int = paddingRes
        set(value) {
            field = value
            padding = context.resources.getDimension(value)
            measure(padding!!)
        }

    var padding: Float? = null
        set(value) {
            field = value
            measure(value!!)
        }

    var sectionTextColor: Int? = null
        set(value) {
            textPaint.color = value!!
        }

    var sectionTextSize: Float? = null
        set(value) {
            textPaint.textSize = value!!
            measure(padding)
        }

    var backgroundResource: Int = backgroundResource
        set(value) {
            field = value
            background = ResourcesCompat.getDrawable(context.resources, value, context.theme)!!
        }

    var backgroundDrawable: Drawable? = null
        set(value) {
            background = value
        }

    open var backgroundColorRes: Int = 0
        set(value) {
            field = value
            val color = ResourcesCompat.getColor(context.resources, value, context.theme)
            background?.mutate()?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }

    var backgroundColor: Int? = null
        set(value) {
            background?.mutate()?.setColorFilter(value!!, PorterDuff.Mode.SRC_IN)
        }

    var isShowing = false
    protected var x = 0
    protected var y = 0
    protected var sectionName: String = ""
    protected var background: Drawable? = null
    protected var textPaint: Paint = Paint()

    override var width: Int = 0
    override var height: Int = 0
    override var onDismissListener: (() -> Unit)? = null

    init {
        val resources = context.resources
        val theme = context.theme

        background = ResourcesCompat.getDrawable(resources, backgroundResource, theme)!!

        textPaint.run {
            color = ResourcesCompat.getColor(resources, textColorRes, theme)
            textSize = resources.getDimension(textSizeDimen)
            typeface = textTypeFace
            isAntiAlias = true
            textAlign = CENTER
        }

        val padding = context.resources.getDimension(paddingRes)
        measure(padding)
    }

    private fun measure(padding: Float?) {
        val padding = padding ?: 0f
        width = (textPaint.textSize + padding).toInt()
        height = width
    }

    override fun show(
        section: String,
        x: Int,
        y: Int,
        parentWidth: Int,
        parentHeight: Int,
        sections: Sections
    ) {
        sectionName = section
        this.x = x
        this.y = y

        isShowing = true

        when (sections.gravity) {
            Gravity.LEFT -> {
                this.x = sections.width.toInt() + sections.width.toInt()
                this.y = y - height / 2
            }

            Gravity.RIGHT -> {
                this.x = (sections.left - width).toInt() - sections.width.toInt()
                this.y = y - height / 2
            }
            //TODO top / bottom
        }

        //corrections
        if (this.x < 0) this.x = 0
        else if (this.x > parentWidth - width) this.x = parentWidth - width
        if (this.y < 0) this.y = 0
        else if (this.y > parentHeight - height) this.y = parentHeight - height + 1000

        println("SectionLetterPopup: ${this.y} and $y")
    }

    override fun dismiss() {
        isShowing = false
        onDismissListener?.invoke()
    }

    override fun draw(canvas: Canvas) {
        if (!isShowing) return
        drawBackground(canvas)
        drawText(canvas)
    }

    override fun getRect() = Rect(x, y, x + width, y + height)

    protected open fun drawBackground(canvas: Canvas) {
        background?.setBounds(x, y, x + width, y + height)
        background?.draw(canvas)
    }

    protected open fun drawText(canvas: Canvas) {
        val offset = ((textPaint.descent() + textPaint.ascent()) / 2f)
        canvas.drawText(
            sectionName,
            x + width / 2f,
            y + height / 2f - offset,
            textPaint
        )
    }
}
package com.example.fastscroll.custom_view.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.fastscroll.R
import com.example.fastscroll.custom_view.sections.Gravity
import com.example.fastscroll.custom_view.sections.SectionBarView
import com.example.fastscroll.custom_view.sections.popup.SectionCirclePopup
import com.example.fastscroll.custom_view.sections.popup.SectionLetterPopup
import com.example.fastscroll.custom_view.sections.popup.SectionPopup

class WildScrollRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    private var customCanvas: Canvas = Canvas()
    private val sectionBar = SectionBarView(this)

    //Section regular text
    @ColorRes
    var textColor: Int = R.color.fastscroll_default_text
        set(value) {
            sectionBar.textColor = ResourcesCompat.getColor(resources, value, context.theme)
        }

    @DimenRes
    var textSize: Int = R.dimen.fastscroll_section_text_size_small
        set(value) {
            sectionBar.textSize = resources.getDimension(value)
        }
    var textTypeFace: Typeface = Typeface.DEFAULT
        set(value) {
            sectionBar.typeFace = value
        }

    //Section highlighted text
    @ColorRes
    var highlightColor: Int = R.color.fastscroll_highlight_text
        set(value) {
            sectionBar.highlightColor = ResourcesCompat.getColor(resources, value, context.theme)
        }

    @DimenRes
    var highlightTextSize: Int = R.dimen.fastscroll_section_highlight_text_size
        set(value) {
            sectionBar.highlightTextSize = resources.getDimension(value)
        }
    var highlightTextFace: Typeface = Typeface.DEFAULT
        set(value) {
            sectionBar.highlightTextFace = value
        }


    //Section bar settings
    @ColorRes
    var sectionBarBackgroundColor: Int = R.color.fastscroll_section_background
        set(value) {
            sectionBar.sectionBarBackgroundColor =
                ResourcesCompat.getColor(resources, value, context.theme)
        }

    @DimenRes
    var sectionBarPaddingLeft: Int = R.dimen.fastscroll_section_padding
        set(value) {
            sectionBar.sectionBarPaddingLeft = resources.getDimension(value)
        }

    @DimenRes
    var sectionBarPaddingRight: Int = R.dimen.fastscroll_section_padding
        set(value) {
            sectionBar.sectionBarPaddingRight = resources.getDimension(value)
        }
    var sectionBarCollapseDigital: Boolean = true
        set(value) {
            sectionBar.sectionBarCollapseDigital = value
        }
    var sectionBarGravity: Gravity = Gravity.RIGHT
        set(value) {
            sectionBar.sectionBarGravity = value
        }
    var sectionBarEnable: Boolean = true
        set(value) {
            sectionBar.sectionBarEnable = value
        }

    var sectionBarFirstIndexMarginTop: Float = 1000f
        set(value) {
            sectionBar.firstIndexMarginTop = value
        }

    var sectionBarDrawMarginTop: Float = 0f
        set(value) {
            sectionBar.drawMarginTop = value
        }

    var sectionBarDrawMarginBottom: Float = 0f
        set(value) {
            sectionBar.drawMarginBottom = value
        }

    var sectionBarModifiedWidth: Float = 0f
        set(value) {
            sectionBar.modifiedWidth = value
        }

    var sectionBarModifiedHeight: Float = 0f
        set(value) {
            sectionBar.modifiedHeight = value
        }

    var setIndexBarCornerRadius: Int = 0
        set(value) {
            sectionBar.setIndexBarCornerRadius = value
        }

    //Popup section
    var popupEnable: Boolean = true
        set(value) {
            sectionBar.popupEnable = value
        }

    @DrawableRes
    var popupBackground: Int = R.drawable.fastscroll_background_popup
        set(value) {
            if (sectionBar.sectionPopup !is SectionLetterPopup) {
                sectionBar.sectionPopup = SectionLetterPopup(context, backgroundResource = value)
            } else {
                (sectionBar.sectionPopup as SectionLetterPopup).backgroundResource = value
            }
        }

    @ColorRes
    var popupBackgroundColor: Int = R.color.fastscroll_popup_background
        set(value) {
            if (sectionBar.sectionPopup is SectionCirclePopup) {
                (sectionBar.sectionPopup as SectionCirclePopup).backgroundColorRes = value
            } else {
                sectionBar.sectionPopup = SectionCirclePopup(context, backgroundColorRes = value)
            }
        }

    @ColorRes
    var popupTextColor: Int = R.color.fastscroll_highlight_text
        set(value) {
            if (sectionBar.sectionPopup is SectionCirclePopup) {
                (sectionBar.sectionPopup as SectionCirclePopup).sectionTextColorRes = value
            } else {
                sectionBar.sectionPopup = SectionCirclePopup(context, sectionTextColorRes = value)
            }
        }

    @DimenRes
    var popupTextSize: Int = R.dimen.fastscroll_popup_section_text_size
        set(value) {
            if (sectionBar.sectionPopup is SectionLetterPopup) {
                (sectionBar.sectionPopup as SectionLetterPopup).sectionTextSizeDimen = value
            } else {
                sectionBar.sectionPopup = SectionCirclePopup(context, sectionTextSizeDimen = value)
            }
        }

    @DimenRes
    var popupPadding: Int = R.dimen.fastscroll_popup_padding
        set(value) {
            if (sectionBar.sectionPopup is SectionLetterPopup) {
                (sectionBar.sectionPopup as SectionLetterPopup).paddingRes = value
            } else {
                sectionBar.sectionPopup = SectionCirclePopup(context, paddingRes = value)
            }
        }
    var sectionPopup: SectionPopup = SectionCirclePopup(context)
        set(value) {
            sectionBar.sectionPopup = value
        }

    init {
        textTypeFace = Typeface.DEFAULT
        highlightTextFace = Typeface.DEFAULT

        sectionPopup = SectionCirclePopup(
            context,
            sectionTextColorRes = popupTextColor,
            sectionTextSizeDimen = popupTextSize,
            backgroundColorRes = popupBackgroundColor
        )

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.WildScroll, defStyle, 0)
            try {
                // Section Bar Text
                sectionBar.textColor = typedArray.getColor(
                    R.styleable.WildScroll_wildScroll_textColor,
                    ResourcesCompat.getColor(
                        resources,
                        R.color.fastscroll_default_text,
                        context.theme
                    )
                )
                sectionBar.textSize = typedArray.getDimension(
                    R.styleable.WildScroll_wildScroll_textSize,
                    resources.getDimension(R.dimen.fastscroll_section_text_size)
                )
                sectionBar.highlightColor = typedArray.getColor(
                    R.styleable.WildScroll_wildScroll_highlightColor,
                    ResourcesCompat.getColor(
                        resources,
                        R.color.fastscroll_highlight_text,
                        context.theme
                    )
                )
                sectionBar.highlightTextSize = typedArray.getDimension(
                    R.styleable.WildScroll_wildScroll_highlightTextSize,
                    resources.getDimension(R.dimen.fastscroll_section_highlight_text_size)
                )

                // Section Bar main settings
                sectionBar.sectionBarBackgroundColor = typedArray.getColor(
                    R.styleable.WildScroll_wildScroll_sectionBarBackgroundColor,
                    ResourcesCompat.getColor(
                        resources,
                        R.color.fastscroll_transparent,
                        context.theme
                    )
                )
                sectionBar.sectionBarPaddingLeft = typedArray.getDimension(
                    R.styleable.WildScroll_wildScroll_sectionBarPaddingLeft,
                    resources.getDimension(R.dimen.fastscroll_section_padding)
                )
                sectionBar.sectionBarPaddingRight = typedArray.getDimension(
                    R.styleable.WildScroll_wildScroll_sectionBarPaddingRight,
                    resources.getDimension(R.dimen.fastscroll_section_padding)
                )
                sectionBarCollapseDigital = typedArray.getBoolean(
                    R.styleable.WildScroll_wildScroll_sectionBarCollapseDigital,
                    true
                )
                sectionBarGravity = Gravity.values()[typedArray.getInt(
                    R.styleable.WildScroll_wildScroll_sectionBarGravity,
                    Gravity.RIGHT.ordinal
                )]
                sectionBarEnable =
                    typedArray.getBoolean(R.styleable.WildScroll_wildScroll_sectionBarEnable, true)

                if (typedArray.hasValue(R.styleable.WildScroll_wildScroll_sectionBarFirstIndexMarginTop)) {
                    sectionBarFirstIndexMarginTop =
                        typedArray.getFloat(
                            R.styleable.WildScroll_wildScroll_sectionBarFirstIndexMarginTop,
                            0f
                        )
                }

                if (typedArray.hasValue(R.styleable.WildScroll_wildScroll_sectionBarDrawMarginTop)) {
                    sectionBarDrawMarginTop =
                        typedArray.getFloat(
                            R.styleable.WildScroll_wildScroll_sectionBarDrawMarginTop,
                            0f
                        )
                }

                // Section popup
                if (typedArray.hasValue(R.styleable.WildScroll_wildScroll_popupEnable)) {
                    popupEnable =
                        typedArray.getBoolean(R.styleable.WildScroll_wildScroll_popupEnable, true)
                }
                if (typedArray.hasValue(R.styleable.WildScroll_wildScroll_popupPadding)) {
                    if (sectionBar.sectionPopup is SectionLetterPopup) {
                        (sectionBar.sectionPopup as SectionLetterPopup).padding =
                            typedArray.getDimension(
                                R.styleable.WildScroll_wildScroll_popupPadding,
                                resources.getDimension(R.dimen.fastscroll_popup_padding)
                            )
                    }
                }
                if (typedArray.hasValue(R.styleable.WildScroll_wildScroll_popupBackgroundDrawable)) {
                    if (sectionBar.sectionPopup is SectionLetterPopup) {
                        (sectionBar.sectionPopup as SectionLetterPopup).backgroundDrawable =
                            typedArray.getDrawable(R.styleable.WildScroll_wildScroll_popupBackgroundDrawable)
                    }
                }
                if (typedArray.hasValue(R.styleable.WildScroll_wildScroll_popupBackgroundColor)) {
                    if (sectionBar.sectionPopup is SectionCirclePopup) {
                        (sectionBar.sectionPopup as SectionCirclePopup).backgroundColor =
                            typedArray.getColor(
                                R.styleable.WildScroll_wildScroll_popupBackgroundColor,
                                ResourcesCompat.getColor(
                                    resources,
                                    R.color.fastscroll_popup_background,
                                    context.theme
                                )
                            )
                    }
                }
                if (typedArray.hasValue(R.styleable.WildScroll_wildScroll_popupTextColor)) {
                    if (sectionBar.sectionPopup is SectionLetterPopup) {
                        (sectionBar.sectionPopup as SectionLetterPopup).sectionTextColor =
                            typedArray.getColor(
                                R.styleable.WildScroll_wildScroll_popupTextColor,
                                ResourcesCompat.getColor(
                                    resources,
                                    R.color.fastscroll_highlight_text,
                                    context.theme
                                )
                            )
                    }
                }
                if (typedArray.hasValue(R.styleable.WildScroll_wildScroll_popupTextSize)) {
                    if (sectionBar.sectionPopup is SectionLetterPopup) {
                        (sectionBar.sectionPopup as SectionLetterPopup).sectionTextSize =
                            typedArray.getDimension(
                                R.styleable.WildScroll_wildScroll_popupTextSize,
                                resources.getDimension(R.dimen.fastscroll_popup_section_text_size)
                            )
                    }
                }
            } finally {
                typedArray.recycle()
            }
        }
    }

    override fun onSizeChanged(width: Int, height: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(width, height, oldw, oldh)
        sectionBar.onSizeChanged(width, height)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        customCanvas = canvas
        sectionBar.draw(canvas, true)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (sectionBar.onTouchEvent(ev)) {
            return true
        }
        return super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean =
        if (sectionBar.onInterceptTouchEvent(ev)) true
        else super.onInterceptTouchEvent(ev)

    override fun setAdapter(adapter: Adapter<*>?) {
        sectionBar.invalidateLayout(adapter)
        super.setAdapter(adapter)
    }

    fun release() {
        adapter?.unregisterAdapterDataObserver(dataObserver)
    }

    private val dataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            println("onChanged")
            sectionBar.invalidateLayout(adapter)
            super.onChanged()
        }
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == SCROLL_STATE_DRAGGING) {
            sectionBar.draw(customCanvas, false)
        } else {
            sectionBar.draw(customCanvas, true)
        }
    }
}
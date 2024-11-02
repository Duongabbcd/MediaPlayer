package com.example.fastscroll.custom_view.sections.popup

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.res.ResourcesCompat
import com.example.fastscroll.R

@SuppressLint("ResourceType")
class SectionCirclePopup(
    context: Context,
    @ColorRes
    sectionTextColorRes: Int = R.color.fastscroll_highlight_text,
    @DimenRes
    sectionTextSizeDimen: Int = R.dimen.fastscroll_popup_section_text_size,
    @ColorRes
    backgroundColorRes: Int = R.color.fastscroll_popup_background,
    @DimenRes
    paddingRes: Int = R.dimen.fastscroll_popup_padding
) : SectionLetterPopup(
    context = context,
    textColorRes = sectionTextColorRes,
    textSizeDimen = sectionTextSizeDimen,
    backgroundResource = R.drawable.fastscroll_popup_circle,
    paddingRes = paddingRes
) {
    override var backgroundColorRes: Int = super.backgroundColorRes
        set(value) {
            field = value
            val color = ResourcesCompat.getColor(context.resources, value, context.theme)
            background?.mutate()?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }

    init {
        this.sectionTextColorRes = backgroundColorRes
    }
}
package com.example.fastscroll.custom_view.sections.popup

import android.graphics.Canvas
import android.graphics.Rect
import com.example.fastscroll.custom_view.sections.Sections

interface SectionPopup {
    fun show(
        section: String,
        x: Int,
        y: Int,
        parentWidth: Int,
        parentHeight: Int,
        sections: Sections
    )

    fun dismiss()
    fun draw(canvas: Canvas)
    fun getRect(): Rect

    var onDismissListener: (() -> Unit)?
    var width: Int
    var height: Int

}
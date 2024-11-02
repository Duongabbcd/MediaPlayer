package com.example.fastscroll.custom_view.adapter

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.fastscroll.custom_view.sections.SectionInfo

abstract class SectionFastScrollAdapter<VH : ViewHolder> : RecyclerView.Adapter<VH>(),
    SectionFastScroll {
    var sections: Map<Int, SectionInfo> = emptyMap()
        set(value) {
            field = value
            value.values.forEach { section ->
                notifyItemChanged(section.position)
            }
        }

    open fun isSection(position: Int): Boolean = sections.containsKey(position)

    fun getSectionInfo(position: Int) = sections[position]

}
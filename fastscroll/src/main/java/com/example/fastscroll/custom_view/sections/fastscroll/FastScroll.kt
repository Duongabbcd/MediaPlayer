package com.example.fastscroll.custom_view.sections.fastscroll

import android.view.MotionEvent
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.fastscroll.R
import com.example.fastscroll.custom_view.adapter.SectionFastScroll
import com.example.fastscroll.custom_view.sections.SectionBarView
import com.example.fastscroll.custom_view.sections.Sections
import kotlin.math.abs
import kotlin.math.round

class FastScroll(private val sectionBar: SectionBarView) {
    private val sections
        get() = sectionBar.sections

    private val recyclerView
        get() = sectionBar.recyclerView

    private var isScrolling = false

    private var lastPositionX = 0f
    private var lastPositionY = 0f
    private var minScrollSensitivity: Float = 0f

    private val scroller: OnScrollListener = object : OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (isScrolling) {
                return
            }
            selectSectionByFirstVisibleItem()
        }
    }

    init {
        recyclerView.addOnScrollListener(scroller)
        minScrollSensitivity =
            recyclerView.resources.getDimension(R.dimen.fastscroll_minimum_scrolling_sensitivity)
    }

    fun onTouchEvent(ev: MotionEvent, firstIndexHeight: Float, heightDiff: Float): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                recyclerView.parent.requestDisallowInterceptTouchEvent(false)
                lastPositionY = ev.y
                lastPositionX = ev.x
                isScrolling = false
            }

            MotionEvent.ACTION_UP -> {
                recyclerView.parent.requestDisallowInterceptTouchEvent(false)
                sectionBar.dismissPopup()

                if (!isScrolling && sections.contains(ev.x, ev.y)) {
                    val sectionIndex = getSectionIndex(ev.y, firstIndexHeight, heightDiff)
                    println("sectionIndex: $sectionIndex")
                    println("sectionIndex: ${sections.getSectionInfoByIndex(sectionIndex)}")
                    val sectionInfo = sections.getSectionInfoByIndex(sectionIndex)

                    if (sectionInfo != null) {
                        scrollToPosition(sectionInfo.position)
                    }

                    sections.selected = sectionIndex
                    sectionBar.invalidateSectionBar()
                    return true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                recyclerView.parent.requestDisallowInterceptTouchEvent(true)
                val dif = when (isHorizontalScroll()) {
                    true -> abs(lastPositionX - ev.x) > minScrollSensitivity
                    false -> abs(lastPositionX - ev.y) > minScrollSensitivity
                }

                if (dif && sections.contains(ev.x, ev.y)) {
                    val sectionIndex = getSectionIndex(ev.y, firstIndexHeight, heightDiff)

                    val sectionInfo = sections.getSectionInfoByIndex(sectionIndex)

                    if (sectionInfo != null) {
                        val dY = ev.y - sections.height * sectionIndex
                        val sectionProgress = dY / sections.height
                        val dPosition = sectionInfo.count * sectionProgress
                        val position = round(sectionInfo.position + dPosition).toInt()
                        println(
                            "FastScroll ACTION MOVE: $sectionIndex and ${
                                sections.getSectionInfoByIndex(
                                    sectionIndex
                                )
                            } and $position"
                        )
                        scrollToPosition(sectionInfo.position)
                        sections.selected = sectionIndex
                        sectionBar.invalidateSectionBar()
                        sectionBar.showPopup(sectionInfo, ev.x.toInt(), ev.y.toInt())
                    }

                    isScrolling = true

                    return true
                }
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
                recyclerView.parent.requestDisallowInterceptTouchEvent(false)
                sectionBar.dismissPopup()
                return true
            }
        }
        return false
    }

    fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return sections.contains(ev.x, ev.y)
    }

    private fun getSectionIndex(pos: Float, firstIndexHeight: Float, heightDiff: Float): Int {
        println("onTouchEvent: $pos and $firstIndexHeight $heightDiff")
        val lastPosition = firstIndexHeight + heightDiff * sections.getCount()
        return when {
            pos < firstIndexHeight - 50 -> -1
            pos > lastPosition + 50 -> -1
            else -> {
                round(((pos - firstIndexHeight) / heightDiff).toDouble()).toInt().also {
                    println("getSectionIndex:  $it")
                }
            }
        }
    }

    fun selectSectionByFirstVisibleItem() {
        val layoutManager = recyclerView.layoutManager

        val firstItemPosition = when (layoutManager) {
            is LinearLayoutManager -> layoutManager.findFirstVisibleItemPosition()
            is StaggeredGridLayoutManager -> layoutManager.findFirstVisibleItemPositions(null)[0]
            else -> RecyclerView.NO_POSITION
        }

        sections.selected = getSectionIndexByAdapterItemPosition(firstItemPosition)
    }

    private fun getSectionIndexByAdapterItemPosition(firstItemPosition: Int): Int {
        if (recyclerView.adapter !is SectionFastScroll || firstItemPosition == RecyclerView.NO_POSITION) return Sections.UNSELECTED
        val sectionName =
            (recyclerView.adapter as SectionFastScroll).getSectionName(firstItemPosition)
        val sectionKey = sections.createShortName(sectionName)

        return sections.sections.indexOfKey(sectionKey)
    }


    private fun scrollToPosition(itemPosition: Int) {
        recyclerView.stopScroll()
        when (recyclerView.layoutManager) {
            is LinearLayoutManager -> (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                itemPosition,
                0
            )

            else -> recyclerView.layoutManager?.scrollToPosition(itemPosition)
        }
    }

    private fun isHorizontalScroll(): Boolean {
        return when (recyclerView.layoutManager) {
            is LinearLayoutManager ->
                (recyclerView.layoutManager as LinearLayoutManager).orientation == HORIZONTAL

            is GridLayoutManager ->
                (recyclerView.layoutManager as GridLayoutManager).orientation == HORIZONTAL

            is StaggeredGridLayoutManager ->
                (recyclerView.layoutManager as StaggeredGridLayoutManager).orientation == HORIZONTAL

            else -> false
        }
    }

    private val smoothScroller = object : LinearSmoothScroller(recyclerView.context) {
        override fun getVerticalSnapPreference(): Int {
            return SNAP_TO_START
        }
    }


}
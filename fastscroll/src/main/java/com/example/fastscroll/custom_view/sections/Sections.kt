package com.example.fastscroll.custom_view.sections

import android.text.TextUtils
import android.util.ArrayMap
import androidx.recyclerview.widget.RecyclerView
import com.example.fastscroll.custom_view.adapter.SectionFastScroll
import com.example.fastscroll.custom_view.adapter.SectionFastScrollAdapter
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.TreeMap


interface OnSectionChangedListener {
    fun onSectionChanged()
}

enum class Gravity {
    LEFT, RIGHT
}

data class SectionInfo(
    val name: String,
    val shortName: Char,
    val position: Int,
    val count: Int
)

class Sections {
    companion object {
        const val UNSELECTED = -1

        private const val SECTION_SHORT_NAME_EMPTY = '~'
        private const val SECTION_SHORT_NAME_DIGITAL = '#'
    }

    var left = 0f
    var top = 0f
    var width = 0f
    var height = 0f

    var modifiedWidth = 0.75f
    var modifiedHeight = 0f

    var gravity = Gravity.RIGHT
    var collapseDigital = true

    var paddingLeft = 0f
    var paddingRight = 0f

    var sections: ArrayMap<Char, SectionInfo> = ArrayMap()


    var selected = UNSELECTED //redraw section bar after set new value

    private var job: Job? = null

    fun getCount() = sections.size

    fun changeSize(w: Int, h: Int, highlightTextSize: Float) {
        val sectionCount = sections.size

        when (gravity) {
            Gravity.LEFT -> {
                width = highlightTextSize + paddingLeft + paddingRight
                height = h / sectionCount.toFloat()
                left = 0f
            }

            Gravity.RIGHT -> {
                width = (highlightTextSize + paddingLeft + paddingRight) * modifiedWidth
                height = h / sectionCount.toFloat() + modifiedHeight
                left = w - width
            }
        }
    }

    fun contains(x: Float, y: Float): Boolean {
        return x >= left && x <= left + width && y >= top && y <= height * sections.size
    }

    fun getSectionInfoByIndex(index: Int): SectionInfo? {
        val key = getSectionByIndex(index)
        return if (key != '0') sections[key] else null
    }

    fun createShortName(name: String): Char {
        val regex = Regex("[a-zA-Z]")
        return when {
            name.isEmpty() -> SECTION_SHORT_NAME_DIGITAL
            collapseDigital && TextUtils.isDigitsOnly(name[0].toString()) -> SECTION_SHORT_NAME_DIGITAL
            name[0].isDigit() ->
                SECTION_SHORT_NAME_DIGITAL

            regex.matchesAt(name, 0) -> name[0].toUpperCase()

            name.indexOfAny(charArrayOf('[', '@', '{', '(', '%', '!')) >= 0 -> '#'

            else -> SECTION_SHORT_NAME_EMPTY
        }
    }


    fun refresh(adapter: RecyclerView.Adapter<*>?, listener: OnSectionChangedListener) {
        job?.cancel()
        job = runBlocking {
            launch {
                sections = fetchSections(adapter).await()
                listener.onSectionChanged()
            }
        }
    }

    private suspend fun prepareSectionAdapter(adapter: RecyclerView.Adapter<*>?) {
        if (adapter !is SectionFastScrollAdapter<*>) return
        adapter.sections = prepareSectionInformationForAdapter().await()
    }

    private fun prepareSectionInformationForAdapter(): Deferred<Map<Int, SectionInfo>> {
        return runBlocking {
            async {
                val map = TreeMap<Int, SectionInfo>()
                sections.values.forEach { sectionInfo -> map[sectionInfo.position] = sectionInfo }
                return@async map
            }
        }
    }

    private fun fetchSections(adapter: RecyclerView.Adapter<*>?): Deferred<ArrayMap<Char, SectionInfo>> {
        return runBlocking {
            async {
                val map = ArrayMap<Char, SectionInfo>()
                if (adapter == null) {
                    return@async map
                }

                if (adapter.itemCount <= 1 || adapter !is SectionFastScroll) {
                    return@async map
                }

                if (adapter.itemCount > 0) {
                    for (position in 0 until adapter.itemCount) {
                        val name = adapter.getSectionName(position)

                        val shortName = createShortName(name)
                        val sectionInfo =
                            if (map.containsKey(shortName)) map[shortName]!!.copy(count = map[shortName]!!.count + 1)
                            else SectionInfo(name, shortName, position, 1)

                        map[shortName] = sectionInfo
                    }
                }
                return@async map
            }
        }
    }

    protected fun getSectionByIndex(index: Int): Char {
        var section = 'Z'
        try {
            if (index == -1) {
                return '0'
            }
            section = sections.keyAt(index)
        } catch (e: ArrayIndexOutOfBoundsException) {
            println("Exception: $e")
        }
        return section
    }
}
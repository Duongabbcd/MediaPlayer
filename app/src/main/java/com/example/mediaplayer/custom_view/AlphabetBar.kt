package com.example.mediaplayer.custom_view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import java.util.Arrays


class AlphabetBar(context: Context, attrs: AttributeSet) :
    RecyclerView(context, attrs) {
    //Default Alphabet
    private var alphabet = arrayOf(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
        "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    )

    //Attributes
    private var width = 0
    private var fontSize = 0f
    private val selectedFontSize = 0
    private var selectedItemColor = 0
    private var selectedItemBackground = 0

    //Adapter & Manager
    private var adapter: SectionIndexAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null

    init {
        this.overScrollMode = OVER_SCROLL_NEVER
        //this.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        getAttributes(context, attrs)
        initRecyclerView()
    }

    private fun getAttributes(context: Context, attrs: AttributeSet) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.Alphabetik)

        //Custom sizes
        width = ta.getDimensionPixelSize(R.styleable.Alphabetik_width, 15)

        val defaultSize = spToPixel(context, 12).toInt()
        val attFontSizeValue =
            ta.getDimensionPixelSize(R.styleable.Alphabetik_fontSize, defaultSize)
        fontSize = pixelsToSp(context, attFontSizeValue)

        //Custom colors
        //Items Color
        val aItemsColor: Int = R.styleable.Alphabetik_itemsColor
        if (ta.hasValue(R.styleable.Alphabetik_itemsColor)) {
            itemsColor = getColor(ta.getResourceId(aItemsColor, 0))
        }

        //TODO
        //Selected Item Color
        val aSelectedItemColor = R.styleable.Alphabetik_selectedItemColor
        if (ta.hasValue(aSelectedItemColor)) {
            selectedItemColor = ta.getResourceId(aSelectedItemColor, 0);
        }

        //Selected Item Background
        val aSelectedItemBackground = R.styleable.Alphabetik_selectedItemBackground
        if (ta.hasValue(aSelectedItemBackground)) {
            selectedItemBackground = ta.getResourceId(aSelectedItemBackground, 0);
        }

        //Recycle
        ta.recycle()
    }

    private fun getColor(id: Int): Int {
        return ContextCompat.getColor(context, id)
    }

    private fun pixelsToSp(context: Context, px: Int): Float {
        val scaledDensity = context.resources.displayMetrics.scaledDensity
        return px / scaledDensity
    }

    private fun spToPixel(context: Context, sp: Int): Float {
        val scaledDensity = context.resources.displayMetrics.scaledDensity
        return sp * scaledDensity
    }

    private fun initRecyclerView() {
        adapter = SectionIndexAdapter(alphabet, context)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        this.setHasFixedSize(true)
        this.setAdapter(adapter)
        this.layoutManager = linearLayoutManager
    }

    /**
     * Setter method. Set a custom alphabet, this method sort it automatically.
     *
     * @param {String} array of characters, e.g. "A", "B", "C"...
     * @method setAlphabet
     */
    fun setAlphabet(alphabet: Array<String>) {
        Arrays.sort(alphabet)
        this.alphabet = alphabet
        initRecyclerView()
    }

    //LISTENER
    fun onSectionIndexClickListener(sectionIndexClickListener: SectionIndexClickListener?) {
        adapter!!.onSectionIndexClickListener(sectionIndexClickListener)
    }

    //Fast Alphabet generation From A-Z
    private fun generateAlphabet(): Array<String?> {
        val alphabetTemp = arrayOfNulls<String>(27)
        var index = 0
        var c = 'A'
        while (c <= 'Z') {
            alphabetTemp[index] = "" + c
            index++
            c++
        }
        return alphabetTemp
    }

    /**
     * Set letter to bold
     *
     * @param {String} "letter"
     * @method setLetterToBold
     */
    fun setLetterToBold(letter: String) {
        var index = Arrays.asList(*alphabet).indexOf(letter)
        val regex = "[0-9]+"
        if (letter.matches(regex.toRegex())) {
            index = alphabet.size - 1
        }
        adapter!!.setBoldPosition(index)
        linearLayoutManager!!.scrollToPositionWithOffset(index, 0)
        getAdapter()!!.notifyDataSetChanged()
    }

    //ADAPTER
    internal inner class SectionIndexAdapter(
        private val alphabet: Array<String>,
        context: Context?
    ) :
        Adapter<SectionIndexAdapter.ViewHolder>() {
        private var boldPosition = 0
        private val mInflater: LayoutInflater = LayoutInflater.from(context)
        private var sectionIndexClickListener: SectionIndexClickListener? = null

        //LISTENER
        fun onSectionIndexClickListener(sectionIndexClickListener: SectionIndexClickListener?) {
            this.sectionIndexClickListener = sectionIndexClickListener
        }

        fun setBoldPosition(position: Int) {
            this.boldPosition = position
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = mInflater.inflate(R.layout.letter_view, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val letter = alphabet[position]
            holder.tvLetter.text = letter

            //Set current position to bold
            val normalTypeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            val boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD)
            holder.tvLetter.typeface =
                if (position == boldPosition) boldTypeface else normalTypeface

            holder.tvLetter.setBackgroundColor(
                if (position == boldPosition) resources.getColor(
                    R.color.textColor
                ) else resources.getColor(R.color.transparent)
            )

            //Custom Font size
            holder.tvLetter.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)

            //Custom color
            if (itemsColor != 0) {
                holder.tvLetter.setTextColor(itemsColor)
            }
        }

        override fun getItemCount(): Int {
            return alphabet.size
        }

        //VIEW HOLDER
        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            OnClickListener {
            val tvLetter: TextView = itemView.findViewById<View>(R.id.tvLetter) as TextView

            init {
                itemView.setOnClickListener(this)
            }

            override fun onClick(view: View) {
                if (sectionIndexClickListener != null) {
                    val character = "" + tvLetter.text.toString()
                    sectionIndexClickListener!!.onItemClick(view, this.position, character)
                    setLetterToBold(character)
                }
            }
        }
    }

    //INTERFACES
    interface SectionIndexClickListener {
        fun onItemClick(view: View?, position: Int, character: String?)
    }

    companion object {
        private var itemsColor = 0
    }
}

class TopSnappedSmoothScroller(context: Context) : LinearSmoothScroller(context) {
    override fun getVerticalSnapPreference(): Int {
        return SNAP_TO_START
    }
}

class LinearLayoutManagerWithSmoothScroller(context: Context) : LinearLayoutManager(context) {
    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State?,
        position: Int
    ) {
        val smoothScroller = TopSnappedSmoothScroller(recyclerView.context)
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }
}
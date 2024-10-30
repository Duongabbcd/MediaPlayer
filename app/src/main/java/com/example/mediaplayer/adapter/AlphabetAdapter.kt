package com.example.mediaplayer.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SectionIndexer
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R

class AlphabetAdapter: RecyclerView.Adapter<AlphabetAdapter.ViewHolder>(), SectionIndexer {

    private val mSections = "ABCDEFGHIJKLMNOPQRSTUVWXYZ#"
    private var sectionsTranslator = HashMap<Int, Int>()
    private var mSectionPositions: ArrayList<Int>? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlphabetAdapter.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: AlphabetAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getSections(): Array<Any> {
        TODO("Not yet implemented")
    }

    override fun getPositionForSection(sectionIndex: Int): Int {
        TODO("Not yet implemented")
    }

    override fun getSectionForPosition(position: Int): Int {
        TODO("Not yet implemented")
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTextView: TextView
        var mImageButton: ImageButton

        init {
            mTextView = itemView.findViewById(R.id.tv_alphabet)
            mImageButton = itemView.findViewById(R.id.ib_alphabet)
        }
    }
}
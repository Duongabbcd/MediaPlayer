package com.example.mediaplayer.fragment.basic

import android.content.Context.MODE_PRIVATE
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fastscroll.custom_view.sections.popup.SectionLetterPopup
import com.example.mediaplayer.R
import com.example.mediaplayer.adapter.AudioAdapter
import com.example.mediaplayer.databinding.FragmentAudioBinding
import com.example.mediaplayer.model.Audio
import com.example.mediaplayer.util.Constant
import com.google.gson.GsonBuilder

class AudioFragment : Fragment() {
    private lateinit var binding: FragmentAudioBinding
    private lateinit var audioAdapter: AudioAdapter
    private lateinit var layoutManager: LinearLayoutManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAudioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scrollToTop()

        val editor = requireContext().getSharedPreferences(Constant.CURRENT_PLAY_SONG, MODE_PRIVATE)
        val selectedSong = editor.getString(Constant.SONG_POSITION, "")

        val result = if (!selectedSong.isNullOrEmpty()) {
            GsonBuilder().create().fromJson(selectedSong, Audio::class.java)
        } else null

        val audios = mutableListOf<Audio>()
        val header = Audio("", "", "", "", 0L, "", "")
        audios.add(header)
        audios.addAll(Constant.audioLists)
        audioAdapter = AudioAdapter(ArrayList(audios), songPath = result)
        layoutManager = LinearLayoutManager(requireContext())
        binding.audioRv.adapter = audioAdapter
        binding.audioRv.layoutManager = layoutManager

        initialiseUI(audios, result)
    }

    private fun initialiseUI(audios: MutableList<Audio>, result: Audio?) {
        binding.audioRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = AudioAdapter(ArrayList(audios), songPath = result )
            textColor = R.color.white
            textSize = com.example.fastscroll.R.dimen.fastscroll_section_text_size_small
            textTypeFace = Typeface.SANS_SERIF

            highlightColor = R.color.white
            highlightTextSize =
                com.example.fastscroll.R.dimen.fastscroll_section_highlight_text_size
            highlightTextFace = Typeface.DEFAULT_BOLD

            sectionBarPaddingLeft = com.example.fastscroll.R.dimen.fastscroll_section_padding
            sectionBarPaddingRight = com.example.fastscroll.R.dimen.fastscroll_section_padding
            sectionBarCollapseDigital = false

            sectionBarFirstIndexMarginTop = 660f
            sectionBarDrawMarginTop = 0.165f
            sectionBarDrawMarginBottom = 0.92f
            sectionBarModifiedWidth = 0.75f
            sectionBarModifiedHeight = 50f
            setIndexBarCornerRadius = 10

            sectionPopup = SectionLetterPopup(
                context,
                textColorRes = R.color.black,
                textSizeDimen = com.example.fastscroll.R.dimen.textPopup,
                textTypeFace = Typeface.DEFAULT_BOLD,
                backgroundResource = com.example.fastscroll.R.drawable.background_round,
                paddingRes = com.example.fastscroll.R.dimen.fastscroll_section_padding
            )
        }
    }

    private fun scrollToTop() {
        binding.audioRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy >= 0) {
                    // Scrolling down
                    binding.moveToTopBtn.visibility = View.VISIBLE
                } else {
                    // Scrolling up
                    binding.moveToTopBtn.visibility = View.GONE
                }
            }
        })

        binding.moveToTopBtn.setOnClickListener {
            binding.audioRv.smoothScrollToPosition(0)
        }
    }

}
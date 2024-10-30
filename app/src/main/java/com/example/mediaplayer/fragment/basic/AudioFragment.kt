package com.example.mediaplayer.fragment.basic

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import com.example.mediaplayer.adapter.AudioAdapter
import com.example.mediaplayer.databinding.FragmentAudioBinding
import com.example.mediaplayer.model.Audio
import com.example.mediaplayer.util.Constant
import com.google.gson.GsonBuilder
import java.util.Objects

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

        println("exitApplication 2: $result")
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
        binding.alphabetBar.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = AudioAdapter(ArrayList(audios), songPath = result )
            setIndexBarVisibility(false)
            setIndexTextSize(12)
            setIndexBarColor(R.color.gray)
            setIndexBarVisibility(false)
            setIndexBarCornerRadius(10)
            setIndexBarTransparentValue(1.toFloat())
            setIndexBarVerticalMargin(220f)
            setIndexBarBottomMargin(130f)
            setIndexBarHorizontalMargin(0f)
            setPreviewPadding(0)
            setIndexBarTextColor(R.color.cool_blue)
            setPreviewTextSize(60)
            setPreviewColor(R.color.cool_pink)
            setPreviewTextColor(R.color.yellow)
            setPreviewTransparentValue(0.6f)
            setIndexBarStrokeVisibility(true)
            setIndexBarStrokeWidth(1)
            setIndexBarWidth(40f)
            setIndexBarStrokeColor("#00E12213")
            setIndexBarHighLightTextColor(R.color.black)
            setIndexBarStrokeVisibility(true)
            setIndexBarHighLightTextVisibility(true)
            this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    setIndexBarVisibility(true)
                }
            })
        }

        Objects.requireNonNull<RecyclerView.LayoutManager>(binding.alphabetBar.layoutManager).scrollToPosition(0)
    }


    private fun getPositionFromData(character: String?): Int {
        for ((position, audio) in Constant.audioLists.withIndex()) {
            val letter = "" + audio.title[0]
            if (letter == "" + character) {
                return position
            }
        }
        return 0
    }

    private fun scrollToTopAutomatically(position: Int) {
        println("scrollToTopAutomatically: $position")
        (binding.audioRv.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0)
    }

    private fun scrollToTop() {
        binding.alphabetBar.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
            binding.alphabetBar.smoothScrollToPosition(0)
        }
    }

}
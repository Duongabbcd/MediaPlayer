package com.example.mediaplayer.fragment.bottomsheet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mediaplayer.activity.PlayerActivity
import com.example.mediaplayer.activity.PlayerActivity.Companion.musicListPA
import com.example.mediaplayer.activity.PlayerActivity.Companion.musicService
import com.example.mediaplayer.activity.PlayerActivity.Companion.songPosition
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentNowPlayingBinding
import com.example.mediaplayer.util.Constant

class NowPlayingFragment : Fragment() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentNowPlayingBinding
    }
    private lateinit var runnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNowPlayingBinding.inflate(
            inflater, container, false
        )
        binding.root.visibility = View.INVISIBLE
        binding.playPauseBtnNP.setOnClickListener {
            if(PlayerActivity.isPlaying) pauseMusic() else playMusic()
        }

        binding.nextBtnNP.setOnClickListener {

            Constant.setSongPosition(increment = true)
            musicService!!.createMediaPlayer()
            Glide.with(this).load(musicListPA[songPosition].artUri)
                .apply(
                    RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).error(
                        R.drawable.music_player_icon_slash_screen
                    ).centerCrop()
                )
                .into(binding.songImgNP)

            binding.songNameNP.text = musicListPA[songPosition].title
            playMusic()
        }

        binding.root.setOnClickListener {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra(Constant.INDEX,songPosition)
            intent.putExtra(Constant.CLASS_NAME,Constant.NOW_PLAYING)
            ContextCompat.startActivity(requireContext(), intent, null)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if(musicService != null) {
            binding.root.visibility = View.VISIBLE
            binding.songNameNP.isSelected = true

            Glide.with(requireContext())
                .load(musicListPA[songPosition].artUri)
                .apply(
                    RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).error(
                        R.drawable.music_player_icon_slash_screen
                    ).centerCrop())
                .into(binding.songImgNP)
            binding.songNameNP.text = musicListPA[songPosition].title

            val icon = if(PlayerActivity.isPlaying) {
              R.drawable.pause_icon
            } else R.drawable.play_icon

            binding.image.setImageResource(icon)

           displayLoadingTrack()
        }
    }

    private fun displayLoadingTrack() {
        println("NowPlayingFragment: $songPosition")
        if(songPosition != -1  ) {

            runnable = Runnable {
                binding.progressBar.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.progressBar.max = musicListPA[songPosition].duration.toInt()

                Handler(Looper.getMainLooper()).postDelayed(runnable, 300)
            }
            Handler(Looper.getMainLooper()).postDelayed(runnable, 0)

        }
    }

    private fun playMusic() {
        musicService!!.mediaPlayer!!.start()
        binding.image.setImageResource(R.drawable.pause_icon)
        musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.nextBtnPA.setIconResource(R.drawable.pause_icon)
        PlayerActivity.isPlaying = true
    }

    private fun pauseMusic(){
        musicService!!.mediaPlayer!!.pause()
        binding.image.setImageResource(R.drawable.play_icon)
        musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.nextBtnPA.setIconResource(R.drawable.play_icon)
        PlayerActivity.isPlaying = false
    }
}
package com.example.mediaplayer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mediaplayer.activity.PlayerActivity
import com.example.mediaplayer.activity.PlayerActivity.Companion.musicListPA
import com.example.mediaplayer.activity.PlayerActivity.Companion.songPosition
import com.example.mediaplayer.R
import com.example.mediaplayer.activity.PlayerActivity.Companion.binding
import com.example.mediaplayer.application.MusicApplication
import com.example.mediaplayer.fragment.bottomsheet.NowPlayingFragment
import com.example.mediaplayer.util.Constant

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            MusicApplication.PREVIOUS -> prevNextSong(
                increment = false,
                context = context!!
            )

            MusicApplication.PLAY -> if (PlayerActivity.isPlaying) pauseMusic() else playMusic()

            MusicApplication.NEXT -> prevNextSong(
                increment = true,
                context = context!!
            )

            MusicApplication.EXIT -> {
                Constant.exitApplication(context!!)
            }
        }
    }

    private fun playMusic() {
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
        //for handling app crash during notification play - pause btn (While app opened through intent)
        try {
            NowPlayingFragment.binding.image.setImageResource(R.drawable.pause_icon)
        } catch (_: Exception) {
        }
    }

    private fun pauseMusic() {
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
        //for handling app crash during notification play - pause btn (While app opened through intent)
        try {
            NowPlayingFragment.binding.image.setImageResource(R.drawable.play_icon)
        } catch (_: Exception) {
        }
    }

    private fun prevNextSong(increment: Boolean, context: Context) {
        Constant.setSongPosition(increment = increment)
        PlayerActivity.musicService!!.createMediaPlayer()

        Glide.with(context).load(musicListPA[songPosition].artUri)
            .apply(
                RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen)
                    .error(R.drawable.music_player_icon_slash_screen)
                    .centerCrop()
            )
            .into(PlayerActivity.binding.songImgPA)
        PlayerActivity.binding.songNamePA.text = musicListPA[songPosition].title

        Glide.with(context)
            .load(musicListPA[songPosition].artUri)
            .apply(
                RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen)
                    .error(R.drawable.music_player_icon_slash_screen)
                    .centerCrop()
            )
            .into(NowPlayingFragment.binding.songImgNP)
        NowPlayingFragment.binding.songNameNP.text = musicListPA[songPosition].title

        playMusic()
        PlayerActivity.fIndex = Constant.favouriteChecker(musicListPA[songPosition].id)
        if(PlayerActivity.isFavourite)  binding.favouriteBtnPA.setImageResource(R.drawable.favourite_icon)
        else binding.favouriteBtnPA.setImageResource(R.drawable.favourite_empty_icon)

    }

}
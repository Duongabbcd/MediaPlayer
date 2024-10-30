package com.example.mediaplayer.service


import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mediaplayer.R
import com.example.mediaplayer.activity.PlayerActivity
import com.example.mediaplayer.activity.PlayerActivity.Companion.musicListPA
import com.example.mediaplayer.activity.PlayerActivity.Companion.songPosition
import com.example.mediaplayer.application.MusicApplication
import com.example.mediaplayer.fragment.bottomsheet.NowPlayingFragment
import com.example.mediaplayer.receiver.NotificationReceiver
import com.example.mediaplayer.util.Constant
import com.example.mediaplayer.util.Constant.formatDuration
import com.example.mediaplayer.util.Constant.setSongPosition

class MusicService : Service(), AudioManager.OnAudioFocusChangeListener {
    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable
    private var currentAudioPosition = -1
    lateinit var audioManager: AudioManager

    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    fun showNotification(playPauseBtn: Int) {
        val intent = Intent(baseContext, PlayerActivity::class.java)
        if (songPosition != currentAudioPosition) currentAudioPosition = songPosition
        intent.putExtra(Constant.INDEX, currentAudioPosition)
        intent.putExtra(Constant.CLASS_NAME, Constant.MUSIC_SERVICE)

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSession.sessionToken).setShowActionsInCompactView(0)

        val prevIntent = Intent(
            baseContext, NotificationReceiver::class.java
        ).setAction(MusicApplication.PREVIOUS)

        val prevPendingIntent = PendingIntent.getBroadcast(baseContext, 0, prevIntent, flag)

        val playIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(MusicApplication.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent, flag).also {
            intent.putExtra(Constant.IS_PLAYING, PlayerActivity.isPlaying)
        }

        val nextIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(MusicApplication.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 0, nextIntent, flag)

        val exitIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(MusicApplication.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 0, exitIntent, flag)

        val contentIntent = PendingIntent.getActivity(this, 0, intent, flag)

        val imgArt = Constant.getImgArt(musicListPA[songPosition].path)
        val image = if (imgArt != null) {
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.music_player_icon_slash_screen)
        }

        val notification =
            androidx.core.app.NotificationCompat.Builder(baseContext, MusicApplication.CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(musicListPA[songPosition].title)
                .setContentText(musicListPA[songPosition].artist)
                .setSmallIcon(R.drawable.music_icon).setLargeIcon(image)
                .setStyle(mediaStyle)
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true).setOngoing(true)
                .addAction(R.drawable.previous_icon, Constant.PREVIOUS_TITLE, prevPendingIntent)
                .addAction(playPauseBtn, Constant.PLAY_TITLE, playPendingIntent)
                .addAction(R.drawable.next_icon, Constant.NEXT_TITLE, nextPendingIntent)
                .addAction(R.drawable.exit_icon, Constant.EXIT_TITLE, exitPendingIntent)
                .build()

        controlCurrentRunningAudio()

        startForeground(13, notification)
    }

    private fun controlCurrentRunningAudio() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mediaSession.setMetadata(
                MediaMetadataCompat.Builder()
                    .putLong(
                        MediaMetadataCompat.METADATA_KEY_DURATION,
                        mediaPlayer!!.duration.toLong()
                    )
                    .build()
            )
            mediaSession.setPlaybackState(getPlayBackState())
            mediaSession.setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    super.onPlay()
                    handlePlayPause()
                }

                override fun onPause() {
                    super.onPause()
                    handlePlayPause()
                }

                override fun onSkipToNext() {
                    super.onSkipToNext()
                    prevNextSong(increment = true, context = baseContext)
                }

                override fun onSkipToPrevious() {
                    super.onSkipToPrevious()
                    prevNextSong(increment = false, context = baseContext)
                }

                /**
                 * called when headphones buttons are pressed
                 * currently only pause or play music on button click
                 */
                override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
                    handlePlayPause()
                    return super.onMediaButtonEvent(mediaButtonEvent)
                }

                //called when seekbar is changed
                override fun onSeekTo(pos: Long) {
                    super.onSeekTo(pos)
                    mediaPlayer?.seekTo(pos.toInt())
                    mediaSession.setPlaybackState(getPlayBackState())
                }
            })
        }
    }

    private fun getPlayBackState(): PlaybackStateCompat? {
        val playbackSpeed = if (PlayerActivity.isPlaying) 1F else 0F
        return PlaybackStateCompat.Builder()
            .setState(
                if (PlayerActivity.isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                mediaPlayer?.currentPosition?.toLong() ?: 0L,
                playbackSpeed
            )
            .setActions(
                PlaybackStateCompat.ACTION_PLAY_PAUSE
                        or PlaybackStateCompat.ACTION_SEEK_TO or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            )
            .build()
    }

    fun createMediaPlayer() {
        try {
            if (mediaPlayer == null) mediaPlayer = MediaPlayer()
            mediaPlayer?.let {
                it.reset()
                it.setDataSource(musicListPA[currentAudioPosition].path)
                it.prepare()
            }
            PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
            showNotification(R.drawable.pause_icon)
            PlayerActivity.binding.tvSeekBarStart.text =
                formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.tvSeekBarEnd.text =
                formatDuration(mediaPlayer!!.duration.toLong())
            PlayerActivity.binding.seekBarPA.progress = 0
            PlayerActivity.binding.seekBarPA.max = mediaPlayer!!.duration
        } catch (e: Exception) {
            return
        }
    }

    fun seekBarSetup() {
        runnable = Runnable {
            PlayerActivity.binding.tvSeekBarStart.text =
                formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekBarPA.progress = mediaPlayer!!.currentPosition

            Handler(Looper.getMainLooper()).postDelayed(runnable, 300)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }


    private fun handlePlayPause() {
        if (PlayerActivity.isPlaying) pauseMusic()
        else playMusic()

        //update playback state for notification
        mediaSession.setPlaybackState(getPlayBackState())
    }

    private fun playMusic() {
        PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
        NowPlayingFragment.binding.image.setImageResource(R.drawable.pause_icon)
        PlayerActivity.isPlaying = true
        mediaPlayer?.start()
        showNotification(R.drawable.pause_icon)
    }

    private fun pauseMusic() {
        PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
        NowPlayingFragment.binding.image.setImageResource(R.drawable.play_icon)
        PlayerActivity.isPlaying = false
        mediaPlayer?.pause()
        showNotification(R.drawable.play_icon)
    }

    private fun prevNextSong(increment: Boolean, context: Context) {

        setSongPosition(increment)

        PlayerActivity.musicService?.createMediaPlayer()
        Glide.with(context).load(musicListPA[songPosition].artUri)
            .apply(
                RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen)
                    .error(R.drawable.music_player_icon_slash_screen)
                    .centerCrop()
            )
            .into(PlayerActivity.binding.songImgPA)

        PlayerActivity.binding.songNamePA.text = musicListPA[songPosition].title

        Glide.with(context).load(musicListPA[songPosition].artUri)
            .apply(
                RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen)
                    .error(R.drawable.music_player_icon_slash_screen)
                    .centerCrop()
            )
            .into(NowPlayingFragment.binding.songImgNP)

        NowPlayingFragment.binding.songNameNP.text = musicListPA[songPosition].title

        playMusic()

        //update playback state for notification
        mediaSession.setPlaybackState(getPlayBackState())
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange <= 0) {
            pauseMusic()
        }
    }
}
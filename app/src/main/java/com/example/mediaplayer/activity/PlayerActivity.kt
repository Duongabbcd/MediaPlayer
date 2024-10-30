package com.example.mediaplayer.activity

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mediaplayer.MainActivity
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.ActivityPlayerBinding
import com.example.mediaplayer.fragment.basic.PlaylistFragment
import com.example.mediaplayer.model.Audio
import com.example.mediaplayer.service.MusicService
import com.example.mediaplayer.util.Constant
import com.example.mediaplayer.util.Constant.ALBUM_AND_ARTIST
import com.example.mediaplayer.util.Constant.AUDIO_ADAPTER
import com.example.mediaplayer.util.Constant.AUDIO_FRAGMENT
import com.example.mediaplayer.util.Constant.CLASS_NAME
import com.example.mediaplayer.util.Constant.FAVOURITE_ADAPTER
import com.example.mediaplayer.util.Constant.FAVOURITE_SHUFFLE
import com.example.mediaplayer.util.Constant.IS_PLAYING
import com.example.mediaplayer.util.Constant.MUSIC_SERVICE
import com.example.mediaplayer.util.Constant.NOW_PLAYING
import com.example.mediaplayer.util.Constant.PLAYLIST_DETAILS_ADAPTER
import com.example.mediaplayer.util.Constant.PLAYLIST_DETAILS_SHUFFLE
import com.example.mediaplayer.util.Constant.formatDuration
import com.example.mediaplayer.util.Constant.setSongPosition
import com.google.gson.GsonBuilder

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {
    companion object {
        lateinit var musicListPA: ArrayList<Audio>
        lateinit var binding: ActivityPlayerBinding
        var isPlaying: Boolean = true
        var isFavourite: Boolean = false
        var musicService: MusicService? = null
        var songPosition = -1
        var fIndex: Int = -1
    }

    private var repeat: Boolean = false
    private lateinit var editor: SharedPreferences.Editor
    private var displayVolumnBtn = false

//    private lateinit var myBroadcastReceiver: AudioReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPinkNav)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        editor = getSharedPreferences(Constant.REPEAT, MODE_PRIVATE).edit()

        initializeLayout()
        binding.backBtnPA.setOnClickListener {
//            val intent = Intent("com.example.ACTION_SEND_DATA")
//            intent.putExtra(Constant.SONG_POSITION, songPosition)
//            sendBroadcast(intent)
            finish()
        }
        println("PlayerActivity onCreate")

        binding.playPauseBtnPA.setOnClickListener {
            if (isPlaying) pauseMusic() else playMusic()
        }

        binding.previousBtnPA.setOnClickListener {
            prevNextSong(increment = false)
        }

        binding.nextBtnPA.setOnClickListener {
            prevNextSong(increment = true)
        }

        binding.seekBarPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicService!!.mediaPlayer!!.seekTo(progress)
                    musicService!!.showNotification(if (isPlaying) R.drawable.pause_icon else R.drawable.play_icon)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        setRepeatSongFunction()
        setEqualizerFunction()
        setBoosterVolume()

        binding.favouriteBtnPA.setOnClickListener {
            fIndex = Constant.favouriteChecker(musicListPA[songPosition].id)
            if(isFavourite) {
                isFavourite = false
                binding.favouriteBtnPA.setImageResource(R.drawable.favourite_empty_icon)
                FavouriteActivity.favouriteSongs.removeAt(fIndex)
            } else {
                isFavourite = true
                binding.favouriteBtnPA.setImageResource(R.drawable.favourite_icon)
                FavouriteActivity.favouriteSongs.add(musicListPA[songPosition])
            }
            setFavouriteSongs()
        }
    }

    private fun setFavouriteSongs() {
        val editor = getSharedPreferences(Constant.FAVOURITE, MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouriteActivity.favouriteSongs)
        println("onCreate: $jsonString")
        editor.putString(Constant.FAVOURITE_SONGS, jsonString)
        editor.commit()
        Constant.getFavouriteSongs(this)
    }

    @SuppressLint("SetTextI18n")
    private fun setBoosterVolume() {
        binding.root.setOnClickListener {
            displayVolumnBtn = !displayVolumnBtn
            binding.volumnModifier.visibility = if (displayVolumnBtn) View.VISIBLE else View.GONE
        }
        binding.boosterBtnPA.setOnClickListener {
            displayVolumnBtn = !displayVolumnBtn
            binding.volumnModifier.visibility = if (displayVolumnBtn) View.VISIBLE else View.GONE

            val maxVolume = musicService!!.audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            binding.volumeSeekbar.max = maxVolume
            binding.volumeSeekbar.progress =
                musicService!!.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            binding.volumeNo.text = "${binding.volumeSeekbar.progress * 100 / maxVolume} %"
            binding.volumeSeekbar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    binding.volumeNo.text = "${binding.volumeSeekbar.progress * 100 / maxVolume} %"
                    musicService!!.audioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        progress,
                        0
                    )
                    playMusic()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // Optional: Add code here if you want to handle the event when the user starts to touch the SeekBar
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // Optional: Add code here if you want to handle the event when the user stops touching the SeekBar
                }
            })
        }
    }

    private fun setEqualizerFunction() {
        binding.equalizerBtnPA.setOnClickListener {
            try {
                val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqIntent.putExtra(
                    AudioEffect.EXTRA_AUDIO_SESSION,
                    musicService!!.mediaPlayer!!.audioSessionId
                )
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eqIntent, 13)
            } catch (e: Exception) {
                Toast.makeText(this, "Equalizer Feature not supported", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setRepeatSongFunction() {
        binding.repeatBtnPA.setOnClickListener {
            if (!repeat) {
                repeat = true
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            } else {
                repeat = false
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_pink))
            }
            editor.putBoolean(Constant.IS_REPEATED, repeat)
            editor.commit()
        }
    }


    private fun setLayout() {
        fIndex = Constant.favouriteChecker(musicListPA[songPosition].id)
        isFavourite = Constant.currentFavouriteSong.contains(musicListPA[songPosition])
        println("setLayout: $isFavourite")
        val editor = getSharedPreferences(Constant.CURRENT_PLAY_SONG, MODE_PRIVATE).edit()
        val currentPlayingSong: String = GsonBuilder().create().toJson(musicListPA[songPosition])
        editor.putString(Constant.SONG_POSITION, currentPlayingSong)
        editor.commit()

        Glide.with(this)
            .load(Constant.getImgArt(musicListPA[songPosition].path))
            .apply(
                RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).error(
                    R.drawable.music_player_icon_slash_screen
                ).centerCrop()
            )
            .into(binding.songImgPA)

        binding.songNamePA.text = musicListPA[songPosition].title
        if (repeat) binding.repeatBtnPA.setColorFilter(
            ContextCompat.getColor(
                applicationContext,
                R.color.purple_500
            )
        )

        if(isFavourite) binding.favouriteBtnPA.setImageResource(R.drawable.favourite_icon)
        else binding.favouriteBtnPA.setImageResource(R.drawable.favourite_empty_icon)
    }

    private fun initializeLayout() {
        binding.volumnModifier.visibility = View.GONE
        repeat = getSharedPreferences(Constant.REPEAT, MODE_PRIVATE).getBoolean(
            Constant.IS_REPEATED,
            false
        )
        musicListPA = Constant.getAllAudioFiles(this)
        songPosition = intent.getIntExtra(Constant.INDEX, 0)
        //start service
        if (intent.getStringExtra(Constant.CLASS_NAME) != NOW_PLAYING) {
            val intent = Intent(this, MusicService::class.java)
            bindService(intent, this, BIND_AUTO_CREATE)
            startService(intent)
            requestBatteryOptimizationExemption()
        }
        println("initializeLayout: ${intent.getStringExtra(CLASS_NAME)}")
        when (intent.getStringExtra(CLASS_NAME)) {
            AUDIO_ADAPTER -> {
                setLayout()
                createMediaPlayer()
            }

            MUSIC_SERVICE -> {
                isPlaying = intent.getBooleanExtra(IS_PLAYING, false)
                setLayout()
                createMediaPlayer()
            }

            NOW_PLAYING -> {
                setLayout()
                binding.tvSeekBarStart.text =
                    formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                val duration = musicListPA[songPosition].duration
                val formatedDuration = formatDuration(musicListPA[songPosition].duration)

                binding.tvSeekBarEnd.text = formatedDuration
                binding.seekBarPA.progress = 0
                binding.seekBarPA.max = duration.toInt()
            }

            ALBUM_AND_ARTIST -> {
                initServiceAndPlayList( PlaylistDetailActivity.audioList,false)
            }

            PLAYLIST_DETAILS_ADAPTER -> {
                initServiceAndPlayList(  PlaylistFragment.musicPlaylist.ref
                    [PlaylistDetailActivity.currentPlaylistPos].playlist,false)
            }

            FAVOURITE_ADAPTER -> {
                isFavourite = true
                initServiceAndPlayList(FavouriteActivity.favouriteSongs,false)
            }

            AUDIO_FRAGMENT -> {
                initServiceAndPlayList(Constant.audioLists,true)
            }

            FAVOURITE_SHUFFLE -> {
                isFavourite = true
                initServiceAndPlayList(FavouriteActivity.favouriteSongs,true)
            }

            PLAYLIST_DETAILS_SHUFFLE -> {
                initServiceAndPlayList(PlaylistDetailActivity.audioList,true)
            }
            
        }
    }

    private fun playMusic() {
        binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
        isPlaying = true
        musicService!!.showNotification(R.drawable.pause_icon)
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic() {
        binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
        isPlaying = false
        musicService!!.showNotification(R.drawable.play_icon)
        musicService!!.mediaPlayer!!.pause()
    }

    private fun prevNextSong(increment: Boolean) {
        if (increment) {
            setSongPosition(increment = true)
            setLayout()
            createMediaPlayer()
        } else {
            setSongPosition(increment = false)
            setLayout()
            createMediaPlayer()
        }
    }

    private fun createMediaPlayer() {
        val duration = musicListPA[songPosition].duration
        val formatedDuration = formatDuration(musicListPA[songPosition].duration)
        println("createMediaPlayer: $formatedDuration")
        binding.tvSeekBarEnd.text = formatedDuration
        binding.seekBarPA.progress = 0
        binding.seekBarPA.max = duration.toInt()

        try {
            if (musicService!!.mediaPlayer == null) {
                musicService!!.mediaPlayer = MediaPlayer()
            }
            musicService!!.mediaPlayer?.let {

                isPlaying = true
                it.reset()

                it.setDataSource(musicListPA[songPosition].path)
                it.prepare()
                playMusic()
            }
            when (isPlaying) {
                true -> {
                    binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
                    musicService!!.showNotification(R.drawable.pause_icon)

                }

                false -> {
                    binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
                    musicService!!.showNotification(R.drawable.play_icon)
                }

            }
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)

        } catch (e: Exception) {
            return
        }
    }

    private fun initServiceAndPlayList(playList: ArrayList<Audio>, shuffle: Boolean = false) {
        musicListPA = ArrayList()
        musicListPA.addAll(
            playList
        )
        if(shuffle) musicListPA.shuffle()
        setLayout()
//        createMediaPlayer()
    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if (musicService == null) {
            val binder = service as MusicService.MyBinder
            musicService = binder.currentService()

            musicService!!.audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            musicService!!.audioManager.requestAudioFocus(
                musicService,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
        createMediaPlayer()

        musicService!!.seekBarSetup()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        if (!repeat) {
            setSongPosition(true)
        }

        createMediaPlayer()
        try {
            setLayout()
        } catch (e: Exception) {
            return
        }
    }

    @SuppressLint("BatteryLife")
    private fun requestBatteryOptimizationExemption() {
        val intent = Intent()
        val packageName = packageName
        val powerManager = getSystemService(PowerManager::class.java)

        if (powerManager != null && !powerManager.isIgnoringBatteryOptimizations(packageName)) {
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package: $packageName")
            startActivity(intent)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 || resultCode == RESULT_OK)
            return
    }
}
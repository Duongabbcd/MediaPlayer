package com.example.mediaplayer.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mediaplayer.R
import com.example.mediaplayer.adapter.AudioAdapter
import com.example.mediaplayer.databinding.ActivityPlaylistDetailsBinding
import com.example.mediaplayer.fragment.basic.PlaylistFragment
import com.example.mediaplayer.model.Audio
import com.example.mediaplayer.model.checkPlaylist
import com.example.mediaplayer.model.setDialogBtnBackground
import com.example.mediaplayer.util.Constant
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

class PlaylistDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistDetailsBinding
    private lateinit var adapter: AudioAdapter

    companion object {
        var currentPlaylistPos: Int = -1
        lateinit var audioList: ArrayList<Audio>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPinkNav)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detail = intent.getBooleanExtra(Constant.ALBUM_ADAPTER, false)
        val general = intent.getStringExtra(Constant.GENERAL)
        if (general != null) {
            binding.playlistFunction.visibility = View.GONE
            displaySongByCondition(detail, general)
        } else {
            binding.playlistFunction.visibility = View.VISIBLE
            audioList = ArrayList()
        }

        currentPlaylistPos = (intent.extras?.get(Constant.INDEX) ?: -1) as Int
        try {
            PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].playlist =
                checkPlaylist(playlist = PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].playlist)
        } catch (e: Exception) {
        }


        val playListDetail = audioList.isEmpty()
        val inputtedList =
            if (playListDetail) PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].playlist else audioList

        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)
        adapter = AudioAdapter(
            inputtedList,
            playlistDetails = playListDetail,
            albumAndArtist = !playListDetail
        )
        binding.playlistDetailsRV.adapter = adapter
        binding.backBtnPD.setOnClickListener { finish() }
        binding.shuffleBtnPD.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra(Constant.INDEX, 0)
            intent.putExtra(Constant.CLASS_NAME, Constant.PLAYLIST_DETAILS_SHUFFLE)
            startActivity(intent)
        }
        binding.addBtnPD.setOnClickListener {
            startActivity(Intent(this, SelectionActivity::class.java))
        }
        binding.removeAllPD.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle(resources.getString(R.string.remove))
                .setMessage(resources.getString(R.string.decide_to_remove_all_songs))
                .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                    PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].playlist.let {
                        it.clear()
                        checkIfRemovedAllData()
                    }
                    adapter.refreshPlaylist()
                    dialog.dismiss()
                }
                .setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()

            setDialogBtnBackground(this, customDialog)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displaySongByCondition(detail: Boolean, general: String) {
        println("displaySongByCondition: $detail and $general")
        if (detail) {
            val artist = resources.getString(R.string.artist_info, general)
            val totalSongs = resources.getString(
                R.string.total_songs_number,
                Constant.audioOfArtist[general]?.count()
            )
            binding.moreInfoPD.text = "$artist \n\n" +
                    totalSongs
            audioList = ArrayList()
            audioList = Constant.audioOfArtist[general]?.let { ArrayList(it) } ?: arrayListOf()

        } else {
            val album = resources.getString(R.string.album_info, general)
            val totalSongs = resources.getString(
                R.string.total_songs_number,
                Constant.audioOfAlbum[general]?.count()
            )
            binding.moreInfoPD.text = "$album \n\n" +
                    totalSongs
            audioList = ArrayList()
            audioList = Constant.audioOfAlbum[general]?.let { ArrayList(it) } ?: arrayListOf()
            println("displaySongByCondition: $audioList")
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        println("onResume: $currentPlaylistPos")
        println("onResume: ${PlaylistFragment.musicPlaylist.ref}")
        if (currentPlaylistPos != -1) {
            val createOn = resources.getString(
                R.string.create_on,
                PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].createdOn
            )
            val totalSongs = resources.getString(R.string.total_songs_number, adapter.itemCount)
            binding.playlistNamePD.text =
                PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].name
            binding.moreInfoPD.text = "$totalSongs\n\n" +
                    "$createOn\n\n" +
                    "  -- ${PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].createdBy}"
            checkIfRemovedAllData()
        }

        binding.shuffleBtnPD.visibility = if (adapter.itemCount > 0) View.VISIBLE else View.GONE
        adapter.notifyDataSetChanged()

        //for storing favourites data using shared preferences
        val editor = getSharedPreferences(Constant.FAVOURITE, MODE_PRIVATE).edit()
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistFragment.musicPlaylist)
        editor.putString(Constant.MUSIC_PLAY_LIST, jsonStringPlaylist)
        editor.commit()

        scrollToTop()
    }

    private fun checkIfRemovedAllData() {
        val displayAvatarImage =
            if (adapter.itemCount > 0) PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].playlist[0].artUri else
                R.drawable.music_player_icon_slash_screen

        Glide.with(this)
            .load(displayAvatarImage)
            .apply(
                RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).error(
                    R.drawable.music_player_icon_slash_screen
                ).centerCrop()
            )
            .into(binding.playlistImgPD)

        adapter.notifyDataSetChanged()
    }

    private fun scrollToTop() {
        binding.playlistDetailsRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    // Scrolling down
                    binding.moveToTopBtn.visibility = View.VISIBLE
                } else if (dy < 0) {
                    // Scrolling up
                    binding.moveToTopBtn.visibility = View.GONE
                }
            }
        })

        binding.moveToTopBtn.setOnClickListener {
            binding.playlistDetailsRV.smoothScrollToPosition(0)
        }
    }
}
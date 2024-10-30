package com.example.mediaplayer.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SectionIndexer
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mediaplayer.R
import com.example.mediaplayer.activity.PlayerActivity
import com.example.mediaplayer.activity.PlaylistDetailActivity
import com.example.mediaplayer.databinding.AudioHeaderBinding
import com.example.mediaplayer.databinding.AudioViewBinding
import com.example.mediaplayer.fragment.basic.PlaylistFragment
import com.example.mediaplayer.model.Audio
import com.example.mediaplayer.util.Constant
import java.util.Locale


class AudioAdapter(
    private var audioList: ArrayList<Audio>,
    private val playlistDetails: Boolean = false,
    private val selectionActivity: Boolean = false,
    private val albumAndArtist: Boolean = false,
    private var songPath: Audio? = null

) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), SectionIndexer {

    private lateinit var context: Context

    private var sectionsTranslator = HashMap<Int, Int>()
    private var mSectionPositions: ArrayList<Int>? = null

    class AudioView(binding: AudioViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.songNameMV
        val album = binding.songAlbumMV
        val image = binding.imageMV
        val duration = binding.songDuration
        val root = binding.root
        val context: Context = binding.root.context
    }

    class AudioHeader(binding: AudioHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        val playAll = binding.playAllBtn
        val shuffle = binding.shuffleBtn
        val totalVideos = binding.totalVideos
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (viewType) {
            TYPE_HEADER -> {

                val binding =
                    AudioHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AudioHeader(binding)
            }

            TYPE_ITEM -> {
                val binding =
                    AudioViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AudioView(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount() =
        if (audioList[0].title == "" && audioList[0].id == "") audioList.size - 1 else audioList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val audio = audioList[position]
        when (holder) {
            is AudioView -> {
                holder.title.text = audio.title
                holder.album.text = audio.album
                holder.duration.text = Constant.formatDuration(audio.duration)
                val backgroundColor = if (songPath?.id == audio.id) {
                    ContextCompat.getColor(holder.context, R.color.orange)
                } else ContextCompat.getColor(holder.context, R.color.gray)

                holder.root.setBackgroundColor(backgroundColor)

                Glide.with(holder.context).load(audio.artUri).apply(
                    RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen)
                        .error(R.drawable.music_player_icon_slash_screen)
                        .centerCrop()
                ).into(holder.image)

                when {
                    albumAndArtist -> {
                        holder.root.setOnClickListener {
                            sendIntent(ref = Constant.ALBUM_AND_ARTIST, pos = position)
                        }
                    }

                    playlistDetails -> {
                        holder.root.setOnClickListener {
                            sendIntent(ref = Constant.PLAYLIST_DETAILS_ADAPTER, pos = position)
                        }
                    }

                    selectionActivity -> {
                        holder.root.setOnClickListener {
                            if (addSong(audioList[position]))
                                holder.root.setBackgroundColor(
                                    ContextCompat.getColor(
                                        holder.context,
                                        R.color.cool_pink
                                    )
                                )
                            else
                                holder.root.setBackgroundColor(
                                    ContextCompat.getColor(
                                        holder.context,
                                        R.color.white
                                    )
                                )

                        }
                    }

                    else -> {
                        holder.root.setOnClickListener {
                            sendIntent(Constant.AUDIO_ADAPTER, position)
                        }
                    }
                }
            }

            is AudioHeader -> {
                holder.totalVideos.text =
                    context.resources.getString(
                        R.string.total_audios,
                        if (audioList[0].title == "" && audioList[0].id == "") audioList.size - 1 else audioList.size
                    )

                holder.shuffle.setOnClickListener {
                    val intent = Intent(context, PlayerActivity::class.java)
                    intent.putExtra(Constant.INDEX, 0)
                    intent.putExtra(Constant.CLASS_NAME, Constant.AUDIO_FRAGMENT)
                    context.startActivity(intent)
                }

                holder.totalVideos.visibility = if (albumAndArtist) View.GONE else View.VISIBLE
            }
        }


    }

    fun updateMusicList(searchList: ArrayList<Audio>) {
        audioList = ArrayList()
        audioList.addAll(searchList)
        notifyDataSetChanged()
    }

    private fun addSong(song: Audio): Boolean {
        PlaylistFragment.musicPlaylist.ref[PlaylistDetailActivity.currentPlaylistPos].playlist.forEachIndexed { index, music ->
            if (song.id == music.id) {
                PlaylistFragment.musicPlaylist.ref[PlaylistDetailActivity.currentPlaylistPos].playlist.removeAt(
                    index
                )
                return false
            }
        }
        PlaylistFragment.musicPlaylist.ref[PlaylistDetailActivity.currentPlaylistPos].playlist.add(
            song
        )
        return true
    }

    fun refreshPlaylist() {
        audioList = ArrayList()
        audioList =
            PlaylistFragment.musicPlaylist.ref[PlaylistDetailActivity.currentPlaylistPos].playlist
        notifyDataSetChanged()
    }

    private fun sendIntent(ref: String, pos: Int) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra(Constant.INDEX,  if (audioList[0].title == "" && audioList[0].id == "") pos - 1 else pos)
        intent.putExtra(Constant.CLASS_NAME, ref)
        ContextCompat.startActivity(context, intent, null)
    }

    override fun getItemViewType(position: Int): Int {
        return if (audioList[position].title == "" && audioList[position].id == "") {
            TYPE_HEADER
        } else TYPE_ITEM
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    override fun getSections(): Array<String> {
     val sections: MutableList<String> = ArrayList(27)
        val alphabetFull =  ArrayList<String>()
        mSectionPositions = ArrayList()
        val nameSongList = audioList.map { it.title }.filter { it != "" }
        run {
            var i = 0
            val size = nameSongList.size
            while(i < size) {
                println("audioList: ${nameSongList[i]}")
                val section = nameSongList[i][0].uppercase(Locale.getDefault())
                if(!sections.contains(section)) {
                    sections.add(section)
                    mSectionPositions?.add(i)
                }
                i++
            }
        }
        for(element in sections) {
            alphabetFull.add(element.toString())
        }
        sectionsTranslator = Constant.sectionsHelper(sections, alphabetFull)
        println("alphabetFull: $sections")
        println("alphabetFull: $alphabetFull")
        println("mSectionPositions: $mSectionPositions")
        println("sectionsTranslator: $sectionsTranslator")
        val x: Array<String> =  alphabetFull.toTypedArray()
        return x
    }

    override fun getPositionForSection(sectionIndex: Int): Int {
        return mSectionPositions!![sectionsTranslator[sectionIndex]!!].also {
            println("getPositionForSection: $it")
        }
    }

    override fun getSectionForPosition(position: Int): Int {
       return 0
    }

}

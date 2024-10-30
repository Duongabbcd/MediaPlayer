package com.example.mediaplayer.adapter

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mediaplayer.activity.PlaylistDetailActivity
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.AlbumViewBinding
import com.example.mediaplayer.model.Audio
import com.example.mediaplayer.util.Constant

class AlbumAdapter(
    private var albums: Map<String, List<Audio>>,
    private val isArtist: Boolean = false
) : RecyclerView.Adapter<AlbumAdapter.MyHolder>() {

    class MyHolder(binding: AlbumViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val songName = binding.songName
        val noOfSong = binding.noOfSong
        val avatar = binding.avatar
        val root = binding.root
        val context: Context = binding.root.context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(
            AlbumViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = albums.size

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val generalInfos: List<String> = albums.map { it.key }
        val detailInfo = albums.map { it.value }

        val general = generalInfos[position]
        val detail = detailInfo[position]
        holder.songName.text = general
        holder.songName.ellipsize = TextUtils.TruncateAt.END
        holder.noOfSong.text = "${detail.size} songs"
        Glide.with(holder.context).load(detail.first().artUri).apply(
            RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen)
                .error(R.drawable.music_player_icon_slash_screen)
                .centerCrop()
        ).into(holder.avatar)

        holder.root.setOnClickListener {
            val intent = Intent(holder.context, PlaylistDetailActivity::class.java)
            intent.putExtra(Constant.ALBUM_ADAPTER, isArtist)
            intent.putExtra(Constant.GENERAL, general)
            ContextCompat.startActivity(holder.context, intent, null)
        }
    }

}
package com.example.mediaplayer.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mediaplayer.activity.PlaylistDetailActivity
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.PlaylistViewBinding
import com.example.mediaplayer.fragment.basic.PlaylistFragment
import com.example.mediaplayer.model.Playlist
import com.example.mediaplayer.model.setDialogBtnBackground
import com.example.mediaplayer.util.Constant
import com.example.mediaplayer.viewmodel.AudioViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

class PlaylistViewAdapter(
    private var playlistList: ArrayList<Playlist>,
    private val viewModel: AudioViewModel
) : RecyclerView.Adapter<PlaylistViewAdapter.MyHolder>() {

    class MyHolder(binding: PlaylistViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.playlistImg
        val name = binding.playlistName
        val root = binding.root
        val delete = binding.playlistDeleteBtn
        val context: Context = binding.root.context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(PlaylistViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text = playlistList[position].name
        holder.name.isSelected = true
        holder.delete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(holder.context)
            builder.setTitle(playlistList[position].name)
                .setMessage("Do you want to delete playlist?")
                .setPositiveButton("Yes") { dialog, _ ->
                    PlaylistFragment.musicPlaylist.ref.removeAt(position)
                    refreshPlaylist(holder.context)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()

            setDialogBtnBackground(holder.context, customDialog)
        }

        holder.root.setOnClickListener {
            val intent = Intent(holder.context, PlaylistDetailActivity::class.java)
            intent.putExtra(Constant.INDEX, position)
            ContextCompat.startActivity(holder.context, intent, null)
        }

        if (PlaylistFragment.musicPlaylist.ref[position].playlist.size > 0) {
            Glide.with(holder.context)
                .load(PlaylistFragment.musicPlaylist.ref[position].playlist[0].artUri)
                .apply(
                    RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen)
                        .error(R.drawable.music_player_icon_slash_screen)
                        .centerCrop()
                )
                .into(holder.image)
        }

    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

    fun refreshPlaylist(context: Context) {
        playlistList = ArrayList()
        playlistList.addAll(PlaylistFragment.musicPlaylist.ref).also {
            val editor = context.getSharedPreferences(Constant.FAVOURITE, MODE_PRIVATE).edit()
            val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistFragment.musicPlaylist)
            println("refreshPlaylist: $jsonStringPlaylist")
            editor.putString(Constant.MUSIC_PLAY_LIST, jsonStringPlaylist)
            editor.commit()
        }

        println("addPlaylist 2: ${PlaylistFragment.musicPlaylist}")
        viewModel.checkDataIsExist(playlistList.isEmpty())
        notifyDataSetChanged()
    }
}
package com.example.mediaplayer.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mediaplayer.R
import com.example.mediaplayer.activity.PlayerActivity
import com.example.mediaplayer.databinding.FavouriteViewBinding
import com.example.mediaplayer.model.Audio
import com.example.mediaplayer.util.Constant

class FavouriteAdapter(
    private var audioList: ArrayList<Audio>
) :
    RecyclerView.Adapter<FavouriteAdapter.MyHolder>() {

    private lateinit var context: Context

    class MyHolder(binding: FavouriteViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.songNameFV
        val image = binding.songImgFV
        val root = binding.root
        val context: Context = binding.root.context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        context = parent.context
        return MyHolder(
            FavouriteViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val audio = audioList[position]
        holder.title.text = audio.title
        Glide.with(holder.context).load(audio.artUri).apply(
            RequestOptions()
                .placeholder(R.drawable.music_player_icon_slash_screen)
        ).error(R.drawable.music_player_icon_slash_screen)
            .centerCrop().into(holder.image)

        holder.root.setOnClickListener {
            sendIntent(ref =Constant.FAVOURITE_ADAPTER, pos = position)
        }
    }

    override fun getItemCount(): Int {
        return audioList.size
    }

    private fun sendIntent(ref: String, pos: Int) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra(Constant.INDEX, pos)
        intent.putExtra(Constant.CLASS_NAME, ref)
        ContextCompat.startActivity(context, intent, null)
    }
}
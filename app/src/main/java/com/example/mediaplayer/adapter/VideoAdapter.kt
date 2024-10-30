package com.example.mediaplayer.adapter

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.VideoViewBinding
import com.example.mediaplayer.model.Video

class VideoAdapter(
    private val videoList: ArrayList<Video>,
) : RecyclerView.Adapter<VideoAdapter.MyHolder>() {

    private lateinit var context: Context

    class MyHolder(binding: VideoViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.videoName
        val folder = binding.folderName
        val duration = binding.duration
        val image = binding.videoImg
        val root = binding.root
        val context: Context = binding.root.context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        context = parent.context
        return MyHolder(VideoViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = videoList.size

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val video = videoList[position]
        holder.title.text = video.title
        holder.folder.text = video.folderName
        holder.duration.text = DateUtils.formatElapsedTime(video.duration / 1000)
        Glide.with(holder.context).asBitmap().load(video.artUri)
            .apply(RequestOptions().placeholder(R.mipmap.ic_launcher).centerCrop())
            .into(holder.image)
    }
}
package com.example.mediaplayer.fragment.bonus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.MainActivity
import com.example.mediaplayer.adapter.VideoAdapter
import com.example.mediaplayer.databinding.FragmentVideosBinding

class VideosFragment : Fragment(){
    lateinit var videoAdapter: VideoAdapter
    private lateinit var binding : FragmentVideosBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVideosBinding.inflate(inflater,container, false)
        binding.totalVideos.text = "Total Videos: ${MainActivity.videoList.size}"
        binding.videoRv.setHasFixedSize(true)
        binding.videoRv.setItemViewCacheSize(10)
        binding.videoRv.layoutManager = LinearLayoutManager(requireContext())
        binding.videoRv.adapter = VideoAdapter( MainActivity.videoList)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
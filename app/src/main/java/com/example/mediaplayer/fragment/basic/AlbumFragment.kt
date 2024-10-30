package com.example.mediaplayer.fragment.basic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaplayer.adapter.AlbumAdapter
import com.example.mediaplayer.databinding.FragmentAlbumBinding
import com.example.mediaplayer.model.Audio
import com.example.mediaplayer.util.Constant

class AlbumFragment : Fragment() {
    companion object {
        lateinit var albums: Map<String, List<Audio>>
    }

    private lateinit var binding: FragmentAlbumBinding
    private lateinit var albumAdapter: AlbumAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)
        albums = Constant.audioOfAlbum
        albumAdapter = AlbumAdapter( albums, false)
        binding.playlistRV.adapter = albumAdapter
        val displayMetrics = resources.displayMetrics
        val width = displayMetrics.widthPixels
        val columnCount = width / 300 // Assuming each item has a width of 300dp
        val layoutManager = GridLayoutManager(requireContext(), columnCount)
        binding.playlistRV.layoutManager = layoutManager

        return binding.root
    }
}
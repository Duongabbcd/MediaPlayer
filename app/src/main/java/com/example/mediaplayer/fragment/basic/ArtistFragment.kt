package com.example.mediaplayer.fragment.basic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaplayer.adapter.AlbumAdapter
import com.example.mediaplayer.databinding.FragmentArtistBinding
import com.example.mediaplayer.model.Audio
import com.example.mediaplayer.util.Constant

class ArtistFragment : Fragment() {
    private lateinit var artist: Map<String, List<Audio>>

    private lateinit var binding: FragmentArtistBinding
    private lateinit var albumAdapter: AlbumAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArtistBinding.inflate(inflater, container, false)
        artist = Constant.audioOfArtist
        println("onCreate: $artist")
        albumAdapter = AlbumAdapter(artist, true)
        binding.playlistRV.adapter = albumAdapter
        val displayMetrics = resources.displayMetrics
        val width = displayMetrics.widthPixels
        val columnCount = width / 300 // Assuming each item has a width of 300dp
        val layoutManager = GridLayoutManager(requireContext(), columnCount)
        binding.playlistRV.layoutManager = layoutManager

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
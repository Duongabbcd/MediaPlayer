package com.example.mediaplayer.fragment.basic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaplayer.R
import com.example.mediaplayer.adapter.PlaylistViewAdapter
import com.example.mediaplayer.databinding.AddPlaylistDialogBinding
import com.example.mediaplayer.databinding.FragmentPlaylistBinding
import com.example.mediaplayer.model.MusicPlaylist
import com.example.mediaplayer.model.Playlist
import com.example.mediaplayer.model.setDialogBtnBackground
import com.example.mediaplayer.util.Constant
import com.example.mediaplayer.viewmodel.AudioViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlaylistFragment : Fragment() {
    private lateinit var binding: FragmentPlaylistBinding

    private val viewModel: AudioViewModel by viewModels()
    private val adapter: PlaylistViewAdapter by lazy {
        PlaylistViewAdapter(playlistList = musicPlaylist.ref, viewModel)
    }

    companion object {
        var musicPlaylist: MusicPlaylist = MusicPlaylist()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)

        val editor = requireActivity().getSharedPreferences(Constant.FAVOURITE, MODE_PRIVATE)
        musicPlaylist = MusicPlaylist()
        val jsonStringPlaylist = editor.getString(Constant.MUSIC_PLAY_LIST, null)
        println("onCreateView 2: $jsonStringPlaylist")
        if (jsonStringPlaylist != null) {
            val dataPlaylist: MusicPlaylist =
                GsonBuilder().create().fromJson(jsonStringPlaylist, MusicPlaylist::class.java)
            musicPlaylist = dataPlaylist
        }

        viewModel.checkDataIsExist(musicPlaylist.ref.isEmpty())

        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)
        binding.playlistRV.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistRV.adapter = adapter

        viewModel.playList.observe(this) { event ->
            binding.instructionPA.visibility = if (event) View.VISIBLE else View.GONE
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addPlaylistBtn.setOnClickListener { customAlertDialog() }
    }

    private fun customAlertDialog() {
        val customDialog = LayoutInflater.from(requireContext())
            .inflate(R.layout.add_playlist_dialog, binding.root, false)
        val binder = AddPlaylistDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(requireContext())
        val dialog = builder.setView(customDialog)
            .setTitle(R.string.playlist_detail)
            .setPositiveButton(R.string.add_song) { dialog, _ ->
                val playlistName = binder.playlistName.text
                val createdBy = binder.yourName.text
                if (playlistName != null && createdBy != null)
                    if (playlistName.isNotEmpty() && createdBy.isNotEmpty()) {
                        addPlaylist(playlistName.toString(), createdBy.toString())
                    }
                dialog.dismiss()
            }.create()
        dialog.show()
        setDialogBtnBackground(requireContext(), dialog)

    }

    private fun addPlaylist(name: String, createdBy: String) {
        var playlistExists = false
        for (i in musicPlaylist.ref) {
            if (name == i.name) {
                playlistExists = true
                break
            }
        }
        if (playlistExists) Toast.makeText(requireContext(), R.string.playlist_exist, Toast.LENGTH_SHORT)
            .show()
        else {
            val tempPlaylist = Playlist()
            tempPlaylist.name = name
            tempPlaylist.playlist = ArrayList()
            tempPlaylist.createdBy = createdBy
            val calendar = Calendar.getInstance().time
            val simpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempPlaylist.createdOn = simpleDateFormat.format(calendar)
            musicPlaylist.ref.add(tempPlaylist)
            println("addPlaylist 1: $musicPlaylist")
            adapter.refreshPlaylist(requireContext())
        }
    }
}
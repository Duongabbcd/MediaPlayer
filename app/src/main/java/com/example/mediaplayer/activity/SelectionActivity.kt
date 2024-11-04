package com.example.mediaplayer.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.MainActivity
import com.example.mediaplayer.R
import com.example.mediaplayer.adapter.AudioAdapter
import com.example.mediaplayer.databinding.ActivitySelectionBinding
import com.example.mediaplayer.util.Constant

class SelectionActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySelectionBinding
    private lateinit var adapter: AudioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setTheme(R.style.coolPinkNav)
        setContentView(binding.root)
        binding.selectionRV.setItemViewCacheSize(30)
        binding.selectionRV.setHasFixedSize(true)
        binding.selectionRV.layoutManager = LinearLayoutManager(this)
        adapter = AudioAdapter(Constant.audioLists, selectionActivity = true)
        binding.selectionRV.adapter = adapter
        binding.backBtnSA.setOnClickListener { finish() }
        //for search View
        binding.searchViewSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                MainActivity.musicListSearch = ArrayList()
                if(newText != null){
                    val userInput = newText.lowercase()
                    for (song in Constant.audioLists)
                        if(song.title.lowercase().contains(userInput))
                            MainActivity.musicListSearch.add(song)
                    MainActivity.search = true
                    adapter.updateMusicList(searchList = MainActivity.musicListSearch)
                }
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()

    }
}
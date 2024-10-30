package com.example.mediaplayer.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.mediaplayer.model.Audio
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaplayer.R
import com.example.mediaplayer.adapter.FavouriteAdapter
import com.example.mediaplayer.databinding.ActivityFavouriteBinding
import com.example.mediaplayer.util.Constant
import com.example.mediaplayer.util.Constant.getFavouriteSongs
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class FavouriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var adapter: FavouriteAdapter

    companion object {
        var favouriteSongs: ArrayList<Audio> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTheme(R.style.coolPinkNav)
        binding.backBtnFA.setOnClickListener { finish() }
        println("onCreate")
        displayFavouriteSongs()


        binding.shuffleBtnFA.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra(Constant.INDEX, 0)
            intent.putExtra(Constant.CLASS_NAME, Constant.FAVOURITE_SHUFFLE)
            intent.putExtra(Constant.IS_FAVOURITE, true)
            startActivity(intent)
        }
    }

    private fun displayFavouriteSongs() {
        favouriteSongs = ArrayList()
        getFavouriteSongs(this)
        favouriteSongs.addAll(Constant.currentFavouriteSong)

        println("onCreateView 4: $favouriteSongs")

        binding.favouriteRV.setHasFixedSize(true)
        binding.favouriteRV.setItemViewCacheSize(13)

        val displayMetrics = resources.displayMetrics
        val width = displayMetrics.widthPixels
        val columnCount = width / 200 // Assuming each item has a width of 300dp
        val layoutManager = GridLayoutManager(this, columnCount)
        binding.favouriteRV.layoutManager = layoutManager

        adapter = FavouriteAdapter(favouriteSongs)
        binding.favouriteRV.adapter = adapter

        binding.favouriteRV.visibility = if (favouriteSongs.isEmpty()) View.GONE else View.VISIBLE
        binding.instructionFV.visibility = if (favouriteSongs.isEmpty()) View.VISIBLE else View.GONE
    }
}
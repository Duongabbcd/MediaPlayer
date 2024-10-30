package com.example.mediaplayer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mediaplayer.util.Constant

open class AudioReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Retrieve the data from the intent
        if(intent?.action == "com.example.ACTION_SEND_DATA") {
            val data = intent.getStringExtra(Constant.SONG_POSITION)
            println("onReceive: $data")
        }
    }
}
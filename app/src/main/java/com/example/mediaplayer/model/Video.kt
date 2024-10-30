package com.example.mediaplayer.model

import android.content.Context
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import com.example.mediaplayer.R
import com.google.android.material.color.MaterialColors
import java.io.File

data class Video(
    val id: String,
    var title: String,
    val duration: Long = 0,
    val folderName: String,
    val size: String,
    var path: String,
    var artUri: Uri
)

data class Folder(
    val id: String,
    val folderName: String
)

data class Audio(
    val id: String,
    val title: String,
    val album: String,
    val artist: String,
    val duration: Long = 0,
    val path: String,
    val artUri: String
)

class Playlist {
    lateinit var name: String
    lateinit var playlist: ArrayList<Audio>
    lateinit var createdBy: String
    lateinit var createdOn: String
}

data class MusicPlaylist (
    var ref: ArrayList<Playlist> = ArrayList()
)

fun setDialogBtnBackground(context: Context, dialog: AlertDialog) {
    //setting button text
    dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)?.setTextColor(
        MaterialColors.getColor(context, R.attr.dialogTextColor, Color.WHITE)
    )
    dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
        MaterialColors.getColor(context, R.attr.dialogTextColor, Color.WHITE)
    )

    //setting button background
    dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)?.setBackgroundColor(
        MaterialColors.getColor(context, R.attr.dialogBtnBackground, Color.RED)
    )
    dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)?.setBackgroundColor(
        MaterialColors.getColor(context, R.attr.dialogBtnBackground, Color.RED)
    )
}

fun checkPlaylist(playlist: ArrayList<Audio>): ArrayList<Audio> {
    val indicesToRemove = mutableListOf<Int>()

    playlist.forEachIndexed { index, music ->
        if (!File(music.path).exists()) indicesToRemove.add(index)
    }

    indicesToRemove.sortDescending()
    indicesToRemove.forEach { index -> playlist.removeAt(index) }
    return playlist
}




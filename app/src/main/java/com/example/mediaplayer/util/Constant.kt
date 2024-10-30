package com.example.mediaplayer.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.recyclerview.widget.RecyclerView
import com.cheonjaeung.powerwheelpicker.android.WheelPicker
import com.example.mediaplayer.MainActivity
import com.example.mediaplayer.MainActivity.Companion.folderList
import com.example.mediaplayer.activity.FavouriteActivity
import com.example.mediaplayer.activity.PlayerActivity
import com.example.mediaplayer.activity.PlayerActivity.Companion.musicListPA
import com.example.mediaplayer.activity.PlayerActivity.Companion.songPosition
import com.example.mediaplayer.adapter.SampleItemEffector
import com.example.mediaplayer.model.Audio
import com.example.mediaplayer.model.Folder
import com.example.mediaplayer.model.Video
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess


object Constant {
    private var sortOrder: Int = 0
    private val sortingList = arrayOf(
        MediaStore.Audio.Media.DATE_ADDED + " DESC", MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.SIZE + " DESC"
    )

    var currentFavouriteSong: ArrayList<Audio> = arrayListOf()
    fun getFavouriteSongs(context: Context) {
        currentFavouriteSong.clear()
        val editor = context.getSharedPreferences(FAVOURITE, MODE_PRIVATE)
        val jsonStringPlaylist = editor.getString(FAVOURITE_SONGS, null)
        val typeToken = object : TypeToken<ArrayList<Audio>>() {}.type
        println("onCreateView 2: $jsonStringPlaylist")
        if (jsonStringPlaylist != null) {
            val dataPlaylist: ArrayList<Audio> =
                GsonBuilder().create().fromJson(jsonStringPlaylist, typeToken)
            println("onCreateView 3: $dataPlaylist")

            currentFavouriteSong.addAll(dataPlaylist)
        }
    }

    @SuppressLint("InlineApi", "Recycle", "Range")
    fun getAllVideos(context: Context): ArrayList<Video> {
        val tempList = ArrayList<Video>()
        val tempFolderList = ArrayList<String>()
        val projection = arrayOf(
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.BUCKET_ID,
        )

        val cursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            MediaStore.Video.Media.DATE_ADDED + " DESC"
        )
        if (cursor != null) {
            if (cursor.moveToNext()) {
                do {
                    val title =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                    val id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                    val folder =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
                    val folderId =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID))
                            ?: "Unknown"
                    val size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
                    val path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                    val duration =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))

                    try {
                        val file = File(path)
                        val artUri = Uri.fromFile(file)
                        val video = Video(
                            id = id,
                            title = title,
                            duration = duration,
                            folderName = folder,
                            size = size,
                            path = path,
                            artUri = artUri
                        )
                        if (file.exists()) tempList.add(video)

                        //for adding folders
                        if (!tempFolderList.contains(folder) && !folder.contains("Internal Storage")) {
                            tempFolderList.add(folder)
                            folderList.add(Folder(id = folderId, folderName = folder))
                        }

                    } catch (e: Exception) {

                    }

                } while (cursor.moveToNext())

            }
        }
        cursor?.close()

        return tempList
    }


    @SuppressLint("Range")
    fun getAllAudioFiles(context: Context): ArrayList<Audio> {
        val audioList = ArrayList<Audio>()
        // Filter Only Music or Audio Files
        val selection =
            MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Media.MIME_TYPE + " LIKE 'audio/%'"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null,
            sortingList[sortOrder], null
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val titleC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                            ?: UNKNOWN
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                        ?: UNKNOWN
                    val albumC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                            ?: UNKNOWN
                    val artistC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                            ?: UNKNOWN
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumId =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                            .toString()
                    val uri = Uri.parse(DEVICE_ALBUM_URL)
                    val artUri = Uri.withAppendedPath(uri, albumId).toString()

                    // Only add the music file if the duration is greater than 0
                    if (durationC > 0) {
                        val music = Audio(
                            id = idC,
                            title = titleC,
                            album = albumC,
                            artist = artistC,
                            path = pathC,
                            duration = durationC,
                            artUri = artUri
                        )

                        if (File(music.path).exists()) audioList.add(music)
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        val sortedList = audioList.sortedBy { it.title }
        return ArrayList(sortedList).also {
            println("getAllAudioFiles: ${sortedList.size}")
        }
    }

    @SuppressLint("DefaultLocale")
    fun formatDuration(duration: Long): String {
        val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
                minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun getImgArt(path: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        return retriever.embeddedPicture
    }

    fun setSongPosition(increment: Boolean) {
        if (increment) {
            if (musicListPA.size - 1 == songPosition) songPosition = 0
            else songPosition++
        } else {
            if (0 == songPosition) songPosition = musicListPA.size - 1 else --songPosition
        }
    }

    fun ArrayList<Audio>.sortTracksByAlbum(): Map<String, List<Audio>> {
        return this.groupBy { it.album }.also {
            println("sortTracksByAlbum: $it")
        }
    }

    fun ArrayList<Audio>.sortTracksByArtist(): Map<String, List<Audio>> {
        return this.groupBy { it.artist }.also {
            println("sortTracksByArtist: $it")
        }
    }


    fun View.hideGeneralInfo() {
        // Set the initial alpha value
        this.alpha = 1f

        // Animate the alpha value to 1 (fully visible) over 500 milliseconds
        this.animate()
            .alpha(0f)
            .setDuration(1000)
            .start()
        this.visibility = View.GONE
    }

    fun View.showGeneralInfo() {
        // Set the initial alpha value
        this.alpha = 0f

        // Animate the alpha value to 1 (fully visible) over 500 milliseconds
        this.animate()
            .alpha(1f)
            .setDuration(1000)
            .start()
        this.visibility = View.VISIBLE
    }

    fun exitApplication(context: Context) {
        val editor = context.getSharedPreferences(CURRENT_PLAY_SONG, MODE_PRIVATE)
        editor.edit().putString(SONG_POSITION, "").commit()

        val selectedSong = editor.getString(SONG_POSITION, "")
        val result = if (!selectedSong.isNullOrEmpty()) {
            GsonBuilder().create().fromJson(selectedSong, Audio::class.java)
        } else null

        println("exitApplication 1: $result")


        if (PlayerActivity.musicService != null) {
            PlayerActivity.musicService!!.audioManager.abandonAudioFocus(PlayerActivity.musicService)
            PlayerActivity.musicService!!.stopForeground(true)
            PlayerActivity.musicService!!.mediaPlayer!!.release()
            PlayerActivity.musicService = null

            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
        exitProcess(1)
    }

    fun favouriteChecker(id: String): Int {
        FavouriteActivity.favouriteSongs.forEachIndexed { index, audio ->
            if (id == audio.id) {
                PlayerActivity.isPlaying = true
                return index
            }
        }
        return -1
    }

    fun checkPlaylist(playList: ArrayList<Audio>): ArrayList<Audio> {
        val indicesToRemove = mutableListOf<Int>()

        playList.forEachIndexed { index, audio ->
            if (!File(audio.path).exists()) indicesToRemove.add(index)
        }

        indicesToRemove.sortDescending()
        indicesToRemove.forEach { index -> playList.removeAt(index) }
        return playList
    }

    fun sectionsHelper(
        sections: MutableList<String>,
        test: java.util.ArrayList<String>
    ): HashMap<Int, Int> {
        val mapOfSections = hashMapOf<Int, Int>()
        var lastFound = 0
        test.forEachIndexed { index, s ->
            if (sections.any { it == s }) {
                val value = sections.indexOfFirst { it == s }
                mapOfSections[index] = value
                lastFound = value
            } else {
                mapOfSections[index] = lastFound
            }
        }
        return mapOfSections
    }

    fun WheelPicker.customItemEffector(context: Context) {
        this.addItemEffector(SampleItemEffector(context, this))
    }

    @SuppressLint("ClickableViewAccessibility")
    fun RecyclerView.customTouchingListener(
        bottomSheetBehavior: BottomSheetBehavior<View>,
        vibrator: Vibrator
    ) {
//        val oneShot = VibrationEffect.createOneShot(5, VibrationEffect.DEFAULT_AMPLITUDE)
        val pattern = longArrayOf(0, 10, 3000, 5)
        this.setOnTouchListener(OnTouchListener { v, event ->
            val action = event.action
            when (action) {
                // Disallow NestedScrollView to intercept touch events.
                MotionEvent.ACTION_DOWN ->
                {
                    bottomSheetBehavior.isDraggable = false
                }
                MotionEvent.ACTION_MOVE -> {
//                    vibrator.vibrate(waveForm)
                }
                // Allow NestedScrollView to intercept touch events.
                MotionEvent.ACTION_UP ->
                {
                    bottomSheetBehavior.isDraggable = true
//                    vibrator.vibrate(waveForm)
                }
            }
            // Handle RecyclerView touch events.
            v.onTouchEvent(event)
            true
        })

        this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    vibrator.cancel()
                } else  {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val waveForm = VibrationEffect.createWaveform(pattern, -1) // -1 means no
                        vibrator.vibrate(waveForm)
                    } else {
                        vibrator.vibrate(5);
                    }
                }
            }
        })
    }

    //audio lists
    var audioLists = ArrayList<Audio>()
    var audioOfAlbum = mapOf<String, List<Audio>>()
    var audioOfArtist = mapOf<String, List<Audio>>()

    //Const values
    const val REPEAT = "REPEAT"
    const val IS_REPEATED = "IS_REPEATED"

    const val PREVIOUS_TITLE = "Previous"
    const val PLAY_TITLE = "Play"
    const val NEXT_TITLE = "Next"
    const val EXIT_TITLE = "Exit"

    const val IS_PLAYING = "isPlaying"
    const val INDEX = "index"
    const val CLASS_NAME = "class"
    const val GENERAL = "general"

    private const val UNKNOWN = "Unknown"
    private const val DEVICE_ALBUM_URL = "content://media/external/audio/albumart"

    //Share preferences
    const val FAVOURITE = "FAVOURITES"
    const val MUSIC_PLAY_LIST = "MusicPlaylist"
    const val FAVOURITE_SONGS = "FavouriteSongs"

    //broadcast receiver
    const val CURRENT_PLAY_SONG = "CurrentPlaySong"
    const val SONG_POSITION = "SongPosition"

    //For intent
    const val AUDIO_FRAGMENT = "AudioFragment"
    const val NOW_PLAYING = "NowPlaying"
    const val AUDIO_ADAPTER = "AudioAdapter"
    const val MUSIC_SERVICE = "MusicService"
    const val PLAYLIST_DETAILS_ADAPTER = "PlaylistDetailsAdapter"
    const val ALBUM_AND_ARTIST = "AlbumAndArtist"
    const val PLAYLIST_DETAILS_SHUFFLE = "PlaylistDetailsShuffle"
    const val ALBUM_ADAPTER = "AlbumAdapter"
    const val FAVOURITE_ADAPTER = "FavouriteAdapter"
    const val FAVOURITE_SHUFFLE = "FavouriteShuffle"
    const val IS_FAVOURITE = "isFavourite"

}
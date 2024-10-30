package com.example.mediaplayer

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.cheonjaeung.powerwheelpicker.android.WheelPicker
import com.example.mediaplayer.activity.FavouriteActivity
import com.example.mediaplayer.activity.PlayerActivity
import com.example.mediaplayer.adapter.TimePickerAdapter
import com.example.mediaplayer.databinding.ActivityMainBinding
import com.example.mediaplayer.fragment.basic.AlbumFragment
import com.example.mediaplayer.fragment.basic.ArtistFragment
import com.example.mediaplayer.fragment.basic.AudioFragment
import com.example.mediaplayer.fragment.basic.PlaylistFragment
import com.example.mediaplayer.model.Audio
import com.example.mediaplayer.model.Folder
import com.example.mediaplayer.model.Video
import com.example.mediaplayer.service.MusicService
import com.example.mediaplayer.util.Constant
import com.example.mediaplayer.util.Constant.customItemEffector
import com.example.mediaplayer.util.Constant.customTouchingListener
import com.example.mediaplayer.util.Constant.sortTracksByAlbum
import com.example.mediaplayer.util.Constant.sortTracksByArtist
import com.example.mediaplayer.viewpager.ViewPagerAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var adapter1: TimePickerAdapter
    private lateinit var adapter2: TimePickerAdapter
    private lateinit var adapter3: TimePickerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestPermission()
        setTheme(R.style.coolPinkNav)

        //for Nav Drawer
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()

        toggle.drawerArrowDrawable.color = resources.getColor(R.color.white)
        initMethod()
        initView()

    }


    private fun initMethod() {
        musicListSearch = ArrayList()
        if (requestPermission()) {
            if(Constant.audioLists.isEmpty()) {
                Constant.audioLists = Constant.getAllAudioFiles(this)
                Constant.audioOfArtist = Constant.audioLists.sortTracksByArtist()
                Constant.audioOfAlbum = Constant.audioLists.sortTracksByAlbum()
            }
            println("initMethod: ${Constant.audioLists}")
            audioList = Constant.audioLists

        } else {
            folderList = ArrayList()
            videoList = ArrayList()
            audioList = ArrayList()
        }
    }

    private fun initView() {
        setViewPager()
        setBottomNavigationView()

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navYoutube -> {
                    binding.main.closeDrawer(GravityCompat.START)
                    showTimeSleeperSetting()
                }
                R.id.navUrl -> Toast.makeText(this, "Time Sleeper", Toast.LENGTH_SHORT).show()
                R.id.aboutNav -> {
                    Toast.makeText(this, "Favourite Song", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, FavouriteActivity::class.java))
                }
                R.id.exitNav -> Toast.makeText(this, "Exit", Toast.LENGTH_SHORT).show()

            }
            return@setNavigationItemSelectedListener true
        }
    }
    
    @SuppressLint("MissingInflatedId")
    private fun showTimeSleeperSetting() {
        val dialog = layoutInflater.inflate(R.layout.fragment_time_picker, null)
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        bottomSheetDialog.setContentView(dialog)

        adapter1 = TimePickerAdapter(true)
        adapter2 = TimePickerAdapter(false)
        adapter3 = TimePickerAdapter(false)

        val wheelPicker1 = dialog.findViewById<WheelPicker>(R.id.wheelPicker1)
        val wheelPicker2 = dialog.findViewById<WheelPicker>(R.id.wheelPicker2)
        val wheelPicker3 = dialog.findViewById<WheelPicker>(R.id.wheelPicker3)
        val nestedScrollView = dialog.findViewById<NestedScrollView>(R.id.nestedScrollView)

        val addBtn = dialog.findViewById<MaterialButton>(R.id.timePickerAddBtn)
        val resetBtn = dialog.findViewById<MaterialButton>(R.id.timePickerCancelBtn)

        wheelPicker1.adapter = adapter1
        wheelPicker2.adapter = adapter2
        wheelPicker3.adapter = adapter3

        wheelPicker1.customItemEffector(this)
        wheelPicker2.customItemEffector(this)
        wheelPicker3.customItemEffector(this)

        val bottomSheetBehavior = BottomSheetBehavior.from(dialog.parent as View)

        val vib: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        wheelPicker1.recyclerView.customTouchingListener(bottomSheetBehavior, vib)
        wheelPicker2.recyclerView.customTouchingListener(bottomSheetBehavior, vib)
        wheelPicker3.recyclerView.customTouchingListener(bottomSheetBehavior, vib)

        nestedScrollView.setOnClickListener {
            bottomSheetBehavior.isDraggable = true
        }

        addBtn.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        resetBtn.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }


        bottomSheetDialog.show()
    }


    private fun setBottomNavigationView() {
        binding.bottomNav.visibility = View.GONE
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.audioBtn -> {
                    binding.viewPager.currentItem = 0
                }

                R.id.albumBtn -> {
                    binding.viewPager.currentItem = 1
                }

                R.id.artistBtn -> {
                    binding.viewPager.currentItem = 2
                }

                R.id.playlistBtn -> {
                    binding.viewPager.currentItem = 3
                }
            }
            return@setOnItemSelectedListener true
        }
    }

    private fun setViewPager() {
        val fragments: ArrayList<Fragment> = arrayListOf(
            AudioFragment(),
            AlbumFragment(),
            ArtistFragment(),
            PlaylistFragment()
        )
        val adapter = ViewPagerAdapter(items = fragments, this)

        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, pos ->
            tab.text = when (pos) {
                0 -> "Audio"
                1 -> "Album"
                2 -> "Artist"
                3 -> "Playlist"
                else -> null
            }
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> binding.bottomNav.menu.findItem(R.id.audioBtn).setChecked(true)
                    1 -> binding.bottomNav.menu.findItem(R.id.artistBtn).setChecked(true)
                    2 -> binding.bottomNav.menu.findItem(R.id.albumBtn).setChecked(true)
                    3 -> binding.bottomNav.menu.findItem(R.id.playlistBtn).setChecked(true)
                }
            }
        })
    }

    private fun requestPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            val permissions = arrayOf(
                READ_EXTERNAL_STORAGE
            )

            val permissionsNeeded = permissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }
            if (permissionsNeeded.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), 13)
                return false
            }
        } else {
            //android 13 or Higher permission request
            if (ActivityCompat.checkSelfPermission(this, READ_MEDIA_AUDIO)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(READ_MEDIA_AUDIO), 13)
                return false
            }
        }

        // Requesting storage permission for devices less than API 28
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), 13)
                return false
            }
        }

        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                Constant.audioLists = Constant.getAllAudioFiles(this)
                Constant.audioOfArtist = Constant.audioLists.sortTracksByArtist()
                Constant.audioOfAlbum = Constant.audioLists.sortTracksByAlbum()
                println("onRequestPermissionsResult: ${Constant.audioLists}")
                audioList = Constant.audioLists
            } else {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Storage permission Needed",
                    Snackbar.LENGTH_LONG
                ).setAction("OK") {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ActivityCompat.requestPermissions(this, arrayOf(READ_MEDIA_VIDEO), 13)
                    }
                }.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setViewPager()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if (PlayerActivity.musicService == null) {
            val binder = service as MusicService.MyBinder
            PlayerActivity.musicService = binder.currentService()

            PlayerActivity.musicService!!.audioManager =
                getSystemService(AUDIO_SERVICE) as AudioManager
            PlayerActivity.musicService!!.audioManager.requestAudioFocus(
                PlayerActivity.musicService,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
//        createMediaPlayer()
        PlayerActivity.musicService!!.seekBarSetup()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        PlayerActivity.musicService = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
//        setSongPosition(true, repeat)
//        createMediaPlayer(true)
        try {
//            setLayout()
        } catch (e: Exception) {
            return
        }
    }

    @SuppressLint("BatteryLife")
    private fun requestBatteryOptimizationExemption() {
        val intent = Intent()
        val packageName = packageName
        val pm = getSystemService(PowerManager::class.java)

        if (pm != null && !pm.isIgnoringBatteryOptimizations(packageName)) {
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package: $packageName")
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(!PlayerActivity.isPlaying && PlayerActivity.musicService != null) {
            Constant.exitApplication(this)
        }
    }

    private var audioList: ArrayList<Audio> = ArrayList()


    companion object {
        lateinit var videoList: ArrayList<Video>
        lateinit var folderList: ArrayList<Folder>

        lateinit var musicListSearch: ArrayList<Audio>
        var search: Boolean = false
        var musicService: MusicService? = null
    }
}
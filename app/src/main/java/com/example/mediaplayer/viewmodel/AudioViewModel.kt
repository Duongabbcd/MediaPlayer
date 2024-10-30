package com.example.mediaplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class AudioViewModel(application: Application): AndroidViewModel(application) {
    private var _playList = MutableLiveData<Boolean>()
    val playList : LiveData<Boolean> = _playList

    fun checkDataIsExist(isEmpty: Boolean) {
        _playList.value = isEmpty
    }
}
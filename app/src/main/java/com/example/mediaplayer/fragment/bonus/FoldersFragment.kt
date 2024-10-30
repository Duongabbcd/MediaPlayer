package com.example.mediaplayer.fragment.bonus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.MainActivity
import com.example.mediaplayer.adapter.FolderAdapter
import com.example.mediaplayer.databinding.FragmentFoldersBinding

class FoldersFragment  : Fragment() {
    private lateinit var binding : FragmentFoldersBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFoldersBinding.inflate(inflater,container, false)

        binding.foldersRv.setHasFixedSize(true)
        binding.foldersRv.setItemViewCacheSize(10)
        binding.foldersRv.layoutManager = LinearLayoutManager(requireContext())
        binding.foldersRv.adapter = FolderAdapter(MainActivity.folderList)
        return binding.root
    }


}
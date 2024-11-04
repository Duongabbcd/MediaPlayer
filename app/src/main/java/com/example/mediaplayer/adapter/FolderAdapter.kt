package com.example.mediaplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.databinding.FolderViewBinding
import com.example.mediaplayer.model.Folder

class FolderAdapter(
    private val foldersList: ArrayList<Folder>
) : RecyclerView.Adapter<FolderAdapter.MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(
            FolderViewBinding.inflate(
                LayoutInflater.from(
                    parent
                        .context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.folderName.text = foldersList[position].folderName
        holder.root.setOnClickListener {
//            val intent = Intent(context, FoldersActivity::class.java)
//            intent.putExtra("position", position)
//            ContextCompat.startActivity(context, intent, null)
        }
    }

    override fun getItemCount() = foldersList.size

    class MyHolder(binding: FolderViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val folderName = binding.folderNameFV
        val root = binding.root
    }
}
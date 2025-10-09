package com.example.umc_android_mission2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_android_mission2.databinding.ItemSavedSongBinding


class SavedSongsRVAdapter(private val savedSongList: MutableList<SavedSongsData>) :
    RecyclerView.Adapter<SavedSongsRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedSongsRVAdapter.ViewHolder {
        val binding: ItemSavedSongBinding =
            ItemSavedSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedSongsRVAdapter.ViewHolder, position: Int) {
        holder.bind(savedSongList[position])

    }

    override fun getItemCount(): Int = savedSongList.size

    //removeItem 함수를 어댑터의 함수로 추가
    fun removeItem(position: Int) {
        savedSongList.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class ViewHolder(private val binding: ItemSavedSongBinding) : RecyclerView.ViewHolder(binding.root) {

        //ViewHolder 생성 시, 람다를 사용해 클릭 리스너 설정
        init {
            binding.itemSavedSongMoreIv.setOnClickListener {
                removeItem(adapterPosition)
            }
        }

        fun bind(savedSong: SavedSongsData) {
            binding.itemSavedSongTitleTv.text = savedSong.title
            binding.itemSavedSongArtistTv.text = savedSong.artist
            savedSong.coverImg?.let { image ->
                binding.itemSavedSongAlbumIv.setImageResource(image)
            }
        }
    }
}
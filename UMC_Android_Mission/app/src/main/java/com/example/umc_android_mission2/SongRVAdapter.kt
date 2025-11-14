package com.example.umc_android_mission2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_android_mission2.databinding.ItemSongBinding

// DB 엔티티인 'List<Song>'을 받도록 수정
class SongRVAdapter(private val songList: List<Song>) :
    RecyclerView.Adapter<SongRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemSongBinding =
            ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // ViewHolder에 현재 아이템의 위치(position)도 함께 전달
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(songList[position], position)
    }

    override fun getItemCount(): Int = songList.size

    inner class ViewHolder(private val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root) {

        //'Song' 엔티티와 트랙 번호(position)를 받아 뷰에 바인딩
        fun bind(song: Song, position: Int) {
            // 트랙 번호는 1부터 시작하도록 position + 1 (기존의 num을 대체)
            binding.itemSongNumTv.text = String.format("%02d", position + 1)
            binding.itemSongTitleTv.text = song.title
            binding.itemSongArtistTv.text = song.artist
        }
    }
}
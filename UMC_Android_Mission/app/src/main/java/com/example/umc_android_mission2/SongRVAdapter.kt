package com.example.umc_android_mission2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_android_mission2.databinding.ItemSongBinding

class SongRVAdapter(private val songList: MutableList<SongData>) :
    RecyclerView.Adapter<SongRVAdapter.ViewHolder>() {

    // ViewHolder를 생성할 때 호출
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemSongBinding =
            ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // ViewHolder에 데이터를 바인딩할 때 호출
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(songList[position])
    }

    // 데이터 세트의 크기를 가져옴
    override fun getItemCount(): Int = songList.size

    // ViewHolder 클래스
    inner class ViewHolder(private val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root) {
        // 데이터를 뷰에 바인딩
        fun bind(song: SongData) {
            // 숫자를 두 자리로, 앞자리가 비면 0으로 채우도록 수정
            binding.itemSongNumTv.text = String.format("%02d", song.num)
            binding.itemSongTitleTv.text = song.title
            binding.itemSongArtistTv.text = song.artist
        }
    }
}
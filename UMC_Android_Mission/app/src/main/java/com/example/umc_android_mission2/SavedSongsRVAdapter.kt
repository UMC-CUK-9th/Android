package com.example.umc_android_mission2

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_android_mission2.databinding.ItemSavedSongBinding

class SavedSongsRVAdapter() :
    RecyclerView.Adapter<SavedSongsRVAdapter.ViewHolder>() {

    // 어댑터가 사용할 데이터 타입을 Song으로 완전히 변경
    private val songList = ArrayList<Song>()

    interface OnItemClickListener {
        fun onMoreClick(song: Song) // 인터페이스의 파라미터 Song으로 변경
    }
    private lateinit var mItemClickListener: OnItemClickListener
    fun setMyItemClickListener(itemClickListener: OnItemClickListener) {
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedSongsRVAdapter.ViewHolder {
        val binding: ItemSavedSongBinding =
            ItemSavedSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedSongsRVAdapter.ViewHolder, position: Int) {
        holder.bind(songList[position])
        holder.binding.itemSavedSongMoreIv.setOnClickListener {
            mItemClickListener.onMoreClick(songList[position])
        }
    }

    override fun getItemCount(): Int = songList.size

    // 외부에서 데이터를 받아올 때도 Song 리스트를 사용
    @SuppressLint("NotifyDataSetChanged")
    fun addSongs(songs: List<Song>) {
        this.songList.clear()
        this.songList.addAll(songs)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemSavedSongBinding) : RecyclerView.ViewHolder(binding.root) {
      //ViewHolder가 Song 객체와 데이터를 바인딩하도록 수정
        fun bind(song: Song) {
            binding.itemSavedSongTitleTv.text = song.title
            binding.itemSavedSongArtistTv.text = song.artist
            song.coverImg?.let {
                binding.itemSavedSongAlbumIv.setImageResource(it)
            }
        }
    }
}

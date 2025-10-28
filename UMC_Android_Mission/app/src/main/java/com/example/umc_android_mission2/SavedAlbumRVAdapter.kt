package com.example.umc_android_mission2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_android_mission2.databinding.ItemSavedAlbumBinding


class SavedAlbumRVAdapter(private val albumList: MutableList<SavedAlbumData>) :
    RecyclerView.Adapter<SavedAlbumRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedAlbumRVAdapter.ViewHolder {
        val binding: ItemSavedAlbumBinding =
            ItemSavedAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedAlbumRVAdapter.ViewHolder, position: Int) {
        holder.bind(albumList[position])
    }

    override fun getItemCount(): Int = albumList.size

    fun removeItem(position: Int) {
        albumList.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class ViewHolder(private val binding: ItemSavedAlbumBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            // 더보기 버튼 클릭 시 아이템 삭제
            binding.itemSavedAlbumMoreIv.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    removeItem(position)
                }
            }

            // 재생 버튼 클릭 시 아이콘 및 상태 변경
            binding.itemSavedAlbumPlayIv.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = albumList[position]
                    item.isPlaying = !item.isPlaying // 상태를 반전
                    notifyItemChanged(position) // 어댑터에게 해당 아이템이 변경되었음을 알림
                }
            }
        }

        fun bind(album: SavedAlbumData) {
            binding.itemSavedAlbumTitleTv.text = album.title
            binding.itemSavedAlbumArtistTv.text = album.artist
            binding.itemSavedAlbumInfoTv.text = album.info
            album.coverImg?.let { image ->
                binding.itemSavedAlbumIv.setImageResource(image)
            }

            // isPlaying 상태에 따라 재생/일시정지 아이콘 설정
            if (album.isPlaying) {
                binding.itemSavedAlbumPlayIv.setImageResource(R.drawable.btn_miniplay_pause)
            } else {
                binding.itemSavedAlbumPlayIv.setImageResource(R.drawable.btn_miniplayer_play)
            }
        }
    }
}
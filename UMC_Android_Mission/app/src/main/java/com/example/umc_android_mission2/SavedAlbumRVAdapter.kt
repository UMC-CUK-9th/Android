package com.example.umc_android_mission2

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_android_mission2.databinding.ItemSavedAlbumBinding

//데이터 모델을 Album으로 변경
class SavedAlbumRVAdapter() :
    RecyclerView.Adapter<SavedAlbumRVAdapter.ViewHolder>() {

    private val albumList = ArrayList<Album>()

    interface OnItemClickListener {
        fun onRemoveAlbum(albumId: Int)
    }

    private lateinit var mItemClickListener: OnItemClickListener

    fun setMyItemClickListener(itemClickListener: OnItemClickListener) {
        mItemClickListener = itemClickListener
    }

    // 어댑터에 데이터를 설정하는 함수
    @SuppressLint("NotifyDataSetChanged")
    fun addAlbums(albums: List<Album>) {
        this.albumList.clear()
        this.albumList.addAll(albums)
        notifyDataSetChanged()
    }

    // position 위치의 아이템을 제거하는 함수
    private fun removeItem(position: Int){
        albumList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, albumList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemSavedAlbumBinding =
            ItemSavedAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(albumList[position])

        // 더보기 버튼 클릭 -> 리스너를 통해 프래그먼트로 이벤트 전달
        holder.binding.itemSavedAlbumMoreIv.setOnClickListener {
            mItemClickListener.onRemoveAlbum(albumList[position].albumIdx)
            removeItem(position)
        }
    }

    override fun getItemCount(): Int = albumList.size

    inner class ViewHolder(val binding: ItemSavedAlbumBinding) : RecyclerView.ViewHolder(binding.root) {
        // bind 함수의 파라미터를 Album으로 변경합니다.
        fun bind(album: Album) {
            binding.itemSavedAlbumTitleTv.text = album.title
            binding.itemSavedAlbumArtistTv.text = album.artist
            album.coverImg?.let {
                binding.itemSavedAlbumIv.setImageResource(it)
            }
        }
    }
}
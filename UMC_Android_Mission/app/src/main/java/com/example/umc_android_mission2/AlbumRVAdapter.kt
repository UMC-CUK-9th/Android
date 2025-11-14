package com.example.umc_android_mission2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_android_mission2.databinding.ItemAlbumBinding

// 생성자가 Room의 Album 엔티티를 직접 받도록 수정
class AlbumRVAdapter(
    private val albumList: List<Album>,
    private val onItemClicked: (Album) -> Unit, //전체 엘범 부분
    private val onPlayClicked: (Album) -> Unit  //Play 버튼 부분
) : RecyclerView.Adapter<AlbumRVAdapter.ViewHolder>() {

    // ViewHolder를 생성할 때 호출
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemAlbumBinding =
            ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // ViewHolder에 데이터를 바인딩할 때 호출
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(albumList[position])
    }
    // 데이터 세트의 크기를 가져옴
    override fun getItemCount(): Int = albumList.size

    inner class ViewHolder(val binding: ItemAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {

            init {
                // 클릭 리스너들이 Album 객체를 직접 받도록 수정
                binding.root.setOnClickListener {
                    onItemClicked(albumList[adapterPosition])
                }
                binding.itemAlbumPlayIv.setOnClickListener {
                    onPlayClicked(albumList[adapterPosition])
                }
            }

        // bind 함수가 Album 엔티티를 직접 받도록 수정
        fun bind(album: Album) {
            binding.itemAlbumTitleTv.text = album.title
            binding.itemAlbumArtistTv.text = album.artist
            // coverImg가 null이 아닐 때만 안전하게 이미지를 설정
            album.coverImg?.let {
                binding.itemAlbumCoverIv.setImageResource(it)
            }
        }
    }
}
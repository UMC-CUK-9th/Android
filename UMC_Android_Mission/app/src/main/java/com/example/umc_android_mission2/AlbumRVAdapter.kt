package com.example.umc_android_mission2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.umc_android_mission2.databinding.ItemAlbumBinding

// 생성자에서 클릭 리스너 역할을 하는 람다 함수를 함께 전달받기
class AlbumRVAdapter(
    private val albumList: MutableList<AlbumData>,
    private val onItemClicked: (AlbumData) -> Unit,//전체 엘범 부분
    private val onPlayClicked: (AlbumData) -> Unit//Play 버튼 부분
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

            init{
                binding.root.setOnClickListener {//itemAlbumCoverIv 대신 root 사용해 play 버튼 제외 모든 부분 클릭할 때로 변경
                    onItemClicked(albumList[adapterPosition])
                }
                binding.itemAlbumPlayIv.setOnClickListener {
                    onPlayClicked(albumList[adapterPosition])
                }
            }

        fun bind(albumData: AlbumData) {
            binding.itemAlbumTitleTv.text = albumData.title
            binding.itemAlbumArtistTv.text = albumData.artist
            // coverImg가 null이 아닐 때만 안전하게 이미지를 설정
            albumData.coverImg?.let {
                binding.itemAlbumCoverIv.setImageResource(it)
            }
        }
    }
}
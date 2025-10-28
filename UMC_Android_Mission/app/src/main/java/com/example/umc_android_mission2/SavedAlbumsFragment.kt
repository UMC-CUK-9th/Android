package com.example.umc_android_mission2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc_android_mission2.databinding.FragmentSavedAlbumsBinding

class SavedAlbumsFragment: Fragment() {
    private var _binding: FragmentSavedAlbumsBinding? = null
    private val binding get() = _binding!!

    private val albumData = mutableListOf<SavedAlbumData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSavedAlbumsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 더미 데이터 추가
        addDummyData()

        // 리사이클러뷰 어댑터 설정
        val savedAlbumRVAdapter = SavedAlbumRVAdapter(albumData)
        binding.lockerSavedAlbumListRv.adapter = savedAlbumRVAdapter
        binding.lockerSavedAlbumListRv.layoutManager = LinearLayoutManager(context)
    }

    private fun addDummyData() {
        albumData.apply {
            add(SavedAlbumData("Lost corner", "Kenshi Yonezu", R.drawable.img_album_lost, info = "2024.08.21 | 정규 | J-Pop"))
            add(SavedAlbumData("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp, "2021.05.21 | 싱글 | 댄스"))
            add(SavedAlbumData("Lilac", "아이유 (IU)", R.drawable.img_album_exp2, "2021.03.25 | 정규 | 팝"))
            add(SavedAlbumData(title = "Spinning Globe", artist ="Kenshi Yonezu", coverImg = R.drawable.img_album_spinning, info = "2023.07.17 | 정규 | J-Pop" ))
            add(SavedAlbumData("Lost corner", "Kenshi Yonezu", R.drawable.img_album_lost, info = "2024.08.21 | 정규 | J-Pop"))
            add(SavedAlbumData("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp, "2021.05.21 | 싱글 | 댄스"))
            add(SavedAlbumData("Lilac", "아이유 (IU)", R.drawable.img_album_exp2, "2021.03.25 | 정규 | 팝"))
            add(SavedAlbumData(title = "Spinning Globe", artist ="Kenshi Yonezu", coverImg = R.drawable.img_album_spinning, info = "2023.07.17 | 정규 | J-Pop" ))

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
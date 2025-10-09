package com.example.umc_android_mission2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.umc_android_mission2.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var albumDatas = ArrayList<AlbumData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        albumDatas.clear()

        albumDatas.apply {
            add(AlbumData(title = "Lost corner", artist = "Kenshi Yonezu", coverImg = R.drawable.img_album_lost))
            add(AlbumData(title = "Lilac", artist = "아이유(IU)", coverImg = R.drawable.img_album_exp2))
            add(AlbumData(title = "Butter", artist = "방탄소년단(BTS)", coverImg = R.drawable.img_album_exp))
            add(AlbumData(title = "Lost corner", artist = "Kenshi Yonezu", coverImg = R.drawable.img_album_lost))
            add(AlbumData(title = "Lilac", artist = "아이유(IU)", coverImg = R.drawable.img_album_exp2))
            add(AlbumData(title = "Butter", artist = "방탄소년단(BTS)", coverImg = R.drawable.img_album_exp))
        }

        // 어댑터 설정
        val albumRVAdapter = AlbumRVAdapter(
            albumDatas,
            // 아이템 전체 클릭 -> AlbumFragment로 이동
            onItemClicked = { album ->
                openAlbumDetail(album)
            },
            // Play 버튼 클릭 -> MainActivity의 미니플레이어 업데이트
            onPlayClicked = { album ->
                (activity as? MainActivity)?.updateMiniPlayer(album)
            }
        )

        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
        binding.homeTodayMusicAlbumRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // 배너
        val bannerAdapter = BannerVPAdapter(this)
        bannerAdapter.addFragment(BannerFragment())
        binding.homeBannerVp.adapter = bannerAdapter
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        return binding.root
    }

    private fun openAlbumDetail(album: AlbumData) {
        val bundle = Bundle().apply {
            putString("title", album.title)
            putString("artist", album.artist)
            album.coverImg?.let { putInt("coverImg", it) }
        }
        findNavController().navigate(R.id.action_homeFragment_to_albumFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
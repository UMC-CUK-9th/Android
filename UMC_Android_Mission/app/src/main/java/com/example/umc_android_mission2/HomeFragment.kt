package com.example.umc_android_mission2

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

    // 자동 슬라이드를 위한 핸들러와 실행 가능한 작업
    private val handler = Handler(Looper.getMainLooper())
    private val autoSlideRunnable = Runnable {
        binding.homeBannerVp.currentItem = (binding.homeBannerVp.currentItem + 1) % (binding.homeBannerVp.adapter?.itemCount ?: 1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- 앨범 리사이클러뷰 설정 ---
        albumDatas.clear()
        albumDatas.apply {
            add(AlbumData(title = "Lost corner", artist = "Kenshi Yonezu", coverImg = R.drawable.img_album_lost))
            add(AlbumData(title = "Lilac", artist = "아이유(IU)", coverImg = R.drawable.img_album_exp2))
            add(AlbumData(title = "Butter", artist = "방탄소년단(BTS)", coverImg = R.drawable.img_album_exp))
            add(AlbumData(title = "Lost corner", artist = "Kenshi Yonezu", coverImg = R.drawable.img_album_lost))
            add(AlbumData(title = "Lilac", artist = "아이유(IU)", coverImg = R.drawable.img_album_exp2))
            add(AlbumData(title = "Butter", artist = "방탄소년단(BTS)", coverImg = R.drawable.img_album_exp))
        }
        //어댑터 설정
        val albumRVAdapter = AlbumRVAdapter(
            albumDatas,
            // 아이템 전체 클릭 -> AlbumFragment로 이동
            onItemClicked = { album -> openAlbumDetail(album) },
            // Play 버튼 클릭 -> MainActivity의 미니플레이어 업데이트
            onPlayClicked = { album -> (activity as? MainActivity)?.updateMiniPlayer(album) }
        )
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
        binding.homeTodayMusicAlbumRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // --- 배너 ViewPager2 설정 ---
        val bannerAdapter = BannerVPAdapter(this)
        // 슬라이드를 위해 여러 개의 배너 프래그먼트 추가
        bannerAdapter.addFragment(BannerFragment())
        bannerAdapter.addFragment(BannerFragment())
        bannerAdapter.addFragment(BannerFragment())

        binding.homeBannerVp.adapter = bannerAdapter
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // CircleIndicator를 ViewPager2에 연결
        binding.homeBannerIndicator.setViewPager(binding.homeBannerVp)

        // 자동 슬라이드를 위한 페이지 변경 콜백 등록
        binding.homeBannerVp.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // 페이지가 선택될 때마다, 기존의 자동 슬라이드 예약을 취소하고 3초 뒤에 다시 시작
                handler.removeCallbacks(autoSlideRunnable)
                handler.postDelayed(autoSlideRunnable, 3000)
            }
        })
    }

    private fun openAlbumDetail(album: AlbumData) {
        val bundle = Bundle().apply {
            putString("title", album.title)
            putString("artist", album.artist)
            album.coverImg?.let { putInt("coverImg", it) }
        }
        findNavController().navigate(R.id.action_homeFragment_to_albumFragment, bundle)
    }

    override fun onResume() {
        super.onResume()
        // 화면이 다시 보일 때 자동 슬라이드 시작
        handler.postDelayed(autoSlideRunnable, 5000)
    }

    override fun onPause() {
        super.onPause()
        // 화면을 벗어날 때 리소스 낭비를 막기 위해 자동 슬라이드 정지
        handler.removeCallbacks(autoSlideRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
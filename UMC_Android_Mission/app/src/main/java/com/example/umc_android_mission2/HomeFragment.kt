package com.example.umc_android_mission2

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.umc_android_mission2.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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

        setupBanner()

        val albumDao = AlbumDatabase.getInstance(requireContext())!!.albumDao()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val albums = albumDao.getAlbums()
            
            withContext(Dispatchers.Main) {
                setupAlbumRecyclerView(albums)
            }
        }
    }

    private fun setupAlbumRecyclerView(albums: List<Album>) {
        val albumRVAdapter = AlbumRVAdapter(
            albums,
            onItemClicked = { album -> openAlbumDetail(album) },
            onPlayClicked = { album ->
                (activity as? MainActivity)?.updateMiniPlayer(album)
            }
        )
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
        binding.homeTodayMusicAlbumRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupBanner() {
        val bannerAdapter = BannerVPAdapter(this)
        bannerAdapter.addFragment(BannerFragment())
        bannerAdapter.addFragment(BannerFragment())
        bannerAdapter.addFragment(BannerFragment())

        binding.homeBannerVp.adapter = bannerAdapter
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        binding.homeBannerIndicator.setViewPager(binding.homeBannerVp)

        binding.homeBannerVp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(autoSlideRunnable)
                handler.postDelayed(autoSlideRunnable, 3000)
            }
        })
    }

    // AlbumFragment로 이동 시, 앨범의 고유 ID(albumIdx)를 넘겨주도록 수정
    private fun openAlbumDetail(album: Album) {
        val bundle = Bundle().apply {
            putInt("albumId", album.albumIdx)
        }
        findNavController().navigate(R.id.action_homeFragment_to_albumFragment, bundle)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(autoSlideRunnable, 5000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(autoSlideRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
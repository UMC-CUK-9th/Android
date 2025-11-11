package com.example.umc_android_mission2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.umc_android_mission2.databinding.FragmentDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private var albumId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            albumId = it.getInt("albumId", 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AlbumDatabase.getInstance(requireContext())!!

        // 코루틴을 사용해 DB에서 앨범 정보 가져오기
        viewLifecycleOwner.lifecycleScope.launch {
            val album = withContext(Dispatchers.IO) {
                db.albumDao().getAlbum(albumId)
            }

            // Main Thread에서 UI 업데이트
            withContext(Dispatchers.Main) {
                // null 체크를 통해 안정성 확보
                binding.detailAlbumTv.text = "이 앨범의 이름은 ${album?.title ?: "정보 없음"} 입니다."
                binding.detailArtistTv.text = "이 앨범의 작곡가는 ${album?.artist ?: "정보 없음"} 입니다."
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
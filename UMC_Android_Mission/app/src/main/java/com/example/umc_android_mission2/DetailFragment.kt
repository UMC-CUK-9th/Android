package com.example.umc_android_mission2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.umc_android_mission2.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private var albumTitle: String? = null
    private var artistName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            albumTitle = it.getString(ARG_ALBUM_TITLE)
            artistName = it.getString(ARG_ARTIST_NAME)
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
        // XML에 정의된 기본 텍스트를 사용하지 않고, 전달받은 값으로 설정
        // 만약 전달받은 값이 없다면 (null이라면) 기본 텍스트나 빈 문자열을 표시할 수 있습니다.
        binding.detailAlbumTv.text = "이 앨범의 이름은 ${albumTitle ?: "정보 없음"} 입니다."
        binding.detailArtistTv.text = "이 앨범의 작곡가는 ${artistName ?: "정보 없음"} 입니다."
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_ALBUM_TITLE = "albumTitle"
        private const val ARG_ARTIST_NAME = "artistName"

        // DetailFragment를 생성하고 필요한 데이터를 arguments Bundle을 통해 안전하게 전달하는 역할
        @JvmStatic
        fun newInstance(albumTitle: String, artistName: String) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ALBUM_TITLE, albumTitle)
                    putString(ARG_ARTIST_NAME, artistName)
                }
            }
    }
}
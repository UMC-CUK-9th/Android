package com.example.umc_android_mission2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.umc_android_mission2.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlbumFragment : Fragment() {

    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!

    private val information = arrayListOf("수록곡", "상세정보", "영상")

    private var albumId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            albumId = it.getInt("albumId", 0)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AlbumDatabase.getInstance(requireContext())!!

        viewLifecycleOwner.lifecycleScope.launch {
            val album = withContext(Dispatchers.IO) {
                db.albumDao().getAlbum(albumId)
            }

            withContext(Dispatchers.Main) {
                if (album == null) {
                    findNavController().popBackStack()
                } else {
                    binding.albumTitleTv.text = album.title
                    binding.albumArtistTv.text = album.artist
                    album.coverImg?.let { binding.albumCoverIv.setImageResource(it) }

                    val albumAdapter = AlbumVPAdapter(this@AlbumFragment, albumId)
                    binding.albumContentVp.adapter = albumAdapter
                    TabLayoutMediator(binding.albumContentTb, binding.albumContentVp) { tab, position ->
                        tab.text = information[position]
                    }.attach()
                }
            }
        }

        binding.albumBackIv.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}